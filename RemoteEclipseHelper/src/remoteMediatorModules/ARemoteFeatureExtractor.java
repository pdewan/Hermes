/**
 * @author Nils Persson
 * @date 2018-Mar-30 6:29:40 PM 
 */
package remoteMediatorModules;

import analyzer.AnAnalyzer;
import difficultyPrediction.AStatusInformation;
import difficultyPrediction.Mediator;
import difficultyPrediction.StatusInformation;
import difficultyPrediction.featureExtraction.ARatioBasedFeatureExtractor;
import difficultyPrediction.featureExtraction.ARatioFeatures;
import difficultyPrediction.featureExtraction.RatioFeatures;
import remoteMessaging.RemoteMessageSenderFactory;
import remoteModuleSelection.MediatorModule;
import util.trace.Tracer;
import util.trace.difficultyPrediction.NewExtractedFeatures;
import util.trace.difficultyPrediction.NewExtractedStatusInformation;

/**
 * 
 */
public class ARemoteFeatureExtractor extends ARatioBasedFeatureExtractor implements RemoteFeatureExtractor{

	/**
	 * @param mediator
	 */
	public ARemoteFeatureExtractor(Mediator mediator) {
		super(mediator);
	}
	
	@Override
	public void onFeatureHandOff(double editRatio, double debugRatio, double navigationRatio, double focusRatio,
			double removeRatio, long timeStamp) {
		if (super.mediator != null)         
        {
            RatioFeatures args = new ARatioFeatures();
            args.setEditRatio(editRatio);
            args.setDebugRatio(debugRatio);
            args.setNavigationRatio(navigationRatio);
            args.setFocusRatio(focusRatio);
            args.setRemoveRatio(removeRatio);
            args.setExceptionsPerRun(0);
            args.setSavedTimeStamp(timeStamp);
            NewExtractedFeatures.newCase(args.toString(), this);         
            
            RemoteMessageSenderFactory.getSingleton().sendMessage(args);
        }
	}
	
//	public void handOff(RatioFeatures details){
//		Tracer.info(this, "difficultyRobot.featureExtractor");
//		StatusInformation statusInformation = new AStatusInformation();
//		statusInformation.setEditRatio(details.getEditRatio());
//		statusInformation.setDebugRatio(details.getDebugRatio());
//		statusInformation.setNavigationRatio(details.getNavigationRatio());
//		statusInformation.setRemoveRatio(details.getRemoveRatio());
//		statusInformation.setFocusRatio(details.getFocusRatio());
//		NewExtractedStatusInformation.newCase(statusInformation.toString(), this);
//		mediator.notifyNewRatios(details);
//		AnAnalyzer.maybeRecordFeatures(details);
//		
//		RemoteMessageSenderFactory.getSingleton().sendMessage(MediatorModule.FEATURE_EXTRACTOR, details);
//	}

}
