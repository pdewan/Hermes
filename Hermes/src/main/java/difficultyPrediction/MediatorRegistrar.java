package difficultyPrediction;

import difficultyPrediction.featureExtraction.BarrierListener;
import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.featureExtraction.RatioFeaturesListener;
import difficultyPrediction.featureExtraction.WebLinkListener;
import difficultyPrediction.statusManager.StatusListener;
import fluorite.commands.DifficultyCommand;
import fluorite.commands.EHICommand;

public interface MediatorRegistrar {
	
	public void addRatioFeaturesListener(RatioFeaturesListener aRatioFeaturesListener) ;
	
	public void removeRatioFeaturesListener(RatioFeaturesListener aRatioFeaturesListener) ;
	
	public void addStatusListener(StatusListener aListener) ;
	public void removeStatusListener(StatusListener aListener) ;
	
	public void addWebLinkListener(WebLinkListener aListener) ;
	public void removeWebLinkListener(WebLinkListener aListener) ;
		
	public void  notifyNewRatios(RatioFeatures aRatios) ;
	
	public void  notifyNewStatus(String aStatus) ;
	public  void  notifyNewManualStatus(String aStatus);
	public  void  notifyNewManualStatus(DifficultyCommand aCommand);

	public  void  notifyNewAggregateStatus(String aStatus);

	

	public abstract void addPluginEventListener(
			PluginEventListener aListener);

	public abstract void removePluginEventListener(
			PluginEventListener aListener);

	public abstract void notifyStartCommand();

	public abstract void notifyStopCommand();

	public abstract void notifyNewCommand(EHICommand aCommand);

	void addBarrierListener(BarrierListener aListener);

	void removeBarrierListener(BarrierListener aListener);

	void notifyNewReplayedStatus(int aStatus);

}
