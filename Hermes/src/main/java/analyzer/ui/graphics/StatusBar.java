package analyzer.ui.graphics;

import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.util.List;

import analyzer.Resettable;
import difficultyPrediction.statusManager.StatusListener;

public interface StatusBar extends MouseListener, PropertyChangeListener,
		DuriRatioFeaturesListener, StatusListener, Resettable {

	public void setData(List<Integer> newPredictedList,
			List<Integer> newActualList);

	public void paint(Graphics g);

}
