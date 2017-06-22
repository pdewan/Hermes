package analyzer.extension;

import java.util.List;

public interface PredictionTracker {
	
	/**Reload or load the instances again from the specified training set arff and testing set arff.
	 * 
	 * @return
	 */
	PredictionTracker loadInstances();

	/**Build the classifier with the provided training set<p>
	 * 
	 * Note that classifier only has to be built once and can be reused. Only invoke if wanting a new classifier
	 * 
	 * @return
	 */
	PredictionTracker buildClassifier();
	
	/**Evaluate the testing with the classifier
	 * 
	 * @return
	 */
	List<PredictedInstance> evaluateTesting();
	
}
