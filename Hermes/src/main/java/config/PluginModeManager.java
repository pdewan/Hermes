package config;

import difficultyPrediction.DifficultyPredictionSettings;

public class PluginModeManager {
	static boolean pluginMode;
	public static boolean isPluginMode() {
		return pluginMode;
	}

	public static void setPluginMode(boolean newVal) {
		pluginMode = newVal;
	}

}
