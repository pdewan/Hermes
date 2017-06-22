package analyzer;

import java.util.List;

import analyzer.extension.StuckInterval;
import analyzer.extension.StuckPoint;

public interface ParticipantTimeLine {
	int INDTERMINATE_INT = -1;
	int PROGRESS_INT = 0;
	int DIFFICULTY_INT = 1;
	int SURMOUNTABLE_INT = 1;
	int INSURMOUNTABLE_INT = 2;
	String INDTERMINATE_STRING = "Indeterminate";
	String PROGRESS_STRING = "Progress";
	String DIFFICULTY_STRING = "YES";
	String SURMOUNTABLE_STRING  = "Surmountable";
	String INSURMOUNTABLE_STRING = "Insurmountable";
	
	public List<Double> getEditList();
	
	public void setEditList(List<Double> editList);

	public abstract List<Double> getInsertionList();

	public abstract void setInsertionList(List<Double> insertionList);

	public abstract List<Double> getDeletionList();

	public abstract void setDeletionList(List<Double> deletionList);

	public abstract List<Double> getDebugList();

	public abstract void setDebugList(List<Double> debugList);

	public abstract List<Double> getNavigationList();

	public abstract void setNavigationList(List<Double> navigationList);

	public abstract List<Double> getFocusList();

	public abstract void setFocusList(List<Double> focusList);

	public abstract List<Double> getRemoveList();

	public abstract void setRemoveList(List<Double> removeList);

	public abstract List<Long> getTimeStampList();

	public abstract void setTimeStampList(List<Long> timeStampList);

	public abstract List<Integer> getPredictions();

	public abstract void setPredictions(List<Integer> predictionList);

	public abstract List<Integer> getPredictionCorrections();

	public abstract void setPredictionCorrections(
			List<Integer> predictionCorrection);

	public abstract List<List<WebLink>> getWebLinks();

	public abstract void setWebLinks(List<List<WebLink>> webLinks);
	public int getIndexBefore(long aTimeStamp) ;
	
	public List<StuckInterval> getStuckInterval();
	public void setStuckInterval(List<StuckInterval> stuckInterval);
	
	public List<StuckPoint> getStuckPoint();
	public void setStuckPoint(List<StuckPoint> stuckPoint);

	StringBuffer toText();
	
	public int getDifficultyPredictionBefore(int aCurrentIndex) ;
	
	public int getDifficultyPredictionAfter(int aCurrentIndex) ;

	int getDifficultyCorrectionBefore(int aCurrentIndex);

	int getActualDifficultyAfter(int aCurrentIndex);

	int getBarrierBefore(int aCurrentIndex);

	int getBarrierAfter(int aCurrentIndex);

	int getWebLinksBefore(int aCurrentIndex);

	int getWebLinksAfter(int aCurrentIndex);

	
}