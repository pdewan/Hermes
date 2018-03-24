package config;

//import dayton.ServerStatusUpdaterFactory;
import difficultyPrediction.ATestPredictionParametersSetter;
import difficultyPrediction.PredictionParametersSetterSelector;
import difficultyPrediction.featureExtraction.ARatioFeaturesFactory;
import difficultyPrediction.featureExtraction.RatioFeaturesFactorySelector;
import difficultyPrediction.metrics.AGenericRatioCalculatorFactory;
import difficultyPrediction.metrics.RatioCalculatorSelector;
import difficultyPrediction.web.WebBrowserAccessorFactory;

public class LiveModeInitializer {
	public static void configure() {
		RatioFeaturesFactorySelector.setFactory(new ARatioFeaturesFactory());
		RatioCalculatorSelector.setFactory(new AGenericRatioCalculatorFactory());

//		RatioCalculatorSelector.setFactory(new ATestRatioCalculatorFactory());
//		RatioCalculatorSelector.setFactory(new ARatioCalculatorFactory());
//		PredictionParametersSetterSelector.setSingleton(new APredictionParametersSetter());
		PredictionParametersSetterSelector.setSingleton(new ATestPredictionParametersSetter());
//		RecorderFactory.getSingleton().connectToDisplay();
//		LocalScreenPlayerFactory.getSingleton(); // does not subsume RecorderFactory
//		ServerStatusUpdaterFactory.getOrCreateSingleton();
//		SarosAccessorFactory.createSingleton();
		WebBrowserAccessorFactory.getSingleton();
//		(new ADisplayBoundsFileWriter()).start();;
	}
}