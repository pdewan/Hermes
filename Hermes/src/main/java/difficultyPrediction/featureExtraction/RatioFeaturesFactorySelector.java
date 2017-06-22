package difficultyPrediction.featureExtraction;

public class RatioFeaturesFactorySelector {
	static RatioFeaturesFactory factory = new ARatioFeaturesFactory();
//	static RatioFeaturesFactory factory;

	public static RatioFeaturesFactory getFactory() {
		return factory;
	}
	public static void setFactory(RatioFeaturesFactory newVal) {
		RatioFeaturesFactorySelector.factory = newVal;
	}
	public static  RatioFeatures createRatioFeatures() {
		return factory.createRatioFeatures();
	}
	
	public static void init() {
		factory = new ARatioFeaturesFactory();
	}
	
}
