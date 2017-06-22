package difficultyPrediction.featureExtraction;

import java.util.List;

import analyzer.FeatureExtractorAnalyzer;
import analyzer.TimeandEventBasedPercentage;
import fluorite.commands.EHICommand;

public class ExtractRatiosBasedOnNumberOfEventsAndBasedOnTime implements
		FeatureExtractionStrategy {

	public ExtractRatiosBasedOnNumberOfEventsAndBasedOnTime() {

	}

	private TimeandEventBasedPercentage metrics = new TimeandEventBasedPercentage();

	private static final int NAVIGATION_PERCENTAGE = 0;
	private static final int DEBUG_PERCENTAGE = 1;
	private static final int FOCUS_PERCENTAGE = 2;
	private static final int EDIT_PERCENTAGE = 3;
	private static final int REMOVE_PERCENTAGE = 4;
	private static final int INSERTION_PERCENTAGE = 5;
	private static final int DELETION_PERCENTAGE = 6;
	private static final int EXCEPTIONS_PER_RUNS_PERCENTAGE = 7;

	@Override
	public void performFeatureExtraction(List<EHICommand> actions,
			RatioBasedFeatureExtractor featureExtractor) {
		
//		List<Double> percentages = null;
//		percentages = metrics.computeMetrics(actions);	
//		featureExtractor.onFeatureHandOff(percentages.get(EDIT_PERCENTAGE),
//				percentages.get(DEBUG_PERCENTAGE),
//				percentages.get(NAVIGATION_PERCENTAGE),
//				percentages.get(FOCUS_PERCENTAGE),
//				percentages.get(REMOVE_PERCENTAGE));

	}

	public void performFeatureExtraction(List<EHICommand> actions,
			FeatureExtractorAnalyzer featureExtractorAnalyzer) {
//		List<Double> percentages = null;
//		percentages = metrics.computeMetrics(actions);
//		featureExtractorAnalyzer.onFeatureHandOff(
//				percentages.get(EDIT_PERCENTAGE),
//				percentages.get(DEBUG_PERCENTAGE),
//				percentages.get(NAVIGATION_PERCENTAGE),
//				percentages.get(FOCUS_PERCENTAGE),
//				percentages.get(REMOVE_PERCENTAGE),
//				percentages.get(INSERTION_PERCENTAGE),
//				percentages.get(DELETION_PERCENTAGE),
//				percentages.get(EXCEPTIONS_PER_RUNS_PERCENTAGE), 0, 0, 0, 0, 0,
//				0);

		// featureExtractorAnalyzer.onFeatureHandOff(percentages.get(EDIT_PERCENTAGE),
		// percentages.get(DEBUG_PERCENTAGE),
		// percentages.get(NAVIGATION_PERCENTAGE),
		// percentages.get(FOCUS_PERCENTAGE),
		// percentages.get(REMOVE_PERCENTAGE),
		// percentages.get(INSERTION_PERCENTAGE),
		// percentages.get(DELETION_PERCENTAGE),
		// percentages.get(EXCEPTIONS_PER_RUNS_PERCENTAGE),
		// null,null,null,null,null,null);
	}

}
