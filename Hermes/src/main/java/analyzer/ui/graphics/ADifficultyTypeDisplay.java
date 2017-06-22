package analyzer.ui.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class ADifficultyTypeDisplay extends JPanel implements
		DifficultyTypeDisplay {

	private static final long serialVersionUID = -5413630047109983241L;
	private List<String> difficultyTypeList = new ArrayList<String>();
	private PlayAndRewindCounter counter;
	private RatioFileReader ratioFileReader;
	private int X_BORDER_GAP = 60;
	private int Y_BORDER_GAP = 10;
	private static final int GRAPH_POINT_WIDTH = 8;

	public ADifficultyTypeDisplay(PlayAndRewindCounter aCounter,
			RatioFileReader aRatioFileReader) {
		setBackground(Color.LIGHT_GRAY);
		addMouseListener(this);
		counter = aCounter;
		counter.addPropertyChangeListener(this);
		ratioFileReader = aRatioFileReader;
		ratioFileReader.addPropertyChangeListener(this);
	}

	public void paint(Graphics g) {
		super.paint(g); // clears the window
		Graphics2D g2 = (Graphics2D) g;
		g2.drawString("Type", 5, getHeight() / 2);
		int xPos = 0;
		for (int i = counter.getStart(); i < counter.getEnd() - 1; i++) {
			if (i < difficultyTypeList.size() - 1 && i >= 0) {
				if (difficultyTypeList.get(i) != null) {
					int x = (xPos * (getWidth() - X_BORDER_GAP * 2)
							/ (counter.getSize() - 1) + X_BORDER_GAP);
					int y = Y_BORDER_GAP;
					if (difficultyTypeList.get(i).equalsIgnoreCase("design")) {
						g2.setColor(new Color(201, 24, 10)); // red
						g2.fillOval(x, y, GRAPH_POINT_WIDTH, GRAPH_POINT_WIDTH);
					} else if (difficultyTypeList.get(i).equalsIgnoreCase(
							"undesirable output")) {
						g2.setColor(new Color(235, 172, 10)); // yellow
						g2.fillOval(x, y, GRAPH_POINT_WIDTH, GRAPH_POINT_WIDTH);
					} else if (difficultyTypeList.get(i)
							.equalsIgnoreCase("API")) {
						g2.setColor(new Color(10, 190, 201)); // teal
						g2.fillOval(x, y, GRAPH_POINT_WIDTH, GRAPH_POINT_WIDTH);
					}
					xPos++;
				}
			} else {
				break;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equalsIgnoreCase("newRatioFeatures")) {
			newRatios((RatioFileComponents) evt.getNewValue());
			repaint();
		} else if (evt.getPropertyName().equalsIgnoreCase("start")
				|| evt.getPropertyName().equalsIgnoreCase("size")) {
			repaint();
		}

	}

	@Override
	public void newRatios(RatioFileComponents ratioFeatures) {
		difficultyTypeList.add(ratioFeatures.getDifficultyType());

	}

	@Override
	public void setData(List<String> newDifficultyTypeList) {
		difficultyTypeList = newDifficultyTypeList;
		repaint();

	}

}
