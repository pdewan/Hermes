package difficultyPrediction.featureExtraction;

import java.beans.PropertyChangeListener;

//import bus.uigen.hermes.HermesPropertyListenerRegistererProxy;
import util.models.PropertyListenerRegisterer;

public interface RatioFetauresObservable 
	extends PropertyListenerRegisterer 
	{
	void addRatioFeaturesListener(RatioFeaturesListener aListener);

	void removeRatioFetauresListener(RatioFeaturesListener aListener);
	void addPropertyChangeListener(PropertyChangeListener aListener);


}
