/**
 * @author Nils Persson
 * @date 2018-Oct-27 7:41:53 PM 
 */
package analyzer.nils;

import java.util.List;

import difficultyPrediction.featureExtraction.ExtractRatiosBasedOnNumberOfEvents;
import difficultyPrediction.featureExtraction.FeatureExtractionStrategy;
import difficultyPrediction.featureExtraction.RatioBasedFeatureExtractor;
import fluorite.commands.EHICommand;

/**
 * 
 */
public class ANilsFeatureExtractionStrategy extends 
		ExtractRatiosBasedOnNumberOfEvents implements FeatureExtractionStrategy{

	@Override
	public void performFeatureExtraction(List<EHICommand> actions, 
			RatioBasedFeatureExtractor featureExtractor) {
		System.out.println("*************** NILS FEATURE EXTRACTOR STRATEGY ****************");
		super.performFeatureExtraction(actions, featureExtractor);
		
		
		for(EHICommand command : actions){
			System.out.println("hiloo");
		}
	}
}
