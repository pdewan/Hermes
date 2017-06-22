package analyzer.extension;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TimeZone;

import util.misc.Common;
import analyzer.AParticipantTimeLine;
import analyzer.AWebLink;
import analyzer.Analyzer;
import analyzer.ParticipantTimeLine;
import analyzer.RatioFilePlayerFactory;
import analyzer.WebLink;
import analyzer.ui.graphics.RatioFileReader;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.DifficultyRobot;
import difficultyPrediction.extension.APrintingDifficultyPredictionListener;
import difficultyPrediction.featureExtraction.ARatioFeatures;
import difficultyPrediction.featureExtraction.RatioFeatures;
import fluorite.commands.DifficulyStatusCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.PredictionCommand;
import fluorite.model.EHEventRecorder;

public class CopyOfAnAnalyzerProcessor extends APrintingDifficultyPredictionListener implements AnalyzerProcessor{
	static Analyzer analyzer;
	static AnalyzerProcessor analyzerProcessor;
	protected Map<String, ParticipantTimeLine> participantToTimeLine = new HashMap();

	String currentParticipant;
	long currentTime;
	long startTime;
	WebLink lastWebLink;
	Integer lastPrediction = 0;
	Integer lastCorrection = 0;
	ParticipantTimeLine participantTimeLine;

	private boolean isStuckPointFileGenerated;
	
	public CopyOfAnAnalyzerProcessor() {
//		RatioFilePlayerFactory.getSingleton().addPluginEventEventListener(this);
//		RatioFilePlayerFactory.getSingleton().addRatioFeaturesListener(this);
	}

	@Override
	public void newParticipant(String anId, String aFolder) {
		System.out.println("Extension**New Participant:" + anId);
		participantTimeLine = new AParticipantTimeLine();
		participantToTimeLine.put(anId, participantTimeLine );

		currentParticipant=anId;
		this.isStuckPointFileGenerated=false;
		
		
		
		if(aFolder!= null) {
			// should this not be in the constructor?
			DifficultyRobot.getInstance().addPluginEventListener(this);
			DifficultyRobot.getInstance().addRatioFeaturesListener(this);
//			
//			RatioFilePlayerFactory.getSingleton().addPluginEventEventListener(this);
//			RatioFilePlayerFactory.getSingleton().addPluginEventEventListener(this);

		}

	}



	public void emptyTimeLine() {
		this.participantToTimeLine=new HashMap<>();

	}

	@Override
	public void newBrowseLine(String aLine) {
		//System.out.println("Browse line:" + aLine);

	}
	@Override
	public void finishedBrowserLines() {
		if (!DifficultyPredictionSettings.isNewRatioFiles() && DifficultyPredictionSettings.isRatioFileExists())
			return;
		String aFileName = DifficultyPredictionSettings.getRatiosFileName();

		//method here
		addStuckData(participantTimeLine);

		StringBuffer timeLineText = participantTimeLine.toText(); 

		try {
			Common.writeText(aFileName, timeLineText.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//Add in the stuck interval and the stuck point data
	public void addStuckData(ParticipantTimeLine l) {
		
		String participant=this.currentParticipant;

		Queue<StuckInterval> interval=new PriorityQueue<>();
		Queue<StuckPoint> point=new PriorityQueue<>();
		
		//COpy everything from the queue so we can have something to work with without changing the original. Which should be immutable
		
		if(analyzer.getStuckIntervalMap().get(participant) != null) {
			for(StuckInterval i:analyzer.getStuckIntervalMap().get(participant)) {
				interval.add(i);
				
			}
			
			fillPoints(l,interval);

			
		}

		if(analyzer.getStuckPointMap().get(participant) != null) {
			for(StuckPoint i:analyzer.getStuckPointMap().get(participant)) {
				point.add(i);
				
			}
			
			fillPoints(l,point);

			
		}
		//pad with singletons
		
		int diff=l.getDebugList().size()-l.getStuckInterval().size();
		
		
		if(diff > 0) {
			StuckInterval[] stuckinterval_filler=new StuckInterval[Math.abs(diff)];
			Arrays.fill(stuckinterval_filler, null);
			
			l.getStuckInterval().addAll(Arrays.asList(stuckinterval_filler));
			
			//trim out the excess nulls
		} else{
			for(int i=0;i<Math.abs(diff);i++)
				l.getStuckInterval().remove(l.getStuckInterval().size()-1);
			
		}
		
		diff=l.getDebugList().size()-l.getStuckPoint().size();
		
		
		
		
		if(diff > 0) {
			StuckPoint[] stuckpoint_filler=new StuckPoint[Math.abs(diff)];
			Arrays.fill(stuckpoint_filler, null);
			
			l.getStuckPoint().addAll(Arrays.asList(stuckpoint_filler));
			
			//trim out the excess nulls
		} else {
			for(int i=0;i<Math.abs(diff);i++)
				l.getStuckPoint().remove(l.getStuckPoint().size()-1);
			
		}
		
	}

	/**Insert the Stuckpoint/stuckinterval from the priority queue q into the timeline l
	 * 
	 * @param l
	 * @param q
	 */
	private void fillPoints(ParticipantTimeLine l, Queue<? extends Comparable> q) {

		List<Long> times=l.getTimeStampList();
		Class<?> c=q.peek() != null? q.peek().getClass():null;

		int diff_interval=Integer.MAX_VALUE;
		for (int i=0;i<times.size() && !q.isEmpty();i++) {
			long time=getTimeValueMilli(times.get(i));

			long intervalTime=getTimeValueMilli((q.peek() instanceof StuckPoint)?
					((StuckPoint) q.peek()).getDate().getTime()
					:((StuckInterval)q.peek()).getDate().getTime());

//			System.out.println("---------");
//			System.out.println("Milliseconds: "+time);
//			System.out.println("Interval millisec: "+intervalTime);
//			System.out.println("Acutal time: "+new Date(times.get(i)));
//			System.out.println("Time: "+new Date(time));
//			System.out.println("IntervalTime" +new Date(intervalTime));

			int diff=(int) Math.abs(intervalTime-time);

			Object o=null;
			//Last one was the best fit or a perfect fit
			if(diff>=diff_interval) {
				o=q.poll();

				//best match was last
				if(diff>diff_interval)
					i--;

				//reset interval
				diff_interval=Integer.MAX_VALUE;
				//keep going better fit (diff<diff_interval)
			} else {
				diff_interval=diff;

			} 

			if(c.equals(AStuckPoint.class) || c.equals(StuckPoint.class)) {
				l.getStuckPoint().add(i,o==null? null:(StuckPoint) o);

			} else {
				l.getStuckInterval().add(i,o==null? null:(StuckInterval) o);

			}

		}
		
	}

	/** Converts milliseconds since epoch time to a long that is a combination of hours, seconds, minutes only
	 * <p>
	 * In other words, milliseconds since the start of the day(hour, minutes, seconds only).
	 */
	private long getTimeValueMilli(long milli) {
		//a day is 86400000 milliseconds. NO daylight saving, if in daylight saving, then move an hour ahead
		return milli%(60*60*1000*24)
				+(TimeZone.getDefault().inDaylightTime(new Date(milli))? 3600000:0);

	}

	@Override
	public void newBrowseEntries(Date aDate, String aSearchString, String aURL) {
		//		System.out.println("Browse Date:" + aDate);
		//		System.out.println("Search string:" + aSearchString);
		//		System.out.println("Search string:" + aURL);
		lastWebLink = new AWebLink(aSearchString, aURL);
		long aTimeStamp = aDate.getTime();
		int anIndex = participantTimeLine.getIndexBefore(aTimeStamp);
		if (anIndex != -1) {
			List<WebLink> aLinks = participantTimeLine.getWebLinks().get(anIndex);
			if (aLinks == null) {
				aLinks = new ArrayList();
				participantTimeLine.getWebLinks().set(anIndex, aLinks);

			}
			aLinks.add(lastWebLink);
		}
		//		participantTimeLine.getWebLinks().add(lastWebLink);
		lastWebLink = null;
	}
	int toInt(PredictionCommand aCommand) {
		if (aCommand.getPredictionType() == null) {
			return -1;
		}
		switch (aCommand.getPredictionType()) {
		
//		case Indeterminate: return -1;
//		case MakingProgress: return 0;
//		case HavingDifficulty: return 1;
		
		case Indeterminate: return ParticipantTimeLine.INDTERMINATE_INT;
		case MakingProgress: return ParticipantTimeLine.PROGRESS_INT;
		case HavingDifficulty: return ParticipantTimeLine.DIFFICULTY_INT;
		}
		return -1;
	}
	int toInt(DifficulyStatusCommand aCommand) {
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
			newStartTimeStamp(newCommand.getTimestamp2() );
		}
	}
	void maybeProcessPrediction(EHICommand newCommand) {
		if (newCommand instanceof PredictionCommand) {
			lastPrediction = toInt((PredictionCommand) newCommand);
		}
	}
	void maybeProcessCorrection(EHICommand newCommand) {
		if (newCommand instanceof DifficulyStatusCommand) {
			lastCorrection = toInt((DifficulyStatusCommand) newCommand);
		}
	}
	@Override
	public void newCommand(EHICommand newCommand) {
		maybeInitializeTimeStamp(newCommand);
		maybeProcessPrediction(newCommand);
		maybeProcessCorrection(newCommand);
		//		if (newCommand.getTimestamp() == 0 && newCommand.getTimestamp2() != 0) {
		//			newStartTimeStamp(newCommand.getTimestamp2() );
		//		}
		System.out.println("Extension**New User/Prediction Command:" + newCommand);	

	}
	@Override
	public void commandProcessingStarted() {
		System.out.println("Extension**Difficulty Prediction Started");		
	}
	@Override
	public void commandProcessingStopped() {
		System.out.println("Extension**Difficulty Prediction Stopped");		
		insertEntriesForPreviousTimeStamp();
	}
	boolean isBehindTimeStamp(List aList) {
		return (aList.size() < participantTimeLine.getTimeStampList().size()) ;

	}
	//	void maybeAddNullWebLink() {
	//		if (isBehindTimeStamp(participantTimeLine.getWebLinks())) {
	//			participantTimeLine.getWebLinks().add(null);
	//		}
	//	}
	void insertEntriesForPreviousTimeStamp() {
		if (participantTimeLine.getTimeStampList().size() == 0) // no previous time stamp
			return;
		participantTimeLine.getPredictions().add(lastPrediction); // do not reset it as it is a status
		participantTimeLine.getPredictionCorrections().add(lastCorrection);
		lastCorrection = -1; // reset it as this is an event rather than a status
	}
	@Override
	public void newRatios(RatioFeatures newVal) {

		insertEntriesForPreviousTimeStamp();
		currentTime = startTime + newVal.getSavedTimeStamp();
		participantTimeLine.getEditList().add(newVal.getEditRatio());
		participantTimeLine.getTimeStampList().add(currentTime);
		participantTimeLine.getDebugList().add(newVal.getDebugRatio());
		participantTimeLine.getDeletionList().add(newVal.getDeletionRatio());
		participantTimeLine.getFocusList().add(newVal.getFocusRatio());
		participantTimeLine.getInsertionList().add(newVal.getInsertionRatio());
		participantTimeLine.getNavigationList().add(newVal.getNavigationRatio());
		participantTimeLine.getRemoveList().add(newVal.getRemoveRatio());
		participantTimeLine.getWebLinks().add(null);
		System.err.println("Extension**New Ratios:" + newVal + " at time:" + (new Date(currentTime)).toString());		
	}
	public void startTimeStamp(long aStartTimeStamp) {
		System.out.println("start time stamp:" + aStartTimeStamp);	
		EHEventRecorder.getInstance().setStartTimeStamp(aStartTimeStamp);

		//		currentTime = aStartTimeStamp;
		//		RatioFeatures aRatioFetaures = new ARatioFeatures();
		//		newRatios(aRatioFetaures);

	}
	// this seems to be called in addition to the previous pne
	void newStartTimeStamp(long aStartTimeStamp) {
		EHEventRecorder.getInstance().setStartTimeStamp(aStartTimeStamp);
		//		System.out.println("Extension**Difficulty Prediction Started");	
		startTime = aStartTimeStamp;
		//System.out.println("New time stamp: " + startTime );
		//System.out.println ("New date:" + new Date(startTime));

		RatioFeatures aRatioFetaures = new ARatioFeatures();
		newRatios(aRatioFetaures);

	}
	public static void main (String[] args) {
		//		DifficultyPredictionSettings.setReplayMode(true);
		//
		//		analyzer = new AnAnalyzer();
		//		analyzerProcessor = new AnAnalyzerProcessor();
		//		analyzer.addAnalyzerListener(analyzerProcessor);
		//		OEFrame frame = ObjectEditor.edit(analyzer);
		//		frame.setSize(550, 275);
		//
		//		JFrame qframe=new JFrame("Query V1.0");
		//		qframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//		qframe.setResizable(false);
		//		qframe.setLocationRelativeTo(null);
		//
		//		qframe.add(new AQueryUI(((AnAnalyzerProcessor) analyzerProcessor).participantToTimeLine));
		//		qframe.pack();
		//		qframe.setVisible(true);

		CopyOfAnAnalyzerProcessor a=new CopyOfAnAnalyzerProcessor();

		System.out.println(a.getTimeValueMilli(new Date().getTime()));
		try {
			System.out.println(new SimpleDateFormat("HH:mm:ss").parse("12:21:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void finishParticipant(String anId, String aFolder) {
		// TODO Auto-generated method stub


	}
	@Override
	public ParticipantTimeLine getParticipantTimeLine() {
		return participantTimeLine;
	}

	@Override
	public void newCorrectStatus(int aStatus) {
		// TODO Auto-generated method stub
		
	}

}
