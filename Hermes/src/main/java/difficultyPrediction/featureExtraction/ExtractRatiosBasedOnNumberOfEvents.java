package difficultyPrediction.featureExtraction;

import java.util.List;

import analyzer.ATimeStampComputer;
import analyzer.TimeStampComputerFactory;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.metrics.AGenericRatioCalculator;
import difficultyPrediction.metrics.RatioCalculator;
import difficultyPrediction.metrics.RatioCalculatorSelector;
import fluorite.commands.EHICommand;

public class ExtractRatiosBasedOnNumberOfEvents implements
		FeatureExtractionStrategy {

	public ExtractRatiosBasedOnNumberOfEvents() {
		
	}
	
	private RatioCalculator ratioCalculator = RatioCalculatorSelector.getRatioCalculator();
//	RatioCalculator genericRatioCalculator = AGenericRatioCalculator.getInstance();
	 private static final int NAVIGATION_PERCENTAGE = 0;
     private static final int DEBUG_PERCENTAGE = 1;
     private static final int FOCUS_PERCENTAGE = 2;
     private static final int EDIT_PERCENTAGE = 3;
     private static final int REMOVE_PERCENTAGE = 4;
     
     protected boolean compare (double editRatio, double debugRatio,
 			double navigationRatio,
 			double focusRatio, 
 			double removeRatio, 
 			long timeStamp, 
 			RatioFeatures aRatioFeatures) {
    	 return aRatioFeatures.getEditRatio() == editRatio &&
    			 aRatioFeatures.getInsertionRatio() == editRatio &&
    			 aRatioFeatures.getNavigationRatio() == navigationRatio &&
    			 aRatioFeatures.getRemoveRatio() == removeRatio &&
    			 aRatioFeatures.getSavedTimeStamp() == timeStamp;
 		
 	}
	
	public void performFeatureExtraction(List<EHICommand> actions, RatioBasedFeatureExtractor featureExtractor) {
			List<Double> percentages = null;
			int i = 2;
			percentages = ratioCalculator.computeMetrics(actions);
			RatioFeatures aRatioFeatures = ratioCalculator.computeFeatures(actions);
//			List<Double> genericPercentages = genericRatioCalculator.computeMetrics(actions);
//			if (!(percentages.equals(genericPercentages))) {
//				System.err.println ("Generic and specific percentages diverge:" + percentages + " " + genericPercentages);
//			}
			EHICommand lastCommand = actions.get(actions.size() - 1);
			long timeStamp = lastCommand.getTimestamp();
			if (DifficultyPredictionSettings.isReplayMode() && !DifficultyPredictionSettings.isReplayRatioFiles()) {
				// we are playing store rations through mediator
				timeStamp = TimeStampComputerFactory.getSingleton().computeTimestamp(lastCommand);
			}
//			if (aRatioFeatures != null) { // this should not be null
			aRatioFeatures.setSavedTimeStamp(timeStamp);
//			}
			boolean correctRatios = compare(percentages.get(EDIT_PERCENTAGE), percentages.get(DEBUG_PERCENTAGE), 
                    percentages.get(NAVIGATION_PERCENTAGE), percentages.get(FOCUS_PERCENTAGE), percentages.get(REMOVE_PERCENTAGE), timeStamp, aRatioFeatures);
				
			if (!correctRatios) {
				System.out.println("compariosn failed");
			}
			featureExtractor.onFeatureHandOff(percentages.get(EDIT_PERCENTAGE), percentages.get(DEBUG_PERCENTAGE), 
                    percentages.get(NAVIGATION_PERCENTAGE), percentages.get(FOCUS_PERCENTAGE), percentages.get(REMOVE_PERCENTAGE), timeStamp);		
	}

}
