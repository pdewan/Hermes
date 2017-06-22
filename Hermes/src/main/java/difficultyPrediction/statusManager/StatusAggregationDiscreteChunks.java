package difficultyPrediction.statusManager;

import difficultyPrediction.APredictionParameters;
import difficultyPrediction.metrics.APredictionHolder;
import difficultyPrediction.predictionManagement.PredictionManagerStrategy;

public class StatusAggregationDiscreteChunks implements StatusManagerStrategy{
	public StatusManager manager;
	private static final int NO_STATUS_INT = -1;
	private static final int STUCK_INT = 1;
	private static final int PROGRESS_INT = 0;
//	public static final String STUCK = "YES";
//	public static final String PROGRESS = "NO";
	public static final String STUCK = PredictionManagerStrategy.DIFFICULTY_PREDICTION;
	public static final String PROGRESS = PredictionManagerStrategy.PROGRESS_PREDICTION;
	private static final int MAX_PERCENTAGE = 60;
	private static final int WHOLE_PERCENTAGE = 100; //Uhh? haha
	private APredictionHolder holdPredictions = new APredictionHolder();
	private int numberOfPredictionsForDominantStatus = 5;
	
	public StatusAggregationDiscreteChunks(StatusManager manager) {
		this.manager = manager;
	}
	int numberOfPredictionsForDominantStatus() {
		return APredictionParameters.getInstance().getStatusAggregated();
	}
	public static int statusStringToInt(String aStatus) {
		if (aStatus.equals(STUCK))
			return STUCK_INT;
		else if (aStatus.equals(PROGRESS)) 
			return PROGRESS_INT;
		else
			return NO_STATUS_INT;
	}
	//TODO debug here and figure out why the tool never predicts stuck, even though im doing actions that indicate that I am stuck
	//TODO add the interdeterminate ("TIE") and check to make sure that this working correctly
	public void aggregateStatuses(String status) {
//		LineGraphComposer.getStatusBar().newStatus(status);
//		LineGraphComposer.getStatusBar().newStatus(statusStringToInt(status));
		holdPredictions.predictions.add(status);
		if(status.toUpperCase().equals(STUCK)) {
			holdPredictions.numberOfYes++;
		}
		else if (status.toUpperCase().equals(PROGRESS))
			holdPredictions.numberOfNo++;
		else {
			//There is an error
			return;
		}
		
		if(holdPredictions.predictions.size() >= numberOfPredictionsForDominantStatus()) {
			double percentage = WHOLE_PERCENTAGE * holdPredictions.numberOfYes / ((double)holdPredictions.predictions.size());
			String aggregateStatusPredicted;
			if(percentage > MAX_PERCENTAGE) {
				aggregateStatusPredicted = STUCK;
				manager.onStatusHandOff(STUCK);
			} else {
				aggregateStatusPredicted = PROGRESS;
				manager.onStatusHandOff(PROGRESS);
			}
//			LineGraphComposer.getStatusBar().newAggregatedStatus(aggregateStatusPredicted);
//			LineGraphComposer.getStatusBar().newAggregatedStatus(statusStringToInt(aggregateStatusPredicted));
			holdPredictions.numberOfNo = 0;
			holdPredictions.numberOfYes = 0;
			holdPredictions.predictions.clear();
		}
	}
}
