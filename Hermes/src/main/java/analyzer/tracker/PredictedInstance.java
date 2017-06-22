package analyzer.tracker;

import weka.core.Instance;

/**Just a container class to hold each instance and its predicted class
 * 
 * @author wangk1
 *
 */
public class PredictedInstance{
	private Instance instance;
	private double prediction;

	public PredictedInstance(Instance i, double prediction) {
		this.instance=i;
		this.prediction=prediction;

	}

	public Instance getInstance() {
		return instance;
	}

	public double getPrediction() {
		return prediction;
	}

	public String getPredictionAsString() {
		return instance.classAttribute().value((int) prediction);

	}
	
	@Override
	public String toString() {
		return instance.toString()+" ; Prediction: "+getPredictionAsString();
		
	}
}
