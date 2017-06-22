package difficultyPrediction.extension;

import difficultyPrediction.DifficultyPredictionPluginEventProcessor;

public interface DifficultyPredictionRegistry {

	public abstract void registerDifficultyPredictionListeners(
			DifficultyPredictionPluginEventProcessor aDifficultyPredictionPluginEventProcessor);

}