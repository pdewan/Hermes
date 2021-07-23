package analyzer.extension.replayView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;

import analyzer.Analyzer;
import analyzer.MainConsoleUI;
import analyzer.extension.ADifficultyPredictionAndStatusPrinter;
import fluorite.commands.ConsoleOutput;
import fluorite.commands.Delete;
import fluorite.commands.DiffBasedFileOpenCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.EclipseCommand;
import fluorite.commands.ExceptionCommand;
import fluorite.commands.FileOpenCommand;
import fluorite.commands.Insert;
import fluorite.commands.LocalCheckCommand;
import fluorite.commands.MoveCaretCommand;
import fluorite.commands.PauseCommand;
import fluorite.commands.Replace;
import fluorite.commands.RunCommand;
import fluorite.commands.SelectTextCommand;
import fluorite.commands.ShellCommand;
import fluorite.commands.WebCommand;
import fluorite.model.EHEventRecorder;
import fluorite.util.EHUtilities;
import programmatically.AnEclipseProgrammaticController;

public class AReplayer extends ADifficultyPredictionAndStatusPrinter{
	private List<List<EHICommand>> nestedCommands;
	private final AnEclipseProgrammaticController PROGRAMATIC_CONTROLLER;
	private Analyzer analyzer;
	private String currentProjectPath;
	private int i = 0;
	private int j = 0;
	private boolean backed = false;
	private boolean forwarded = false;
	private FileOpenCommand currentFile = null;
	private IEditorPart editor = null;
	private	boolean fileChanged = false;
	private String replacedString = "";
	private ArrayList<EHICommand> commandList = new ArrayList<>();
	private int numOfCommands = 0;
	private int currentExceptions = 0; 
	private int totalExceptions = 0;
	private long currentTimeSpent = 0;
	private long totalTimeSpent = 0;
	private final static long ONE_MINUTE = 60 * 1000;
	private final static long ONE_HOUR = 60 * ONE_MINUTE;
	private String replayedFile = "";
	private List<List<List<String>>> metrics = null;
	private boolean changeByTime = false;
	private int filej = 0;
	private long previousTime = 0;
	private long currentTime = 0;
	private List<List<String>> newFiles = null; 
	private List<List<String>> deletedFiles = null; 
	private List<List<String>> refactorRecords = null;
	private static final long PAUSE = 5*60*1000;


	public AReplayer(Analyzer anAnalyzer) {
		super(anAnalyzer);
		analyzer = anAnalyzer;
		PROGRAMATIC_CONTROLLER = AnEclipseProgrammaticController.getInstance();
		currentProjectPath = getCurrentProjectPath();
	}

	public List<List<EHICommand>> replayLogs(String projectPath, Analyzer analyzer){
		File logFolder = new File(projectPath, "Logs"+File.separator+"Eclipse");
		if (!logFolder.exists()) {
			System.out.println("No logs found for project " + projectPath);
		}
		File[] logFiles = logFolder.listFiles(File::isDirectory);
		if (logFiles != null && logFiles.length > 0) {
			logFolder = logFiles[0];
		} 
		if (logFolder.getName().equals("Eclipse")) {
			refineLogFiles(logFolder.getPath());
		}
		nestedCommands = analyzer.convertXMLLogToObjects(logFolder.getPath());
		sortNestedCommands(nestedCommands);
		buildRefactorRecord(projectPath);
		if (nestedCommands.isEmpty()) {
			i = 0;
			j = 0;
		} else {
			i = nestedCommands.size()-1;
			if (nestedCommands.get(i).isEmpty()) {
				j = 0;
			} else {
				j = nestedCommands.get(i).size()-1;
			}
		}
		return nestedCommands;
	}
	
	protected void sortNestedCommands(List<List<EHICommand>> nestedCommands){
		for (int i = 0; i < nestedCommands.size(); i++) {
			List<EHICommand> commands = nestedCommands.get(i);
			if (commands == null || commands.size() < 2) {
				nestedCommands.remove(i);
				i--;
			} else if (commands.size() > 2) {
				sortCommands(commands, 0, commands.size()-1);
			}
		}
	}
	
	private void sortCommands(List<EHICommand> commands, int start, int end){
		for(int i = 0; i < commands.size(); i++) {
			if (commands.get(i) == null) {
				commands.remove(i);
				i--;
			}
		}
		EHICommand command = null;
		long cur = 0;
		for(int i = 2; i < commands.size(); i++) {
			command = commands.get(i);
			cur = command.getStartTimestamp()+command.getTimestamp();
			int j = i-1;
			while (j > 1){
				if (commands.get(j).getStartTimestamp() + commands.get(j).getTimestamp() > cur) {
					j--;
				} else {
					break;
				}
			}
			if (j < i-1) {
				commands.remove(i);
				commands.add(j+1, command);
			}
		}
	}

	public void createMetrics(String projectPath) {
		File metricFolder = new File(projectPath+File.separator+"Logs"+File.separator+"Metrics");
		List<String> metricFiles = MainConsoleUI.getFilesForFolder(metricFolder);
		metrics = new ArrayList<>();
		String largestFileName = "";
		String secondLargestFileName = "";
		for (int i = 0; i < metricFiles.size(); i++) {
			String aFileName = metricFiles.get(i);
			if (aFileName.compareTo(largestFileName) > 0) {
				secondLargestFileName = largestFileName;
				largestFileName = aFileName;
			} else if (aFileName.compareTo(secondLargestFileName) > 0) {
				secondLargestFileName = aFileName;
			}
		}
		for (int i = 0; i < metricFiles.size(); i++) {
			List<List<String>> metric = new ArrayList<>();
			metrics.add(metric);
			BufferedReader r;
			try {
				r = new BufferedReader(new FileReader(new File(metricFolder.getPath()+File.separator+metricFiles.get(i))));
				String line=r.readLine();
				while(true){
					line = r.readLine();
					if (line == null) {
						break;
					}
					try {
						Integer.parseInt(line.substring(0, line.indexOf(",")));
					} catch (NumberFormatException e) {
						continue;
					}
					metric.add(Arrays.asList(line.split(","))); 
				}
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static final String XML_FILE_ENDING = "\r\n</Events>"; 

	public void refineLogFiles(String logPath){
		try {
			File logDirectory = new File(logPath);
			File currentLCKFile = new File(logDirectory, EHEventRecorder.loggerToFileName.get(Logger.getLogger(EHEventRecorder.class.getName())).getName()+".lck");
			for (File file : logDirectory.listFiles()) {
				if (!file.getPath().contains(".lck") && !currentLCKFile.getPath().contains(file.getPath())) {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String lastLine = null;
					String currentLine = null;
					while((currentLine = reader.readLine()) != null) {
						lastLine = currentLine;
					}
					if (lastLine != null && !lastLine.endsWith("</Events>")) {
						BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
						writer.write(XML_FILE_ENDING);
						writer.close();
					}	
					reader.close();
				} else if (file.getPath().contains(".lck") && !file.getPath().equals(currentLCKFile.getPath())) {
					file.delete();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} 
	}

	public void buildRefactorRecord(String projectPath){
		refactorRecords = new ArrayList<>();
		File refactorHistory = new File(projectPath + File.separator + "Logs" + File.separator + "RefactorHistory.txt");
		try {
			if (refactorHistory.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(refactorHistory));
//				refactorRecordsWithTimeStamps = new TreeMap<>(Collections.reverseOrder());
//				Map<String, String> refactorHistoryMap = new HashMap<>();
				String line = null;

				while((line = br.readLine()) != null){
					List<String> refactorRecord = new ArrayList<>();
					refactorRecord.add(line.substring(0, line.indexOf(" ")));
					refactorRecord.add(line.substring(line.indexOf(" ")+1, line.indexOf("----->")));
					refactorRecord.add(line.substring(line.indexOf("----->")+7));
					refactorRecords.add(refactorRecord);					
//					if (line.startsWith("Time stamp: ")) {
//						if (timeStamp != 0) {
//							refactorRecordsWithTimeStamps.put(timeStamp, refactorHistoryMap);
//							refactorHistoryMap = new HashMap<>();
//						}
//						timeStamp = Long.parseLong(line.substring(12));
//					} else if (line.contains("-----")) {
//						refactorHistoryMap.put(line.substring(line.indexOf("-----")+5), line.substring(0,line.indexOf("-----")));
//					}
//					line = br.readLine();
				}
//				refactorRecordsWithTimeStamps.put(timeStamp, refactorHistoryMap);
				br.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public void setup() {
		if (nestedCommands == null || !getCurrentProjectPath().equals(currentProjectPath)) {
			currentProjectPath = getCurrentProjectPath();
			replayLogs(currentProjectPath, analyzer);
			createMetrics(currentProjectPath);
//			createFileLogs(currentProjectPath);
			outer: 
				for(int n = nestedCommands.size()-1; n >= 0; n--) {
					for(int m = nestedCommands.get(n).size()-1; m >= 0; m--) {
						if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
							currentFile = (FileOpenCommand)nestedCommands.get(n).get(m);
							PROGRAMATIC_CONTROLLER.getOrCreateProject(currentProjectPath.substring(currentProjectPath.lastIndexOf(File.separator)+1), currentProjectPath);
							String path = currentFile.getDataMap().get("filePath");
							PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
							editor = EHUtilities.getActiveEditor();
							filej = m;
							break outer;
						}
					}
				}
			forwarded = false;
			backed = false;
			replacedString = "";
			numOfCommands = 0;
			currentExceptions = 0;
			totalTimeSpent = 0;
			currentTimeSpent = 0;
			currentTime = System.currentTimeMillis();
			calculateCurrentTimeSpent();
			calculateTotalTimeSpent();
			for (List<EHICommand> commands : nestedCommands) {
				numOfCommands += commands.size();
				for (EHICommand command : commands) {
					if (command instanceof ExceptionCommand) {
						currentExceptions++;
					}
				}
			}
			totalExceptions = currentExceptions;
			i = nestedCommands.size() - 1;
			j = nestedCommands.get(i).size() - 1;
//			EHICommand startCommand = nestedCommands.get(i).get(j);
//			difficultyTime = startCommand.getTimestamp()+startCommand.getStartTimestamp() == 0? startCommand.getTimestamp2():startCommand.getTimestamp()+startCommand.getStartTimestamp();
		} else {
			IEditorPart currentEditor = EHUtilities.getActiveEditor();
			if (editor == null || !editor.equals(currentEditor) || currentFile != null) {
				String path = currentFile.getDataMap().get("filePath");
				if (path.contains("src")) {
					PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				}
				editor = EHUtilities.getActiveEditor();
			}
		}
	}
	
	public void maybeAddToCommandList(EHICommand command) {
		if (command instanceof WebCommand || command instanceof ExceptionCommand || 
			command instanceof ConsoleOutput || command instanceof PauseCommand || 
			command instanceof LocalCheckCommand) {
			commandList.add(command);
		}
	}

	public ArrayList<EHICommand> back(String numStep, String step){
		changeByTime = false;
		commandList.clear();
		previousTime = currentTime;
		switch (step) {
		case ReplayView.EDIT:
			for (int a = 0; a < numStep.length(); a++) {
				if (!Character.isDigit(numStep.charAt(a))) {
					JOptionPane.showMessageDialog(null, "Please enter a positive integer", "ParseException", JOptionPane.INFORMATION_MESSAGE);
					return null;
				}
			}
			return backEdit(Integer.parseInt(numStep));
		case ReplayView.ONE_MINUTE:
			for (int a = 0; a < numStep.length(); a++) {
				if (!Character.isDigit(numStep.charAt(a))) {
					JOptionPane.showMessageDialog(null, "Please enter a positive integer", "ParseException", JOptionPane.INFORMATION_MESSAGE);
					return null;
				}
			}
			changeByTime = true;
			currentTimeSpent -= Integer.parseInt(numStep)*ONE_MINUTE;
			return backByTime(Integer.parseInt(numStep)*ONE_MINUTE);
		case ReplayView.ONE_HOUR:
			for (int a = 0; a < numStep.length(); a++) {
				if (!Character.isDigit(numStep.charAt(a))) {
					JOptionPane.showMessageDialog(null, "Please enter a positive integer", "ParseException", JOptionPane.INFORMATION_MESSAGE);
					return null;
				}
			}
			changeByTime = true;
			currentTimeSpent -= Integer.parseInt(numStep)*ONE_HOUR;
			return backByTime(Integer.parseInt(numStep)*ONE_HOUR);
		case ReplayView.EXCEPTION:
			return backToException();
		case ReplayView.EXCEPTION_TO_FIX:
			return backToException();
		case ReplayView.FIX:
			return backToFix();
		case ReplayView.LOCALCHECKS:
			return backToLocalCheck();
		case ReplayView.RUN:
			return backToRun();
		case ReplayView.DEBUG:
			return backToDebug();
		case ReplayView.COMPILE:
			return backToCompile();
		case ReplayView.DIFFICULTY:
		case ReplayView.DIFFICULTY_TO_NO_DIFFICULTY:
			return backToDifficulty();
//		case ReplayView.NEW_FILE:
//			return backToNewFile();
//		case ReplayView.DELETE_FILE:
//			return backToDeleteFile();
//		case ReplayView.REFACTOR:
//			return backToRefactor();
		case ReplayView.OPEN_FILE:
			return backToOpenFile();
		case ReplayView.SAVE:
			return backToSave();
		case ReplayView.PAUSE:
			for (int a = 0; a < numStep.length(); a++) {
				if (!Character.isDigit(numStep.charAt(a))) {
					JOptionPane.showMessageDialog(null, "Please enter a positive integer", "ParseException", JOptionPane.INFORMATION_MESSAGE);
					return null;
				}
			}
			return backToPause(Integer.parseInt(numStep));
		case ReplayView.WEB:
			return backToWeb();
		}
		commandList.clear();
		return commandList;
	}
	public ArrayList<EHICommand> backToWeb(){
		forwarded = false;
		int insertLength = 0;
		int caretOffset = -1;
		outer:
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				insertLength = 0;
				caretOffset = -1;
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					maybeAddToCommandList(command);
					if (command instanceof FileOpenCommand && command.equals(currentFile)) {
						commandList.add(command);
						int m = j-1;
						inner:
							for(int n = i; n >= 0; n--) {
								for(; m >= 0; m--) {
									if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
										currentFile = (FileOpenCommand)nestedCommands.get(n).get(m);
										fileChanged = true;

										filej = m;
										break inner;
									}
								}
								if (n > 0) {
									m = nestedCommands.get(n-1).size()-1;
								}
							}
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null && !replacedString.equals(trimNewLine(insertedText))) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								replacedString = trimNewLine(command.getDataMap().get("deletedText"));
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof WebCommand) {
						j--;
						break outer;
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions--;
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		backed = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (!path.contains("src") || !(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
				j--;
				return commandList;
			}
			PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
			editor = EHUtilities.getActiveEditor();
			fileChanged = false;
		}
		PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		return commandList;
	}

	public ArrayList<EHICommand> backToPause(int time){
		forwarded = false;
		int insertLength = 0;
		int caretOffset = -1;
		outer:
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				insertLength = 0;
				caretOffset = -1;
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					maybeAddToCommandList(command);
					if (command instanceof FileOpenCommand && command.equals(currentFile)) {
						commandList.add(command);
						int m = j-1;
						inner:
							for(int n = i; n >= 0; n--) {
								for(; m >= 0; m--) {
									if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
										currentFile = (FileOpenCommand)nestedCommands.get(n).get(m);
										fileChanged = true;

										filej = m;
										break inner;
									}
								}
								if (n > 0) {
									m = nestedCommands.get(n-1).size()-1;
								}
							}
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null && !replacedString.equals(trimNewLine(insertedText))) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								replacedString = trimNewLine(command.getDataMap().get("deletedText"));
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof PauseCommand) {
//						commandList.add(command);
						int pause = Integer.parseInt(command.getDataMap().get("pause"));
						if (pause/1000 >= time) {
							j--;
							break outer;
						}
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions--;
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		backed = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (!(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
				j--;
				return commandList;
			}
			PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
			editor = EHUtilities.getActiveEditor();
			fileChanged = false;
		}
		PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		return commandList;
	}

	public ArrayList<EHICommand> backToLocalCheck(){
		forwarded = false;
		int insertLength = 0;
		int caretOffset = -1;
		outer:
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				insertLength = 0;
				caretOffset = -1;
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					maybeAddToCommandList(command);
					if (command instanceof FileOpenCommand && command.equals(currentFile)) {
						commandList.add(command);
						int m = j-1;
						inner:
							for(int n = i; n >= 0; n--) {
								for(; m >= 0; m--) {
									if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
										currentFile = (FileOpenCommand)nestedCommands.get(n).get(m);
										fileChanged = true;

										filej = m;
										break inner;
									}
								}
								if (n > 0) {
									m = nestedCommands.get(n-1).size()-1;
								}
							}
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null && !replacedString.equals(trimNewLine(insertedText))) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								replacedString = trimNewLine(command.getDataMap().get("deletedText"));
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof LocalCheckCommand) {
						j--;
						break outer;
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions--;
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		backed = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (!(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
				j--;
				return commandList;
			}
			PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
			editor = EHUtilities.getActiveEditor();
			fileChanged = false;
		}
		PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		return commandList;
	}

	public ArrayList<EHICommand> backToRun(){
		forwarded = false;
		boolean hitRun = false;
		int insertLength = 0;
		int caretOffset = -1;
		outer:
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				insertLength = 0;
				caretOffset = -1;
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					maybeAddToCommandList(command);
					if (command instanceof RunCommand && command.getAttributesMap().get("type").equals("Run")) {
						commandList.add(command);
						hitRun = true;
					}
					if (hitRun && !(command instanceof RunCommand)) {
						break outer;
					}
					if (command instanceof FileOpenCommand && command.equals(currentFile)) {
						commandList.add(command);
						int m = j-1;
						inner:
							for(int n = i; n >= 0; n--) {
								for(; m >= 0; m--) {
									if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
										currentFile = (FileOpenCommand)nestedCommands.get(n).get(m);
										fileChanged = true;
										filej = m;
										break inner;
									}
								}
								if (n > 0) {
									m = nestedCommands.get(n-1).size()-1;
								}
							}
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null && !replacedString.equals(trimNewLine(insertedText))) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								replacedString = trimNewLine(command.getDataMap().get("deletedText"));
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions--;
					}

				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		backed = true;
		if (hitRun) {
			if (fileChanged) {
				String path = currentFile.getDataMap().get("filePath");
				if (!(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
					return commandList;
				}
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
			return commandList;

		}
		return commandList;
	}

	public ArrayList<EHICommand> backToDebug(){
		forwarded = false;
		boolean hitDebug = false;
		int insertLength = 0;
		int caretOffset = -1;
		outer:
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				insertLength = 0;
				caretOffset = -1;
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					maybeAddToCommandList(command);
					if (command instanceof RunCommand && command.getAttributesMap().get("type").equals("Debug")) {
						commandList.add(command);
						hitDebug = true;
					}
					if (hitDebug && !(command instanceof RunCommand)) {
						break outer;
					}
					if (command instanceof FileOpenCommand && command.equals(currentFile)) {
						commandList.add(command);
						int m = j-1;
						inner:
							for(int n = i; n >= 0; n--) {
								for(; m >= 0; m--) {
									if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
										currentFile = (FileOpenCommand)nestedCommands.get(n).get(m);
										fileChanged = true;
										filej = m;
										break inner;
									}
								}
								if (n > 0) {
									m = nestedCommands.get(n-1).size()-1;
								}
							}
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null && !replacedString.equals(trimNewLine(insertedText))) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								replacedString = trimNewLine(command.getDataMap().get("deletedText"));
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions--;
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		backed = true;
		if (hitDebug) {
			if (fileChanged) {
				String path = currentFile.getDataMap().get("filePath");
				if (!(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
					return commandList;
				}
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
			return commandList;

		}
		return commandList;
	}

	public ArrayList<EHICommand> backToCompile(){
		forwarded = false;
		boolean hitRun = false;
		int insertLength = 0;
		int caretOffset = -1;
		outer:
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				insertLength = 0;
				caretOffset = -1;
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					maybeAddToCommandList(command);
					if (command instanceof RunCommand) {
						commandList.add(command);
						hitRun = true;
					}
					if (hitRun && !(command instanceof RunCommand)) {
						break outer;
					}
					if (command instanceof FileOpenCommand && command.equals(currentFile)) {
						commandList.add(command);
						int m = j-1;
						inner:
							for(int n = i; n >= 0; n--) {
								for(; m >= 0; m--) {
									if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
										currentFile = (FileOpenCommand)nestedCommands.get(n).get(m);
										fileChanged = true;
										filej = m;
										break inner;
									}
								}
								if (n > 0) {
									m = nestedCommands.get(n-1).size()-1;
								}
							}
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null && !replacedString.equals(trimNewLine(insertedText))) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								replacedString = trimNewLine(command.getDataMap().get("deletedText"));
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions--;
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		backed = true;
		if (hitRun) {
			if (fileChanged) {
				String path = currentFile.getDataMap().get("filePath");
				if (!(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
					return commandList;
				}
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
			return commandList;

		}
		return commandList;
	}

	public ArrayList<EHICommand> backToDifficulty(){
		previousTime = currentTime;
		boolean difficulty = false;
		outer:
			for(int k = metrics.size()-1; k >= 0; k--) {
				List<List<String>> metric = metrics.get(k);
				for(int l = metric.size()-1; l >= 0; l--) {
					if (metric.get(l).get(1).equals("YES") && Long.parseLong(metric.get(l).get(4)) < currentTime) {
						currentTime = Long.parseLong(metric.get(l).get(4));
						difficulty = true;
						break outer;
					}
				}
			}
		if (!difficulty) {
//			difficultyTime = -1;
			currentTime = 0;
		}
		forwarded = false;
		int insertLength = 0;
		int caretOffset = -1;
		outer:
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				insertLength = 0;
				caretOffset = -1;
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					maybeAddToCommandList(command);
					if (command == null) {
						continue;
					}
					if ((command.getStartTimestamp() == 0 && command.getTimestamp2() < currentTime) || (command.getStartTimestamp() != 0 && command.getStartTimestamp()+command.getTimestamp() < currentTime)) {
						if (j==nestedCommands.get(i).size()-1) {
							i++;
							j = 0;
						} else {
							j++;
						}
						break outer;
					} 
					if (command instanceof FileOpenCommand && command.equals(currentFile)) {
						int m = j-1;
						inner:
							for(int n = i; n >= 0; n--) {
								for(; m >= 0; m--) {
									if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
										currentFile = (FileOpenCommand)nestedCommands.get(n).get(m);
										fileChanged = true;
										filej = m;
										break inner;
									}
								}
								if (n > 0) {
									m = nestedCommands.get(n-1).size()-1;
								}
							}
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null && !replacedString.equals(trimNewLine(insertedText))) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								replacedString = trimNewLine(command.getDataMap().get("deletedText"));
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions--;
					}
				}

				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}

		backed = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (!(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
				return commandList;
			}
			PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
			editor = EHUtilities.getActiveEditor();
			fileChanged = false;
		}
		PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		return commandList;
	}

	public ArrayList<EHICommand> backToNewFile(){
		String newFileName = null;
		for (int k = newFiles.size()-1; k >= 0; k--) {
			long newFileTime = Long.parseLong(newFiles.get(k).get(0));
			if (newFileTime < currentTime) {
				currentTime = newFileTime;
				newFileName = newFiles.get(k).get(1);
				break;
			}
		}
		forwarded = false;

		if (newFileName == null) {
			currentTime = 0;
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					if (command == null) {
						continue;
					}
					if (command instanceof Insert || command instanceof Replace || command instanceof Delete || 
							command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || 
							command instanceof ConsoleOutput || command instanceof LocalCheckCommand || 
							command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						commandList.add(command);
						currentExceptions--;
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
			currentExceptions = 0;
		} else {
			boolean moved = false;
		outer:
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					if (command == null) {
						continue;
					}
					if ((command.getStartTimestamp() == 0 && command.getTimestamp2() < currentTime) || (command.getStartTimestamp() != 0 && command.getStartTimestamp()+command.getTimestamp() < currentTime)) {
						if (moved) {
							if (j==nestedCommands.get(i).size()-1) {
								i++;
								j = 0;
							} else {
								j++;
							}
						} else {
							moved = true;
						}
						break outer;
					} 
					if (command instanceof Insert || command instanceof Replace || command instanceof Delete || 
							command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || 
							command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						commandList.add(command);
						currentExceptions--;
					}
				}

				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		}
		backed = true;
		if (newFileName != null) {
			if (!(new File(newFileName).exists())) {
				return commandList;
			}
			PROGRAMATIC_CONTROLLER.openEditor(newFileName.replace(getCurrentProjectPath(), ""));
			editor = EHUtilities.getActiveEditor();
			fileChanged = false;
		}
		return commandList;
	}
	
	public ArrayList<EHICommand> backToDeleteFile(){
		String deleteFileName = null;
		for (int k = deletedFiles.size()-1; k >= 0; k--) {
			long deleteFileTime = Long.parseLong(deletedFiles.get(k).get(0));
			if (deleteFileTime < currentTime) {
				currentTime = deleteFileTime;
				deleteFileName = deletedFiles.get(k).get(1);
				break;
			}
		}
		
		forwarded = false;
		
		if (deleteFileName == null) {
			currentTime = 0;
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					if (command == null) {
						continue;
					}
					if (command instanceof Insert || command instanceof Replace || command instanceof Delete || 
							command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || 
							command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						commandList.add(command);
						currentExceptions--;
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
			currentExceptions = 0;
		} else {
			boolean moved = false;
		outer:
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					if (command == null) {
						continue;
					}
					if ((command.getStartTimestamp() == 0 && command.getTimestamp2() < currentTime) || (command.getStartTimestamp() != 0 && command.getStartTimestamp()+command.getTimestamp() < currentTime)) {
						if (moved) {
							if (j==nestedCommands.get(i).size()-1) {
								i++;
								j = 0;
							} else {
								j++;
							}
						} else {
							moved = true;
						}
						break outer;
					} 
					if (command instanceof Insert || command instanceof Replace || command instanceof Delete || 
							command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || 
							command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						commandList.add(command);
						currentExceptions--;
					}
				}

				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		}
		backed = true;
		if (deleteFileName != null) {
			if (!(new File(deleteFileName).exists())) {
				return commandList;
			}
			PROGRAMATIC_CONTROLLER.openEditor(deleteFileName.replace(getCurrentProjectPath(), ""));
			editor = EHUtilities.getActiveEditor();
			fileChanged = false;
		}
		return commandList;
	}
	
	public ArrayList<EHICommand> backToRefactor(){
		String refactoredName = null;
		for (int k = refactorRecords.size()-1; k >= 0; k--) {
			long refactorTime = Long.parseLong(refactorRecords.get(k).get(0));
			if (refactorTime < currentTime) {
				currentTime = refactorTime;
				refactoredName = refactorRecords.get(k).get(2);
				break;
			}
		}
		
		if (refactoredName == null) {
			currentTime = 0;
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					if (command == null) {
						continue;
					}
					if (command instanceof Insert || command instanceof Replace || command instanceof Delete || 
							command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof ConsoleOutput) {
						commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						commandList.add(command);
						currentExceptions--;
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
			currentExceptions = 0;
		} else {
			boolean moved = false;
		outer:
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					if (command == null) {
						continue;
					}
					if ((command.getStartTimestamp() == 0 && command.getTimestamp2() < currentTime) || (command.getStartTimestamp() != 0 && command.getStartTimestamp()+command.getTimestamp() < currentTime)) {
						if (moved) {
							if (j==nestedCommands.get(i).size()-1) {
								i++;
								j = 0;
							} else {
								j++;
							}
						} else {
							moved = true;
						}
						break outer;
					} 
					if (command instanceof Insert || command instanceof Replace || command instanceof Delete || 
							command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || 
							command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						commandList.add(command);
						currentExceptions--;
					}
				}

				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		}
		backed = true;
		if (refactoredName != null) {
			if (!(new File(refactoredName).exists())) {
				return commandList;
			}
			PROGRAMATIC_CONTROLLER.openEditor(refactoredName.replace(getCurrentProjectPath(), ""));
			editor = EHUtilities.getActiveEditor();
			fileChanged = false;
		}
		return commandList;
	}

	public ArrayList<EHICommand> backToSave(){
		forwarded = false;
		int insertLength = 0;
		int caretOffset = -1;
		outer:
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				insertLength = 0;
				caretOffset = -1;
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					if (command instanceof FileOpenCommand && command.equals(currentFile)) {
						int m = j-1;
						inner:
							for(int n = i; n >= 0; n--) {
								for(; m >= 0; m--) {
									if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
										currentFile = (FileOpenCommand)nestedCommands.get(n).get(m);
										fileChanged = true;
										
										filej = m;
										break inner;
									}
								}
								if (n > 0) {
									m = nestedCommands.get(n-1).size()-1;
								}
							}
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null && !replacedString.equals(trimNewLine(insertedText))) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								replacedString = trimNewLine(command.getDataMap().get("deletedText"));
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("org.eclipse.ui.file.save")) {
						j--;
						commandList.add(command);
						break outer;
					}
					if (command instanceof ExceptionCommand) {
						commandList.add(command);
						currentExceptions--;
					}
					if (command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		backed = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (!(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
				j--;
				return commandList;
			}
			PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
			editor = EHUtilities.getActiveEditor();
			fileChanged = false;
		}
		PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		return commandList;
	}

	public ArrayList<EHICommand> backToOpenFile(){
		forwarded = false;
//		EHICommand newFileComand = null;
		outer:
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					if (command instanceof FileOpenCommand /*&& !command.equals(currentFile)*/) {
						currentFile = (FileOpenCommand)command;
						fileChanged = true;
						filej = j;
						j--;
//						newFileComand = command;
						commandList.add(command);
						break outer;
					}
					if (command instanceof Insert || command instanceof Replace || command instanceof Delete || 
							command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || 
							command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						commandList.add(command);
						currentExceptions--;
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
//		if (newFileComand != null) {
//			commandList.add(newFileComand);
//		}
		backed = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (!(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
				j--;
//				return backToOpenFile();
				return commandList;
			}
			PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
			editor = EHUtilities.getActiveEditor();
			fileChanged = false;
			return commandList;
		}
		return commandList;
	}

	public ArrayList<EHICommand> backEdit(int limit) {
//		if (forwarded) {
//			forwarded = false;
//			backEdit(limit);
//		}
		int num = 0;
		for(; i >= 0; i--) {
			if (i >= nestedCommands.size()) {
				i = nestedCommands.size()-1;
			}
			List<EHICommand> commands = nestedCommands.get(i);
			if (j >= commands.size()) {
				j = commands.size()-1;
			}
			int insertLength = 0;
			int caretOffset = -1;
			for(; j >=0; j--) {
				EHICommand command = commands.get(j);
				if (command instanceof FileOpenCommand && command.equals(currentFile)) {
//					if (num > 0) {
//						num = limit;
//						break;
//					}
					int m = j-1;
					outer:
						for(int n = i; n >= 0; n--) {
							for(; m >= 0; m--) {
								if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
									currentFile = (FileOpenCommand)nestedCommands.get(n).get(m);
									fileChanged = true;
									
									filej = m;
									break outer;
								}
							}
							if (n > 0) {
								m = nestedCommands.get(n-1).size()-1;
							}
						}
				}
				if (command instanceof Insert) {
					String insertedText = command.getDataMap().get("text");
					num++;
					if (insertedText != null && !replacedString.equals(trimNewLine(insertedText))) {
						int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
						int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
						if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
							caretOffset = tempCaretOffset;
							insertLength += tempLength;
							commandList.add(command);
						}
//						} else 
//							break;
					}
					
				}
				if (command instanceof Replace) {
					num++;
					if (command.getDataMap().get("insertedText") != null) {
						int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
						int tempLength = command.getDataMap().get("insertedText").length();
						if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
							replacedString = trimNewLine(command.getDataMap().get("deletedText"));
							caretOffset = tempCaretOffset;
							insertLength += tempLength;
							commandList.add(command);
						} 
//						else 
//							break;
					}
					
				}
				if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
					commandList.add(command);
					insertLength--;
//					if (insertLength < 0) {
//						j--;
//						//						commandList.clear();
//						return backEdit(limit);
//					}
					num++;
				}
				if (command instanceof ExceptionCommand) {
					commandList.add(command);
					currentExceptions--;
				}
				if (command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
					commandList.add(command);
				}
				if (num >= limit) {
					j--;
					break;
				}
			}
			if (num >= limit) {
				backed = true;
				if (fileChanged) {
					String path = currentFile.getDataMap().get("filePath");
					if (!(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
						j--;
//						if (limit-insertLength <= 0) {
							return commandList;
//						}
//						return backEdit(limit-insertLength);
					}
					PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
					editor = EHUtilities.getActiveEditor();
					fileChanged = false;
				}
				PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
				return commandList;
			}
			if (i != 0) {
				j = nestedCommands.get(i-1).size()-1;
			}
		}
		return commandList;
	}

	public ArrayList<EHICommand> backByTime(long backTime) {
		forwarded = false;

		outer:
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				long startTime = nestedCommands.get(i).get(j).getTimestamp();
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				outer2:
					for(; j > 0; j--) {
						EHICommand command = commands.get(j);
						while (command == null) {
							j--;
							if (j < 0) {
								break outer2;
							} else {
								command = commands.get(j);
							}
						}
						if (startTime - command.getTimestamp() > backTime) {
							break outer;
						} 
						if(command instanceof FileOpenCommand) {
							currentFile = (FileOpenCommand)command;
							fileChanged = true;
							commandList.add(command);
						} 
						if(command instanceof Insert || command instanceof Replace || command instanceof ExceptionCommand ||
								(command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS")) || 
								command instanceof Delete || command instanceof ConsoleOutput || command instanceof LocalCheckCommand || 
								command instanceof PauseCommand || command instanceof WebCommand) {
							commandList.add(command);
						}
						if (command instanceof ExceptionCommand) {
							commandList.add(command);
							currentExceptions--;
						}

					}
				if (i > 0) {
					j = nestedCommands.get(i-1).size()-1;
					backTime -= startTime - commands.get(0).getTimestamp();
				}
			}
		if (commandList.size() == 0) {
			return commandList;
		}


		if (!(commandList.get(commandList.size()-1) instanceof FileOpenCommand)) {
			int m = j-1;
			outer:
				for(int n = i; n >= 0; n--) {
					for(; m >= 0; m--) {
						if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
							currentFile = (FileOpenCommand)nestedCommands.get(n).get(m);
							fileChanged = true;
							
							filej = m;
							break outer;
						}
					}
					if (n > 0) {
						m = nestedCommands.get(n-1).size()-1;
					}
				}
		}


		int selectedLength = 0;
		int insertLength = 0;
		int caretOffset = -1;
		for(int index = 0; index < commandList.size(); index++) {
			String insertedString = "";
			EHICommand command = commandList.get(index);	
			if (command instanceof FileOpenCommand) {
				//				if (index != 0) {
				//					break;
				//				}
				//				currentFile = (FileOpenCommand)command;
				//				fileChanged = true;
				selectedLength = 0;
			}
			if (command instanceof Insert) {
				String insertedText = command.getDataMap().get("text");
				if (insertedText != null) {
					int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
					int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
					if (caretOffset == -1) {
						caretOffset = tempCaretOffset;
						insertLength += tempLength;
						insertedString = trimNewLine(insertedText);
					} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
						insertLength += tempLength;
						insertedString = trimNewLine(insertedText);
					} 
				}
				selectedLength = 0;
			}
			if (command instanceof Replace) {
				if (command.getDataMap().get("insertedText") != null) {
					int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
					int tempLength = command.getDataMap().get("insertedText").length();
					if (caretOffset == -1) {
						caretOffset = tempCaretOffset;
						insertLength += tempLength;
					} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
						if (trimNewLine(command.getDataMap().get("deletedText")).equals(insertedString)) {
							insertLength -= command.getDataMap().get("deletedText").length();
						}
						insertLength += tempLength;
					} 
				}
				selectedLength = 0;
			}
			if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS")) {
				if (insertLength > 0) {
					insertLength--;
				}
				selectedLength = 0;
			}
			if (command instanceof SelectTextCommand) {
				selectedLength = Integer.parseInt(command.getAttributesMap().get("end")) - Integer.parseInt(command.getAttributesMap().get("start")) + 1;
				caretOffset = Integer.parseInt(command.getAttributesMap().get("caretOffset"));
			}
			if (command instanceof Delete) {
				if (selectedLength > 0) {
					insertLength -= selectedLength;
					caretOffset -= selectedLength;
				}
			}
		}

		backed = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists()) {
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
		}
		if (insertLength > 0) {
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		}
		return commandList;
	}

	public ArrayList<EHICommand> backToException(){
		forwarded = false;

		int insertLength = 0;
		int caretOffset = -1;
		outer2:
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				insertLength = 0;
				caretOffset = -1;
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					if (command instanceof FileOpenCommand && command.equals(currentFile)) {
						int m = j-1;
						outer:
							for(int n = i; n >= 0; n--) {
								for(; m >= 0; m--) {
									if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
										currentFile = (FileOpenCommand)nestedCommands.get(n).get(m);
										fileChanged = true;
										
										filej = m;
										break outer;
									}
								}
								if (n > 0) {
									m = nestedCommands.get(n-1).size()-1;
								}
							}
						insertLength = 0;
						caretOffset = -1;
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null && !replacedString.equals(trimNewLine(insertedText))) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							} 
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								replacedString = trimNewLine(command.getDataMap().get("deletedText"));
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions--;
						j--;
//						if (j < commands.size()) {
//							i--;
//							if (i >= 0) {
//								j = nestedCommands.get(i).size()-1;
//							} else {
//								j = 0;
//							}
//
//						}
						commandList.add(command);
						break outer2;
					}
					if (command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}

		backed = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (path.contains("src") && new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists()) {
//				String projectName = currentProjectPath.substring(currentProjectPath.lastIndexOf(File.separator)+1);
//				path = path.substring(path.lastIndexOf(projectName));
//				path = path.substring(path.indexOf(File.separator)+1);
//				PROGRAMATIC_CONTROLLER.openEditor(path);
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
//				currentProjectPath = "";
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
		}
		if (insertLength > 0 & caretOffset >= 0) {
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		}
		return commandList;
	}

	public ArrayList<EHICommand> backToFix(){
		//		commandList.clear();
		forwarded = false;

		int insertLength = 0;
		int caretOffset = -1;
		outer2:
			for(; i >= 0; i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				insertLength = 0;
				caretOffset = -1;
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					if (command instanceof FileOpenCommand && command.equals(currentFile)) {
						int m = j-1;
						outer:
							for(int n = i; n >= 0; n--) {
								for(; m >= 0; m--) {
									if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
										currentFile = (FileOpenCommand)nestedCommands.get(n).get(m);
										fileChanged = true;
										
										filej = m;
										break outer;
									}
								}
								if (n > 0) {
									m = nestedCommands.get(n-1).size()-1;
								}
							}
						insertLength = 0;
						caretOffset = -1;
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null && !replacedString.equals(trimNewLine(insertedText))) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							} 
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1 || (tempCaretOffset < caretOffset && tempCaretOffset + tempLength >= caretOffset)) {
								replacedString = trimNewLine(command.getDataMap().get("deletedText"));
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions--;
						commandList.add(command);
					}
					if (command instanceof ConsoleOutput) {
						j--;
						if (j < commands.size()) {
							i--;
							if (i >= 0) {
								j = nestedCommands.get(i).size()-1;
							} else {
								j = 0;
							}

						}
						commandList.add(command);
						break outer2;
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}

		backed = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists()) {
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
		}
		if (insertLength > 0 & caretOffset >= 0) {
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		}
		return commandList;
	}

	public ArrayList<EHICommand> forward(String numStep, String step){
		commandList.clear();
		changeByTime = false;
		switch (step) {
		case ReplayView.EDIT:
			for (int a = 0; a < numStep.length(); a++) {
				if (!Character.isDigit(numStep.charAt(a))) {
					JOptionPane.showMessageDialog(null, "Please enter a positive integer", "ParseException", JOptionPane.INFORMATION_MESSAGE);
					return null;
				}
			}
			return forwardEdit(Integer.parseInt(numStep));
		case ReplayView.ONE_MINUTE:
			for (int a = 0; a < numStep.length(); a++) {
				if (!Character.isDigit(numStep.charAt(a))) {
					JOptionPane.showMessageDialog(null, "Please enter a positive integer", "ParseException", JOptionPane.INFORMATION_MESSAGE);
					return null;
				}
			}
			changeByTime = true;
			currentTimeSpent += Integer.parseInt(numStep)*ONE_MINUTE;
			return forwardByTime(Integer.parseInt(numStep)*ONE_MINUTE);
		case ReplayView.ONE_HOUR:
			for (int a = 0; a < numStep.length(); a++) {
				if (!Character.isDigit(numStep.charAt(a))) {
					JOptionPane.showMessageDialog(null, "Please enter a positive integer", "ParseException", JOptionPane.INFORMATION_MESSAGE);
					return null;
				}
			}
			changeByTime = true;
			currentTimeSpent += Integer.parseInt(numStep)*ONE_HOUR;
			return forwardByTime(Integer.parseInt(numStep)*ONE_HOUR);
		case ReplayView.EXCEPTION:
			return forwardToException();
		case ReplayView.EXCEPTION_TO_FIX:
			return forwardToFix();
		case ReplayView.FIX:
			return forwardToFix();
		case ReplayView.LOCALCHECKS:
			return forwardToLocalCheck();
		case ReplayView.RUN:
			return forwardToRun();
		case ReplayView.DEBUG:
			return forwardToDebug();
//		case ReplayView.COMPILE:
//			return forwardToCompile();
		case ReplayView.DIFFICULTY:
			return forwardToDifficulty();
		case ReplayView.DIFFICULTY_TO_NO_DIFFICULTY:
			return forwardToNoDifficulty();
//		case ReplayView.NEW_FILE:
//			return forwardToNewFile();
//		case ReplayView.DELETE_FILE:
//			return forwardToDeleteFile();
//		case ReplayView.REFACTOR:
//			return forwardToRefactor();
		case ReplayView.OPEN_FILE:
			return forwardToOpenFile();
//		case ReplayView.SAVE:
//			return forwardToSave();
		case ReplayView.PAUSE:
			for (int a = 0; a < numStep.length(); a++) {
				if (!Character.isDigit(numStep.charAt(a))) {
					JOptionPane.showMessageDialog(null, "Please enter a positive integer", "ParseException", JOptionPane.INFORMATION_MESSAGE);
					return null;
				}
			}
			return forwardToPause(Integer.parseInt(numStep));
		case ReplayView.WEB:
			return forwardToWeb();
		}
//		commandList.clear();
		return commandList;
	}
	
	public ArrayList<EHICommand> forwardToWeb(){
		backed = false;

		int insertLength = 0;
		int caretOffset = -1;

		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				insertLength = 0;
				caretOffset = -1;
				String insertedString = "";
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command instanceof FileOpenCommand) {
						commandList.add(command);
						currentFile = (FileOpenCommand)command;
						fileChanged = true;
						insertLength = 0;
						caretOffset = -1;
						insertedString = "";
						
						filej = j;
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} 
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								if (trimNewLine(command.getDataMap().get("deletedText")).equals(insertedString)) {
									insertLength -= command.getDataMap().get("deletedText").length();
								}
								insertLength += tempLength;
								commandList.add(command);
							} 
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof WebCommand) {
						commandList.add(command);
						j++;
						if (j >= commands.size()) {
							i++;
							j = 0;
						}
						break outer;
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions++;
						commandList.add(command);
					}
					if (command instanceof ConsoleOutput || command instanceof PauseCommand || command instanceof LocalCheckCommand) {
						commandList.add(command);
					}
				}
				if (i < nestedCommands.size()-1) {
					j = 0;
				}
			}

		forwarded = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (path.contains("src") && new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists()) {
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
		}
		if (insertLength > 0 & caretOffset >= 0) {
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		}
		return commandList;
	}
	
	public ArrayList<EHICommand> forwardToLocalCheck(){
		backed = false;

		int insertLength = 0;
		int caretOffset = -1;

		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				insertLength = 0;
				caretOffset = -1;
				String insertedString = "";
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command instanceof FileOpenCommand) {
						commandList.add(command);
						currentFile = (FileOpenCommand)command;
						fileChanged = true;
						insertLength = 0;
						caretOffset = -1;
						insertedString = "";
						
						filej = j;
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} 
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								if (trimNewLine(command.getDataMap().get("deletedText")).equals(insertedString)) {
									insertLength -= command.getDataMap().get("deletedText").length();
								}
								insertLength += tempLength;
								commandList.add(command);
							} 
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof LocalCheckCommand) {
						commandList.add(command);
						j++;
						if (j >= commands.size()) {
							i++;
							j = 0;
						}
						break outer;
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions++;
						commandList.add(command);
					}
					if (command instanceof ConsoleOutput || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
				}
				if (i < nestedCommands.size()-1) {
					j = 0;
				}
			}

		forwarded = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists()) {
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
		}
		if (insertLength > 0 & caretOffset >= 0) {
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		}
		return commandList;
	}
	
	public ArrayList<EHICommand> forwardToPause(int time){
		backed = false;

		int insertLength = 0;
		int caretOffset = -1;

		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				insertLength = 0;
				caretOffset = -1;
				String insertedString = "";
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					maybeAddToCommandList(command);
					if (command instanceof FileOpenCommand) {
						currentFile = (FileOpenCommand)command;
						fileChanged = true;
						insertLength = 0;
						caretOffset = -1;
						insertedString = "";
						commandList.add(command);
						filej = j;
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} 
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								if (trimNewLine(command.getDataMap().get("deletedText")).equals(insertedString)) {
									insertLength -= command.getDataMap().get("deletedText").length();
								}
								insertLength += tempLength;
								commandList.add(command);
							} 
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof PauseCommand) {
//						j++;
//						commandList.add(command);
//						if (j >= commands.size()) {
//							i++;
//							j = 0;
//						}
						int pause = Integer.parseInt(command.getDataMap().get("pause"));
						if (pause/1000 >= time) {
							j++;
							break outer;
						}
//						break outer;
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions++;
//						commandList.add(command);
					}
//					if (command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof WebCommand) {
//						commandList.add(command);
//					}
				}
				if (i < nestedCommands.size()-1) {
					j = 0;
				}
			}

		forwarded = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists()) {
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
		}
		if (insertLength > 0 & caretOffset >= 0) {
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		}
		return commandList;
	}

	public ArrayList<EHICommand> forwardToRun(){
		backed = false;
		boolean hitRun = false;
		int insertLength = 0;
		int caretOffset = -1;
		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				insertLength = 0;
				caretOffset = -1;
				String insertedString = "";
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command instanceof RunCommand && command.getAttributesMap().get("type").equals("Run")) {
						commandList.add(command);
						hitRun = true;
					}
					if (hitRun && !(command instanceof RunCommand)) {
						break outer;
					}
					if (command instanceof FileOpenCommand) {
						currentFile = (FileOpenCommand)command;
						fileChanged = true;
						insertedString = "";
						insertLength = 0;
						caretOffset = -1;
						commandList.add(command);
						filej = j;
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} 
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								if (trimNewLine(command.getDataMap().get("deletedText")).equals(insertedString)) {
									insertLength -= command.getDataMap().get("deletedText").length();
								}
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions++;
						commandList.add(command);
					}
					if (command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
				}
				j = 0;
			}
		forwarded = true;
		if (hitRun) {
			if (fileChanged) {
				String path = currentFile.getDataMap().get("filePath");
				if (!(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
					return forwardToRun();
				}
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
			return commandList;
		}
		return commandList;
	}

	public ArrayList<EHICommand> forwardToDebug(){
		// 		commandList.clear();
		backed = false;
		boolean hitDebug = false;
		int insertLength = 0;
		int caretOffset = -1;
		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				insertLength = 0;
				caretOffset = -1;
				String insertedString = "";
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command instanceof RunCommand && command.getAttributesMap().get("type").equals("Debug")) {
						commandList.add(command);
						hitDebug = true;
					}
					if (hitDebug && !(command instanceof RunCommand)) {
						break outer;
					}
					if (command instanceof FileOpenCommand) {
						currentFile = (FileOpenCommand)command;
						fileChanged = true;
						insertedString = "";
						insertLength = 0;
						caretOffset = -1;
						
						filej = j;
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} 
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								if (trimNewLine(command.getDataMap().get("deletedText")).equals(insertedString)) {
									insertLength -= command.getDataMap().get("deletedText").length();
								}
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions++;
						commandList.add(command);
					}
					if (command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
				}
				j = 0;
			}
		forwarded = true;
		if (hitDebug) {
			if (fileChanged) {
				String path = currentFile.getDataMap().get("filePath");
				if (!(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
					return forwardToDebug();
				}
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
			return commandList;
		}
		return commandList;
	}

	public ArrayList<EHICommand> forwardToCompile(){
		backed = false;
		boolean hitRun = false;
		int insertLength = 0;
		int caretOffset = -1;
		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				insertLength = 0;
				caretOffset = -1;
				String insertedString = "";
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command instanceof RunCommand) {
						commandList.add(command);
						hitRun = true;
					}
					if (hitRun && !(command instanceof RunCommand)) {
						break outer;
					}
					if (command instanceof FileOpenCommand) {
						currentFile = (FileOpenCommand)command;
						fileChanged = true;
						insertedString = "";
						insertLength = 0;
						caretOffset = -1;
						
						filej = j;
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} 
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								if (trimNewLine(command.getDataMap().get("deletedText")).equals(insertedString)) {
									insertLength -= command.getDataMap().get("deletedText").length();
								}
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions++;
						commandList.add(command);
					}
					if (command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
				}
				j = 0;
			}
		forwarded = true;
		if (hitRun) {
			if (fileChanged) {
				String path = currentFile.getDataMap().get("filePath");
				if (!(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
					j++;
					return forwardToDebug();
				}
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
			return commandList;
		}
		return commandList; 	
	}

	public ArrayList<EHICommand> forwardToDifficulty(){
		previousTime = currentTime;
		boolean difficulty = false;
		outer:
			for(int k = 0; k < metrics.size(); k++) {
				List<List<String>> metric = metrics.get(k);
				for(int l = 0; l < metrics.get(k).size(); l++) {
					if (metric.get(l).get(1).equals("YES") && Long.parseLong(metric.get(l).get(4)) > currentTime) {
						currentTime = Long.parseLong(metric.get(l).get(4));
						difficulty = true;
						break outer;
					}
				}
			}
		if (!difficulty) {
			currentTime = System.currentTimeMillis();
		}
		backed = false;
		int insertLength = 0;
		int caretOffset = -1;
		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				insertLength = 0;
				caretOffset = -1;
				String insertedString = "";
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command == null) {
						continue;
					}
					if ((command.getStartTimestamp() == 0 && command.getTimestamp2() > currentTime) || (command.getStartTimestamp() != 0 && command.getStartTimestamp()+command.getTimestamp() > currentTime)) {
						if (j==0) {
							i--;
							j = nestedCommands.get(i).size()-1;
						} else {
							j--;
						}
						break outer;
					} 
					if (command instanceof FileOpenCommand) {
						currentFile = (FileOpenCommand)command;
						fileChanged = true;
						insertedString = "";
						insertLength = 0;
						caretOffset = -1;
						
						filej = j;
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} 
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								if (trimNewLine(command.getDataMap().get("deletedText")).equals(insertedString)) {
									insertLength -= command.getDataMap().get("deletedText").length();
								}
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions++;
						commandList.add(command);
					}
					if (command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
				}
				j = 0;
			}

		forwarded = true;
		//		if (hitRun) {
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (!(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
				return commandList;
			}
			PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
			editor = EHUtilities.getActiveEditor();
			fileChanged = false;
		}
		PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		return commandList;
		//		}
		//		return null;
	}

	public ArrayList<EHICommand> forwardToNoDifficulty(){
		boolean difficulty = true;
		previousTime = currentTime;
		outer:
			for(int k = 0; k < metrics.size(); k++) {
				List<List<String>> metric = metrics.get(k);
				for(int l = 0; l < metrics.get(k).size(); l++) {
					if (metric.get(l).get(1).equals("NO") && Long.parseLong(metric.get(l).get(4)) > currentTime) {
						currentTime = Long.parseLong(metric.get(l).get(4));
						difficulty = false;
						break outer;
					}
				}
			}
		if (difficulty) {
			currentTime = System.currentTimeMillis();
		}

		backed = false;
		int insertLength = 0;
		int caretOffset = -1;
		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				insertLength = 0;
				caretOffset = -1;
				String insertedString = "";
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command == null) {
						continue;
					}
					if ((command.getStartTimestamp() == 0 && command.getTimestamp2() > currentTime) || (command.getStartTimestamp() != 0 && command.getStartTimestamp()+command.getTimestamp() > currentTime)) {
						if (j==0) {
							i--;
							j = nestedCommands.get(i).size()-1;
						} else {
							j--;
						}
						break outer;
					} 
					if (command instanceof FileOpenCommand) {
						currentFile = (FileOpenCommand)command;
						fileChanged = true;
						insertedString = "";
						insertLength = 0;
						caretOffset = -1;
						
						filej = j;
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} 
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								if (trimNewLine(command.getDataMap().get("deletedText")).equals(insertedString)) {
									insertLength -= command.getDataMap().get("deletedText").length();
								}
								insertLength += tempLength;
								commandList.add(command);
							}  
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions++;
						commandList.add(command);
					}
					if (command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
				}
				j = 0;
			}
		forwarded = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (!(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
				return commandList;
			}
			PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
			editor = EHUtilities.getActiveEditor();
			fileChanged = false;
		}
		PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		return commandList;
	}

	public ArrayList<EHICommand> forwardToNewFile(){
		String newFileName = null;
		for (int k = 0; k < newFiles.size(); k++) {
			long newFileTime = Long.parseLong(newFiles.get(k).get(0));
			if (newFileTime > currentTime) {
				currentTime = newFileTime;
				newFileName = newFiles.get(k).get(1);
				break;
			}
		}
		
		backed = false;
		
		if (newFileName == null) {
			currentTime = System.currentTimeMillis();
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command == null) {
						continue;
					}
					if (command instanceof Insert || command instanceof Replace || command instanceof Delete || 
							command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || 
							command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						commandList.add(command);
						currentExceptions++;
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
			currentExceptions = totalExceptions;
		} else {
			boolean moved = false;
		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command == null) {
						continue;
					}
					if ((command.getStartTimestamp() == 0 && command.getTimestamp2() > currentTime) || (command.getStartTimestamp() != 0 && command.getStartTimestamp()+command.getTimestamp() > currentTime)) {
						if (moved) {
							if (j==0) {
								i--;
								j = nestedCommands.get(i).size()-1;
							} else {
								j--;
							}
						} else {
							moved = true;
						}
						break outer;
					} 
					if (command instanceof Insert || command instanceof Replace || command instanceof Delete || 
							command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || 
							command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						commandList.add(command);
						currentExceptions++;
					}
				}

				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		}
		
		forwarded = true;
		if (newFileName != null) {
			if (!(new File(newFileName).exists())) {
				return commandList;
			}
			PROGRAMATIC_CONTROLLER.openEditor(newFileName.replace(getCurrentProjectPath(), ""));
			editor = EHUtilities.getActiveEditor();
			fileChanged = false;
		}
		return commandList;
	}

	public ArrayList<EHICommand> forwardToDeleteFile(){
		String deleteFileName = null;
		for (int k = 0; k < deletedFiles.size(); k++) {
			long deleteFileTime = Long.parseLong(deletedFiles.get(k).get(0));
			if (deleteFileTime > currentTime) {
				currentTime = deleteFileTime;
				deleteFileName = deletedFiles.get(k).get(1);
				break;
			}
		}
		
		backed = false;

		if (deleteFileName == null) {
			currentTime = System.currentTimeMillis();
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command == null) {
						continue;
					}
					if (command instanceof Insert || command instanceof Replace || command instanceof Delete || 
							command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || 
							command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						commandList.add(command);
						currentExceptions++;
					}
				}

				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
			currentExceptions = totalExceptions;
		} else {
			boolean moved = false;
		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command == null) {
						continue;
					}
					if ((command.getStartTimestamp() == 0 && command.getTimestamp2() > currentTime) || (command.getStartTimestamp() != 0 && command.getStartTimestamp()+command.getTimestamp() > currentTime)) {
						if (moved) {
							if (j==0) {
								i--;
								j = nestedCommands.get(i).size()-1;
							} else {
								j--;
							}
						} else {
							moved = true;
						}
						break outer;
					} 
					if (command instanceof Insert || command instanceof Replace || command instanceof Delete || 
							command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || 
							command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
				}

				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		}
		
		forwarded = true;
		if (deleteFileName != null) {
			if (!(new File(deleteFileName).exists())) {
				return commandList;
			}
			PROGRAMATIC_CONTROLLER.openEditor(deleteFileName.replace(getCurrentProjectPath(), ""));
			editor = EHUtilities.getActiveEditor();
			fileChanged = false;
		}
		return commandList;
	}
	
	public ArrayList<EHICommand> forwardToRefactor(){
		String refactoredName = null;
		for (int k = 0; k < refactorRecords.size(); k++) {
			long refactorTime = Long.parseLong(refactorRecords.get(k).get(0));
			if (refactorTime > currentTime) {
				currentTime = refactorTime;
				refactoredName = refactorRecords.get(k).get(2);
				break;
			}
		}
		
		if (refactoredName == null) {
			currentTime = System.currentTimeMillis();
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command == null) {
						continue;
					}
					if (command instanceof Insert || command instanceof Replace || command instanceof Delete || 
							command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || 
							command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						commandList.add(command);
						currentExceptions++;
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
			currentExceptions = totalExceptions;
		} else {
			boolean moved = false;
		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command == null) {
						continue;
					}
					if ((command.getStartTimestamp() == 0 && command.getTimestamp2() > currentTime) || (command.getStartTimestamp() != 0 && command.getStartTimestamp()+command.getTimestamp() > currentTime)) {
						if (moved) {
							if (j == 0) {
								i--;
								j = nestedCommands.get(i).size()-1;
							} else {
								j--;
							}
						} else {
							moved = true;
						}
						break outer;
					} 
					if (command instanceof Insert || command instanceof Replace || command instanceof Delete || 
							command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || 
							command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
				}

				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		}
		backed = true;
		if (refactoredName != null) {
			if (!(new File(refactoredName).exists())) {
				return commandList;
			}
			PROGRAMATIC_CONTROLLER.openEditor(refactoredName.replace(getCurrentProjectPath(), ""));
			editor = EHUtilities.getActiveEditor();
			fileChanged = false;
		}
		return commandList;
	}
	
	public ArrayList<EHICommand> forwardToOpenFile(){
		backed = false;

		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command instanceof FileOpenCommand) {
						currentFile = (FileOpenCommand)command;
						fileChanged = true;
						
						filej = j;
						j++;
						commandList.add(command);
						break outer;
					}
					if (command instanceof Insert || command instanceof Replace || command instanceof Delete || 
							command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || 
							command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						commandList.add(command);
						currentExceptions++;
					}
				}
				if (i < nestedCommands.size()-1) {
					j = 0;
				}
			}

		forwarded = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists()) {
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
		}
		return commandList;
	}

	public ArrayList<EHICommand> forwardToSave(){
		backed = false;

		int insertLength = 0;
		int caretOffset = -1;

		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				insertLength = 0;
				caretOffset = -1;
				String insertedString = "";
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command instanceof FileOpenCommand) {
						currentFile = (FileOpenCommand)command;
						fileChanged = true;
						insertLength = 0;
						caretOffset = -1;
						insertedString = "";
						commandList.add(command);
						filej = j;
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} 
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								if (trimNewLine(command.getDataMap().get("deletedText")).equals(insertedString)) {
									insertLength -= command.getDataMap().get("deletedText").length();
								}
								insertLength += tempLength;
								commandList.add(command);
							} 
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("org.eclipse.ui.file.save")) {
						j++;
						if (j >= commands.size()) {
							i++;
							j = 0;
						}
						break outer;
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions++;
						commandList.add(command);
					}
					if (command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
				}
				if (i < nestedCommands.size()-1) {
					j = 0;
				}
			}

		forwarded = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists()) {
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
		}
		if (insertLength > 0 & caretOffset >= 0) {
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		}
		return commandList;
	}

	public ArrayList<EHICommand> forwardEdit(int limit) {
//		if (backed) {
//			backed = false;
//			forwardEdit(limit);
//		}
		int num = 0;
		for(; i < nestedCommands.size(); i++) {
			if (i >= nestedCommands.size()) {
				return commandList;
			}
			List<EHICommand> commands = nestedCommands.get(i);
			if (j >= commands.size()) {
				i++;
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				j = 0;
			}
			int insertLength = 0;
			int caretOffset = -1;
			String insertedString = "";
			for(; j < commands.size(); j++) {
				EHICommand command = commands.get(j);	
				if (command instanceof FileOpenCommand) {
					currentFile = (FileOpenCommand)command;
					fileChanged = true;
					insertedString = "";
					insertLength = 0;
					caretOffset = -1;
					commandList.add(command);
					filej = j;
				}
				if (command instanceof Insert) {
					String insertedText = command.getDataMap().get("text");
					if (insertedText != null) {
						int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
						int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
						if (caretOffset == -1) {
							caretOffset = tempCaretOffset;
							insertLength += tempLength;
							insertedString = trimNewLine(insertedText);
							commandList.add(command);
						} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
							insertLength += tempLength;
							insertedString = trimNewLine(insertedText);
							commandList.add(command);
						} else
							break;
					}
					num++;
				}
				if (command instanceof Replace) {
					if (command.getDataMap().get("insertedText") != null) {
						int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
						int tempLength = command.getDataMap().get("insertedText").length();
						if (caretOffset == -1) {
							caretOffset = tempCaretOffset;
							insertLength += tempLength;
							commandList.add(command);
						} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
							if (trimNewLine(command.getDataMap().get("deletedText")).equals(insertedString)) {
								insertLength -= command.getDataMap().get("deletedText").length();
							}
							insertLength += tempLength;
							commandList.add(command);
						} else 
							break;
					}
					num++;
				}
				if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
//					if (insertLength > 0) 
						commandList.add(command);
						insertLength--;
//					else {
//						j++;
//						return forwardEdit(limit);
//					}
						num++;
				}
				if (command instanceof ExceptionCommand) {
					currentExceptions++;
					commandList.add(command);
				}
				if (command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
					commandList.add(command);
				}
				if (num >= limit) {
					j++;
					break;
				}
			}
			if (num >= limit) {
				forwarded = true;
				if (fileChanged) {
					String path = currentFile.getDataMap().get("filePath");
					if (!(new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists())) {
						j++;
//						if (limit-insertLength <= 0) {
							return commandList;
//						}
//						return forwardEdit(limit-insertLength);
					}
					PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
					editor = EHUtilities.getActiveEditor();
					fileChanged = false;
				}
				PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
				return commandList;
			}
			j = 0;
		}
		return commandList;
	}

	public ArrayList<EHICommand> forwardByTime(long forwardTime) {
		backed = false;

		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				long startTime = nestedCommands.get(i).get(j).getTimestamp();
				outer2:
					for(; j < commands.size(); j++) {
						EHICommand command = commands.get(j);
						while (command == null) {
							j++;
							if (j >= commands.size()) {
								break outer2;
							} else {
								command = commands.get(j);
							}
						}
						if (command.getTimestamp() - startTime > forwardTime) {
							commandList.add(command);
							break outer;
						} 
						if(command instanceof FileOpenCommand) {
							currentFile = (FileOpenCommand)command;
							fileChanged = true;
							commandList.add(command);
							commandList.add(command);
							filej = j;
						} 
						if(command instanceof Insert || command instanceof Replace || command instanceof ExceptionCommand ||
								(command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS")) || 
								command instanceof Delete || command instanceof ConsoleOutput || command instanceof LocalCheckCommand || 
								command instanceof PauseCommand || command instanceof WebCommand) {
							commandList.add(command);
						}
						if (command instanceof ExceptionCommand) {
							currentExceptions++;
							commandList.add(command);
						}
						
					}
				if (i < nestedCommands.size()-1) {
					j = 0;
					forwardTime -= commands.get(commands.size()-1).getTimestamp() - startTime;
				}
			}
		if (commandList.size() == 0) {
			return commandList;
		}	

		int selectedLength = 0;
		int insertLength = 0;
		int caretOffset = -1;
		for(int index = commandList.size()-1; index >= 0; index--) {
			String insertedString = "";
			EHICommand command = commandList.get(index);	
			if (command instanceof FileOpenCommand) {
				currentFile = (FileOpenCommand)command;
				fileChanged = true;
				selectedLength = 0;
				insertLength = 0;
				caretOffset = -1;
				
				filej = j;
			}
			if (command instanceof Insert) {
				String insertedText = command.getDataMap().get("text");
				if (insertedText != null) {
					int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
					int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
					if (caretOffset == -1) {
						caretOffset = tempCaretOffset;
						insertLength += tempLength;
						insertedString = trimNewLine(insertedText);
					} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
						insertLength += tempLength;
						insertedString = trimNewLine(insertedText);
					} 
				}
				selectedLength = 0;
			}
			if (command instanceof Replace) {
				if (command.getDataMap().get("insertedText") != null) {
					int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
					int tempLength = command.getDataMap().get("insertedText").length();
					if (caretOffset == -1) {
						caretOffset = tempCaretOffset;
						insertLength += tempLength;
					} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
						if (trimNewLine(command.getDataMap().get("deletedText")).equals(insertedString)) {
							insertLength -= command.getDataMap().get("deletedText").length();
						}
						insertLength += tempLength;
					} 
				}
				selectedLength = 0;
			}
			if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS")) {
				if (insertLength > 0) {
					insertLength--;
				}
				selectedLength = 0;
			}
			if (command instanceof SelectTextCommand) {
				selectedLength = Integer.parseInt(command.getAttributesMap().get("end")) - Integer.parseInt(command.getAttributesMap().get("start")) + 1;
				caretOffset = Integer.parseInt(command.getAttributesMap().get("caretOffset"));
			}
			if (command instanceof Delete) {
				if (selectedLength > 0) {
					insertLength -= selectedLength;
					caretOffset -= selectedLength;
				}
			}
		}

		forwarded = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists()) {
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
		}
		if (insertLength > 0 & caretOffset >= 0) {
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		}
		return commandList;
	}

	public ArrayList<EHICommand> forwardToException(){
		backed = false;

		int insertLength = 0;
		int caretOffset = -1;

		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				insertLength = 0;
				caretOffset = -1;
				String insertedString = "";
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command instanceof FileOpenCommand) {
						currentFile = (FileOpenCommand)command;
						fileChanged = true;
						insertLength = 0;
						caretOffset = -1;
						insertedString = "";
						commandList.add(command);
						filej = j;
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} 
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								if (trimNewLine(command.getDataMap().get("deletedText")).equals(insertedString)) {
									insertLength -= command.getDataMap().get("deletedText").length();
								}
								insertLength += tempLength;
								commandList.add(command);
							} 
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions++;
						j++;
						if (j >= commands.size()) {
							i++;
							j = 0;
						}
						commandList.add(command);
						break outer;
					}
					if (command instanceof ConsoleOutput || command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
				}
				if (i < nestedCommands.size()-1) {
					j = 0;
				}
			}

		forwarded = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists()) {
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
		}
		if (insertLength > 0 & caretOffset >= 0) {
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		}
		return commandList;
	}

	public ArrayList<EHICommand> forwardToFix(){
		commandList.clear();
		backed = false;

		int insertLength = 0;
		int caretOffset = -1;

		outer:
			for(; i < nestedCommands.size(); i++) {
				if (i >= nestedCommands.size()) {
					return commandList;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					i++;
					if (i >= nestedCommands.size()) {
						return commandList;
					}
					j = 0;
				}
				insertLength = 0;
				caretOffset = -1;
				String insertedString = "";
				for(; j < commands.size(); j++) {
					EHICommand command = commands.get(j);
					if (command instanceof FileOpenCommand) {
						currentFile = (FileOpenCommand)command;
						fileChanged = true;
						filej = j;
						insertLength = 0;
						caretOffset = -1;
						insertedString = "";
					}
					if (command instanceof Insert) {
						String insertedText = command.getDataMap().get("text");
						if (insertedText != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = Integer.parseInt(command.getAttributesMap().get("length"));
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								insertLength += tempLength;
								insertedString = trimNewLine(insertedText);
								commandList.add(command);
							} 
						}
					}
					if (command instanceof Replace) {
						if (command.getDataMap().get("insertedText") != null) {
							int tempCaretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
							int tempLength = command.getDataMap().get("insertedText").length();
							if (caretOffset == -1) {
								caretOffset = tempCaretOffset;
								insertLength += tempLength;
								commandList.add(command);
							} else if (caretOffset <= tempCaretOffset && caretOffset + insertLength >= tempCaretOffset) {
								if (trimNewLine(command.getDataMap().get("deletedText")).equals(insertedString)) {
									insertLength -= command.getDataMap().get("deletedText").length();
								}
								insertLength += tempLength;
								commandList.add(command);
							} 
						}
					}
					if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS") || command instanceof Delete) {
						if (insertLength > 0) 
							commandList.add(command);
					}
					if (command instanceof ExceptionCommand) {
						currentExceptions++;
						commandList.add(command);
					}
					if (command instanceof LocalCheckCommand || command instanceof PauseCommand || command instanceof WebCommand) {
						commandList.add(command);
					}
					if (command instanceof ConsoleOutput) {
						commandList.add(command);
						j++;
						if (j >= commands.size()) {
							i++;
							j = 0;
						}
						break outer;
					}
//					if (command instanceof RunCommand) {
//						commandList.add(command);
//						j++;
//						if (j >= commands.size()) {
//							i++;
//							j = 0;
//						}
//						break outer;
//					}
				}
				if (i < nestedCommands.size()-1) {
					j = 0;
				}
			}

		forwarded = true;
		if (fileChanged) {
			String path = currentFile.getDataMap().get("filePath");
			if (new File(getCurrentProjectPath() + path.substring(path.lastIndexOf(File.separator + "src"))).exists()) {
				PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
				editor = EHUtilities.getActiveEditor();
				fileChanged = false;
			}
		}
		if (insertLength > 0 & caretOffset >= 0) {
			PROGRAMATIC_CONTROLLER.selectTextInCurrentEditor(caretOffset, insertLength+1);
		}
		return commandList;
	}

	private String trimNewLine(String s) {
		String retVal = "";
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != '\n' && s.charAt(i) != '\r' && s.charAt(i) != '\t') {
				retVal += s.charAt(i);
			}
		}
		return retVal;
	}

	private String getCurrentProjectPath(){
		IProject currentProject = EHUtilities.getCurrentProject();
		if (currentProject == null) {
			return "";
		}
		return currentProject.getLocation().toOSString();
//		return projectPath.replace('/', '\\');
	}

	public String[] getReplayedFile() {
		String[] retval = {"null", "null"};
		String filePath = currentFile.getDataMap().get("filePath");
		if (filePath == null) {
			return retval;
		}
		retval[0] = filePath.substring(filePath.lastIndexOf(File.separator)+1);
		int caretOffset = 0;
		int selectedLength = 1;
		replayedFile = currentFile.getDataMap().get("snapshot");
		if (replayedFile == null) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(new File(currentProjectPath + File.separator + filePath.substring(filePath.indexOf("src")))));
				StringBuffer sb = new StringBuffer();
				String nextLine = null;
				while ((nextLine = br.readLine()) != null) {
					sb.append(nextLine+"\r\n");
				}
				replayedFile = sb.toString();
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		retval[1] = replayedFile;
		List<EHICommand> commands = nestedCommands.get(i);
		for (int j2 = filej; j2 <= j; j2++) {
			if (j2 >= commands.size()) {
				break;
			}
			EHICommand command = commands.get(j2);
			if (command instanceof Insert) {
				String insertedText = command.getDataMap().get("text");
				if (insertedText != null && !replacedString.equals(trimNewLine(insertedText))) {
					caretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
					replayedFile = replayedFile.substring(0, caretOffset) + insertedText + replayedFile.substring(caretOffset);
					caretOffset += insertedText.length();
				}
				selectedLength = 1;
			}
			if (command instanceof Replace) {
				if (command.getDataMap().get("insertedText") != null) {
					caretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
					replayedFile = replayedFile.substring(0, caretOffset) + command.getDataMap().get("insertedText") + replayedFile.substring(caretOffset+command.getDataMap().get("deletedText").length());
					caretOffset += command.getDataMap().get("insertedText").length();
				}
				selectedLength = 1;
			}
			if (command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("eventLogger.styledTextCommand.DELETE_PREVIOUS")) {
				if (replayedFile.length() < caretOffset-selectedLength) {
//					replayedFile = replayedFile.substring(0, caretOffset);
				} else {
					replayedFile = replayedFile.substring(0, caretOffset) + replayedFile.substring(caretOffset+selectedLength);
				}
				selectedLength = 1;
			}
			if (command instanceof Delete) {
				caretOffset = Integer.parseInt(command.getAttributesMap().get("offset"));
				int length = Integer.parseInt(command.getAttributesMap().get("length"));
				replayedFile = replayedFile.substring(0, caretOffset) + replayedFile.substring(caretOffset+length);
				selectedLength = 1;
			}
			if (command instanceof MoveCaretCommand) {
				caretOffset = Integer.parseInt(command.getAttributesMap().get("caretOffset"));
				selectedLength = 1;
			}
			if (command instanceof SelectTextCommand) {
				selectedLength = Integer.parseInt(command.getAttributesMap().get("end")) - Integer.parseInt(command.getAttributesMap().get("start")) + 1;
				caretOffset = Integer.parseInt(command.getAttributesMap().get("start"));
			}
		}
		retval[1] = replayedFile;
		return retval;
	}

//	private void calculateTotalTimeSpent(){
//		long projectTime = 0;
//		for(int k = 0; k < nestedCommands.size(); k++) {
//			List<EHICommand> commands = nestedCommands.get(k);
//			long timestamp1 = commands.get(1).getTimestamp();
//			EHICommand command2 = commands.get(commands.size()-1);
//			int offset = 1;
//			while (command2 == null) {
//				offset++;
//				command2 = commands.get(commands.size() - offset);
//			}
//			long timestamp2 = command2.getTimestamp();
//			projectTime += timestamp2 - timestamp1;
//		}
//		totalTimeSpent = projectTime;
//	}
	protected void calculateTotalTimeSpent(){
		long projectTime = 0;
		for(int k = 0; k < nestedCommands.size(); k++) {
			List<EHICommand> commands = nestedCommands.get(k);
			if (commands.size() == 0) {
				continue;
			}
			int j = 0;
			for(; j < commands.size(); j++) {
//				if (!(commands.get(j) instanceof LocalCheckCommand) && (commands.get(j).getStartTimestamp() > 0 || commands.get(j).getTimestamp() > 0)) {
//					break;
//				}
				if (commands.get(j).getStartTimestamp() > 0 || commands.get(j).getTimestamp() > 0) {
					break;
				}
			}
			long timestamp1 = commands.get(j).getTimestamp() + commands.get(j).getStartTimestamp();
			EHICommand command2 = commands.get(commands.size()-1);
			long timestamp2 = command2.getStartTimestamp() + command2.getTimestamp();
			projectTime += timestamp2 - timestamp1;
		}
		totalTimeSpent = projectTime - restTime(10*60*1000);
	}

	protected long restTime(long time, int i2, int j2) {
		long restTime = 0;
		for (int i = 0; i <= i2; i++) {
			List<EHICommand> commands = nestedCommands.get(i);
			for (int j = 0; j < (i==i2?j2:commands.size()-1); j++) {
				EHICommand command = commands.get(j);
				if (command instanceof PauseCommand) {
					long pause = Long.parseLong(command.getDataMap().get("pause"));
					if (pause > time) {
						restTime += pause;
					}
				}
			}
		}
		return restTime;
	}
	
	protected long restTime(long time) {
		long restTime = 0;
		for (List<EHICommand> commands : nestedCommands) {
			for (EHICommand command : commands) {
				if (command instanceof PauseCommand) {
					long pause = Long.parseLong(command.getDataMap().get("pause"));
					if (pause > time) {
						restTime += pause;
					}
				}
			}
		}
		return restTime;
	}
	
	public String getTotalTimeSpent(){
		calculateTotalTimeSpent();
		return convertToHourMinuteSecond(totalTimeSpent);
	}
	
	public String getWorkTime(String path) {
		long workTime = 0;
		if (nestedCommands == null || !path.equals(currentProjectPath)) {
			currentProjectPath = path;
			if (currentProjectPath.isEmpty()) {
				return "Cannot find current project, open a file in the project and try again";
			}
			replayLogs(currentProjectPath, analyzer);
		}
		for(int i = 0; i < nestedCommands.size(); i++) {
			List<EHICommand> commands = nestedCommands.get(i);
			if (commands.size() == 0) {
				continue;
			}
			int j = 0;
			for(; j < commands.size(); j++) {
				if (commands.get(j).getStartTimestamp() > 0 || commands.get(j).getTimestamp() > 0) {
					break;
				}
			}
			long timestamp1 = commands.get(j).getTimestamp() + commands.get(j).getStartTimestamp();
			EHICommand command2 = commands.get(commands.size()-1);
			long timestamp2 = command2.getStartTimestamp() + command2.getTimestamp();
			workTime += timestamp2 - timestamp1;
		}
		List<EHICommand> commands = EHEventRecorder.getInstance().getCommands();
		int end = commands.size();
		if (end > 0) {
			int j = 0;
			int k = -1;
			for(; j < end; j++) {
				EHICommand command = commands.get(j);
				if (k == -1 && command.getStartTimestamp() + command.getTimestamp() > 0) {
					k = j;
				}
				IProject project = command.getProject();
				if (project != null && path.equals(project.getLocation().toOSString()) && (command.getStartTimestamp() > 0 || command.getTimestamp() > 0)) {
					break;
				}
			}
			long startTime = 0;
			if (j >= end) {
				startTime = commands.get(k).getTimestamp() + commands.get(k).getStartTimestamp();	
			}
			startTime = commands.get(j).getTimestamp() + commands.get(j).getStartTimestamp();
			EHICommand command2 = commands.get(end-1);
			long endTime = command2.getStartTimestamp() + command2.getTimestamp();
			workTime += endTime - startTime;
		}
		return convertToHourMinuteSecond(workTime-restTime(commands, end, PAUSE, path));
	}
	
	public long restTime(List<EHICommand> commands, int end, long time, String path) {
		long restTime = 0;
		for (List<EHICommand> commandList : nestedCommands) {
			for (EHICommand command : commandList) {
				if (command instanceof PauseCommand) {
					long pause = Long.parseLong(command.getDataMap().get("pause"));
					if (pause > time) {
						restTime += pause;
					}
				}
			}
		}
		long last = 0;
		for (int i = 0; i < end; i++) {
			EHICommand command = commands.get(i);
			IProject project = command.getProject();
			if (project == null || !path.equals(project.getLocation().toOSString())) {
				continue;
			}
			if (last == 0) {
				last = command.getStartTimestamp() + command.getTimestamp();
			} else {
				long current = command.getStartTimestamp() + command.getTimestamp();
				long pause = current - last;
				if (pause > time) {
					restTime += pause; 
				}
				last = current;
			}
//			if (command instanceof PauseCommand) {
//				long pause = Long.parseLong(command.getDataMap().get("pause"));
//				if (pause > time) {
//					restTime += pause;
//				}
//			}
		}
		return restTime;
	}
	
	public String getWallTime(String path) {
		long wallTime = 0;
//		EHICommand c1 = null;
//		EHICommand c2 = null;
		if (nestedCommands == null || !path.equals(currentProjectPath)) {
			currentProjectPath = path;
			if (currentProjectPath.isEmpty()) {
				return "Cannot find current project, open a file in the project and try again";
			}
			replayLogs(currentProjectPath, analyzer);
		}
		List<EHICommand> commands = EHEventRecorder.getInstance().getCommands();
		int end = commands.size();
		long startTime = 0;
		long endTime = 0;

		//start Time
		if (nestedCommands.size() > 0) {
			for (int i = 0; i < nestedCommands.get(0).size(); i++) {
				EHICommand command = nestedCommands.get(0).get(i);
				startTime = command.getStartTimestamp() + command.getTimestamp();
				if (startTime > 0) {
					break;
				}
			}
//			if (end > 0) {
//				c2 = commands.get(end-1);
//			} else {
//				commands = nestedCommands.get(nestedCommands.size()-1);
//				c2 = commands.get(commands.size()-1);
//			}
		} else if (end > 0){
			for (int i = 0; i < end; i++) {
				EHICommand command = commands.get(i);
				IProject project = command.getProject();
				if (project != null && path.equals(project.getLocation().toOSString())) {
					startTime = command.getStartTimestamp() + command.getTimestamp();
					if (startTime > 0) {
						break;
					}
				}
			}
//			c2 = commands.get(end-1);
		} 
		if (startTime == 0) {
			return "0";
		}
		//end time
		if (end > 0) {
			for (int i = end - 1; i >= 0; i--) {
				EHICommand command = commands.get(i);
				IProject project = command.getProject();
				if (project != null && project.getLocation().toOSString().equals(currentProjectPath)) {
					endTime = command.getStartTimestamp() + command.getTimestamp();
					if (endTime > 0) {
						break;
					}
				}
			}
		} else if (nestedCommands.size() > 0) {
			for (int i = nestedCommands.size() - 1; i >= 0; i--) {
				List<EHICommand> commands2 = nestedCommands.get(i);
				if (commands2.size() > 0) {
					for (int j = commands2.size() - 1; j >= 0; j--) {
						EHICommand command = commands2.get(j);
						endTime = command.getStartTimestamp() + command.getTimestamp();
						if (endTime > 0) {
							break;
						}
					}
				}
			}
		}
		if (endTime == 0) {
			return "0";
		}
		
		wallTime = endTime - startTime;
		return convertToHourMinuteSecond(wallTime);
	}

	public void calculateCurrentTimeSpent() {
//		if (changeByTime && currentTimeSpent <= 0) {
//			currentTimeSpent = 0;
//			return;
//		}
//		long projectTime = 0;
//		if (i >= nestedCommands.size()) {
//			i = nestedCommands.size() - 1;
//			j = nestedCommands.get(i).size() - 1;
//		} else if (i < 0) {
//			i = 0;
//			j = 0;
//		}
//		if (j >= nestedCommands.get(i).size()) {
//			j = nestedCommands.get(i).size() - 1;
//		} else if (j < 0) {
//			j = 0;
//		}
//		System.out.println("\n\ni = " + i + "\nj = " + j + "\n\n");
//		for(int k = 0; k < i; k++) {
//			List<EHICommand> commands = nestedCommands.get(k);
//			long timestamp1 = commands.get(1).getTimestamp();
//			EHICommand command2 = commands.get(commands.size()-1);
//			int offset = 1;
//			while (command2 == null) {
//				offset++;
//				command2 = commands.get(commands.size() - offset);
//			}
//			long timestamp2 = command2.getTimestamp();
//			projectTime += timestamp2 - timestamp1;
//		}
//		projectTime += nestedCommands.get(i).get(j).getTimestamp() - nestedCommands.get(i).get(0).getTimestamp();
//		if (changeByTime && currentTimeSpent <= projectTime) 
//			return;
//		currentTimeSpent = projectTime;
		long projectTime = 0;
		for(int k = 0; k <= i; k++) {
			List<EHICommand> commands = nestedCommands.get(k);
			if (commands.size() == 0) {
				continue;
			}
			int j = 0;
			for(; j < (k==i?this.j:commands.size()); j++) {
				if (commands.get(j).getStartTimestamp() > 0 || commands.get(j).getTimestamp() > 0) {
					break;
				}
//				if (!(commands.get(j) instanceof LocalCheckCommand) && (commands.get(j).getStartTimestamp() > 0 || commands.get(j).getTimestamp() > 0)) {
//					break;
//				}
			}
			long timestamp1 = commands.get(j).getTimestamp() + commands.get(j).getStartTimestamp();
			EHICommand command2 = commands.get(k==i?this.j:commands.size()-1);
			long timestamp2 = command2.getStartTimestamp() + command2.getTimestamp();
			projectTime += timestamp2 - timestamp1;
		}
		currentTimeSpent = projectTime - restTime(20*60*1000, i, j);
	}

	public String getCurrentTimeSpent() {
		calculateCurrentTimeSpent();
		return convertToHourMinuteSecond(currentTimeSpent);
	}

	private String unknownFile = "Unknown";

	protected void printTimeSpentOnEachFile(String projectPath, int sort){
		Map<String, Long> fileTimeMap = new TreeMap<>();
		fileTimeMap.put(unknownFile, (long)0);
		for (List<EHICommand> commands: nestedCommands) {
			long lastTimestamp = 0;
			long currentTimestap = 0;
			String currentFile = unknownFile;
			for (int i = 0; i < commands.size(); i++){
				EHICommand command = commands.get(i);
				if (command == null) 
					continue;
				currentTimestap = command.getTimestamp();
				if (command instanceof DiffBasedFileOpenCommand) {					
					String filePath = ((DiffBasedFileOpenCommand)command).getFilePath();					
					if (fileTimeMap.containsKey(currentFile)) {
						long savedTime = fileTimeMap.get(currentFile);
						fileTimeMap.put(currentFile, savedTime + currentTimestap - lastTimestamp);
					} else {
						fileTimeMap.put(currentFile, currentTimestap - lastTimestamp);
					}
					lastTimestamp = currentTimestap;
					currentFile = filePath;
				}
			}

			if (fileTimeMap.containsKey(currentFile)) {
				long savedTime = fileTimeMap.get(currentFile);
				fileTimeMap.put(currentFile, savedTime + currentTimestap - lastTimestamp);
			} else {
				fileTimeMap.put(currentFile, currentTimestap - lastTimestamp);
			}
		}
	}

	protected void printTimeSpentOutsideOfEclipse() {
		long timeSpentOutsideOfEclipse = 0;
		for (List<EHICommand> commands: nestedCommands){
			long focusLostTimestamp = 0;
			for (EHICommand command : commands) 
				if (command instanceof ShellCommand) 
					if (((ShellCommand) command).isFocusLost()) 
						focusLostTimestamp = command.getTimestamp();
					else if (((ShellCommand) command).isFocusGain()) 
						timeSpentOutsideOfEclipse += command.getTimestamp() - focusLostTimestamp;
		}
		System.out.println("Time spent ouside of Eclipse: " + convertToHourMinuteSecond(timeSpentOutsideOfEclipse));
	}

	protected String convertToHourMinuteSecond(long timeSpent){
		int hour = (int) (timeSpent / 3600000);
		int minute = (int) (timeSpent % 3600000 / 60000);
		int second = (int) (timeSpent % 60000 / 1000);
		return hour + " hours " + minute + " minutes " + second + " seconds";
	}

	public ArrayList<EHICommand> jumpTo(int indexPercent, String step) {
		int index = numOfCommands * indexPercent / 1000;
		int k = 0;
		for (; k < nestedCommands.size(); k++) {
			if ((index - nestedCommands.get(k).size()) >= 0) {
				index -= nestedCommands.get(k).size();
			} else
				break;
		}
		i = k;
		j = index;

		currentExceptions = 0;
		for (int p = 0; p < i; p++) 
			for (EHICommand command: nestedCommands.get(p)) 
				if (command instanceof ExceptionCommand) 
					currentExceptions++;
		for (int q = 0; q <= j; q++)
			if (nestedCommands.get(i).get(q) instanceof ExceptionCommand) 
				currentExceptions++;

		if (indexPercent >= 990) {
			ArrayList<EHICommand> retVal = back(null, step);
			//			while (retVal == null) {
			//				retVal = back(step);
			//			}
			return retVal;
		} else {
			ArrayList<EHICommand> retVal = forward(null, step);
			//			while (retVal == null) {
			//				retVal = forward(step);
			//			}
			return retVal;
		}
	}

	public int timelineIndex() {
		int index = 0;
		for (int k = 0; k < i; k++) {
			index += nestedCommands.get(k).size();
		}
		return (index + j) * 1000 / numOfCommands;
	}

	public int getCurrentExceptions() {
		return currentExceptions;
	}

	public int getTotalExceptions() {
		return totalExceptions;
	}

	public List<List<List<String>>> getMetrics() {
		List<List<List<String>>> retval = new ArrayList<>();
		long startTime = currentTime;
		long endTime = previousTime;
		if (startTime > endTime) {
			long temp = startTime;
			startTime = endTime;
			endTime = temp;
		} 
		for(int k = 0, m = 0; k < metrics.size(); k++) {
			List<List<String>> metric = metrics.get(k);
			if (metric.size() == 0) {
				continue;
			}
			if ((Long.parseLong(metric.get(metric.size()-1).get(4)) < startTime) || (Long.parseLong(metric.get(0).get(4)) > endTime)) {
				continue;
			}
			retval.add(new ArrayList<>());
			for(int l = 0; l < metrics.get(k).size(); l++) {
				if ((Long.parseLong(metric.get(l).get(4)) >= startTime) && (Long.parseLong(metric.get(l).get(4)) <= endTime)) {
					retval.get(m).add(metric.get(l));
				}
			}
			m++;
		}
		return retval;
	}
	
	public void createFileLogs(String projectPath) {
		createNewFileLog(projectPath);
		createDeleteFileLog(projectPath);
		createRefactorLog(projectPath);
	}
	
	public void createNewFileLog(String projectPath) {
		newFiles = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(projectPath+ File.separator + "Logs" + File.separator + "NewFileHistory.txt")));
			String line = null;
			while ((line = br.readLine()) != null) {
				List<String> newFile = new ArrayList<>();
				newFile.add(line.substring(0, line.indexOf(" ")));
				newFile.add(line.substring(line.indexOf(" ")+1));
				newFiles.add(newFile);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createDeleteFileLog(String projectPath) {
		deletedFiles = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(projectPath + File.separator + "Logs" + File.separator + "DeleteHistory.txt")));
			String line = null;
			while ((line = br.readLine()) != null) {
				List<String> deleteFile = new ArrayList<>();
				deleteFile.add(line.substring(0, line.indexOf(" ")));
				deleteFile.add(line.substring(line.indexOf(" ")+1));
				deletedFiles.add(deleteFile);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createRefactorLog(String projectPath) {
		refactorRecords = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(projectPath + File.separator + "Logs" + File.separator + "RefactorHistory.txt")));
			String line = null;
			while((line = br.readLine()) != null){
				List<String> refactorRecord = new ArrayList<>();
				refactorRecord.add(line.substring(0, line.indexOf(" ")));
				refactorRecord.add(line.substring(line.indexOf(" ")+1, line.indexOf("----->")));
				refactorRecord.add(line.substring(line.indexOf("----->")+7));
				refactorRecords.add(refactorRecord);					
			}
			br.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void cleanup() {
		if (i >= nestedCommands.size()) {
			i = nestedCommands.size()-1;
			j = nestedCommands.get(i).size()-1;
		} else if (i < 0) {
			i = 0;
			j = 0;
		} else if (j >= nestedCommands.get(i).size()) {
			j = nestedCommands.get(i).size()-1;
		} else if (j < 0) {
			j = 0;
		}
		
	}
}
