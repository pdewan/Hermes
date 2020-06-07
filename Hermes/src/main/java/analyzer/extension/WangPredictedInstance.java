package analyzer.extension;

import weka.core.Instance;

public /**Just a container class to hold each instance and its predicted class
 * 
 * @author wangk1
 *
 */
class WangPredictedInstance{
	private Instance instance;
	private double prediction;

	public WangPredictedInstance(Instance i, double prediction) {
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
