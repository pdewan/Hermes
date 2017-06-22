package analyzer.ui.video;

import java.beans.PropertyChangeListener;

//import bus.uigen.hermes.HermesPropertyListenerRegistererProxy;
//import util.models.PropertyListenerRegisterer;
import context.recording.DisplayBoundsOutputter;
import util.models.PropertyListenerRegisterer;

public interface LocalScreenRecorderAndPlayer extends DisplayBoundsOutputter,
	PropertyListenerRegisterer
	{
	public void seek(long aTime);
	public void play();
	public void pause();
	public long getWallTime();
	void addPropertyChangeListener(PropertyChangeListener aListener);
	

}
