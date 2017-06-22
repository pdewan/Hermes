package difficultyPrediction.featureExtraction;

public interface RatioBasedFeatureExtractor {

	public abstract void onFeatureHandOff(double editRatio, double debugRatio,
			double navigationRatio, double focusRatio, double removeRatio, long timeStamp);

	public abstract void onFeatureHandOff(double editRatio, double debugRatio,
			double navigationRatio, double focusRatio, double removeRatio,
			double exceptionsPerRun);
	

	FeatureExtractionStrategy getFeatureExtractionStrategy();

	void setFeatureExtractionStrategy(
			FeatureExtractionStrategy featureExtractionStrategy);

	void onFeatureHandOff(RatioFeatures aRatioFeatures);

}