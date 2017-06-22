package analyzer.ui.graphics;

import java.beans.PropertyChangeListener;

import util.models.PropertyListenerRegisterer;
import analyzer.Resettable;
//import bus.uigen.hermes.HermesPropertyListenerRegistererProxy;

public interface PlayAndRewindCounter extends PropertyListenerRegisterer,
		PropertyChangeListener, Resettable {
	public void back();

	void setStart(int newValue);

	public void play();

	public void rewind();

	public void pause();

	public void forward();

	public int getStart();

	public int getEnd();

	public int getSize();

	public void setSize(int size);

	public int getCurrentTime();

	public void setCurrentFeatureIndex(int newVal);

	public boolean preRewind();

	public boolean prePause();

	public boolean prePlay();
	void addPropertyChangeListener(PropertyChangeListener aListener);

}
