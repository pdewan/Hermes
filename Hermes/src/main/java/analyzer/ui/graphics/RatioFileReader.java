package analyzer.ui.graphics;

import java.beans.PropertyChangeListener;
import java.util.List;

//import bus.uigen.hermes.HermesPropertyListenerRegistererProxy;

import util.models.PropertyListenerRegisterer;

public interface RatioFileReader extends PropertyListenerRegisterer 
{
	public static final String START_RATIOS = "startRatioFeatures";
	public static final String END_RATIOS = "endRatioFeatures";
	public static final String NEW_RATIO = "newRatioFeatures";

	
	public void readFile(String fileName);
	public String getPath();
	void addPropertyChangeListener(PropertyChangeListener aListener);
//	List<RatioFileComponents> getRatioFeaturesList();

}
