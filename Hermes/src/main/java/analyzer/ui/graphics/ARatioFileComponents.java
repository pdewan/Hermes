package analyzer.ui.graphics;

import java.util.List;

import analyzer.WebLink;
import difficultyPrediction.featureExtraction.ARatioFeatures;

public class ARatioFileComponents extends ARatioFeatures implements RatioFileComponents {
//	private double editRatio;
//	private double debugRatio;
//	private double navigationRatio;
//	private double focusRatio;
//	private double removeRatio;
//	private double exceptionsPerRun;
//	private double insertionRatio;
//	private double deletionRatio;
//	private double insertionTimeRatio;
//	private double deletionTimeRatio;
//	private double debugTimeRatio;
//	private double navigationTimeRatio;
//	private double focusTimeRatio;
//	private double removeTimeRatio;
//	private long savedTimeStamp;
	private int predictedStatus;
	private int actualStatus;
	private List<WebLink> webLinkList;
	private String difficultyType;

	public ARatioFileComponents() {

	}

	public int getPredictedStatus() {
		return predictedStatus;
	}

	public void setPredictedStatus(int predictedStatus) {
		this.predictedStatus = predictedStatus;
	}

	public int getActualStatus() {
		return actualStatus;
	}

	public void setActualStatus(int actualStatus) {
		this.actualStatus = actualStatus;
	}

	public double getEditRatio() {
		return editRatio;
	}

//	public void setEditRatio(double editRatio) {
//		this.editRatio = editRatio;
//	}
//
//	public double getDebugRatio() {
//		return debugRatio;
//	}
//
//	public void setDebugRatio(double debugRatio) {
//		this.debugRatio = debugRatio;
//	}
//
//	public double getNavigationRatio() {
//		return navigationRatio;
//	}
//
//	public void setNavigationRatio(double navigationRatio) {
//		this.navigationRatio = navigationRatio;
//	}
//
//	public double getFocusRatio() {
//		return focusRatio;
//	}
//
//	public void setFocusRatio(double focusRatio) {
//		this.focusRatio = focusRatio;
//	}
//
//	public double getRemoveRatio() {
//		return removeRatio;
//	}
//
//	public void setRemoveRatio(double removeRatio) {
//		this.removeRatio = removeRatio;
//	}
//
//	public double getExceptionsPerRun() {
//		return exceptionsPerRun;
//	}
//
//	public void setExceptionsPerRun(double exceptionsPerRun) {
//		this.exceptionsPerRun = exceptionsPerRun;
//	}
//
//	public double getInsertionRatio() {
//		return insertionRatio;
//	}
//
//	public void setInsertionRatio(double insertionRatio) {
//		this.insertionRatio = insertionRatio;
//	}
//
//	public double getDeletionRatio() {
//		return deletionRatio;
//	}
//
//	public void setDeletionRatio(double deletionRatio) {
//		this.deletionRatio = deletionRatio;
//	}
//
//	public double getInsertionTimeRatio() {
//		return insertionTimeRatio;
//	}
//
//	public void setInsertionTimeRatio(double insertionTimeRatio) {
//		this.insertionTimeRatio = insertionTimeRatio;
//	}
//
//	public double getDeletionTimeRatio() {
//		return deletionTimeRatio;
//	}
//
//	public void setDeletionTimeRatio(double deletionTimeRatio) {
//		this.deletionTimeRatio = deletionTimeRatio;
//	}
//
//	public double getDebugTimeRatio() {
//		return debugTimeRatio;
//	}
//
//	public void setDebugTimeRatio(double debugTimeRatio) {
//		this.debugTimeRatio = debugTimeRatio;
//	}
//
//	public double getNavigationTimeRatio() {
//		return navigationTimeRatio;
//	}
//
//	public void setNavigationTimeRatio(double navigationTimeRatio) {
//		this.navigationTimeRatio = navigationTimeRatio;
//	}
//
//	public double getFocusTimeRatio() {
//		return focusTimeRatio;
//	}
//
//	public void setFocusTimeRatio(double focusTimeRatio) {
//		this.focusTimeRatio = focusTimeRatio;
//	}
//
//	public double getRemoveTimeRatio() {
//		return removeTimeRatio;
//	}
//
//	public void setRemoveTimeRatio(double removeTimeRatio) {
//		this.removeTimeRatio = removeTimeRatio;
//	}
//
//	public long getSavedTimeStamp() {
//		return savedTimeStamp;
//	}
//
//	public void setSavedTimeStamp(long savedTimeStamp) {
//		this.savedTimeStamp = savedTimeStamp;
//	}

	public List<WebLink> getWebLinkList() {
		return webLinkList;
	}

	public void setWebLinkList(List<WebLink> newWebLinkList) {
		webLinkList = newWebLinkList;
	}

	public void setDifficultyType(String newType) {
		difficultyType = newType;
	}

	public String getDifficultyType() {
		return difficultyType;
	}
	
	protected RatioFileComponents emptyCopy() {
		return new ARatioFileComponents();
	}
	
	public RatioFileComponents clone() {
		ARatioFileComponents retVal = (ARatioFileComponents) super.clone();
		retVal.actualStatus = actualStatus;		
		retVal.difficultyType = difficultyType;		
		retVal.predictedStatus = predictedStatus;		
		retVal.webLinkList = webLinkList;
		return retVal;
		
	}

}
