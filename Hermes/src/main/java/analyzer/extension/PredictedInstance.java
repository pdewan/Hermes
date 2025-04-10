package analyzer.extension;

import weka.core.Instance;

public /**Just a container class to hold each instance and its predicted class
 * 
 * @author wangk1
 *
 */
class PredictedInstance{
	private Instance instance;
	public double prediction;
	public double classification;
	public long time;

	public PredictedInstance(Instance i, double prediction) {
		this.instance=i;
		this.prediction=prediction;

	}
	
	public PredictedInstance(double classification, double prediction, long time) {
		this.time = time;
		this.classification = classification;
		this.prediction = prediction;
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
