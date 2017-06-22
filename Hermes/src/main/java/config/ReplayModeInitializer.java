package config;

import difficultyPrediction.ATestPredictionParametersSetter;
import difficultyPrediction.PredictionParametersSetterSelector;
import difficultyPrediction.featureExtraction.ARatioFeaturesFactory;
import difficultyPrediction.featureExtraction.RatioFeaturesFactorySelector;
import difficultyPrediction.metrics.AGenericRatioCalculatorFactory;
import difficultyPrediction.metrics.ATestRatioCalculatorFactory;
import difficultyPrediction.metrics.RatioCalculatorSelector;

public class ReplayModeInitializer {
	public static void configure() {
		RatioFeaturesFactorySelector.setFactory(new ARatioFeaturesFactory());
		RatioCalculatorSelector.setFactory(new AGenericRatioCalculatorFactory());
//		RatioCalculatorSelector.setFactory(new ATestRatioCalculatorFactory());
		PredictionParametersSetterSelector.setSingleton(new ATestPredictionParametersSetter());
//		RatioCalculatorSelector.setFactory(new ARatioCalculatorFactory());
	}

}
