package difficultyPrediction.predictionManagement;

import difficultyPrediction.Mediator;

public class APredictionManager implements PredictionManager {
	
	Mediator mediator;
	
	public APredictionManager(Mediator mediator) {
		this.mediator = mediator;
	}
	
	PredictionManagerStrategy predictionStrategy;
	
	/* (non-Javadoc)
	 * @see difficultyPrediction.predictionManagement.PredictionManager#onPredictionHandOff(java.lang.String)
	 */
	@Override
	public void onPredictionHandOff(String predictionValue) {
		if(mediator != null) {
			APredictionManagerDetails details = new APredictionManagerDetails(predictionValue);
			mediator.predictionManager_HandOffPrediction(this, details);
		}
	}

	public Mediator getMediator() {
		return mediator;
	}

	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
	}
	@Override
	public PredictionManagerStrategy getPredictionStrategy() {
		return predictionStrategy;
	}
	@Override
	public void setPredictionStrategy(PredictionManagerStrategy predictionStrategy) {
		this.predictionStrategy = predictionStrategy;
	}
	
	
}
