package analyzer.extension.replayView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import org.eclipse.core.resources.IProject;
import analyzer.Analyzer;
import analyzer.MainConsoleUI;
import analyzer.extension.ADifficultyPredictionAndStatusPrinter;
import fluorite.commands.AnnotateCommand;
import fluorite.commands.AnnotationCommand;
import fluorite.commands.ConsoleOutput;
import fluorite.commands.Delete;
import fluorite.commands.DiffBasedFileOpenCommand;
import fluorite.commands.DifficultyCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.EclipseCommand;
import fluorite.commands.ExceptionCommand;
import fluorite.commands.FileOpenCommand;
import fluorite.commands.Insert;
import fluorite.commands.LocalCheckCommand;
import fluorite.commands.MoveCaretCommand;
import fluorite.commands.PauseCommand;
import fluorite.commands.PiazzaPostCommand;
import fluorite.commands.Replace;
import fluorite.commands.RunCommand;
import fluorite.commands.ShellCommand;
import fluorite.commands.WebCommand;
import fluorite.commands.ZoomChatCommand;
import fluorite.commands.ZoomSessionEndCommand;
import fluorite.commands.ZoomSessionStartCommand;
import fluorite.model.EHEventRecorder;
import programmatically.AnEclipseProgrammaticController;

public class AReplayer2 extends ADifficultyPredictionAndStatusPrinter{
	private List<List<EHICommand>> nestedCommands;
	private File[] currentLogFiles;
	private final AnEclipseProgrammaticController PROGRAMATIC_CONTROLLER;
	private Analyzer analyzer;
	private String currentProjectPath;
	private int i = 0;
	private int j = 0;
	private FileOpenCommand currentFile = null;
	private List<EHICommand> commandList = new ArrayList<>();
	private int numOfCommands = 0;
	private int currentExceptions = 0; 
	private long currentTimeSpent = 0;
	private long totalTimeSpent = 0;
	private final static long ONE_MINUTE = 60 * 1000;
	private final static long ONE_HOUR = 60 * ONE_MINUTE;
//	private List<List<List<String>>> metrics = null;
	private long currentTime = 0;
	private static final long PAUSE = 5*60*1000;
	private FileEditor currentFileEditor;
	private SimpleDateFormat df;

	public AReplayer2(Analyzer anAnalyzer) {
		super(anAnalyzer);
		analyzer = anAnalyzer;
		PROGRAMATIC_CONTROLLER = AnEclipseProgrammaticController.getInstance();
		currentProjectPath = ReplayUtility.getCurrentProjectPath();
		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
		df.setTimeZone(TimeZone.getTimeZone("America/New_York"));
	}

	public List<List<EHICommand>> replayLogs(String projectPath, Analyzer analyzer){
		currentLogFiles = ReplayUtility.getLogFiles(projectPath);
		nestedCommands = ReplayUtility.replayLogs(projectPath, analyzer);
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

//	public void createMetrics(String projectPath) {
//		File metricFolder = new File(projectPath+File.separator+"Logs"+File.separator+"Metrics");
//		List<String> metricFiles = MainConsoleUI.getFilesForFolder(metricFolder);
//		metrics = new ArrayList<>();
//		String largestFileName = "";
//		String secondLargestFileName = "";
//		for (int i = 0; i < metricFiles.size(); i++) {
//			String aFileName = metricFiles.get(i);
//			if (aFileName.compareTo(largestFileName) > 0) {
//				secondLargestFileName = largestFileName;
//				largestFileName = aFileName;
//			} else if (aFileName.compareTo(secondLargestFileName) > 0) {
//				secondLargestFileName = aFileName;
//			}
//		}
//		for (int i = 0; i < metricFiles.size(); i++) {
//			List<List<String>> metric = new ArrayList<>();
//			metrics.add(metric);
//			BufferedReader r;
//			try {
//				r = new BufferedReader(new FileReader(new File(metricFolder.getPath()+File.separator+metricFiles.get(i))));
//				String line=r.readLine();
//				while(true){
//					line = r.readLine();
//					if (line == null) {
//						break;
//					}
//					try {
//						Integer.parseInt(line.substring(0, line.indexOf(",")));
//					} catch (NumberFormatException e) {
//						continue;
//					}
//					metric.add(Arrays.asList(line.split(","))); 
//				}
//				r.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	public static final String XML_FILE_ENDING = "\r\n</Events>"; 

	public void setup() {
		if (nestedCommands == null || !ReplayUtility.getCurrentProjectPath().equals(currentProjectPath)) {
			currentProjectPath = ReplayUtility.getCurrentProjectPath();
			replayLogs(currentProjectPath, analyzer);
//			createMetrics(currentProjectPath);
			reset();
//			outer: 
//				for(int n = nestedCommands.size()-1; n >= 0; n--) {
//					for(int m = nestedCommands.get(n).size()-1; m >= 0; m--) {
//						if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
//							currentFile = (FileOpenCommand)nestedCommands.get(n).get(m);
//							String path = ReplayUtility.getFilePath(currentFile);
//							currentFileEditor = FileEditor.getEditor(path);
//							PROGRAMATIC_CONTROLLER.getOrCreateProject(ReplayUtility.getCurrentProjectName(), currentProjectPath);
//							PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
//							break outer;
//						}
//					}
//				}
//			numOfCommands = 0;
//			currentExceptions = 0;
//			totalTimeSpent = 0;
//			currentTimeSpent = 0;
//			calculateCurrentTimeSpent();
//			calculateTotalTimeSpent();
//			for (List<EHICommand> commands : nestedCommands) {
//				numOfCommands += commands.size();
//				for (EHICommand command : commands) {
//					if (command instanceof ExceptionCommand) {
//						currentExceptions++;
//					}
//				}
//			}
//			i = nestedCommands.size() - 1;
//			j = nestedCommands.get(i).size() - 1;
		} else {
			openEditor(currentFile);
		}
	}
	
	public void reset() {
		if (nestedCommands == null) {
			return;
		}
		outer: 
			for(int n = nestedCommands.size()-1; n >= 0; n--) {
				for(int m = nestedCommands.get(n).size()-1; m >= 0; m--) {
					if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
						currentFile = (FileOpenCommand)nestedCommands.get(n).get(m);
						String path = ReplayUtility.getFilePath(currentFile);
						currentFileEditor = FileEditor.getEditor(path);
						PROGRAMATIC_CONTROLLER.getOrCreateProject(ReplayUtility.getCurrentProjectName(), currentProjectPath);
						PROGRAMATIC_CONTROLLER.openEditor(path.substring(path.lastIndexOf("src")));
						break outer;
					}
				}
			}
		numOfCommands = 0;
		currentExceptions = 0;
		totalTimeSpent = 0;
		currentTimeSpent = 0;
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
		i = nestedCommands.size() - 1;
		j = nestedCommands.get(i).size() - 1;
	}
	private void openEditor(EHICommand currentFile) {
		if (currentFile == null) return;
		String path = currentFile.getDataMap().get("filePath");
		int srcIdx = path.lastIndexOf("src");
		if (srcIdx == -1) return;
//		if (new File(path))
		PROGRAMATIC_CONTROLLER.openEditor(path.substring(srcIdx));
	}
	
	public void maybeAddToCommandList(EHICommand command) {
		if (command instanceof WebCommand || command instanceof ExceptionCommand || 
			command instanceof ConsoleOutput || command instanceof PauseCommand || 
			command instanceof LocalCheckCommand || command instanceof FileOpenCommand ||
			command instanceof Insert || command instanceof Delete || command instanceof Replace ||
			command instanceof PiazzaPostCommand || command instanceof ZoomSessionStartCommand ||
			command instanceof ZoomChatCommand || command instanceof ZoomSessionEndCommand ||
			command instanceof AnnotationCommand) {
			commandList.add(command);
		}
	}

	public List<EHICommand> back(String numStep, String step){
		commandList.clear();
		switch (step) {
		case ReplayView.EDIT:
			if (isPositiveInteger(numStep)) {
				backEdit(Integer.parseInt(numStep));
			} 
			break;
		case ReplayView.ONE_MINUTE:
			if (isPositiveInteger(numStep)) {
				currentTimeSpent -= Integer.parseInt(numStep)*ONE_MINUTE;
				backByTime(Integer.parseInt(numStep)*ONE_MINUTE);
			} 
			break;
		case ReplayView.ONE_HOUR:
			if (isPositiveInteger(numStep)) {
				currentTimeSpent -= Integer.parseInt(numStep)*ONE_HOUR;
				backByTime(Integer.parseInt(numStep)*ONE_HOUR);
			} 
			break;
		case ReplayView.PAUSE:
			if (isPositiveInteger(numStep)) {
				return backToPause(Integer.parseInt(numStep));
			} 
			break;
		case ReplayView.RUN:
			backToRun();
			break;
		case ReplayView.DEBUG:
			backToDebug();
			break;
//		case ReplayView.DIFFICULTY:
//		case ReplayView.DIFFICULTY_TO_NO_DIFFICULTY:
//		case ReplayView.OPEN_FILE:
//		case ReplayView.WEB:
//		case ReplayView.EXCEPTION:
//		case ReplayView.EXCEPTION_TO_FIX:
//		case ReplayView.FIX:
//		case ReplayView.LOCALCHECKS:
//		case ReplayView.SAVE:
//		case ReplayView.OFFICE_HOUR:
//		case ReplayView.PIAZZA:
//		case ReplayView.ANNOTATION:
//			backToCommandType(step);
//			break;
		default:
			backToCommandType(step);
//			commandList.clear();
			break;
		}
		openEditor(currentFile);
		if (i < 0) {
			i = 0;
		}
		if (j < 0) {
			j = 0;
		}
		return commandList;
	}
	
	public List<EHICommand> backToCommandType(String type){
		outer:
			for(; i >= 0 && i < nestedCommands.size(); i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					processBackCommand(command);
					if (commandIsType(command, type)) {
						j--;
						break outer;
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		return commandList;
	}

	public List<EHICommand> backToPause(int time){
		outer:
			for(; i >= 0 && i < nestedCommands.size(); i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					processBackCommand(command);
					if (command instanceof PauseCommand) {
						int pause = Integer.parseInt(command.getDataMap().get("pause"));
						if (pause/1000 >= time) {
							j--;
							break outer;
						}
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		return commandList;
	}

	public List<EHICommand> backToRun(){
		boolean hitRun = false;
		outer:
			for(; i >= 0 && i < nestedCommands.size(); i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}
				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					processBackCommand(command);
					if (command instanceof RunCommand && command.getAttributesMap().get("type").equals("Run")) {
//						commandList.add(command);
						hitRun = true;
					}
					if (hitRun && !(command instanceof RunCommand)) {
						break outer;
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		return commandList;
	}

	public List<EHICommand> backToDebug(){
		boolean hitDebug = false;
		outer:
			for(; i >= 0 && i < nestedCommands.size(); i--) {
				if (i >= nestedCommands.size()) {
					i = nestedCommands.size()-1;
				}
				List<EHICommand> commands = nestedCommands.get(i);
				if (j >= commands.size()) {
					j = commands.size()-1;
				}

				for(; j >=0; j--) {
					EHICommand command = commands.get(j);
					processBackCommand(command);
					if (command instanceof RunCommand && command.getAttributesMap().get("type").equals("Debug")) {
//						commandList.add(command);
						hitDebug = true;
					}
					if (hitDebug && !(command instanceof RunCommand)) {
						break outer;
					}
				}
				if (i != 0) {
					j = nestedCommands.get(i-1).size()-1;
				}
			}
		return commandList;
	}

//	public List<EHICommand> backToDifficulty(){
//		boolean difficulty = false;
////		outer:
////			for(int k = metrics.size()-1; k >= 0; k--) {
////				List<List<String>> metric = metrics.get(k);
////				for(int l = metric.size()-1; l >= 0; l--) {
////					if (metric.get(l).get(1).equals("YES") && Long.parseLong(metric.get(l).get(4)) < currentTime) {
////						currentTime = Long.parseLong(metric.get(l).get(4));
////						difficulty = true;
////						break outer;
////					}
////				}
////			}
//		if (!difficulty) {
//			currentTime = 0;
//		}
//		outer:
//			for(; i >= 0; i--) {
//				if (i >= nestedCommands.size()) {
//					i = nestedCommands.size()-1;
//				}
//				List<EHICommand> commands = nestedCommands.get(i);
//				if (j >= commands.size()) {
//					j = commands.size()-1;
//				}
//				for(; j >=0; j--) {
//					EHICommand command = commands.get(j);
//					processBackCommand(command);
//					if ((command.getStartTimestamp() == 0 && command.getTimestamp2() < currentTime) || (command.getStartTimestamp() != 0 && command.getStartTimestamp()+command.getTimestamp() < currentTime)) {
//						if (j==nestedCommands.get(i).size()-1) {
//							i++;
//							j = 0;
//						} else {
//							j++;
//						}
//						break outer;
//					} 
//				}
//
//				if (i != 0) {
//					j = nestedCommands.get(i-1).size()-1;
//				}
//			}
//		return commandList;
//	}

	public List<EHICommand> backEdit(int limit) {
		for(; i >= 0 && i < nestedCommands.size(); i--) {
			if (i >= nestedCommands.size()) {
				i = nestedCommands.size()-1;
			}
			List<EHICommand> commands = nestedCommands.get(i);
			if (j >= commands.size()) {
				j = commands.size()-1;
			}
			int editLength = 0;
			for(; j >=0; j--) {
				EHICommand command = commands.get(j);
				processBackCommand(command);
				if (command instanceof Insert) {
					editLength += command.getDataMap().get("text").length();
				}
				if (command instanceof Replace) {
					editLength += command.getDataMap().get("deletedText").length() + command.getDataMap().get("insertedText").length();
				}
				if (command instanceof Delete) {
					editLength += command.getDataMap().get("text").length();
				}
				if (editLength >= limit) {
					j--;
					break;
				}
			}
			if (editLength >= limit) {
				openEditor(currentFile);
				return commandList;
			}
			if (i != 0) {
				j = nestedCommands.get(i-1).size()-1;
			}
		}
		return commandList;
	}

	public List<EHICommand> backByTime(long backTime) {
		outer:
			for(; i >= 0 && i < nestedCommands.size(); i--) {
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
						processBackCommand(command);
						if (startTime - command.getTimestamp() > backTime) {
							break outer;
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
			currentFile = lastOpenFile();
		} else {
			currentFile = (FileOpenCommand)commandList.get(commandList.size()-1);
		}
		openEditor(currentFile);
		return commandList;
	}

	private FileOpenCommand lastOpenFile() {
		int[] idx = ReplayUtility.lastOpenFile(nestedCommands, i, j);
		return (FileOpenCommand)nestedCommands.get(idx[0]).get(idx[1]);
	}
	
	private boolean commandIsType(EHICommand command, String type) {
		switch (type) {
		case ReplayView.EXCEPTION:
		case ReplayView.EXCEPTION_TO_FIX:
			return command instanceof ExceptionCommand;
		case ReplayView.FIX:
			return command instanceof ConsoleOutput;
		case ReplayView.LOCALCHECKS:
			return command instanceof LocalCheckCommand;
		case ReplayView.RUN:
			return command instanceof RunCommand && command.getAttributesMap().get("type").equals("Run");
		case ReplayView.DEBUG:
			return command instanceof RunCommand && command.getAttributesMap().get("type").equals("Debug");
		case ReplayView.DIFFICULTY:
		case ReplayView.DIFFICULTY_TO_NO_DIFFICULTY:
//			return (command.getStartTimestamp() == 0 && command.getTimestamp2() < currentTime) || (command.getStartTimestamp() != 0 && command.getStartTimestamp()+command.getTimestamp() < currentTime);
			return command instanceof DifficultyCommand;
		case ReplayView.OPEN_FILE:
			return command instanceof FileOpenCommand;
		case ReplayView.PAUSE:
			return command instanceof PauseCommand;
		case ReplayView.WEB:
			return command instanceof WebCommand;
		case ReplayView.SAVE:
			return command instanceof EclipseCommand && ((EclipseCommand)command).getCommandID().equals("org.eclipse.ui.file.save");
		case ReplayView.PIAZZA:
			return command instanceof PiazzaPostCommand;
		case ReplayView.OFFICE_HOUR:
			return command instanceof ZoomSessionStartCommand;
		case ReplayView.ANNOTATION:
			return command instanceof AnnotationCommand;
		default:
			return false;
		}
	}

	private boolean isPositiveInteger(String numStep) {
		for (int a = 0; a < numStep.length(); a++) {
			if (!Character.isDigit(numStep.charAt(a))) {
				JOptionPane.showMessageDialog(null, "Please enter a positive integer", "ParseException", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		}
		return true;
	}
	
	public List<EHICommand> forward(String numStep, String step){
		commandList.clear();
		switch (step) {
		case ReplayView.EDIT:
			if (isPositiveInteger(numStep)) {
				forwardEdit(Integer.parseInt(numStep));
			} 
			break;
		case ReplayView.ONE_MINUTE:
			if (isPositiveInteger(numStep)) {
				currentTimeSpent += Integer.parseInt(numStep)*ONE_MINUTE;
				forwardByTime(Integer.parseInt(numStep)*ONE_MINUTE);
			} 
			break;
		case ReplayView.ONE_HOUR:
			if (isPositiveInteger(numStep)) {
				currentTimeSpent += Integer.parseInt(numStep)*ONE_HOUR;
				forwardByTime(Integer.parseInt(numStep)*ONE_HOUR);
			} 
			break;
		case ReplayView.PAUSE:
			if (isPositiveInteger(numStep)) {
				forwardToPause(Integer.parseInt(numStep));
			} 
			break;
		case ReplayView.RUN:
			forwardToRun();
			break;
		case ReplayView.DEBUG:
			forwardToDebug();
			break;
//		case ReplayView.EXCEPTION:
//		case ReplayView.EXCEPTION_TO_FIX:
//		case ReplayView.FIX:
//		case ReplayView.LOCALCHECKS:
//		case ReplayView.DIFFICULTY:
//		case ReplayView.DIFFICULTY_TO_NO_DIFFICULTY:
//		case ReplayView.OPEN_FILE:
//		case ReplayView.WEB:
//		case ReplayView.SAVE:
//		case ReplayView.OFFICE_HOUR:
//		case ReplayView.PIAZZA:
//			forwardToCommandType(step);
//			break;
		default: 
//			commandList.clear();
			forwardToCommandType(step);
			break;
		}
		if (i >= nestedCommands.size()) {
			i = nestedCommands.size() - 1;
		}
		if (j >= nestedCommands.size()) {
			j = nestedCommands.size() - 1;
		}
//		commandList.clear();
		openEditor(currentFile);
		return commandList;
	}
	
	public List<EHICommand> forwardToPause(int time){
		outer:
			for(; i < nestedCommands.size() && i >= 0; i++) {
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
					processForwardCommand(command);
					if (command instanceof PauseCommand) {
						int pause = Integer.parseInt(command.getDataMap().get("pause"));
						if (pause/1000 >= time) {
							j++;
							break outer;
						}
					}
				}
				if (i < nestedCommands.size()-1) {
					j = 0;
				}
			}
		return commandList;
	}

	public List<EHICommand> forwardToRun(){
		boolean hitRun = false;
		outer:
			for(; i < nestedCommands.size() && i >= 0; i++) {
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
					processForwardCommand(command);
					if (command instanceof RunCommand && command.getAttributesMap().get("type").equals("Run")) {
//						commandList.add(command);
						hitRun = true;
					}
					if (hitRun && !(command instanceof RunCommand)) {
						break outer;
					}
				}
				j = 0;
			}
		return commandList;
	}

	public List<EHICommand> forwardToDebug(){
		boolean hitDebug = false;
		outer:
			for(; i < nestedCommands.size() && i >= 0; i++) {
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
					processForwardCommand(command);
					if (command instanceof RunCommand && command.getAttributesMap().get("type").equals("Debug")) {
//						commandList.add(command);
						hitDebug = true;
					}
					if (hitDebug && !(command instanceof RunCommand)) {
						break outer;
					}

				}
				j = 0;
			}
		return commandList;
	}

//	public List<EHICommand> forwardToCompile(){
//		boolean hitRun = false;
//		outer:
//			for(; i < nestedCommands.size(); i++) {
//				if (i >= nestedCommands.size()) {
//					return commandList;
//				}
//				List<EHICommand> commands = nestedCommands.get(i);
//				if (j >= commands.size()) {
//					i++;
//					if (i >= nestedCommands.size()) {
//						return commandList;
//					}
//					j = 0;
//				}
//				for(; j < commands.size(); j++) {
//					EHICommand command = commands.get(j);
//					processForwardCommand(command);
//					if (command instanceof RunCommand) {
//						commandList.add(command);
//						hitRun = true;
//					}
//					if (hitRun && !(command instanceof RunCommand)) {
//						break outer;
//					}
//				}
//				j = 0;
//			}
//		return commandList; 	
//	}

//	public List<EHICommand> forwardToDifficulty(){
//		boolean difficulty = false;
////		outer:
////			for(int k = 0; k < metrics.size(); k++) {
////				List<List<String>> metric = metrics.get(k);
////				for(int l = 0; l < metrics.get(k).size(); l++) {
////					if (metric.get(l).get(1).equals("YES") && Long.parseLong(metric.get(l).get(4)) > currentTime) {
////						currentTime = Long.parseLong(metric.get(l).get(4));
////						difficulty = true;
////						break outer;
////					}
////				}
////			}
//		if (!difficulty) {
//			currentTime = System.currentTimeMillis();
//		}
//		outer:
//			for(; i < nestedCommands.size(); i++) {
//				if (i >= nestedCommands.size()) {
//					return commandList;
//				}
//				List<EHICommand> commands = nestedCommands.get(i);
//				if (j >= commands.size()) {
//					i++;
//					if (i >= nestedCommands.size()) {
//						return commandList;
//					}
//					j = 0;
//				}
//				for(; j < commands.size(); j++) {
//					EHICommand command = commands.get(j);
//					processForwardCommand(command);
//					if (command == null) {
//						continue;
//					}
//					if ((command.getStartTimestamp() == 0 && command.getTimestamp2() > currentTime) || (command.getStartTimestamp() != 0 && command.getStartTimestamp()+command.getTimestamp() > currentTime)) {
//						if (j==0) {
//							i--;
//							j = nestedCommands.get(i).size()-1;
//						} else {
//							j--;
//						}
//						break outer;
//					} 
//				}
//				j = 0;
//			}
//		return commandList;
//	}

//	public List<EHICommand> forwardToNoDifficulty(){
//		boolean difficulty = true;
//		outer:
////			for(int k = 0; k < metrics.size(); k++) {
////				List<List<String>> metric = metrics.get(k);
////				for(int l = 0; l < metrics.get(k).size(); l++) {
////					if (metric.get(l).get(1).equals("NO") && Long.parseLong(metric.get(l).get(4)) > currentTime) {
////						currentTime = Long.parseLong(metric.get(l).get(4));
////						difficulty = false;
////						break outer;
////					}
////				}
////			}
//		if (difficulty) {
//			currentTime = System.currentTimeMillis();
//		}
//		outer:
//			for(; i < nestedCommands.size(); i++) {
//				if (i >= nestedCommands.size()) {
//					return commandList;
//				}
//				List<EHICommand> commands = nestedCommands.get(i);
//				if (j >= commands.size()) {
//					i++;
//					if (i >= nestedCommands.size()) {
//						return commandList;
//					}
//					j = 0;
//				}
//				for(; j < commands.size(); j++) {
//					EHICommand command = commands.get(j);
//					if (command == null) {
//						continue;
//					}
//					processForwardCommand(command);
//					if ((command.getStartTimestamp() == 0 && command.getTimestamp2() > currentTime) || (command.getStartTimestamp() != 0 && command.getStartTimestamp()+command.getTimestamp() > currentTime)) {
//						if (j==0) {
//							i--;
//							j = nestedCommands.get(i).size()-1;
//						} else {
//							j--;
//						}
//						break outer;
//					} 
//				}
//				j = 0;
//			}
//		return commandList;
//	}

	public List<EHICommand> forwardToCommandType(String type){
		outer:
			for(; i < nestedCommands.size() && i >= 0; i++) {
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
					processForwardCommand(command);
					if (commandIsType(command, type)) {
						j++;
						break outer;
					}
				}
				if (i < nestedCommands.size()-1) {
					j = 0;
				}
			}
		return commandList;
	}

	public List<EHICommand> forwardEdit(int limit) {
		for(; i < nestedCommands.size() && i >= 0; i++) {
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
			int editLength = 0;
			for(; j < commands.size(); j++) {
				EHICommand command = commands.get(j);	
				processForwardCommand(command);
				if (command instanceof FileOpenCommand) {
					break;
				}
				if (command instanceof Insert || command instanceof Delete) {
					editLength += command.getDataMap().get("text").length();
				}
				if (command instanceof Replace) {
					editLength += command.getDataMap().get("insertedText").length() + command.getDataMap().get("deletedText").length();;
				}
				if (editLength >= limit) {
					j++;
					break;
				}
			}
			if (editLength >= limit) {
				return commandList;
			}
			j = 0;
		}
		return commandList;
	}

	public List<EHICommand> forwardByTime(long forwardTime) {
		outer:
			for(; i < nestedCommands.size() && i >= 0; i++) {
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
						processForwardCommand(command);
						if (command.getTimestamp() - startTime > forwardTime) {
							break outer;
						} 
					}
				if (i < nestedCommands.size()-1) {
					j = 0;
					forwardTime -= commands.get(commands.size()-1).getTimestamp() - startTime;
				}
			}
		return commandList;
	}

//	private String trimNewLine(String s) {
//		String retVal = "";
//		for (int i = 0; i < s.length(); i++) {
//			if (s.charAt(i) != '\n' && s.charAt(i) != '\r' && s.charAt(i) != '\t') {
//				retVal += s.charAt(i);
//			}
//		}
//		return retVal;
//	}
//
//	public List<List<List<String>>> getMetrics() {
//		List<List<List<String>>> retval = new ArrayList<>();
//		long startTime = currentTime;
//		long endTime = previousTime;
//		if (startTime > endTime) {
//			long temp = startTime;
//			startTime = endTime;
//			endTime = temp;
//		} 
//		for(int k = 0, m = 0; k < metrics.size(); k++) {
//			List<List<String>> metric = metrics.get(k);
//			if (metric.size() == 0) {
//				continue;
//			}
//			if ((Long.parseLong(metric.get(metric.size()-1).get(4)) < startTime) || (Long.parseLong(metric.get(0).get(4)) > endTime)) {
//				continue;
//			}
//			retval.add(new ArrayList<>());
//			for(int l = 0; l < metrics.get(k).size(); l++) {
//				if ((Long.parseLong(metric.get(l).get(4)) >= startTime) && (Long.parseLong(metric.get(l).get(4)) <= endTime)) {
//					retval.get(m).add(metric.get(l));
//				}
//			}
//			m++;
//		}
//		return retval;
//	}
	
	protected void calculateTotalTimeSpent(){
		long projectTime = 0;
		for(int k = 0; k < nestedCommands.size(); k++) {
			List<EHICommand> commands = nestedCommands.get(k);
			if (commands.isEmpty()) {
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
			if (commands.isEmpty()) {
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
		}
		return restTime;
	}
	
	public String getWallTime(String path) {
		long wallTime = 0;
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
			}
			long timestamp1 = commands.get(j).getTimestamp() + commands.get(j).getStartTimestamp();
			EHICommand command2 = commands.get(k==i?Math.max(0, this.j-1):commands.size()-1);
			long timestamp2 = command2.getStartTimestamp() + command2.getTimestamp();
			projectTime += timestamp2 - timestamp1;
		}
		currentTimeSpent = projectTime - restTime(20*60*1000, i, j);
	}

	public String getCurrentTimeSpent() {
		calculateCurrentTimeSpent();
		return convertToHourMinuteSecond(currentTimeSpent);
	}
	
	public String getCurrentTimestamp() {
		EHICommand command = nestedCommands.get(i).get(j==nestedCommands.get(i).size()?Math.max(j-1, 0):j);
		return df.format(new Date(command.getStartTimestamp()+command.getTimestamp()));
	}

//	private String unknownFile = "Unknown";
//
//	protected void printTimeSpentOnEachFile(String projectPath, int sort){
//		Map<String, Long> fileTimeMap = new TreeMap<>();
//		fileTimeMap.put(unknownFile, (long)0);
//		for (List<EHICommand> commands: nestedCommands) {
//			long lastTimestamp = 0;
//			long currentTimestap = 0;
//			String currentFile = unknownFile;
//			for (int i = 0; i < commands.size(); i++){
//				EHICommand command = commands.get(i);
//				if (command == null) 
//					continue;
//				currentTimestap = command.getTimestamp();
//				if (command instanceof DiffBasedFileOpenCommand) {					
//					String filePath = ((DiffBasedFileOpenCommand)command).getFilePath();					
//					if (fileTimeMap.containsKey(currentFile)) {
//						long savedTime = fileTimeMap.get(currentFile);
//						fileTimeMap.put(currentFile, savedTime + currentTimestap - lastTimestamp);
//					} else {
//						fileTimeMap.put(currentFile, currentTimestap - lastTimestamp);
//					}
//					lastTimestamp = currentTimestap;
//					currentFile = filePath;
//				}
//			}
//
//			if (fileTimeMap.containsKey(currentFile)) {
//				long savedTime = fileTimeMap.get(currentFile);
//				fileTimeMap.put(currentFile, savedTime + currentTimestap - lastTimestamp);
//			} else {
//				fileTimeMap.put(currentFile, currentTimestap - lastTimestamp);
//			}
//		}
//	}
//
//	protected void printTimeSpentOutsideOfEclipse() {
//		long timeSpentOutsideOfEclipse = 0;
//		for (List<EHICommand> commands: nestedCommands){
//			long focusLostTimestamp = 0;
//			for (EHICommand command : commands) 
//				if (command instanceof ShellCommand) 
//					if (((ShellCommand) command).isFocusLost()) 
//						focusLostTimestamp = command.getTimestamp();
//					else if (((ShellCommand) command).isFocusGain()) 
//						timeSpentOutsideOfEclipse += command.getTimestamp() - focusLostTimestamp;
//		}
//		System.out.println("Time spent ouside of Eclipse: " + convertToHourMinuteSecond(timeSpentOutsideOfEclipse));
//	}

	protected String convertToHourMinuteSecond(long timeSpent){
		int hour = (int) (timeSpent / 3600000);
		int minute = (int) (timeSpent % 3600000 / 60000);
		int second = (int) (timeSpent % 60000 / 1000);
		return hour + " hours " + minute + " minutes " + second + " seconds";
	}

	public List<EHICommand> jumpTo(int indexPercent, String step) {
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
			return back(null, step);
		} else {
			return forward(null, step);
		}
	}

	public int timelineIndex() {
		int index = 0;
		for (int k = 0; k < i; k++) {
			index += nestedCommands.get(k).size();
		}
		return (index + j) * 1000 / numOfCommands;
	}

	private void processBackCommand(EHICommand command) {
		if (command == null) return;
		maybeAddToCommandList(command);
		if (command instanceof ExceptionCommand) {
			currentExceptions--;
		}
		if (command instanceof FileOpenCommand) {
			int[] idx = ReplayUtility.lastOpenFile(nestedCommands, i, j);
			if (idx[0] == -1 || idx[1] == -1) {
				return;
			}
			currentFile = (FileOpenCommand)nestedCommands.get(idx[0]).get(idx[1]);
			String snapshot = ReplayUtility.getSnapshot(currentFile);
			if (!ReplayUtility.isNull(snapshot)) {
				currentFileEditor = FileEditor.getEditor(ReplayUtility.getFilePath(currentFile), snapshot);
			} else {
				currentFileEditor = FileEditor.getEditor(ReplayUtility.getFilePath(currentFile));
			}
			for (int a = idx[0]; a <= i; a++) {
				List<EHICommand> commands = nestedCommands.get(a);
				for (int b = idx[1]; b < (a == i? j:commands.size()); b++) {
					EHICommand command2 = commands.get(b);
					if (command2 instanceof Insert) {
						int offset = Integer.parseInt(command2.getAttributesMap().get("offset"));
						String text = command2.getDataMap().get("text");
						currentFileEditor.insert(offset, text);
					}
					if (command2 instanceof Delete) {
						int offset = Integer.parseInt(command2.getAttributesMap().get("offset"));
						String text  = command2.getDataMap().get("text");
						currentFileEditor.delete(offset, text);
					}
					if (command2 instanceof Replace) {
						int offset = Integer.parseInt(command2.getAttributesMap().get("offset"));
						currentFileEditor.replace(offset, command2.getDataMap().get("deletedText"), command2.getDataMap().get("insertedText"));
					}
					if (command instanceof MoveCaretCommand) {
						currentFileEditor.moveCursor(Integer.parseInt(command2.getAttributesMap().get("caretOffset")));
					}
				}
			}
		}
		if (command instanceof Insert) {
			int offset = Integer.parseInt(command.getAttributesMap().get("offset"));
			String text = command.getDataMap().get("text");
			currentFileEditor.undoInsert(offset, text);
		}
		if (command instanceof Delete) {
			int offset = Integer.parseInt(command.getAttributesMap().get("offset"));
			String text  = command.getDataMap().get("text");
			currentFileEditor.undoDelete(offset, text);
		}
		if (command instanceof Replace) {
			int offset = Integer.parseInt(command.getAttributesMap().get("offset"));
			currentFileEditor.undoReplace(offset, command.getDataMap().get("deletedText"), command.getDataMap().get("insertedText"));
		}
		if (command instanceof MoveCaretCommand) {
			currentFileEditor.moveCursor(Integer.parseInt(command.getAttributesMap().get("caretOffset")));
		}
	}
	
	private void processForwardCommand(EHICommand command) {
		if (command == null) return;
		maybeAddToCommandList(command);
		if (command instanceof ExceptionCommand) {
			currentExceptions++;
		}
		if (command instanceof FileOpenCommand) {
			currentFile = (FileOpenCommand)command;
			String snapshot = ReplayUtility.getSnapshot(currentFile);
			if (!ReplayUtility.isNull(snapshot)) {
				currentFileEditor = FileEditor.getEditor(ReplayUtility.getFilePath(currentFile), snapshot);
			} else {
				currentFileEditor = FileEditor.getEditor(ReplayUtility.getFilePath(currentFile));
			}
		}
		if (command instanceof Insert) {
			int offset = Integer.parseInt(command.getAttributesMap().get("offset"));
			String text = command.getDataMap().get("text");
			currentFileEditor.insert(offset, text);
		}
		if (command instanceof Delete) {
			int offset = Integer.parseInt(command.getAttributesMap().get("offset"));
			String text  = command.getDataMap().get("text");
			currentFileEditor.delete(offset, text);
		}
		if (command instanceof Replace) {
			int offset = Integer.parseInt(command.getAttributesMap().get("offset"));
			currentFileEditor.replace(offset, command.getDataMap().get("deletedText"), command.getDataMap().get("insertedText"));
		}
		if (command instanceof MoveCaretCommand) {
			currentFileEditor.moveCursor(Integer.parseInt(command.getAttributesMap().get("caretOffset")));
		}
	}
	
	public String[] getReplayedFile() {
		String[] file = new String[2];
		file[0] = currentFileEditor.getFileName();
		file[1] = currentFileEditor.getContent();
		return file;
	}
	
	public void annotate(String annotation) {
		List<EHICommand> commands = nestedCommands.get(i);
		EHICommand lastCommand = commands.get(j);
		AnnotationCommand annotationCommand = new AnnotationCommand(annotation);
		annotationCommand.setStartTimestamp(lastCommand.getStartTimestamp());
		annotationCommand.setTimestamp(lastCommand.getTimestamp()+1);
		File logFile = currentLogFiles[i];
		commands.add(++j, annotationCommand);
		FileUtility.updateLogFile(logFile, commands);
	}
}
