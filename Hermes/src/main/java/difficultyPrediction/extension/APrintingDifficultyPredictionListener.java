package difficultyPrediction.extension;

import difficultyPrediction.DifficultyPredictionEventListener;
import difficultyPrediction.featureExtraction.RatioFeatures;
import fluorite.commands.EHICommand;

public class APrintingDifficultyPredictionListener implements DifficultyPredictionEventListener{

	@Override
	public void newCommand(EHICommand newCommand) {
		System.out.println("Extension**New User/Prediction Command:" + newCommand);		
	}
	@Override
	public void commandProcessingStarted() {
		System.out.println("Extension**Difficulty Prediction Started");		
	}
	public void startTimeStamp(long aStartTimeStamp) {
		System.out.println("Extension**Difficulty Prediction Started");		

	}
	@Override
	public void commandProcessingStopped() {
		System.out.println("Extension**Difficulty Prediction Stopped");			
	}
	@Override
	public void newRatios(RatioFeatures newVal) {
		System.out.println("Extension**New Ratios:" + newVal);		
	}

}
