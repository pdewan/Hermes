package analyzer.ui;

import analyzer.ui.graphics.PlayAndRewindCounter;

public interface GeneralizedPlayAndRewindCounter extends PlayAndRewindCounter {
	public void live();

	public void start() ;

	public void end() ;
	public int getNextFeatureIndex() ;
	
	public void setNextFeatureIndex(int newVal) ;
	
	public boolean isPlayBack() ;
	public long getCurrentWallTime();
	public void nextPredictedDifficulty();
	public void previousPredictedDifficulty();
	public void nextDifficultyCorrection();
	public void previousDifficultyCorrection();
	
	public void nextBarrier();
	public void previousBarrier();

	long getAbsoluteStartTime();

	void previousWebLinks();

	boolean preNextWebLinks();

	void nextWebLinks();

	String getCurrentFormattedWallTime();

}
