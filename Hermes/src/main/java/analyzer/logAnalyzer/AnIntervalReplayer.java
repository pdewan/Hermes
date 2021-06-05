package analyzer.logAnalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import fluorite.commands.EHICommand;
import fluorite.commands.PauseCommand;
import fluorite.util.EHLogReader;

public class AnIntervalReplayer {
	public static final long FIVE_MIN = 5 * 60 * 1000;
	public static final String XML_FILE_ENDING = "\r\n</Events>"; 
	public static final String[] TYPES = {"Edit", "Debug", "Run", "IO", "Exception", 
			"Request", "Web", "Save", "Gained Focus", 
			"Lost Focus", "Terminate", "Difficulty", 
			"Move Caret", "Open File", "Select", "Compile", 
			"LocalChecks", "Other"}; 
	public static final long[] THRESHOLD = {15109, 22531, 34266, 0, 9641, 0, 493000, 6921, 77564,
			24868, 0, 50953, 102979, 3984, 50202, 51718, 0, 79218};
	public static final long[] NEXT_THRESHOLD = {59079, 30031, 13407, 0, 19062, 0, 493000, 10125, 
			472825, 104170, 0, 13780, 102797, 65204, 50202, 20110, 0, 58702};
	
	private Map<String, Long> pauseMap;
	private Map<String, Long> nextPauseMap;
	private EHLogReader reader = new EHLogReader();
	
	long defaultPauseTime = FIVE_MIN;
	double multiplier = 1;
	
	public AnIntervalReplayer(double multiplier, int defaultPauseTime) {
		if (defaultPauseTime > 0) {
			this.defaultPauseTime = defaultPauseTime*60000L;
		} else {
			this.defaultPauseTime = FIVE_MIN;
		}
		if (multiplier > 0) {
			this.multiplier = multiplier;
		}
	}
	
	protected void initPauseMap() {
		pauseMap = new HashMap<>();
		nextPauseMap = new HashMap<>();
		for (int i = 0; i < TYPES.length; i++) {
			pauseMap.put(TYPES[i], THRESHOLD[i]==0?defaultPauseTime:THRESHOLD[i]);
			nextPauseMap.put(TYPES[i], NEXT_THRESHOLD[i]==0?defaultPauseTime:NEXT_THRESHOLD[i]);
		}
	}
	
	public long[] getWorkTime(File student, long start, long end) {
		if (pauseMap == null || nextPauseMap == null) {
			initPauseMap();
		}
		Map<String, List<EHICommand>> commandMap = readStudent(student);
		long[] retVal = new long[2];
		if (commandMap == null) {
			System.err.println("Error: Cannot read student log");
			retVal[0] = -1;
			retVal[1] = -1;
			return retVal;
		}
		List<List<EHICommand>> nestedCommands = new ArrayList<>();
		out:
		for (String logFile : commandMap.keySet()) {
			List<EHICommand> commands = commandMap.get(logFile);
			int startIndex = 0;
			int lastIndex = commands.size()-1;
			EHICommand last = commands.get(lastIndex);
			long lastTimestamp = last.getStartTimestamp() + last.getTimestamp();
			if (lastTimestamp < start) {
				continue;
			}
			for (; startIndex < commands.size(); startIndex++) {
				EHICommand command = commands.get(startIndex);
				long timestamp = command.getStartTimestamp() + command.getTimestamp();
				if (end > 0 && timestamp > end) {
					break out;
				} else if (timestamp > start) {
					break;
				}
			}
			for (; lastIndex >= 0; lastIndex--) {
				EHICommand command = commands.get(lastIndex);
				long timestamp = command.getStartTimestamp() + command.getTimestamp();
				if (end == 0 || timestamp < end) {
					break;
				}
			}
			nestedCommands.add(commands.subList(startIndex, lastIndex+1));
		}
		long totalTime = totalTimeSpent(nestedCommands);
		long[] restTime = restTime(nestedCommands, defaultPauseTime);
		retVal[0] = totalTime - restTime[0];
		retVal[1] = totalTime - restTime[1];
		return retVal;
	}
	
	protected long[] restTime(List<List<EHICommand>> nestedCommands, long time) {
		long[] restTime = new long[2];
		for (List<EHICommand> commands : nestedCommands) {
			for (EHICommand command : commands) {
				if (command instanceof PauseCommand) {
					long pause = Long.parseLong(command.getDataMap().get("pause"));
					String prevType = command.getDataMap().get("prevType");
					String nextType = command.getDataMap().get("nextType");
					if (pause > pauseMap.get(prevType) && pause > nextPauseMap.get(nextType)) {
						restTime[0] += pause;
					}
					if (pause > time) {
						restTime[1] += pause;
					}
				}
			}
		}
		return restTime;
	}
	
	protected long totalTimeSpent(List<List<EHICommand>> nestedCommands){
		long projectTime = 0;
		for(int k = 0; k < nestedCommands.size(); k++) {
			List<EHICommand> commands = nestedCommands.get(k);
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
			projectTime += timestamp2 - timestamp1;
		}
		return projectTime;
	}
	
	public Map<String, List<EHICommand>> readStudent(File student) {
		System.out.println("Reading student " + student);
		if (!student.exists()) {
			System.out.println("Folder " + student + " does not exist");
			return null;
		}
		File logFolder = null;
		File submission = new File(student,"Submission attachment(s)");
		if (submission.exists()) {
			logFolder = getProjectFolder(submission);
			if (logFolder != null) {
				logFolder = new File(logFolder, "Logs"+File.separator+"Eclipse");
			}
		} else {
			logFolder = new File(student, "Eclipse");
			if (!logFolder.exists()) {
				logFolder = getProjectFolder(student);
				if (logFolder != null) {
					logFolder = new File(logFolder, "Logs"+File.separator+"Eclipse");
				}
			}
		}
		if (logFolder == null || !logFolder.exists()) {
			System.out.println("No logs found for student " + student.getName());
			return null;
		}
		refineLogFiles(logFolder);
		File[] logFiles = logFolder.listFiles(File::isDirectory);
		if (logFiles != null && logFiles.length > 0) {
			logFiles = logFiles[0].listFiles((file)->{return file.getName().startsWith("Log") && file.getName().endsWith(".xml");});
		} else {
			logFiles = logFolder.listFiles((file)->{return file.getName().startsWith("Log") && file.getName().endsWith(".xml");});
		}
		if (logFiles == null) {
			System.out.println("No logs found for student " + student.getName());
			return null;
		}
		Map<String, List<EHICommand>> logs = new TreeMap<>();
		for (File logFile : logFiles) {
			List<EHICommand> ret = readOneLogFile(logFile);
			if (ret != null) {
				logs.put(logFile.getPath(), ret);
			}
		}
		return logs;
	}
	
	public List<EHICommand> readOneLogFile(File log){
		String path = log.getPath();
		System.out.println("Reading file " + path);
		if (!log.exists()) {
			System.err.println("log does not exist:" + path);
			return null;
		}
		if (!path.endsWith(".xml")) {
			System.err.println("log is not in xml format:" + path);
			return null;
		}
		try {
			List<EHICommand> commands = reader.readAll(path);
			sortCommands(commands, 0, commands.size()-1);
			return commands;
		} catch (Exception e) {
			System.err.println("Could not read file" + path + "\n"+ e);
		}
		return null;
	}
	
	public void refineLogFiles(File logFolder){
		try {
			for (File file : logFolder.listFiles((filename)->{return filename.getName().endsWith(".lck");})) {
				File logFile = new File(file.getParent(), file.getName().substring(0, file.getName().indexOf(".lck")));
				BufferedReader reader = new BufferedReader(new FileReader(logFile));
				String lastLine = null;
				String currentLine = null;
				while((currentLine = reader.readLine()) != null) {
					lastLine = currentLine;
				}
				if (lastLine != null && !lastLine.endsWith("</Events>")) {
					BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
					writer.write(XML_FILE_ENDING);
					writer.close();
				}	
				reader.close();
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void sortCommands(List<EHICommand> commands, int start, int end){
		for(int i = 0; i < commands.size(); i++) {
			if (commands.get(i) == null) {
				commands.remove(i);
				i--;
			}
		}
		EHICommand command = null;
		long cur = 0;
		for(int i = 0; i < commands.size(); i++) {
			command = commands.get(i);
			cur = command.getStartTimestamp()+command.getTimestamp();
			int j = i-1;
			while (j >= 0){
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
	
	public File getProjectFolder(File folder) {
		for (File file : folder.listFiles(File::isDirectory)) {
			if (file.getName().equals("src")) {
				return folder;
			} 
		}
		for (File file : folder.listFiles(File::isDirectory)) {
			if ((file = getProjectFolder(file)) != null) {
				return file;
			}
		}
		return null;
	}
}
