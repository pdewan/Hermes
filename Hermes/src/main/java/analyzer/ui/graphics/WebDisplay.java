package analyzer.ui.graphics;

import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.util.List;

import analyzer.WebLink;

public interface WebDisplay extends MouseListener, PropertyChangeListener,
		DuriRatioFeaturesListener {
	public void setData(List<List<WebLink>> newWebsiteList);

	public void paint(Graphics g);

}
