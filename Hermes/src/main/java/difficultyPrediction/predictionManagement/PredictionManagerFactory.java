package difficultyPrediction.predictionManagement;

import difficultyPrediction.Mediator;
import difficultyPrediction.MediatorFactory;

public class PredictionManagerFactory {
//	static PredictionManager predictionManager = new APredictionManager(MediatorFactory.getMediator());
	static PredictionManager predictionManager;


	public static PredictionManager getPredictionManager() {
		if (predictionManager == null) {
//			Mediator aMediator = MediatorFactory.getMediator();
			predictionManager = new APredictionManager();
		}
		
		return predictionManager;
	}

	public static void setPredictionStrategy(PredictionManager newVal) {
		predictionManager = newVal;
	}

}
