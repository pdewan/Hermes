package config;

import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.PredictionParametersSetterSelector;

public class FactorySingletonInitializer {
	static boolean configured  = false;
	public static void configure() {
		if (configured)
			return;
		configured = true; // configure can be called from ADifficultyPredictionPluginEventProcessor each time it is created by AnAnalyzer
		if (DifficultyPredictionSettings.isReplayMode()) {
			ReplayModeInitializer.configure();
		}
		else 
			LiveModeInitializer.configure();

		
	PredictionParametersSetterSelector.getSingleton().setPredictionParameters();
	}

}
