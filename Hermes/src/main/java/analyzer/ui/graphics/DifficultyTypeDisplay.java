package analyzer.ui.graphics;

import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.util.List;

public interface DifficultyTypeDisplay extends MouseListener,
		PropertyChangeListener, DuriRatioFeaturesListener {
	public void setData(List<String> newDifficultyTypeList);

	public void paint(Graphics g);
}
