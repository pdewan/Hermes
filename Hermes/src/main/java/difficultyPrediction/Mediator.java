package difficultyPrediction;

import difficultyPrediction.eventAggregation.AnEventAggregator;
import difficultyPrediction.eventAggregation.AnEventAggregatorDetails;
import difficultyPrediction.eventAggregation.EventAggregator;
import difficultyPrediction.featureExtraction.RatioBasedFeatureExtractor;
import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.featureExtraction.RatioFeaturesListener;
import difficultyPrediction.predictionManagement.APredictionManagerDetails;
import difficultyPrediction.predictionManagement.PredictionManager;
import difficultyPrediction.statusManager.StatusListener;
import difficultyPrediction.statusManager.StatusManager;
import difficultyPrediction.statusManager.StatusManagerDetails;
import fluorite.commands.EHICommand;


public interface Mediator extends MediatorRegistrar {
	public void eventAggregator_HandOffEvents(AnEventAggregator aggregator, AnEventAggregatorDetails details);
//	public void featureExtractor_HandOffFeatures(RatioBasedFeatureExtractor extractor, AFeatureExtractorDetails details);
	public void predictionManager_HandOffPrediction(PredictionManager manager, APredictionManagerDetails details);
	public void statusManager_HandOffStatus(StatusManager manager, StatusManagerDetails details);
	void featureExtractor_HandOffFeatures(RatioBasedFeatureExtractor extractor,
			RatioFeatures details);
	void processEvent(EHICommand e);
	public EventAggregator getEventAggregator();


	public void setEventAggregator(EventAggregator eventAggregator) ;


	public RatioBasedFeatureExtractor getFeatureExtractor() ;


	public void setFeatureExtractor(RatioBasedFeatureExtractor featureExtractor);


	public PredictionManager getPredictionManager() ;


	public void setPredictionManager(PredictionManager predictionManager) ;


	public StatusManager getStatusManager() ;


	public void setStatusManager(StatusManager statusManager) ;

	public StatusInformation getStatusInformation() ;


	public void setStatusInformation(StatusInformation statusInformation) ;
//	public void addRatioFeaturesListener(RatioFeaturesListener aRatioFeaturesListener) ;
//	
//	public void removeRatioFeaturesListener(RatioFeaturesListener aRatioFeaturesListener) ;
//	
//	public void addStatusListener(StatusListener aListener) ;
//	public void removeStatusListener(StatusListener aListener) ;
//		
//	public void  notifyNewRatios(RatioFeatures aRatios) ;
//	
//	public void  notifyNewStatus(String aStatus) ;
//	public abstract void addPluginEventEventListener(
//			PluginEventListener aListener);
//
//	public abstract void removePluginEventListener(
//			PluginEventListener aListener);
//
//	public abstract void notifyStartCommand();
//
//	public abstract void notifyStopCommand();
//
//	public abstract void notifyNewCommand(ICommand aCommand);
}
