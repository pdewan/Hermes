package dayton.ellwanger.timetracker.hermes;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;

import dayton.ellwanger.hermes.xmpp.ConnectionManager;
import fluorite.model.EHEventRecorder;
import fluorite.model.EclipseEventListener;
import hermes.proxy.JSONProxy;
import hermes.tags.Tags;
import util.trace.hermes.timetracker.ActivityDetected;
import util.trace.hermes.timetracker.ActivitySessionEnded;
import util.trace.hermes.timetracker.ActivitySessionStarted;
import util.trace.hermes.timetracker.EclipeSessionEnded;
import util.trace.hermes.timetracker.EclipeSessionStarted;
import util.trace.hermes.timetracker.IdleCycleDetected;
import util.trace.hermes.timetracker.TimeWorkedForwardedToConnectionManager;

public class FluoriteListener implements 
	EclipseEventListener,
	TimeTrackerJSON, Tags
//	EHDocumentChangeListener,
//	DocumentChangeListener, 
//	CommandExecutionListener,
//	EHCommandExecutionListener,
//	RecorderListener
	{

	protected IdleTimer idleTimer;
	/*
	 * The granularity affects the delay before a session information is sent to a server
	 */
//	public static long IDLE_CYCLE_GRANULARITY = 3*1000; // 1 sec

	public static long IDLE_TIME_THRESHOLD = 3*60*1000; // 3 minutes
	public static long IDLE_CYCLE_GRANULARITY = IDLE_TIME_THRESHOLD; // since we look at command time stamps, this does not have to be smaller than the threshold 

	public static long IDLE_CYCLES_THRESHOLD = IDLE_TIME_THRESHOLD/IDLE_CYCLE_GRANULARITY;
	protected long startTimestamp;
	protected long lastCommandTimestamp;
	protected long activityStartTimestamp;
	protected long activityEndTimestamp;
	protected int numCommands;
	protected int numDocChanges;
	protected int numActions;
	protected int idleCycles = 0;
	protected Date startDate; // not really needed
	protected Date endDate; // not really needed
//	protected int activeCycles = 0;

	public FluoriteListener() {
		idleTimer = new IdleTimer();
		EHEventRecorder eventRecorder = EHEventRecorder.getInstance();
//		EventRecorder eventRecorder = EventRecorder.getInstance();

//		eventRecorder.addCommandExecutionListener(this);
//		eventRecorder.addDocumentChangeListener(this);
//		eventRecorder.addRecorderListener(this);
		eventRecorder.addEclipseEventListener(this);
	}
	@Override
	public void eventRecordingStarted(long aStartTimestamp) {
		startTimestamp = aStartTimestamp;
		EclipeSessionStarted.newCase(this, aStartTimestamp);
		
	}
	
	protected void initActivitySession() {
		numCommands = 0;
		numDocChanges = 0;
		numActions = 0;
		idleCycles = 0;
//		activeCycles = 0;
	}

	@Override
	public void eventRecordingEnded() {
		EclipeSessionEnded.newCase(this, startTimestamp, lastCommandTimestamp);
		sendSession(startDate, endDate);

		
	}
	@Override
	public void commandExecuted(String aCommandName, long aTimestamp) {
		lastCommandTimestamp = aTimestamp;
		numActions++;
		idleTimer.recordActivity();
		
	}
	@Override
	public void documentChanged(String aCommandName, long aTimestamp) {
		lastCommandTimestamp = aTimestamp;
		numDocChanges++;
		numActions++;

		idleTimer.recordActivity();
		
	}
	@Override
	public void documentChangeFinalized(long aTimestamp) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void commandExecuted(ICommand aCommand) {
//		lastCommandTimestamp = aCommand.getTimestamp();
//		numCommands++;
//		numActions++;
//		idleTimer.recordActivity();
//	}
//	@Override
//	public void activeFileChanged(EHFileOpenCommand foc) {
//		// TODO Auto-generated method stub
//		
//	}

//	@Override
//	public void documentChanged(EHBaseDocumentChangeEvent docChange) {
//		lastCommandTimestamp = docChange.getTimestamp();
//		numDocChanges++;
//		numActions++;
//
//		idleTimer.recordActivity();
//
//		
//	}
//
//	@Override
//	public void documentChangeFinalized(EHBaseDocumentChangeEvent docChange) {
//		// TODO Auto-generated method stub
//		
//	}

//	@Override
//	public void activeFileChanged(FileOpenCommand arg0) {}
//
//	@Override
//	public void documentChangeAmended(DocChange arg0, DocChange arg1) {}
//
//	@Override
//	public void documentChangeFinalized(DocChange arg0) {}
//
//	@Override
//	public void documentChangeUpdated(DocChange arg0) {}
//
//	@Override
//	public void documentChanged(DocChange arg0) {
//		idleTimer.recordActivity();
//	}
	
	private void sessionEnded(Date startDate, Date endDate) {
//		System.out.println("Session ended");
		ActivitySessionEnded.newCase(this, startTimestamp, lastCommandTimestamp);
		double sessionLength = ((endDate.getTime() - startDate.getTime()) / (60.0 * 1000));
		File sessionFile = new File(System.getProperty("user.home") + "/sessions.txt");
		try {
			sessionFile.createNewFile();
		} catch (Exception ex) {ex.printStackTrace();}
		try {
			PrintWriter output = new PrintWriter(new FileWriter(sessionFile, true));
			output.append("Session start: " + startDate);
			output.append("\nSession end: " + endDate);
			output.append("\nSession duration: " + sessionLength + "\n\n");
			output.close();
		} catch (Exception ex) {ex.printStackTrace();}
		EclipeSessionEnded.newCase(this, startTimestamp, lastCommandTimestamp);
		sendSession(startDate, endDate);
	}
	
//	public void sendSession(Date startDate, Date endDate) {
//		if(ConnectionManager.getInstance() != null) {
//			JSONObject messageData = new JSONObject();
//			try {
//				messageData.put("startDate", startDate.toString());
//				messageData.put("endDate", endDate.toString());
//				JSONArray tags = new JSONArray();
//				tags.put("TIME_TRACKER");
//				messageData.put("tags", tags);
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//			TimeWorkedForwardedToConnectionManager.newCase(this, messageData.toString());
////			JSONObjectForwardedToConnectionManager.newCase(this, messageData.toString());
//			ConnectionManager.getInstance().sendMessage(messageData);
//		}
//	}
	// we wll have to handle this in Hermes as JSON object conflict occurs otherwise
	public void sendSession(Date startDate, Date endDate) {
		Object[][] aPairs = {
				{START_TIME,activityStartTimestamp},
				{END_TIME, activityEndTimestamp},
				{NUM_DOC_CHANGES, numDocChanges},
				{NUM_COMMANDS, numCommands}					
		};
		JSONProxy.sendJSONObject(aPairs, TIME_TRACKER);
		TimeWorkedForwardedToConnectionManager.newCase(this, Arrays.toString(aPairs));
		
	}
//	// we wll have to handle this in Hermes as JSON object conflict occurs otherwise
//		public void sendSession(Date startDate, Date endDate) {
//			if(ConnectionManager.getInstance() != null) {
//				JSONObject messageData = new JSONObject();
//				try {
//					messageData.put(START_TIME, activityStartTimestamp);
//					messageData.put(END_TIME, activityEndTimestamp);
//					messageData.put(NUM_DOC_CHANGES, numDocChanges);
//					messageData.put(NUM_COMMANDS, numCommands);
//					Tags.putTags(messageData, TIME_TRACKER);
////					JSONArray tags = new JSONArray();
////					tags.put(Tags.TIME_TRACKER);
////					tags.put(hermes.tags.Tags.)
////					messageData.put("tags", tags);
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//				TimeWorkedForwardedToConnectionManager.newCase(this, messageData.toString());
////				JSONObjectForwardedToConnectionManager.newCase(this, messageData.toString());
//				ConnectionManager.getInstance().sendMessage(messageData);
//			}
//		}
	
	class IdleTimer implements Runnable {
		
		private boolean inActivitySession;
		private boolean idleLastCycle;

//		protected int idleCycles = 0;
//		protected int activeCycles;
		
		public IdleTimer() {
			inActivitySession = false;
		}
		/**
		 * This thread is constantly polling because a sleeping process cannot be interrupted.
		 * Can have a thread wait on a timeout and be notified with each command, but
		 * is probbaly more context switches than just sleeping for some period.
		 */
		public void run() {
			activityStartTimestamp = startTimestamp + lastCommandTimestamp;
//			System.out.println("Session started");
			ActivitySessionStarted.newCase(this, activityStartTimestamp);
//			Date startDate = new Date(); // make this global also
			 startDate = new Date(); // make this global also

//			Date endDate = new Date(); // in case we get no more commands or we terminate early
//			int idleCycles = 0; // make this a global so eclipse termination can access it
			// make this global also
			endDate = new Date(); // in case we get no more commands or we terminate early
			idleCycles = 0; // make this a global so eclipse termination can access it
			while(idleCycles < IDLE_CYCLES_THRESHOLD) {

//			while(idleCycles < 10) {
				
				idleLastCycle = true;
				try {
//					Thread.sleep(/*60 * */ 1000); // why is 60 removed?
					Thread.sleep(IDLE_CYCLE_GRANULARITY); // why is 60 removed?

				} catch (Exception ex) {}
				if(idleLastCycle) {
					idleCycles++;
					IdleCycleDetected.newCase(this, startTimestamp + lastCommandTimestamp, idleCycles);
				} else {
					endDate = new Date(); // this seems wrong, we have not ended session
					// actually it is correct as this is ths last cycle in which the session was active
//					idleCycles = 0;
					initActivitySession();
				}
			}
//			Date endDate = new Date(); // current time
//
//			inActivitySession = false;
//			sessionEnded(startDate, endDate);
			recordEndSession();
		}
		
		public void recordEndSession() {
//			Date endDate = new Date(); // current time

			inActivitySession = false;
			activityEndTimestamp = startTimestamp + lastCommandTimestamp;
			long ativityDuration = activityEndTimestamp - activityStartTimestamp;
			ActivitySessionEnded.newCase(this, activityEndTimestamp, ativityDuration);
			Date aStartDate = new Date(activityStartTimestamp);
			Date anEndDate = new Date (activityEndTimestamp);
			System.out.println("Start dates:" + aStartDate + "," + startDate);
			System.out.println("End dates:" + anEndDate + "," + endDate);
			sessionEnded(startDate, endDate);
		}
		
		public void recordActivity() {
			if(inActivitySession) {
				idleLastCycle = false;
			} else {
				inActivitySession = true;
				(new Thread(this)).start();
			}
			
			ActivityDetected.newCase(this, startTimestamp + lastCommandTimestamp);

		}
		
	}

	@Override
	public void timestampReset(long aStartTimestamp) {
		// TODO Auto-generated method stub
		
	}



	





	
	
}
