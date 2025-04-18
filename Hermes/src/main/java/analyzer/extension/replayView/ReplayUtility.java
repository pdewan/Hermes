package analyzer.extension.replayView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
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
						writer.write(AReplayer2.XML_FILE_ENDING);
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
				commands.sort((a,b) -> Long.compare(a.getTimestamp()+a.getStartTimestamp(),b.getTimestamp()+b.getStartTimestamp()));
			}
		}
	}
	
	public static List<List<EHICommand>> replayLogs(String projectPath, Analyzer analyzer){
//		File logFolder = new File(projectPath, "Logs"+File.separator+"Eclipse");
//		if (!logFolder.exists()) {
//			System.out.println("No logs found for project " + projectPath);
//		}
//		File[] logFiles = logFolder.listFiles(File::isDirectory);
//		if (logFiles != null && logFiles.length > 0) {
//			logFolder = logFiles[0];
//		} 
//		if (logFolder.getName().equals("Eclipse")) {
//			refineLogFiles(logFolder.getPath());
//		}
//		File logFolder = getLogFolder(projectPath);
//		File logFolder = getAndFixLogFolder(projectPath);
		File logFolder = searchLogFolders(projectPath);


		List<List<EHICommand>> nestedCommands = analyzer.convertXMLLogToObjects(logFolder.getPath());
		sortNestedCommands(nestedCommands);
		return nestedCommands;
	}
	
	public static File[] getLogFiles(String projectPath) {
		File[] logFiles = getLogFolder(projectPath).listFiles((parent, name)->name.endsWith(".xml"));
//		Arrays.sort(logFiles, (a,b) -> a.getName().compareTo(b.getName()));
		return logFiles;
	}
	
	public static File getImportedLogFolder(File aLogFolder) {
		File retVal = new File(aLogFolder.getParentFile(), "Imported/Eclipse");
		if (retVal.exists()) {
			return retVal;
		}
		else {
			return null;
		}
	}
	public static File getGeneratedLogFolder(File aLogFolder) {
		File retVal = new File(aLogFolder, "Generated");
		if (retVal.exists()) {
			return retVal;
		}
		else {
			return null;
		}
	}
	
	public static File searchLogFolders(String projectPath) {
		File aLogFolder = getLogFolder(projectPath); // this will not refine log files
		if (aLogFolder == null) {
			return aLogFolder;
		}
		File anImportedLogFolder = getImportedLogFolder(aLogFolder);
		if (anImportedLogFolder != null ) {
			aLogFolder = anImportedLogFolder;
		} 
		File aGeneratedLogFolder = getGeneratedLogFolder(aLogFolder);
		if (aGeneratedLogFolder == null) {
			refineLogFiles(aLogFolder.getPath());
			return aLogFolder;
		}
		return aGeneratedLogFolder;
		
	}
	
	public static File getLogFolderOld(String projectPath) {
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
		return logFolder;
	}
	
	public static File getLogFolder(String projectPath) {
		File logFolder = new File(projectPath, "Logs"+File.separator+"Eclipse");
		if (!logFolder.exists()) {
			System.out.println("No logs found for project " + projectPath);
		}
		File[] logFiles = logFolder.listFiles(File::isDirectory);
		if (logFiles != null && logFiles.length > 0) {
			logFolder = logFiles[0];
		} 		
		return logFolder;
	}
	public static File getAndFixLogFolder(String projectPath) {
		File logFolder = getLogFolder(projectPath);
		if (logFolder.getName().equals("Eclipse")) {
			refineLogFiles(logFolder.getPath());
		}
		return logFolder;
	}
	
	public static int[] lastOpenFile(List<List<EHICommand>> nestedCommands, int i, int j) {
		int[] idx = new int[] {-1,-1};
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
	
	public static String getCurrentProjectName(){
		IProject currentProject = EHUtilities.getCurrentProject();
		if (currentProject == null) {
			return "";
		}
		return currentProject.getName();
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
