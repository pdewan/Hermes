/**
 * @author Nils Persson
 * @date 2018-Oct-27 7:24:54 PM 
 */
package analyzer.nils;

import difficultyPrediction.Mediator;
import difficultyPrediction.featureExtraction.ARatioBasedFeatureExtractor;
import difficultyPrediction.featureExtraction.RatioBasedFeatureExtractor;

/**
 * 
 */
public class ANilsFeatureExtractor extends ARatioBasedFeatureExtractor implements RatioBasedFeatureExtractor{

	/**
	 * @param mediator
	 */
	public ANilsFeatureExtractor(Mediator mediator) {
		super(mediator);
		// TODO Auto-generated constructor stub
	}

}
