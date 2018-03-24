package difficultyPrediction;

import java.beans.PropertyChangeListener;
import java.util.List;

import analyzer.AnalyzerListener;
import analyzer.Resettable;
//import bus.uigen.hermes.HermesLabelBeanModelProxy;
//import bus.uigen.hermes.HermesPropertyListenerRegistererProxy;
import difficultyPrediction.featureExtraction.BarrierListener;
import difficultyPrediction.featureExtraction.RatioFeaturesListener;
import difficultyPrediction.featureExtraction.WebLinkListener;
import difficultyPrediction.statusManager.StatusListener;
import util.models.LabelBeanModel;
import util.models.PropertyListenerRegisterer;

public interface MultiLevelAggregator extends
	RatioFeaturesListener, WebLinkListener, StatusListener, 
	DifficultyPredictionEventListener, 
	PropertyListenerRegisterer, 
	Resettable, BarrierListener, AnalyzerListener{

	String getManualStatus();

//	void setManualStatus(String newVal);

	String getManualBarrier();

//	void setBarrier(String newVal);

	String getAggregatedStatus();

	String getPredictions();

	String getRatios();

	List<LabelBeanModel> getWebLinks();

	String getCorrectStatus();
	void addPropertyChangeListener(PropertyChangeListener aListener);


}
