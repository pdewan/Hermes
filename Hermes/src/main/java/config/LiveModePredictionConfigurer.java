package config;

import analyzer.extension.RatioFileGeneratorFactory;
import analyzer.extension.LiveAnalyzerProcessorFactory;
import analyzer.ui.APredictionController;
import analyzer.ui.balloons.ABalloonCreator;
import analyzer.ui.graphics.LineGraphFactory;
import analyzer.ui.text.AggregatorFactory;
import difficultyPrediction.APredictionParameters;
import difficultyPrediction.metrics.logging.MetricsFileGeneratorFactory;

public class LiveModePredictionConfigurer {
	public static void configure() {
		if (HelperConfigurationManagerFactory.getSingleton().isLogMetrics()) {
			MetricsFileGeneratorFactory.getSingleton();
		}
		if (HelperConfigurationManagerFactory.getSingleton().isVisualizePredictions())
		visualizePrediction();
	}
	// can be called by analyzer
	public static void visualizePrediction() {
		RatioFileGeneratorFactory.setSingleton(LiveAnalyzerProcessorFactory.getSingleton());
		LiveAnalyzerProcessorFactory.getSingleton();
		LineGraphFactory.createSingleton();
		APredictionParameters.getInstance();
		AggregatorFactory.createSingleton();
//		LocalScreenRecorderAndPlayerFactory.createSingleton();
		ABalloonCreator.getInstance();
//		ThreadSupport.sleep(10000);
		APredictionController.createUI();
		
//		LineGraphComposer.composeUI();
// 		APredictionParameters.createUI();
// 		AMultiLevelAggregator.createUI();
// 		ALocalScreenRecorderAndPlayer.createUI();
// 		ABalloonCreator.createUI();
	}


}
