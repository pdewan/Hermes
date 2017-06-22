package difficultyPrediction.featureExtraction;

import java.util.Date;

import analyzer.extension.StuckInterval;
import analyzer.extension.StuckPoint;
import analyzer.ui.graphics.ARatioFileComponents;
import analyzer.ui.graphics.RatioFileComponents;
import fluorite.model.EHEventRecorder;

public class ARatioFeatures implements RatioFeatures {
	protected double editRatio;
	protected double debugRatio;
	protected double navigationRatio;
	protected  double focusRatio;
	protected double removeRatio;
	protected double exceptionsPerRun;
	protected double insertionRatio;
	protected double deletionRatio;
	protected double insertionTimeRatio;
	protected double deletionTimeRatio;
	protected double debugTimeRatio;
	protected  double navigationTimeRatio;
	protected double focusTimeRatio;
	protected double removeTimeRatio;
	protected long savedTimeStamp;
	
	private StuckPoint stuckPoint;
	private StuckInterval stuckInterval;
	 

	public ARatioFeatures() {
		 
	 }
	public double getEditRatio() {
		return editRatio;
	}
	public void setEditRatio(double editRatio) {
		this.editRatio = editRatio;
	}
	public double getDebugRatio() {
		return debugRatio;
	}
	public void setDebugRatio(double debugRatio) {
		this.debugRatio = debugRatio;
	}
	public double getNavigationRatio() {
		return navigationRatio;
	}
	public void setNavigationRatio(double navigationRatio) {
		this.navigationRatio = navigationRatio;
	}
	public double getFocusRatio() {
		return focusRatio;
	}
	public void setFocusRatio(double focusRatio) {
		this.focusRatio = focusRatio;
	}
	public double getRemoveRatio() {
		return removeRatio;
	}
	public void setRemoveRatio(double removeRatio) {
		this.removeRatio = removeRatio;
	}
	public double getExceptionsPerRun() {
		return exceptionsPerRun;
	}
	public void setExceptionsPerRun(double exceptionsPerRun) {
		this.exceptionsPerRun = exceptionsPerRun;
	}
	public double getInsertionRatio() {
		return insertionRatio;
	}
	public void setInsertionRatio(double insertionRatio) {
		this.insertionRatio = insertionRatio;
	}
	public double getDeletionRatio() {
		return deletionRatio;
	}
	public void setDeletionRatio(double deletionRatio) {
		this.deletionRatio = deletionRatio;
	}
	public double getInsertionTimeRatio() {
		return insertionTimeRatio;
	}
	public void setInsertionTimeRatio(double insertionTimeRatio) {
		this.insertionTimeRatio = insertionTimeRatio;
	}
	public double getDeletionTimeRatio() {
		return deletionTimeRatio;
	}
	public void setDeletionTimeRatio(double deletionTimeRatio) {
		this.deletionTimeRatio = deletionTimeRatio;
	}
	public double getDebugTimeRatio() {
		return debugTimeRatio;
	}
	public void setDebugTimeRatio(double debugTimeRatio) {
		this.debugTimeRatio = debugTimeRatio;
	}
	public double getNavigationTimeRatio() {
		return navigationTimeRatio;
	}
	public void setNavigationTimeRatio(double navigationTimeRatio) {
		this.navigationTimeRatio = navigationTimeRatio;
	}
	public double getFocusTimeRatio() {
		return focusTimeRatio;
	}
	public void setFocusTimeRatio(double focusTimeRatio) {
		this.focusTimeRatio = focusTimeRatio;
	}
	public double getRemoveTimeRatio() {
		return removeTimeRatio;
	}
	public void setRemoveTimeRatio(double removeTimeRatio) {
		this.removeTimeRatio = removeTimeRatio;
	}
	public long getSavedTimeStamp() {
		return savedTimeStamp;
	}
	public void setSavedTimeStamp(long savedTimeStamp) {
		this.savedTimeStamp = savedTimeStamp;
	}
	public StuckPoint getStuckPoint() {
		return stuckPoint;
	}
	public void setStuckPoint(StuckPoint stuckPoint) {
		this.stuckPoint = stuckPoint;
	}
	public StuckInterval getStuckInterval() {
		return stuckInterval;
	}
	public void setStuckInterval(StuckInterval stuckInterval) {
		this.stuckInterval = stuckInterval;
	}
	
	Date date = new Date();
	// add other ratios later
	public String toString () {
		date.setTime(getSavedTimeStamp() + EHEventRecorder.getInstance().getStartTimestamp());
		return date
				+ "Edit (" + getEditRatio() + ") "
				+ "Debug (" + getDebugRatio() + ") " 
				+ "Navigation (" + getNavigationRatio() + ") "
				+ "Focus (" + getFocusRatio() + ") ";
		
	}
	protected RatioFeatures emptyCopy() {
		return new ARatioFeatures();
	}
	

	public RatioFeatures clone() {
		ARatioFeatures retVal = (ARatioFeatures) emptyCopy();
//		retVal.actualStatus = actualStatus;
		retVal.debugRatio = debugRatio;
		retVal.debugTimeRatio = debugTimeRatio;
		retVal.deletionRatio = deletionRatio;
		retVal.deletionTimeRatio = deletionTimeRatio;
//		retVal.difficultyType = difficultyType;
		retVal.editRatio = editRatio;
		retVal.exceptionsPerRun = exceptionsPerRun;
		retVal.focusRatio = focusRatio;
		retVal.focusTimeRatio = focusTimeRatio;
		retVal.insertionRatio = insertionRatio;
		retVal.navigationRatio = navigationRatio;
		retVal.navigationTimeRatio = navigationTimeRatio;
//		retVal.predictedStatus = predictedStatus;
		retVal.removeRatio = removeRatio;
		retVal.removeTimeRatio = removeTimeRatio;
		retVal.savedTimeStamp = savedTimeStamp;
//		retVal.webLinkList = webLinkList;
		return retVal;
		
	}
	
	
}
