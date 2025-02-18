package analyzer.extension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TimeZone;

import analyzer.AModularAnalyzer;
import analyzer.AParticipantTimeLine;
import analyzer.AResettingTimeStampComputer;
import analyzer.AWebLink;
import analyzer.AnAnalyzer;
import analyzer.Analyzer;
import analyzer.AnalyzerFactories;
import analyzer.ParticipantTimeLine;
import analyzer.TimeStampComputer;
import analyzer.WebLink;
import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.DifficultyRobot;
import difficultyPrediction.extension.APrintingDifficultyPredictionListener;
import difficultyPrediction.featureExtraction.ARatioFeatures;
import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.predictionManagement.DecisionTreeModel;
import difficultyPrediction.predictionManagement.ModularDecisionTreeModel;
import difficultyPrediction.predictionManagement.PredictionManagerFactory;
import difficultyPrediction.predictionManagement.PredictionManagerStrategy;
import difficultyPrediction.predictionManagement.PredictionManagerStrategyFactory;
import difficultyPrediction.statusManager.StatusListener;
import fluorite.commands.DifficultyCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.PredictionCommand;
import fluorite.commands.PredictionType;
import fluorite.commands.Status;
import fluorite.commands.WebVisitCommand;
import fluorite.model.EHEventRecorder;
//import bus.uigen.hermes.HermesObjectEditorProxy;
import util.misc.Common;
import util.trace.difficultyPrediction.analyzer.AnalyzerPredictionStartNotification;
import util.trace.difficultyPrediction.analyzer.AnalyzerPredictionStopNotification;
/**
 * 
 * Serves two purposes: generates ratio files, also called output files.
 * Shows how one can extend the functionality of analyzer by listening to its events
 * Can listen to live events or stored events 
 * Actually not sure it listens to stored events as it creates stored events.
 * It does get web links and other information from ground truth.
 * Have not figured out how web links are being written by the time line, but they do exist
 * in the frozen output files
 *
 */
public class AModularRatioAndPredictionListener extends APrintingDifficultyPredictionListener
		implements RatioFileGenerator, StatusListener {
	Analyzer analyzer;
	protected Map<String, ParticipantTimeLine> participantToTimeLine = new HashMap();

	String currentParticipant;
	protected long currentTime;
	private long startTime;
	


	WebLink lastWebLink;
	Integer lastPrediction = 0;
	Integer lastCorrection = 0;
	protected ParticipantTimeLine participantTimeLine;
	
	public AModularRatioAndPredictionListener(Analyzer a) {
		this.analyzer=a;
		
	}


	private boolean isStuckPointFileGenerated;
	
	public AModularRatioAndPredictionListener() {
//		RatioFilePlayerFactory.getSingleton().addPluginEventEventListener(this);
//		RatioFilePlayerFactory.getSingleton().addRatioFeaturesListener(this);
	}
	
	

	@Override
	public void newParticipant(String anId, String aFolder) {
//		System.out.println("Extension**New Participant:" + anId);
//		participantTimeLine = new AParticipantTimeLine();
		participantTimeLine = AnalyzerFactories.createParticipantTimeLine();
		participantToTimeLine.put(anId, participantTimeLine);
		participantTimeLine.setId(anId);

		currentParticipant = anId;
		this.isStuckPointFileGenerated=false;
		
		
		// strange use of aFolder
		if(aFolder!= null) { // live mode vs non libe mode it seems
			// should this not be in the constructor?
			DifficultyRobot.getInstance().addPluginEventListener(this);
			DifficultyRobot.getInstance().addRatioFeaturesListener(this);
			DifficultyRobot.getInstance().addStatusListener(this);

//			
//			RatioFilePlayerFactory.getSingleton().addPluginEventEventListener(this);
//			RatioFilePlayerFactory.getSingleton().addPluginEventEventListener(this);

		}

	}

	public void emptyTimeLine() {
		this.participantToTimeLine = new HashMap<>();

	}

	@Override
	public void newBrowseLine(String aLine) {
		// System.out.println("Browse line:" + aLine);
		
	}
	
	protected boolean preSaveRatioFile() {
		return (DifficultyPredictionSettings.isNewRatioFiles() || !DifficultyPredictionSettings.isRatioFileExists())
				&& DifficultyPredictionSettings.shouldCreateRatioFiles();
	}
	
	protected String workspaceMetrcsFileName() {
		return DifficultyPredictionSettings.getRatiosFileName();
	}
	public void saveRatioFile(String aFileName) {
		// method here
				addStuckData(participantTimeLine);

				StringBuffer timeLineText = participantTimeLine.toText();

				try {
//					if(DifficultyPredictionSettings.shouldCreateRatioFiles())
						Common.writeText(aFileName, timeLineText.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	protected void saveWorkspaceRatios() {
		String aFileName = workspaceMetrcsFileName();
		saveRatioFile(aFileName);
		
	}
	protected void doSaveRatios() {
		saveWorkspaceRatios();

	}
	
	public void saveRatioFile() {
//		if (!DifficultyPredictionSettings.isNewRatioFiles()
//				&& DifficultyPredictionSettings.isRatioFileExists())
//			return;
		if (!preSaveRatioFile())
			return;
		doSaveRatios();
//		String aFileName = DifficultyPredictionSettings.getRatiosFileName();
//		String aFileName = workspaceRatioFileName();
//		saveRatioFile(aFileName);

//
//		// method here
//		addStuckData(participantTimeLine);
//
//		StringBuffer timeLineText = participantTimeLine.toText();
//
//		try {
//			if(DifficultyPredictionSettings.shouldCreateRatioFiles())
//				Common.writeText(aFileName, timeLineText.toString());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
////		System.out.println("e");
	}

	// this does not get called actuall
	@Override
	public void finishedBrowserLines() {
		saveRatioFile(); // no op, does not get called
		
		
		
		// this is the original code
//		if (!DifficultyPredictionSettings.isNewRatioFiles()
//				&& DifficultyPredictionSettings.isRatioFileExists())
//			return;
//		String aFileName = DifficultyPredictionSettings.getRatiosFileName();
//
//		// method here
//		addStuckData(participantTimeLine);
//
//		StringBuffer timeLineText = participantTimeLine.toText();
//
//		try {
//			if(DifficultyPredictionSettings.shouldCreateRatioFiles())
//				Common.writeText(aFileName, timeLineText.toString());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
////		System.out.println("e");

	}
	
	// stuff below seems to be added by Kevin

	// Add in the stuck interval and the stuck point data
	public void addStuckData(ParticipantTimeLine l) {
//		l.setAggregateStatistics();

		String participant = this.currentParticipant;

		Queue<StuckInterval> interval = new PriorityQueue<>();
		Queue<StuckPoint> point = new PriorityQueue<>();

		// COpy everything from the queue so we can have something to work with
		// without changing the original. Which should be immutable

		if (analyzer.getStuckIntervalMap().get(participant) != null) {
			for (StuckInterval i : analyzer.getStuckIntervalMap().get(
					participant)) {
//				Date aDate = i.getDate();
//				int anHour = aDate.getHours();
//				int aMInute = aDate.getMinutes();
				
				interval.add(i);

			}

			fillPoints(l, interval);

		}

		if (analyzer.getStuckPointMap().get(participant) != null) {
			for (StuckPoint i : analyzer.getStuckPointMap().get(participant)) {
				point.add(i);

			}

			fillPoints(l, point);

		}
		// pad with singletons

		int diff = l.getDebugList().size() - l.getStuckInterval().size();

		if (diff > 0) {
			StuckInterval[] stuckinterval_filler = new StuckInterval[Math
					.abs(diff)];
			Arrays.fill(stuckinterval_filler, null);

			l.getStuckInterval().addAll(Arrays.asList(stuckinterval_filler));

			// trim out the excess nulls
		} else {
			for (int i = 0; i < Math.abs(diff); i++)
				l.getStuckInterval().remove(l.getStuckInterval().size() - 1);

		}

		diff = l.getDebugList().size() - l.getStuckPoint().size();

		if (diff > 0) {
			StuckPoint[] stuckpoint_filler = new StuckPoint[Math.abs(diff)];
			Arrays.fill(stuckpoint_filler, null);

			l.getStuckPoint().addAll(Arrays.asList(stuckpoint_filler));

			// trim out the excess nulls
		} else {
			for (int i = 0; i < Math.abs(diff); i++)
				l.getStuckPoint().remove(l.getStuckPoint().size() - 1);

		}

	}
	protected void addFillDataToStuckPointsAndIntervals(ParticipantTimeLine l) {
		
		int aNumEntries = l.getNumElements();
		if (l.getStuckPoint().size() > 0) {
			return;
		}
			StuckInterval[] stuckinterval_filler = new StuckInterval[aNumEntries];
			Arrays.fill(stuckinterval_filler, null);

			l.getStuckInterval().addAll(Arrays.asList(stuckinterval_filler));
			StuckPoint[] stuckpoint_filler = new StuckPoint[aNumEntries];
			Arrays.fill(stuckpoint_filler, null);

			l.getStuckPoint().addAll(Arrays.asList(stuckpoint_filler));

	

		
	}

	/**
	 * Insert the Stuckpoint/stuckinterval from the priority queue q into the
	 * timeline l
	 * 
	 * @param l
	 * @param q
	 */
	private void fillPoints(ParticipantTimeLine l, Queue<? extends Comparable> q) {
		addFillDataToStuckPointsAndIntervals(l);
		List<Long> times = l.getTimeStampList();
		Class<?> c = q.peek() != null ? q.peek().getClass() : null;

		int diff_interval = Integer.MAX_VALUE;
		long aLastTime = 0;
		for (int i = 0; i < times.size() && !q.isEmpty(); i++) {
		
			long time = times.get(i);


//			long intervalTime = getTimeValueMilli((q.peek() instanceof StuckPoint) ? ((StuckPoint) q
//					.peek()).getDate().getTime() : ((StuckInterval) q.peek())
//					.getDate().getTime());
			long intervalTime = normalizeTime(l, (q.peek() instanceof StuckPoint) ? ((StuckPoint) q
					.peek()).getDate().getTime() : ((StuckInterval) q.peek())
					.getDate().getTime());
			
			if (aLastTime == 0) {
				aLastTime = time;
				continue;
			}
			if (intervalTime >= aLastTime && intervalTime < time ) {
				Comparable o = q.poll();
				if (o instanceof StuckPoint) {
					l.getStuckPoint().set(i-1, (StuckPoint) o);
				}
				if (o instanceof StuckInterval) {
					l.getStuckInterval().set(i-1, (StuckInterval) o);
				}
			}
			aLastTime = time;

		}

	}
	
	private void fillPointsKevin(ParticipantTimeLine l, Queue<? extends Comparable> q) {

		List<Long> times = l.getTimeStampList();
		Class<?> c = q.peek() != null ? q.peek().getClass() : null;

		int diff_interval = Integer.MAX_VALUE;
		for (int i = 0; i < times.size() && !q.isEmpty(); i++) {
			long time = getTimeValueMilli(times.get(i));
//			long time = getHoursMinutesSeconds(times.get(i));


			long intervalTime = getTimeValueMilli((q.peek() instanceof StuckPoint) ? ((StuckPoint) q
					.peek()).getDate().getTime() : ((StuckInterval) q.peek())
					.getDate().getTime());
//			long intervalTime = normalizeTime(l, (q.peek() instanceof StuckPoint) ? ((StuckPoint) q
//					.peek()).getDate().getTime() : ((StuckInterval) q.peek())
//					.getDate().getTime());

			int diff = (int) Math.abs(intervalTime - time);

			Object o = null;
			// Last one was the best fit or a perfect fit
			if (diff >= diff_interval) {
				o = q.poll();

				// best match was last
				if (diff > diff_interval)
					i--;

				// reset interval
				diff_interval = Integer.MAX_VALUE;
				// keep going better fit (diff<diff_interval)
			} else {
				diff_interval = diff;

			}

			if (c.equals(AStuckPoint.class) || c.equals(StuckPoint.class)) {
				l.getStuckPoint().add(i, o == null ? null : (StuckPoint) o);

			} else {
				l.getStuckInterval().add(i,
						o == null ? null : (StuckInterval) o);

			}

		}

	}
	
	private long normalizeTime(ParticipantTimeLine aTimestamp, long milli) {
		Date aDate = new Date(milli);
		int anYear = aDate.getYear();
		if (anYear != 70) {
			return milli;
		}

			Date aStartDate = new Date (aTimestamp.getStartTimestamp());
			int aMonth = aStartDate.getMonth();
			int aDay = aStartDate.getDate();
			anYear = aStartDate.getYear();
		
		int anHours = aDate.getHours();
		int aMinutes = aDate.getMinutes();
		int aSeconds = aDate.getSeconds();
		
		
		
		Date aReturnDate = new Date(anYear, aMonth, aDay, anHours, aMinutes, aSeconds);
		return aReturnDate.getTime();
		

	}

	/**
	 * Converts milliseconds since epoch time to a long that is a combination of
	 * hours, seconds, minutes only
	 * <p>
	 * In other words, milliseconds since the start of the day(hour, minutes,
	 * seconds only).
	 */
	private long getTimeValueMilli(long milli) {
		// a day is 86400000 milliseconds. NO daylight saving, if in daylight
		// saving, then move an hour ahead
		return milli
				% (60 * 60 * 1000 * 24)
				+ (TimeZone.getDefault().inDaylightTime(new Date(milli)) ? 3600000
						: 0);

	}

	@Override
	public void newBrowseEntries(Date aDate, String aSearchString, String aURL) {
		// System.out.println("Browse Date:" + aDate);
		// System.out.println("Search string:" + aSearchString);
		// System.out.println("Search string:" + aURL);
		lastWebLink = new AWebLink(aSearchString, aURL);
		long aTimeStamp = aDate.getTime();
		int anIndex = participantTimeLine.getIndexBefore(aTimeStamp);
		if (anIndex != -1) {
			List<WebLink> aLinks = participantTimeLine.getWebLinks().get(
					anIndex);
			if (aLinks == null) {
				aLinks = new ArrayList();
				participantTimeLine.getWebLinks().set(anIndex, aLinks);

			}
			aLinks.add(lastWebLink);
		}
		// participantTimeLine.getWebLinks().add(lastWebLink);
		lastWebLink = null;
	}
	
	public static int toInt(PredictionType aPredictionType) {
		switch (aPredictionType) {
		
//		case Indeterminate: return -1;
//		case MakingProgress: return 0;
//		case HavingDifficulty: return 1;
		
		case Indeterminate: return ParticipantTimeLine.INDTERMINATE_INT;
		case MakingProgress: return ParticipantTimeLine.PROGRESS_INT;
		case HavingDifficulty: return ParticipantTimeLine.DIFFICULTY_INT;
		default: return ParticipantTimeLine.INDTERMINATE_INT;
		}
		
	}
	public static int toInt(Status aStatus) {
		if (aStatus == null) {
			return ParticipantTimeLine.INDTERMINATE_INT;
		}
		switch (aStatus) {
		
//		case Indeterminate: return -1;
//		case MakingProgress: return 0;
//		case HavingDifficulty: return 1;
		
		case Making_Progress: return ParticipantTimeLine.PROGRESS_INT;
		case Surmountable: return ParticipantTimeLine.SURMOUNTABLE_INT;
		case Insurmountable: return ParticipantTimeLine.INSURMOUNTABLE_INT;
		default: return ParticipantTimeLine.INDTERMINATE_INT;
		}
		
	}

	public static int toInt(PredictionCommand aCommand) {
		if (aCommand.getPredictionType() == null) {
			return ParticipantTimeLine.INDTERMINATE_INT;
		}
		return toInt(aCommand.getPredictionType());
//		switch (aCommand.getPredictionType()) {
//		
////		case Indeterminate: return -1;
////		case MakingProgress: return 0;
////		case HavingDifficulty: return 1;
//		
//		case Indeterminate: return ParticipantTimeLine.INDTERMINATE_INT;
//		case MakingProgress: return ParticipantTimeLine.PROGRESS_INT;
//		case HavingDifficulty: return ParticipantTimeLine.DIFFICULTY_INT;
//		}
//		return -1;
	}

	public static int toInt(DifficultyCommand aCommand) {
		if (aCommand.getStatus() == null)
			return -1;
		switch (aCommand.getStatus()) {
		
//		case Insurmountable:return 1;
//		case Surmountable: return 1;
//		case Making_Progress: return 0;
		
		case Insurmountable:return ParticipantTimeLine.INSURMOUNTABLE_INT;
		case Surmountable: return ParticipantTimeLine.SURMOUNTABLE_INT;
		case Making_Progress: return ParticipantTimeLine.PROGRESS_INT;

		}
		return -1;
	}

	void maybeInitializeTimeStamp(EHICommand newCommand) {
		if (newCommand.getTimestamp() == 0 && newCommand.getTimestamp2() != 0) {
			newStartTimeStamp(newCommand.getTimestamp2());
		}
	}

	void maybeProcessPrediction(EHICommand newCommand) {
		if (newCommand instanceof PredictionCommand) {
			lastPrediction = toInt((PredictionCommand) newCommand);
		}
	}

	void maybeProcessCorrection(EHICommand newCommand) {
		if (newCommand instanceof DifficultyCommand) { // what if status is null?
			lastCorrection = toInt((DifficultyCommand) newCommand);
		}
	}
	long lastTime;
	@Override
	public void newCommand(EHICommand newCommand) {
		maybeInitializeTimeStamp(newCommand);
		maybeProcessPrediction(newCommand);
		maybeProcessCorrection(newCommand);
		lastTime = startTime + newCommand.getTimestamp();
		// if (newCommand.getTimestamp() == 0 && newCommand.getTimestamp2() !=
		// 0) {
		// newStartTimeStamp(newCommand.getTimestamp2() );
		// }
		//System.out.println("Extension**New User/Prediction Command:"
				//+ newCommand);

	}

	@Override
	public void commandProcessingStarted() {
		AnalyzerPredictionStartNotification.newCase(this);
//		System.out.println("Extension**Difficulty Prediction Started");
	}

	@Override
	public void commandProcessingStopped() {
//		System.out.println("Extension**Difficulty Prediction Stopped");
		AnalyzerPredictionStopNotification.newCase(this);
		insertEntriesForPreviousTimeStamp();
	}

	boolean isBehindTimeStamp(List aList) {
		return (aList.size() < participantTimeLine.getTimeStampList().size());

	}

	// void maybeAddNullWebLink() {
	// if (isBehindTimeStamp(participantTimeLine.getWebLinks())) {
	// participantTimeLine.getWebLinks().add(null);
	// }
	// }
	protected void insertEntriesForPreviousTimeStamp() {
		if (participantTimeLine.getTimeStampList().size() == 0) // no previous
																// time stamp
			return;
		participantTimeLine.getPredictions().add(lastPrediction); // do not
																	// reset it
																	// as it is
																	// a status
		participantTimeLine.getPredictionCorrections().add(lastCorrection);
		lastCorrection = -1; // reset it as this is an event rather than a
								// status
	}

	@Override
	public void newRatios(RatioFeatures newVal) {

		insertEntriesForPreviousTimeStamp();
		currentTime = getStartTime() + newVal.getSavedTimeStamp();
		participantTimeLine.getEditList().add(newVal.getEditRatio());
		participantTimeLine.getTimeStampList().add(currentTime);
		participantTimeLine.getDebugList().add(newVal.getDebugRatio());
		participantTimeLine.getDeletionList().add(newVal.getDeletionRatio());
		participantTimeLine.getFocusList().add(newVal.getFocusRatio());
		participantTimeLine.getInsertionList().add(newVal.getInsertionRatio());
		participantTimeLine.getNavigationList()
				.add(newVal.getNavigationRatio());
		participantTimeLine.getRemoveList().add(newVal.getRemoveRatio());
		participantTimeLine.getWebLinks().add(null);
		System.out.println("New ratios " + lastTime  + " " + AResettingTimeStampComputer.toDateString(lastTime) + currentTime + " " + AResettingTimeStampComputer.toDateString(currentTime));
		
	}

	public void startTimestamp(long aStartTimeStamp) {
		//System.out.println("start time stamp:" + aStartTimeStamp);
		EHEventRecorder.getInstance().setStartTimeStamp(aStartTimeStamp);

		// currentTime = aStartTimeStamp;
		// RatioFeatures aRatioFetaures = new ARatioFeatures();
		// newRatios(aRatioFetaures);

	}

	// this seems to be called in addition to the previous pne
	void newStartTimeStamp(long aStartTimeStamp) {
		EHEventRecorder.getInstance().setStartTimeStamp(aStartTimeStamp);
		// System.out.println("Extension**Difficulty Prediction Started");
//		startTime = aStartTimeStamp;
		setStartTime(aStartTimeStamp);
		// System.out.println("New time stamp: " + startTime );
		// System.out.println ("New date:" + new Date(startTime));

		RatioFeatures aRatioFetaures = new ARatioFeatures();
		aRatioFetaures.setSavedTimeStamp(0);
		System.out.println("new time stamp:" + aStartTimeStamp);
		newRatios(aRatioFetaures);

	}

	
	// dual of newParticipant
	@Override
	public void finishParticipant(String anId, String aFolder) {
		// TODO Auto-generated method stub
		saveRatioFile();


	}
	
	@Override
	public ParticipantTimeLine getParticipantTimeLine() {
		return participantTimeLine;
	}

//	@Override
//	public void newCorrectStatus(int aStatus) {
//		// TODO Auto-generated method stub
//		
//	}

	
	

	@Override
	public void newCorrectStatus(DifficultyCommand newCommand, Status aStatus, long aStartRelativeTime, long aDuration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newPrediction(PredictionCommand newParam, PredictionType aPredictionType, long aStartRelativeTime, long aDuration) {
		
		System.out.println (AResettingTimeStampComputer.toDateString(aStartRelativeTime) + " " +aStartRelativeTime + " new stored prediction:" + aPredictionType);
	}

	@Override
	public void newStoredCommand(EHICommand aNewCommand, long aStartRelativeTime, long aDuration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newStoredInputCommand(EHICommand aNewCommand, long aStartRelativeTime, long aDuration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newWebVisit(WebVisitCommand aWebVisitCommand, long aStartAbsoluteTime, long aDuration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newBrowserCommands(List<WebVisitCommand> aCommands) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void experimentStartTimestamp(long aStartTimeStamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replayFinished() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replayStarted() {
		// TODO Auto-generated method stub
		
	}
	public long getStartTime() {
		return startTime;
	}



	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public static void main(String[] args) {
		 RatioFileGenerator analyzerProcessor; // why is this static

		 DifficultyPredictionSettings.setReplayMode(true);
		//
//		 Analyzer analyzer = new AnAnalyzer();
		 Analyzer analyzer = new AModularAnalyzer();

//		 analyzerProcessor = new ARatioFileGenerator();
		 analyzerProcessor = new AModularRatioAndPredictionListener(analyzer);
		 
		  PredictionManagerStrategy predictionManagerStrategy = 
			       new ModularDecisionTreeModel();

		  PredictionManagerStrategyFactory.setPredictionManagerStrategy(predictionManagerStrategy);
		 analyzer.addAnalyzerListener(analyzerProcessor);
//		 HermesObjectEditorProxy.edit(analyzer);
		 
		 // if we want to process all logs interactively
//		 OEFrame frame = ObjectEditor.edit(analyzer);
		 
		 // if we want to programmaticall choose one
			analyzer.loadDirectory();
			analyzer.getAnalyzerParameters().getParticipants().setValue("17");
//			analyzer.addAnalyzerListener(analyzerProcessor);
			analyzer.getAnalyzerParameters().replayLogs();
			analyzer.getAnalyzerParameters().setMakePredictions(true);
			
			analyzer.getAnalyzerParameters().setNewOutputFiles(true);

		// frame.setSize(550, 275);
		//
		// JFrame qframe=new JFrame("Query V1.0");
		// qframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// qframe.setResizable(false);
		// qframe.setLocationRelativeTo(null);
		//
		// qframe.add(new AQueryUI(((AnAnalyzerProcessor)
		// analyzerProcessor).participantToTimeLine));
		// qframe.pack();
		// qframe.setVisible(true);

		/*
		AnAnalyzerProcessor a = new AnAnalyzerProcessor();

		System.out.println(a.getTimeValueMilli(new Date().getTime()));
		try {
			System.out.println(new SimpleDateFormat("HH:mm:ss")
					.parse("12:21:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	*/
	}



	@Override
	public void modelBuilt(boolean newVal, Exception e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void predictionError(Exception e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void newStatus(String aStatus) {
		System.out.println(AResettingTimeStampComputer.toDateString(lastTime) + " " + lastTime + "  new caclulated status:" + aStatus);
	}



	@Override
	public void newAggregatedStatus(String aStatus) {
		System.out.println(AResettingTimeStampComputer.toDateString(lastTime) + " " + lastTime + "  new calculated aggregated Status:" + aStatus);

	}



	@Override
	public void newStatus(int aStatus) {
		System.out.println(AResettingTimeStampComputer.toDateString(lastTime) + " " + lastTime + "  new caclulated int status:" + aStatus);

		
	}



	@Override
	public void newAggregatedStatus(int aStatus) {
		System.out.println(AResettingTimeStampComputer.toDateString(lastTime) + " " + lastTime + "  new calculated int aggregated Status:" + aStatus);
		
	}



	@Override
	public void newManualStatus(String aStatus) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void newManualStatus(DifficultyCommand aCommand) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void newReplayedStatus(int aStatus) {
		// TODO Auto-generated method stub
		
	}
}
