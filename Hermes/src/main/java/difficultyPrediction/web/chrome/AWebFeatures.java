package difficultyPrediction.web.chrome;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import analyzer.extension.StuckInterval;
import analyzer.extension.StuckPoint;
import difficultyPrediction.APredictionParameters;
import difficultyPrediction.metrics.CommandCategory;
import difficultyPrediction.metrics.CommandCategoryMapping;
import difficultyPrediction.metrics.CommandClassificationSchemeName;
import difficultyPrediction.web.WebFeatures;
import fluorite.model.EHEventRecorder;
/**
 * This should really be a map, rather than a record
 *
 */
public class AWebFeatures implements WebFeatures {

//	public static final String SAVED_TIME_STAMP = "savedTimeStamp";
//	public static final String UNIX_START_TIME = "unixStartTime";
//	public static final String FILE_NAME = "fileName";
//
//	public static final String ELAPSED_TIME = "elapsedTime";
//	public static final String ESTIMATED_BUSY_TIME = "busyTime";
//
//	public static final String COMMAND_STRING = "commandString";
//	public static final String PAGE_VISITS = "pageVisits";
//	public static final String NUM_PAGES_VISITED = "numURLs";
//	public static final String NUM_SEARCHES = "numSearches";
//	public static final String MAX_SEARCH_LENGTH = "maxSearchLength";
	


	


//	protected double other1Feature;
//	protected double other2Feature;
//	protected double other3Feature;
//	protected double other4Feature;
//	protected double other5Feature;
//	protected double other6Feature;
//	protected double other7Feature;
//	protected double other8Feature;
//	protected double other9Feature;
	protected Map<String, Object> featureNameToValue = new HashMap<>();


	
//	private StuckPoint stuckPoint;
//	private StuckInterval stuckInterval;
	protected CommandCategoryMapping commandCategoryMapping; 
	static List<PageVisit> emptyPageVisits = new ArrayList();

	public AWebFeatures() {
		commandCategoryMapping = APredictionParameters.getInstance().
				getCommandClassificationScheme().
					getCommandCategoryMapping();
//		setCommandRate(0);
//		setCommandString("");
//		setDebugRate(0);
//		setDebugRatio(0);
//		setDeletionRatio(0);
//		setEditRate(0);
//		setEditRatio(0);
//		setElapsedTime(0);
//		setEstimatedBusyTime(0);
//		setExceptionsPerRun(0);
//		setFocusRate(0);
//		setFocusRatio(0);
//		setInsertionRate(0);
//		setInsertionRatio(0);
//		setNavigationRate(0);
//		setNavigationRatio(0);
		setNumPagesVisited(0);
		setPageVisits(emptyPageVisits);
		setNumWebSearches(0);
		setMaxSearchLength(0);
		setUnixStartTime(0);
//		setFileName("");
		

		
	 }

//	public double getInsertRatio() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.INSERT)) ;
//	}
//	public void setInsertRatio(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.INSERT), newVal);
//	}
//
//	public double getEditRatio() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.EDIT)) ;
//	}
//	public void setEditRatio(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.EDIT), newVal);
//	}
//	
//	public double getDebugRatio() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.DEBUG)) ;
//	}
//	public void setDebugRatio(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.DEBUG), newVal) ;
//
////		this.debugRatio = debugRatio;
//	}
//	public double getNavigationRatio() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.NAVIGATION)) ;
//	}
//	public void setNavigationRatio(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.NAVIGATION), newVal) ;
//
//	}
//	public double getFocusRatio() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.FOCUS)) ;
//	}
//	public void setFocusRatio(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.FOCUS), newVal) ;
//
//	}
//	public double getRemoveRatio() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.REMOVE)) ;
//	}
//	public void setRemoveRatio(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.REMOVE), newVal) ;
//	}
//	public double getRemoveClassRatio() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.REMOVE_CLASS)) ;
//	}
//	public void setRemoveClassRatio(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.REMOVE_CLASS), newVal) ;
//	}
//	public double getExceptionsPerRun() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.EXEEPTIONS_PER_RUN)) ;
//	}
//	public void setExceptionsPerRun(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.EXEEPTIONS_PER_RUN), newVal) ;
//	}
//	public double getInsertionRatio() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.INSERT)) ;
//	}
//	public void setInsertionRatio(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.INSERT), newVal) ;
//
//	}
//	/*
//	 * Same as remove?
//	 */
//	public double getDeletionRatio() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.REMOVE)) ;
//	}
//	public void setDeletionRatio(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.REMOVE), newVal) ;
//	}
//	public double getInsertionRate() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.INSERT_RATE)) ;
//	}
//	public void setInsertionRate(double newVal) {
//		 featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.INSERT_RATE), newVal) ;
//	}
////	public double getDeletionRate() {
////		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.REMOVE_RATE)) ;
////	}
////	public void setDeletionRate(double newVal) {
////		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.REMOVE_RATE), newVal) ;	}
//	public double getDebugRate() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.DEBUG_RATE)) ;
//	}
//	public void setDebugRate(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.DEBUG_RATE), newVal) ;
//	}
//	public double getNavigationRate() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.NAVIGATION_RATE)) ;
//	}
//	public void setNavigationRate(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.NAVIGATION_RATE), newVal) ;
//	}
//	public double getFocusRate() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.FOCUS_RATE)) ;
//
//	}
//	public void setFocusRate(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.FOCUS_RATE), newVal) ;
//	}
//	public double getRemoveRate() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.REMOVE_RATE)) ;
//	}
//	public void setRemoveRate(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.REMOVE_RATE), newVal) ;
//	}
//	public double getEditRate() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.EDIT_RATE)) ;
//	}
//	public void setEditRate(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.EDIT_RATE), newVal) ;
//	}
//	public double getCommandRate() {
//		return (Double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.COMMAND_RATE)) ;
//	}
//	public void setCommandRate(double newVal) {
//		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.COMMAND_RATE), newVal) ;
//	}
//	public long getSavedTimeStamp() {
//		return (Long) featureNameToValue.get(SAVED_TIME_STAMP) ;
//
////		return savedTimeStamp;
//	}
//	public void setSavedTimeStamp(long newVal) {
////		this.savedTimeStamp = savedTimeStamp;
//		featureNameToValue.put(SAVED_TIME_STAMP, newVal) ;
//
//	}
	public long getElapsedTime() {
		return (Long) featureNameToValue.get(ELAPSED_TIME) ;

//		return savedTimeStamp;
	}
	public void setElapsedTime(long newVal) {
//		this.savedTimeStamp = savedTimeStamp;
		featureNameToValue.put(ELAPSED_TIME, newVal) ;

	}
//	@Override
//	public long getEstimatedBusyTime() {
//		return (Long) featureNameToValue.get(ESTIMATED_BUSY_TIME) ;
//
////		return savedTimeStamp;
//	}
//	@Override
//	public void setEstimatedBusyTime(long newVal) {
////		this.savedTimeStamp = savedTimeStamp;
//		featureNameToValue.put(ESTIMATED_BUSY_TIME, newVal) ;
//
//	}
//	@Override
//	public String getCommandString() {
//		return (String) featureNameToValue.get(COMMAND_STRING) ;
//
////		return savedTimeStamp;
//	}
//	public void setCommandString(String newVal) {
////		this.savedTimeStamp = savedTimeStamp;
//		featureNameToValue.put(COMMAND_STRING, newVal) ;
//
//	}
//	public StuckPoint getStuckPoint() {
//		return stuckPoint;
//	}
//	public void setStuckPoint(StuckPoint stuckPoint) {
//		this.stuckPoint = stuckPoint;
//	}
//	public StuckInterval getStuckInterval() {
//		return stuckInterval;
//	}
//	public void setStuckInterval(StuckInterval stuckInterval) {
//		this.stuckInterval = stuckInterval;
//	}
//	
//	Date date = new Date();
//	// add other ratios later
//	public String toString () {
////		date.setTime(getSavedTimeStamp() + EHEventRecorder.getInstance().getStartTimestamp());
////		return date
////				+ "Edit (" + getEditRatio() + ") "
////				+ "Debug (" + getDebugRatio() + ") " 
////				+ "Navigation (" + getNavigationRatio() + ") "
////				+ "Focus (" + getFocusRatio() + ") ";
//		return featureNameToValue.toString();
//		
//	}
//	protected RatioFeatures emptyCopy() {
//		return new AWebFeatures();
//	}
//	
//
//	public RatioFeatures clone() {
//		AWebFeatures retVal = (AWebFeatures) emptyCopy();
//		retVal.featureNameToValue = new HashMap();
//		for (String aKey:featureNameToValue.keySet()) {
//			retVal.featureNameToValue.put(aKey, featureNameToValue.get(aKey));
//		}
//////		retVal.actualStatus = actualStatus;
////		retVal.debugRatio = debugRatio;
////		retVal.debugTimeRatio = debugTimeRatio;
////		retVal.deletionRatio = deletionRatio;
////		retVal.deletionTimeRatio = deletionTimeRatio;
//////		retVal.difficultyType = difficultyType;
////		retVal.editRatio = editRatio;
////		retVal.exceptionsPerRun = exceptionsPerRun;
////		retVal.focusRatio = focusRatio;
////		retVal.focusTimeRatio = focusTimeRatio;
////		retVal.insertionRatio = insertionRatio;
////		retVal.navigationRatio = navigationRatio;
////		retVal.navigationTimeRatio = navigationTimeRatio;
//////		retVal.predictedStatus = predictedStatus;
////		retVal.removeRatio = removeRatio;
////		retVal.removeTimeRatio = removeTimeRatio;
////		retVal.savedTimeStamp = savedTimeStamp;
//////		retVal.webLinkList = webLinkList;
//		return retVal;
//		
//	}
////	@Override
////	public double geOther1Feature() {
////		return other1Feature;
////	}
////	@Override
////	public void setOther1Feature(double newVal) {
////		other1Feature = newVal;
////	}
////	@Override
////	public double geOther2Feature() {
////		return other2Feature;
////	}
////	@Override
////	public void setOther2Feature(double newVal) {
////		other2Feature = newVal;
////	}
////	@Override
////	public double geOther3Feature() {
////		return other2Feature;
////	}
////	@Override
////	public void setOther3Feature(double newVal) {
////		other3Feature = newVal;
////	}
////	@Override
////	public double geOther4Feature() {
////		return other4Feature;
////	}
////	@Override
////	public void setOther4Feature(double newVal) {
////		other4Feature = newVal;		
////	}
////	@Override
////	public double geOther5Feature() {
////		return other5Feature;
////	}
////	@Override
////	public void setOther5Feature(double newVal) {
////		other5Feature = newVal;	
////	}
////	@Override
////	public double geOther6Feature() {
////		return other6Feature;
////	}
////	@Override
////	public void setOther6Feature(double newVal) {
////		other6Feature = newVal;	
////
////	}
////	@Override
////	public double geOther7Feature() {
////		return other7Feature;
////	}
////	@Override
////	public void setOther7Feature(double newVal) {
////		other7Feature = newVal;	
////
////	}
////	@Override
////	public double geOther8Feature() {
////		return other8Feature;
////	}
////	@Override
////	public void setOther8Feature(double newVal) {
////		other8Feature = newVal;	
////
////	}
////	@Override
////	public double geOther9Feature() {
////		return other9Feature;
////	}
////	@Override
////	public void setOther9Feature(double newVal) {
////		other9Feature = newVal;
////	}
	@Override
	public Object getFeature(String aFeatureName) {
		return featureNameToValue.get(aFeatureName);
	}
	@Override
	public void setFeature(String aFeatureName, Object newVal) {
		featureNameToValue.put(aFeatureName, newVal);
	}

	@Override
	public void setPageVisits(List<PageVisit> aPageVisits) {
		featureNameToValue.put(PAGE_VISITS, aPageVisits);
	}

	@Override
	public List<PageVisit> getPageVisits() {
		return (List<PageVisit>) featureNameToValue.get(PAGE_VISITS);
	}

	@Override
	public void setNumPagesVisited(double aNumPages) {
		featureNameToValue.put(commandCategoryMapping.getFeatureName(CommandCategory.WEB_LINK_TIMES), aNumPages);
	}

	@Override
	public double getNumPagesVisited() {
		return (double) featureNameToValue.get(commandCategoryMapping.getFeatureName(CommandCategory.WEB_LINK_TIMES));
	}

	@Override
	public void setNumWebSearches(double aNumPages) {
		featureNameToValue.put(NUM_SEARCHES, aNumPages);

	}

	@Override
	public double getNumWebSearches() {
		return (double) featureNameToValue.get(NUM_SEARCHES);
	}

	@Override
	public void setMaxSearchLength(double newValue) {
		featureNameToValue.put(MAX_SEARCH_LENGTH, newValue);
	}

	@Override
	public double getMaxSearchLength() {
		return  (int) featureNameToValue.get(MAX_SEARCH_LENGTH);
	}

	@Override
	public long getUnixStartTime() {
		return (long) featureNameToValue.get(UNIX_START_TIME);
	}

	@Override
	public void setUnixStartTime(long newVal) {
		featureNameToValue.put(UNIX_START_TIME, newVal);
	}

//	@Override
//	public String getFileName() {
//		return 	(String) featureNameToValue.get(FILE_NAME);
//
//	}
//
//	@Override
//	public void setFileName(String newVal) {
//		featureNameToValue.put(FILE_NAME, newVal);
//	}
	
	
}
