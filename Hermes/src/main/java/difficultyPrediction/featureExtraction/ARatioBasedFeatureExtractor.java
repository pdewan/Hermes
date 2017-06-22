package difficultyPrediction.featureExtraction;

import difficultyPrediction.Mediator;
import util.trace.difficultyPrediction.NewExtractedFeatures;

public class ARatioBasedFeatureExtractor implements RatioBasedFeatureExtractor {
	
	Mediator mediator;
	
	public ARatioBasedFeatureExtractor(Mediator mediator) {
		this.mediator = mediator;
	}
	
	 FeatureExtractionStrategy featureExtractionStrategy;
	
	

	/* (non-Javadoc)
	 * @see difficultyPrediction.featureExtraction.RatioBasedFeatureExtractor#onFeatureHandOff(double, double, double, double, double)
	 */
	@Override
	public void onFeatureHandOff(double editRatio, double debugRatio, double navigationRatio, double focusRatio,
			double removeRatio, long timeStamp) {
		if (mediator != null)                           //Any handlers attached to this event?  
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
            mediator.featureExtractor_HandOffFeatures(this, args);                       //Raise the event  
        }
	}
	
	 /* (non-Javadoc)
	 * @see difficultyPrediction.featureExtraction.RatioBasedFeatureExtractor#onFeatureHandOff(double, double, double, double, double, double)
	 */
	@Override
	public void onFeatureHandOff(double editRatio, double debugRatio, double navigationRatio, double focusRatio, double removeRatio, double exceptionsPerRun)
     {
		 if (mediator != null)                           //Any handlers attached to this event?  
	        {
//	            ARatioFeatures args = new ARatioFeatures();
				RatioFeatures args = RatioFeaturesFactorySelector.createRatioFeatures();
	            args.setEditRatio(editRatio);
	            args.setDebugRatio(debugRatio);
	            args.setNavigationRatio(navigationRatio);
	            args.setFocusRatio(focusRatio);
	            args.setRemoveRatio(removeRatio);
	            args.setExceptionsPerRun(exceptionsPerRun);
	            mediator.featureExtractor_HandOffFeatures(this, args);                       //Raise the event  
	        }
		 
     }
	@Override
	public void onFeatureHandOff(RatioFeatures aRatioFeatures)
     {
		 if (mediator != null)                           //Any handlers attached to this event?  
	        {
//	            
	            mediator.featureExtractor_HandOffFeatures(this, aRatioFeatures);                       //Raise the event  
	        }
		 
     }
	@Override
	public FeatureExtractionStrategy getFeatureExtractionStrategy() {
		return featureExtractionStrategy;
	}
	@Override
	public void setFeatureExtractionStrategy(
			FeatureExtractionStrategy featureExtractionStrategy) {
		this.featureExtractionStrategy = featureExtractionStrategy;
	}
	 
	 
	
}
