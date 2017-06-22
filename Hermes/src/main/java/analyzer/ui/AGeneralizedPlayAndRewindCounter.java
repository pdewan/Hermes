package analyzer.ui;

import util.annotations.Column;
import util.annotations.ComponentWidth;
import util.annotations.Label;
import util.annotations.Row;
import util.annotations.Visible;
import analyzer.ATimeStampComputer;
import analyzer.ParticipantTimeLine;
import analyzer.extension.AnalyzerProcessorFactory;
import analyzer.extension.LiveAnalyzerProcessorFactory;
import analyzer.ui.graphics.APlayAndRewindCounter;
import analyzer.ui.graphics.ARatioFileReader;
import analyzer.ui.graphics.RatioFileReader;
import bus.uigen.ObjectEditor;

public class AGeneralizedPlayAndRewindCounter extends APlayAndRewindCounter implements GeneralizedPlayAndRewindCounter {

    boolean playBack;
    int nextFeatureIndex;
//    ParticipantTimeLine liveParticipantTimeLine;
    long absoluteStartTime;
    @Override
    @Visible(false)
	public long getAbsoluteStartTime() {
		return absoluteStartTime;
	}

	public AGeneralizedPlayAndRewindCounter(RatioFileReader reader) {
		super(reader);
		absoluteStartTime = System.currentTimeMillis();	
//		liveParticipantTimeLine = LiveAnalyzerProcessorFactory.getSingleton().getParticipantTimeLine();
		
	}
	
	public void setPlayBack(boolean newValue) {
		playBack = newValue;
	}

	public AGeneralizedPlayAndRewindCounter() {
		this (new ARatioFileReader());
	}
	@Override
	@Row(0)
	@Column(0)
	@ComponentWidth(100)
	public void back() {
//		playBack = true;
		setPlayBack(true);
		super.back();		
	}
	@Override
	@Row(0)
	@Column(4)
	@ComponentWidth(100)
	public void forward() {
//		playBack = true;
		setPlayBack(true);

		super.forward();		
	}

	@Row(1)
	@Column(2)
	@ComponentWidth(100)
	public void live() {		
		end(); //play back all past events
//		playBack = false;
//		setPlayBack()
		setPlayBack(false);

	}
	@Row(1)
	@Column(0)
	@ComponentWidth(100)
	public void start() {
		playBack = true;
		setCurrentFeatureIndex(0);
	}
	@Row(1)
	@Column(1)
	@ComponentWidth(100)
	public void end() {
//		playBack = true;
		setPlayBack(true);

		setCurrentFeatureIndex(nextFeatureIndex - 1);
	}
	int previousPredictedDifficulty;
	public boolean prePreviousPredictedDifficulty() {
		previousPredictedDifficulty = AnalyzerProcessorFactory.getSingleton().getParticipantTimeLine().getDifficultyPredictionBefore(getCurrentTime());
		return previousPredictedDifficulty >= 0;
	}
	@Row(1)
	@Column(3)
	@ComponentWidth(150)
	public void previousPredictedDifficulty() {
		if (!prePreviousPredictedDifficulty())
			return;
		playBack = true;
		setCurrentFeatureIndex(previousPredictedDifficulty);		
	}
	int nextPredictedDifficulty;
	public boolean preNextPredictedDifficulty() {
		nextPredictedDifficulty = AnalyzerProcessorFactory.getSingleton().getParticipantTimeLine().getDifficultyPredictionAfter(getCurrentTime());
		return nextPredictedDifficulty >= 0;
	}
	@Row(1)
	@Column(4)
	@ComponentWidth(150)
	public void nextPredictedDifficulty() {
		if (!preNextPredictedDifficulty())
			return;
		setPlayBack(true);
		setCurrentFeatureIndex(nextPredictedDifficulty);		
	}
	
	public void setCurrentFeatureIndex(int newVal) {
		super.setCurrentFeatureIndex(newVal);
		setCurrentFormattedWallTime();
	}
	
	int previousDifficultyCorrection;
	public boolean prePreviousDifficultyCorrection() {
		previousDifficultyCorrection = AnalyzerProcessorFactory.getSingleton().
				getParticipantTimeLine().
				getDifficultyCorrectionBefore(getCurrentTime());
		return previousDifficultyCorrection >= 0;
	}
	@Row(1)
	@Column(5)
	@ComponentWidth(150)
	public void previousDifficultyCorrection() {
		if (!prePreviousDifficultyCorrection())
			return;
//		playBack = true;
		setPlayBack(true);

		setCurrentFeatureIndex(previousDifficultyCorrection);		
	}
	int nextDifficultyCorrection;
	public boolean preNextDifficultyCorrection() {
		nextDifficultyCorrection = AnalyzerProcessorFactory.getSingleton().getParticipantTimeLine().getActualDifficultyAfter(getCurrentTime());
		return nextDifficultyCorrection >= 0;
	}
	@Row(1)
	@Column(6)
	@ComponentWidth(150)
	public void nextDifficultyCorrection() {
		if (!preNextDifficultyCorrection())
			return;
//		playBack = true;
		setPlayBack(true);

		setCurrentFeatureIndex(nextDifficultyCorrection);		
	}
	
/////
	int previousBarrier;
	public boolean prePreviousBarrier() {
		previousBarrier = AnalyzerProcessorFactory.getSingleton().
				getParticipantTimeLine().
				getBarrierBefore(getCurrentTime());
		return previousBarrier >= 0;
	}
	@Row(2)
	@Column(0)
	@ComponentWidth(150)
	public void previousBarrier() {
		if (!prePreviousBarrier())
			return;
//		playBack = true;
		setPlayBack(true);

		setCurrentFeatureIndex(previousBarrier);		
	}
	int nextBarrier;
	public boolean preNextBarrier() {
		nextBarrier = AnalyzerProcessorFactory.getSingleton().getParticipantTimeLine().getBarrierAfter(getCurrentTime());
		return nextBarrier >= 0;
	}
	@Row(2)
	@Column(1)
	@ComponentWidth(150)
	public void nextBarrier() {
		if (!preNextBarrier())
			return;
//		playBack = true;
		setPlayBack(true);

		setCurrentFeatureIndex(nextBarrier);		
	}
	
	
////	
	int previousWebLinks;
	public boolean prePreviousWebLinks() {
		previousWebLinks = AnalyzerProcessorFactory.getSingleton().
				getParticipantTimeLine().
				getWebLinksBefore(getCurrentTime());
		return previousWebLinks >= 0;
	}
	@Row(2)
	@Column(2)
	@ComponentWidth(150)
	@Override
	public void previousWebLinks() {
		if (!prePreviousWebLinks())
			return;
//		playBack = true;
		setPlayBack(true);

		setCurrentFeatureIndex(previousWebLinks);		
	}
	int nextWebLinks;
	@Override
	public boolean preNextWebLinks() {
		nextWebLinks = AnalyzerProcessorFactory.getSingleton().getParticipantTimeLine().getWebLinksAfter(getCurrentTime());
		return nextWebLinks >= 0;
	}
	@Row(2)
	@Column(3)
	@ComponentWidth(150)
	@Override
	public void nextWebLinks() {
		if (!preNextWebLinks())
			return;
		playBack = true;
		setCurrentFeatureIndex(nextWebLinks);		
	}
	
	@Visible(false)
	public int getNextFeatureIndex() {
		return nextFeatureIndex;
	}
	
	public void setNextFeatureIndex(int newVal) {
		nextFeatureIndex = newVal;
		if (!isPlayBack())
		setCurrentFeatureIndex(nextFeatureIndex -1);
	}
	@Visible(false)
	public int getSize() {
		return super.getSize();
	}

	
	public boolean isPlayBack() {
		return playBack;
	}
	
//	void propagatePre() {
//		//propertyChangeSupport().firePropertyChange("this", null, this);
//    }
	
//	
	
	public static void main (String[] args) {
		ObjectEditor.edit(new AGeneralizedPlayAndRewindCounter());
	}
	
	@Row(0)
	@Column(6)
	@ComponentWidth(50)	
	public int getCurrentTime() {
		return super.getCurrentTime();
	}
	
	

	@Override
	@Visible(false)
	public long getCurrentWallTime() {
		if ( AnalyzerProcessorFactory.getSingleton().getParticipantTimeLine() == null)
				return 0;
		// we do not know in which order currentimes are updated in listeners of ratio file replayer
		int aCurrentTime = Math.min(getCurrentTime(),  AnalyzerProcessorFactory.getSingleton().getParticipantTimeLine().
			   	getTimeStampList().size( ) - 1);
		if (aCurrentTime < 0)
			return 0;
		;
		return AnalyzerProcessorFactory.getSingleton().getParticipantTimeLine().getTimeStampList().get(aCurrentTime);
	}
	 
	@Override
	@Row(0)
	@Column(7)
	@ComponentWidth(150)
	@Label("")
	public String getCurrentFormattedWallTime() {
		long aCurrentTime = getCurrentWallTime();
		if (aCurrentTime == 0)
			return "";
		return ATimeStampComputer.toDateString(aCurrentTime);
	}
	String currentFormattedWallTime = "";
	 void setCurrentFormattedWallTime() {
		 String oldValue = currentFormattedWallTime;
		 long aCurrentTime = getCurrentWallTime();
		 currentFormattedWallTime =  aCurrentTime == 0?
			"":	 ATimeStampComputer.toDateString(aCurrentTime);
	
		propertyChangeSupport
				.firePropertyChange("currentFormattedWallTime", oldValue, currentFormattedWallTime);
	}
}
