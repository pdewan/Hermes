package config;

import util.misc.ThreadSupport;
import analyzer.extension.ALiveAnalyzerProcessor;
import analyzer.extension.AnalyzerProcessorFactory;
import analyzer.extension.LiveAnalyzerProcessor;
import analyzer.extension.LiveAnalyzerProcessorFactory;
import analyzer.ui.APredictionController;
import analyzer.ui.balloons.ABalloonCreator;
import analyzer.ui.graphics.LineGraphFactory;
import analyzer.ui.text.AggregatorFactory;
import difficultyPrediction.APredictionParameters;

public class LiveModePredictionConfigurer {
	public static void configure() {
		visualizePrediction();
	}
	// can be called by analyzer
	public static void visualizePrediction() {
		AnalyzerProcessorFactory.setSingleton(LiveAnalyzerProcessorFactory.getSingleton());
//		LiveAnalyzerProcessorFactory.getSingleton();
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
