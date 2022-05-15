package analyzer.extension.replayView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;

import analyzer.Analyzer;
import fluorite.commands.EHICommand;
import fluorite.commands.FileOpenCommand;
import fluorite.model.EHEventRecorder;
import fluorite.util.EHUtilities;

public class ReplayUtility {
	static final Pattern DIFF = Pattern.compile("(?s)Diff\\((EQUAL|INSERT),\\\"(.*?)\\\"\\)(?=$|, Diff\\((EQUAL|INSERT|DELETE))", Pattern.MULTILINE);

//	public static void refineLogFiles(String logPath){
//		try {
//			File logDirectory = new File(logPath);
//			for (File file : logDirectory.listFiles()) {
//				if (!file.getPath().contains(".lck")) {
//					BufferedReader reader = new BufferedReader(new FileReader(file));
//					String lastLine = null;
//					String currentLine = null;
//					while((currentLine = reader.readLine()) != null) {
//						lastLine = currentLine;
//					}
//					if (lastLine != null && !lastLine.endsWith("</Events>")) {
//						BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
//						writer.write(AReplayer.XML_FILE_ENDING);
//						writer.close();
//					}	
//					reader.close();
//				} else if (file.getPath().contains(".lck")) {
//					file.delete();
//				}
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		} 
//	}
	
	public static void refineLogFiles(String logPath){
		try {
			File logDirectory = new File(logPath);
			File currentLoggerFile = EHEventRecorder.loggerToFileName.get(Logger.getLogger(EHEventRecorder.class.getName()));
			File currentLCKFile = null;
			if (currentLoggerFile != null) {
				currentLCKFile = new File(logDirectory, currentLoggerFile.getName()+".lck");
			} 
			for (File file : logDirectory.listFiles()) {
				if (!file.getPath().contains(".lck") &&  (currentLCKFile == null || !currentLCKFile.getPath().contains(file.getPath()))) {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String lastLine = null;
					String currentLine = null;
					while((currentLine = reader.readLine()) != null) {
						lastLine = currentLine;
					}
					if (lastLine != null && !lastLine.endsWith("</Events>")) {
						BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
						writer.write(AReplayer.XML_FILE_ENDING);
						writer.close();
					}	
					reader.close();
				} else if (file.getPath().contains(".lck") && (currentLCKFile == null || !file.getPath().equals(currentLCKFile.getPath()))) {
					file.delete();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} 
	}
	
	protected static void sortNestedCommands(List<List<EHICommand>> nestedCommands){
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

	private static void sortCommands(List<EHICommand> commands, int start, int end){
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
	
	public static List<List<EHICommand>> replayLogs(String projectPath, Analyzer analyzer){
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
		List<List<EHICommand>> nestedCommands = analyzer.convertXMLLogToObjects(logFolder.getPath());
		sortNestedCommands(nestedCommands);
		return nestedCommands;
	}
	
	public static int[] lastOpenFile(List<List<EHICommand>> nestedCommands, int i, int j) {
		int[] idx = new int[2];
		int m = j-1;
		for(int n = i; n >= 0; n--) {
			for(; m >= 0; m--) {
				if (nestedCommands.get(n).get(m) instanceof FileOpenCommand) {
					idx[0] = n;
					idx[1] = m;
					return idx;
				}
			}
			if (n > 0) {
				m = nestedCommands.get(n-1).size()-1;
			}
		}
		return idx;
	}
	
	public static String getCurrentProjectPath(){
		IProject currentProject = EHUtilities.getCurrentProject();
		if (currentProject == null) {
			return "";
		}
		return currentProject.getLocation().toOSString();
	}
	
	public static int[] findFirstFile(List<List<EHICommand>> nestedCommands) {
		int[] idx = new int[2];
		for (int i = 0; i < nestedCommands.size(); i++) {
			List<EHICommand> commands = nestedCommands.get(i);
			for (int j = 0; j < commands.size(); j++) {
				EHICommand command = commands.get(j);
				if (command instanceof FileOpenCommand) {
					idx[0] = i;
					idx[1] = j;
					return idx;
				}
			}
		}
		return idx;
	}
	
	public static String getRelativeFilePath(EHICommand command) {
		if (command instanceof FileOpenCommand) {
			String path = command.getDataMap().get("filePath");
			path = path.substring(path.indexOf("src") + ("src" + File.separator).length());
			return path;
		}
		return "";
	}
	
	public static String getSnapshot(EHICommand command) {
		if (command instanceof FileOpenCommand) {
			String snapshot = command.getDataMap().get("snapshot");
			if (isNull(snapshot)) {
				String diff = command.getDataMap().get("diff");
				snapshot = processDiff(diff);
			}
			if (!isNull(snapshot)) {
				return snapshot;
			}
		}
		return "";
	}
	
	private static String processDiff(String diff) {
		if (isNull(diff)) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Matcher matcher = DIFF.matcher(diff);
		while (matcher.find()) {
			String type = matcher.group(1);
			if (type.equals("EQUAL") || type.equals("INSERT")) {
				sb.append(matcher.group(2));
			}
		}
		return sb.toString();
	}
	
	public static boolean isNull(String s) {
		return s == null || s.equals("null");
	}
	
	public static String getFilePath(FileOpenCommand command) {
		return command.getDataMap().get("filePath");
	}
}
