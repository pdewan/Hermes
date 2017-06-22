package difficultyPrediction.extension;

import difficultyPrediction.DifficultyPredictionPluginEventProcessor;

public class ADifficultyPredictionRegistry implements DifficultyPredictionRegistry {
	static DifficultyPredictionRegistry instance;
	/* (non-Javadoc)
	 * @see difficultyPrediction.extension.DifficultyPredictionRegistry#registerDifficultyPredictionListeners(difficultyPrediction.DifficultyPredictionPluginEventProcessor)
	 */
	//
	@Override
	public void registerDifficultyPredictionListeners(DifficultyPredictionPluginEventProcessor aDifficultyPredictionPluginEventProcessor) {
//		aDifficultyPredictionPluginEventProcessor.
//			addDifficultyPredictionEventListener(new APrintingDifficultyPredictionListener());		
	}
	
	public static DifficultyPredictionRegistry getInstance() {
		if (instance == null)
			instance = new ADifficultyPredictionRegistry();
		return instance;
	}

}
