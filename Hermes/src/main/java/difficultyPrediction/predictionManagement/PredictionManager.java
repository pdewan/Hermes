package difficultyPrediction.predictionManagement;

import difficultyPrediction.Mediator;

public interface PredictionManager {

	public abstract void onPredictionHandOff(String predictionValue);
	void modelBuilt(boolean newVal, Exception anException);
	void predictionError(Exception e);

	PredictionManagerStrategy getPredictionStrategy();

	void setPredictionStrategy(PredictionManagerStrategy predictionStrategy);
	Mediator getMediator();
	void setMediator(Mediator mediator);

}