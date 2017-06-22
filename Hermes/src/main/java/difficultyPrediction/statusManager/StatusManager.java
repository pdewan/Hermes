package difficultyPrediction.statusManager;

import difficultyPrediction.Mediator;

public class StatusManager {
	
	Mediator mediator;
	public StatusManagerStrategy strategy;
	
	public StatusManager(Mediator mediator) {
		this.mediator = mediator;
	}
	
	public void onStatusHandOff(String predictionValue) {
		if(mediator != null) {
			StatusManagerDetails details = new StatusManagerDetails();
			details.predictionValue = predictionValue;
			mediator.statusManager_HandOffStatus(this, details);
		}
	}
	
	
}
