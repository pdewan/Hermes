package difficultyPrediction.predictionManagement;

import difficultyPrediction.Mediator;

public class APredictionManager implements PredictionManager {
	
	Mediator mediator;
	
//	public APredictionManager(Mediator mediator) {
//		this.mediator = mediator;
//	}
	public APredictionManager() {
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
	@Override
	public Mediator getMediator() {
		return mediator;
	}
	@Override
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

	@Override
	public void modelBuilt(boolean newVal, Exception anException) {
		if(mediator != null) {
			mediator.predictionManager_modelBuilt(newVal, anException);
		}
		
	}

	@Override
	public void predictionError(Exception e) {
		if(mediator != null) {
			mediator.predictionError(e);
		}
	}
	
	
}
