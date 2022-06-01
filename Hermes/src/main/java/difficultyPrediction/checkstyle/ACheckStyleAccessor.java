package difficultyPrediction.checkstyle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import analyzer.extension.replayView.AReplayer;
import analyzer.extension.replayView.FileUtility;
import fluorite.commands.CheckStyleCommand;
import fluorite.model.EHEventRecorder;
import fluorite.util.EHUtilities;

public class ACheckStyleAccessor {
	
//	protected int eventsReadSoFar = 0;
	protected long lastReadTime = 0;
	File checkStyleFile;
	List<String> recentEvents = new LinkedList();
	List<String[]> recentTuples = new LinkedList();
	List<Long> recentTimes = new LinkedList();
	
	
	public ACheckStyleAccessor() {
		checkStyleFile = getCheckStyleFile();
		lastReadTime = System.currentTimeMillis();
	}
	protected File getCheckStyleFile() {
		if (checkStyleFile == null) {
			
		
		String aProjectpath = EHUtilities.getCurrentOrStoredProjectPath();
		if (aProjectpath == null || aProjectpath.isEmpty()) {
			return null;
		}
		checkStyleFile = FileUtility.getCheckStyleLogFile(aProjectpath );
		}
		return checkStyleFile;

	}
	public void processRecentEvents() {
		File aFile = getCheckStyleFile();
		long aCurrentTime = System.currentTimeMillis();
		recentEvents.clear();
		recentTuples.clear();
		recentTimes.clear();
		FileUtility.getRecentEvents(aFile, recentEvents, recentTuples, recentTimes, lastReadTime, 1, df);;
		for (int i = 0; i < recentEvents.size(); i++) {
			CheckStyleCommand aCommand = new CheckStyleCommand(recentEvents.get(i));

			EHEventRecorder.getInstance().recordCommand(aCommand);
//			aCommand.setTimestamp(recentTimes.get(i) - aCommand.getStartTimestamp());

		}
		lastReadTime = aCurrentTime; 
	}
	 SimpleDateFormat df = new SimpleDateFormat("EEEE MMM dd HH:mm:ss z yyyy");
//	 static List<String> allEvents = new ArrayList();
	 
	
//	public static void getRecentEvents(File aFile, 
//			List<String> aRecentEvents, 
//			List<String[]> aRecentTuples,
//			List <Long> aRecentTimes,
//			long aLastReadTime,
//			int aDateColumn,
//			SimpleDateFormat aDateFormat) {
////		checkStyleFile = getCheckStyleFile();
//		if (aFile == null || !aFile.exists()) {
//			return;
//		}
//		long aLastModifed = aFile.lastModified();
//		if (aLastReadTime >=  aLastModifed   ) {
//			return; 
//		}
//		allEvents.clear();
//		readLines(aFile, allEvents);
//		for (int i = allEvents.size() -1 ; i >= 0 ; i--) {
//			String anEventRow = allEvents.get(i);
//			String[] anEvents = anEventRow.split(",");
//			try {
//				long aTime = aDateFormat.parse(anEvents[aDateColumn]).getTime();
//				if (aLastReadTime < aTime) {
//					aRecentEvents.add(0, anEventRow);
//					aRecentTuples.add(0, anEvents);
//					aRecentTimes.add(0, aTime);
//					
//				} else {
//					break; // previous events occur earlier
//				}
//				
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				
//			}
////			recentEvents.add(anAllEvents.get(i));
//		}
//
//	}
//	public static void readCheckstyleEvents(String aStudentProject, List<String> retVal) {
////		String aCheckStyleAllFileName = aStudent + "/Submission attachment(s)/Logs/LocalChecks/CheckStyle_All.csv";
////		File aFile = new File(aCheckStyleAllFileName);
//		File aCheckStyleFile = FileUtility.getCheckStyleLogFile(aStudentProject);
//		 readLines(aCheckStyleFile, retVal);
//		
//	}
////	static List<String> allEvents = new ArrayList();
//
//	public static void readLines(File aFile, List<String> anAllEvents) {
////		List<String> retVal = new ArrayList();
//
//		if (aFile == null || !aFile.exists()) {
//			return ;
//		}
//
//			try {
//				BufferedReader br = new BufferedReader(new FileReader(aFile));
//			    String line = null;
//			    while ((line = br.readLine()) != null) {
//			    	anAllEvents.add(line);
//			    }
//			    br.close();
//				return ;
//			}
//			
//				
//				
//			
//
//		catch (Exception e) {
//			e.printStackTrace();
//		} 
//	}
//	
	static ACheckStyleAccessor instance;
	public static ACheckStyleAccessor getInstance() {
		if (instance == null) {
			instance = new ACheckStyleAccessor();
		}
		return instance;
	}
}
