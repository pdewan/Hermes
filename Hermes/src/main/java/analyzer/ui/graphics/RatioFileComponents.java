package analyzer.ui.graphics;

import java.util.List;

import analyzer.WebLink;
import difficultyPrediction.featureExtraction.RatioFeatures;

public interface RatioFileComponents extends RatioFeatures {
	public void setEditRatio(double editRatio);

	public double getDebugRatio();

	public void setDebugRatio(double debugRatio);

	public double getNavigationRatio();

	public void setNavigationRatio(double navigationRatio);

	public double getFocusRatio();

	public void setFocusRatio(double focusRatio);

	public double getRemoveRatio();

	public void setRemoveRatio(double removeRatio);

	public double getExceptionsPerRun();

	public void setExceptionsPerRun(double exceptionsPerRun);

	public double getInsertionRatio();

	public void setInsertionRatio(double insertionRatio);

	public double getDeletionRatio();

	public void setDeletionRatio(double deletionRatio);

	public double getInsertionTimeRatio();

	public void setInsertionTimeRatio(double insertionTimeRatio);

	public double getDeletionTimeRatio();

	public void setDeletionTimeRatio(double deletionTimeRatio);

	public double getDebugTimeRatio();

	public void setDebugTimeRatio(double debugTimeRatio);

	public double getNavigationTimeRatio();

	public void setNavigationTimeRatio(double navigationTimeRatio);

	public double getFocusTimeRatio();

	public void setFocusTimeRatio(double focusTimeRatio);

	public double getRemoveTimeRatio();

	public void setRemoveTimeRatio(double removeTimeRatio);

	public long getSavedTimeStamp();

	public void setSavedTimeStamp(long savedTimeStamp);

	public double getEditRatio();

	public int getPredictedStatus();

	public void setPredictedStatus(int predictedStatus);

	public int getActualStatus();

	public void setActualStatus(int actualStatus);

	public List<WebLink> getWebLinkList();

	public void setWebLinkList(List <WebLink> newWebLinkList);

	public void setDifficultyType(String newType);

	public String getDifficultyType();

}
