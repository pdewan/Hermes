package difficultyPrediction.predictionManagement;

import org.eclipse.ui.internal.views.markers.ProblemsSeverityAndDescriptionConfigurationArea;

import difficultyPrediction.MediatorFactory;

public class PredictionManagerStrategyFactory {
//	static PredictionManagerStrategy predictionManagerStrategy = 
//			       new DecisionTreeModel(PredictionManagerFactory.getPredictionManager());
	static PredictionManagerStrategy predictionManagerStrategy ;

	public static PredictionManagerStrategy getPredictionManagerStrategy() {
		if (predictionManagerStrategy == null) {
			predictionManagerStrategy = new DecisionTreeModel();			
			predictionManagerStrategy.setPredictionManager(PredictionManagerFactory.getPredictionManager());
		}
		return predictionManagerStrategy;
	}

	public static void setPredictionManagerStrategy(PredictionManagerStrategy newVal) {
		PredictionManagerStrategyFactory.predictionManagerStrategy = newVal;
		PredictionManager aPredictionManager = PredictionManagerFactory.getPredictionManager();
		predictionManagerStrategy.setPredictionManager(aPredictionManager);
		aPredictionManager.setPredictionStrategy(predictionManagerStrategy);
	}

}
