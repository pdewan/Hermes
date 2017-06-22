package difficultyPrediction.predictionManagement;

public interface PredictionManager {

	public abstract void onPredictionHandOff(String predictionValue);

	PredictionManagerStrategy getPredictionStrategy();

	void setPredictionStrategy(PredictionManagerStrategy predictionStrategy);

}