package analyzer.ui.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import analyzer.WebLink;

public class AWebDisplay extends JPanel implements WebDisplay {

	private static final long serialVersionUID = -1030791239928741198L;
	private List<List<WebLink>> websiteList = new ArrayList<List<WebLink>>();
	private PlayAndRewindCounter counter;
	private RatioFileReader ratioFileReader;
	private int X_BORDER_GAP = 60;
	private int Y_BORDER_GAP = 10;

	public AWebDisplay(PlayAndRewindCounter aCounter,
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
		int xPos = 0;
		for (int i = counter.getStart(); i < counter.getEnd() - 1; i++) {
			if (i < websiteList.size() - 1 && i >= 0) {
				if (websiteList.get(i) != null) {
					int x = (xPos * (getWidth() - X_BORDER_GAP * 2)
							/ (counter.getSize() - 1) + X_BORDER_GAP);
					int y = Y_BORDER_GAP;
					String webLabel = "W(" + websiteList.get(i).size() + ")";
					g2.setColor(new Color(63, 0, 178));
					g2.setFont(new Font("default", Font.BOLD, 14));
					g2.drawString(webLabel, x, y);
				}
				xPos++;
			} else {
				break;
			}
		}
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

	public void mousePressed(MouseEvent event) {
		repaint();
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void mouseReleased(MouseEvent event) {
	}

	public void keyPressed(KeyEvent event) {
	}

	public void keyReleased(KeyEvent event) {
	}

	public void mouseClicked(MouseEvent event) {
	}

	public void newRatios(RatioFileComponents ratioFeatures) {
		websiteList.add(ratioFeatures.getWebLinkList());
	}

	public void setData(List<List<WebLink>> newWebsiteList) {
		websiteList = newWebsiteList;
		repaint();
	}

}
