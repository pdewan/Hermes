package difficultyPrediction.predictionManagement;

public interface PredictionManager {

	public abstract void onPredictionHandOff(String predictionValue);
	void modelBuilt(boolean newVal, Exception anException);
	void predictionError(Exception e);

	PredictionManagerStrategy getPredictionStrategy();

	void setPredictionStrategy(PredictionManagerStrategy predictionStrategy);

}