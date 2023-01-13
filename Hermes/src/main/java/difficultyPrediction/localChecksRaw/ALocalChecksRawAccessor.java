package difficultyPrediction.localChecksRaw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import analyzer.extension.replayView.AReplayer;
import analyzer.extension.replayView.FileUtility;
import fluorite.commands.CheckStyleCommand;
import fluorite.commands.LocalChecksRawCommand;
import fluorite.model.EHEventRecorder;
import fluorite.util.EHUtilities;

public class ALocalChecksRawAccessor {
	
//	protected int eventsReadSoFar = 0;
	protected long lastReadTime = 0;
	File[] localChecksRawFiles;
//	Map<File, Integer> eventsReadSoFar = new HashMap();
//	List<String> recentEvents = new ArrayList();
	List<String> recentEvents = new LinkedList();
	List<String[]> recentTuples = new LinkedList();
	List<Long> recentTimes = new LinkedList();
	SimpleDateFormat df = new SimpleDateFormat("EEEE MMM dd HH:mm:ss z yyyy");

	
	boolean foundChangingFile;
	
	
	public ALocalChecksRawAccessor() {
		localChecksRawFiles = getLocalChecksRawFiles();
		lastReadTime = System.currentTimeMillis();
	}
	protected File[] getLocalChecksRawFiles() {
		if (localChecksRawFiles == null || !foundChangingFile) {
			
		
		String aProjectpath = EHUtilities.getCurrentOrStoredProjectPath();
		if (aProjectpath == null || aProjectpath.isEmpty()) {
			return null;
		}
		localChecksRawFiles = FileUtility.getLocalChecksRawsLogFiles(aProjectpath );
		}
		return localChecksRawFiles;

	}
	

	public void processRecentEvents() {
		long aCurrentTime = System.currentTimeMillis();
		recentEvents.clear();
		recentTuples.clear();
		recentTimes.clear();
		localChecksRawFiles = getLocalChecksRawFiles();
		if (localChecksRawFiles == null || localChecksRawFiles.length == 0 ) {
			return ;
		}
		for (File aLocalChecksRawFile:localChecksRawFiles) {
			FileUtility.getRecentEvents(aLocalChecksRawFile, recentEvents, recentTuples, recentTimes, lastReadTime, 1, df);
		}
		if (recentEvents.size() > 0) {
			foundChangingFile = true;
		}
		for (int i = 0; i < recentEvents.size(); i++) {
//			CheckStyleCommand aCommand = new CheckStyleCommand(" a heck style command");

			LocalChecksRawCommand aCommand = new LocalChecksRawCommand(recentEvents.get(i));

			EHEventRecorder.getInstance().recordCommand(aCommand);
//			aCommand.setTimestamp(recentTimes.get(i) - aCommand.getStartTimestamp());

		}
		lastReadTime = aCurrentTime; 
		
			
	}
	
	

	
	static ALocalChecksRawAccessor instance;
	public static ALocalChecksRawAccessor getInstance() {
		if (instance == null) {
			instance = new ALocalChecksRawAccessor();
		}
		return instance;
	}
}
