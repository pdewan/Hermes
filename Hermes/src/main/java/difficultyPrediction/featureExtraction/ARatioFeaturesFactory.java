package difficultyPrediction.featureExtraction;

public class ARatioFeaturesFactory implements RatioFeaturesFactory {
	
	
	public   RatioFeatures createRatioFeatures() {
		return new ARatioFeatures();
	}
	
}
