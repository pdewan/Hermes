package analyzer.extension;

import java.util.Date;

import analyzer.RatioFilePlayerFactory;
import analyzer.ui.graphics.DuriRatioFeaturesListener;
import analyzer.ui.graphics.RatioFileComponents;
import difficultyPrediction.featureExtraction.RatioFeatures;
import fluorite.commands.EHICommand;

public class AFileReplayAnalyzerProcessor extends ALiveAnalyzerProcessor{
	public AFileReplayAnalyzerProcessor() {
		RatioFilePlayerFactory.getSingleton().addPluginEventListener(this);
		RatioFilePlayerFactory.getSingleton().addRatioFeaturesListener(this);
	}
	// do nothing, the new Ratios has everything needed
	public void newCommand(EHICommand newCommand) {
//		maybeInitializeTimeStamp(newCommand);
//		maybeProcessPrediction(newCommand);
//		maybeProcessCorrection(newCommand);
//		//		if (newCommand.getTimestamp() == 0 && newCommand.getTimestamp2() != 0) {
//		//			newStartTimeStamp(newCommand.getTimestamp2() );
//		//		}
//		System.out.println("Extension**New User/Prediction Command:" + newCommand);	

	}
	public void newRatios(RatioFeatures newVal) {
		RatioFileComponents aRatioFeatures = (RatioFileComponents) newVal;
		participantTimeLine.getTimeStampList().add(aRatioFeatures.getSavedTimeStamp());
		participantTimeLine.getDebugList().add(aRatioFeatures.getDebugRatio());
		participantTimeLine.getDeletionList().add(aRatioFeatures.getDeletionRatio());
		participantTimeLine.getEditList().add(aRatioFeatures.getEditRatio());
		participantTimeLine.getFocusList().add(aRatioFeatures.getFocusRatio());
		participantTimeLine.getInsertionList().add(aRatioFeatures.getInsertionRatio());		
		participantTimeLine.getNavigationList().add(aRatioFeatures.getNavigationRatio());
		participantTimeLine.getPredictions().add(aRatioFeatures.getPredictedStatus());
		participantTimeLine.getWebLinks().add(aRatioFeatures.getWebLinkList());
//		if (! (aRatioFeatures.getPredictedStatus() != aRatioFeatures.getActualStatus())) {
			participantTimeLine.getPredictionCorrections().add(aRatioFeatures.getActualStatus()) ;
//		}
		String type = aRatioFeatures.getDifficultyType();
//		if (!(type != null || type.isEmpty())) {
			StuckPoint aStuckPoint = new AStuckPoint();
			aStuckPoint.setType(type);		
			participantTimeLine.getStuckPoint().add(aStuckPoint);
//		}
		
		System.err.println("Extension**New Ratios:" + newVal + " at time:" + (new Date(currentTime)).toString());		
	}

}
