package difficultyPrediction.predictionManagement;

import difficultyPrediction.featureExtraction.RatioFeatures;

public interface PredictionManagerStrategy {
	public static final String PROGRESS_PREDICTION = "NO";
	public static final String DIFFICULTY_PREDICTION = "YES";
	public void predictSituation(double editRatio, double debugRatio, double navigationRatio, double focusRatio, double removeRatio);
	public String predictSituation(RatioFeatures aRatioFeatures);
	PredictionManager getPredictionManager();
	void setPredictionManager(PredictionManager predictionManager);

}
