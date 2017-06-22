package analyzer.tracker;

import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.filters.Filter;


/**
 * @author wangk1
 *
 */

public interface PredictionTracker {
	
	/**Reload or load the instances again from the specified training set arff and testing set arff files.
	 */
	void loadInstances();
	
	
	/**Set new testing instance file
	 * 
	 * @param testingInstance
	 */
	void setTestingFile(String testingInstanceFile);
	
	
	/**Load the training set from the specified file
	 * 
	 * @param trainingInstanceFile
	 */
	void setTrainingFile(String trainingInstanceFile);
	
	PredictionTracker setTrainingInstance(Instances i);

	/**Build the classifier with the provided training set<p>
	 * 
	 * Note that classifier only has to be built once and can be reused. Only invoke if wanting a new classifier
	 * 
	 * @return
	 */
	PredictionTracker buildClassifier(Classifier c);
	
	/**Filter training instances with filter
	 * 
	 * @param f
	 */
	void filterTraining(Filter f);
	
	/**Evaluate the testing with the classifier
	 * 
	 * @return
	 */
	PredictionTracker evaluateTesting();
	
	
	/**Output the result of evaluationtesting on the {@link PredictionOutputter}<br>
	 * If the stream should be open, set keepStreamOpen to true
	 * 
	 * @param p
	 */
	PredictionOutputter outputResults(PredictionOutputter p,boolean...keepStreamOpen);
	
	/**Outputs predicton results as list
	 * 
	 * @return
	 */
	List<PredictedInstance> outputResultsAsList();
	
	Instances getTestingInstances();
	
	Instances getTrainingInstances();
	
}
