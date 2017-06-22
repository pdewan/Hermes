package analyzer.ui.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import difficultyPrediction.DifficultyRobot;
import difficultyPrediction.featureExtraction.RatioFeatures;

public class ALineGraph extends JPanel implements LineGraph {

	private static final long serialVersionUID = -259260652449816321L;
	private static final int Y_MAX = 100;
	private static final int Y_BORDER_GAP = 40;
	private static final int X_BORDER_GAP = 65;
	private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
	private static final int GRAPH_POINT_WIDTH = 12;
	private static final int Y_HATCH_CNT = 5;
	private static final int X_HATCH_CNT = 9;
	private ArrayList<Color> colors = new ArrayList<Color>();
	private List<Double> editList = new ArrayList<Double>();
	private List<Double> insertionList = new ArrayList<Double>();
	private List<Double> deletionList = new ArrayList<Double>();
	private List<Double> debugList = new ArrayList<Double>();
	private List<Double> navigationList = new ArrayList<Double>();
	private List<Double> focusList = new ArrayList<Double>();
	private List<Double> removeList = new ArrayList<Double>();
	private List<Long> timeStampList = new ArrayList<Long>();
	private int editListIndex = 0;
	private int insertionListIndex = 1;
	private int deletionListIndex = 2;
	private int debugListIndex = 3;
	private int navigationListIndex = 4;
	private int focusListIndex = 5;
	private int removeListIndex = 6;
	private List<List<Double>> lists = new ArrayList<List<Double>>();
	private int editKeyStart;
	private int editKeyEnd;
	private int insertionKeyStart;
	private int insertionKeyEnd;
	private int deletionKeyStart;
	private int deletionKeyEnd;
	private int debugKeyStart;
	private int debugKeyEnd;
	private int navigationKeyStart;
	private int navigationKeyEnd;
	private int focusKeyStart;
	private int focusKeyEnd;
	private int removeKeyStart;
	private int removeKeyEnd;
	private PlayAndRewindCounter counter;
	private RatioFileReader ratioFileReader;

	public ALineGraph(PlayAndRewindCounter aCounter,
			RatioFileReader aRatioFileReader) {
		setBackground(Color.LIGHT_GRAY);
		addMouseListener(this);
		// if (!DifficultyPredictionSettings.isReplayMode()) {
		DifficultyRobot.getInstance().addRatioFeaturesListener(this);
		// }
		counter = aCounter;
		counter.addPropertyChangeListener(this);
		ratioFileReader = aRatioFileReader;
		ratioFileReader.addPropertyChangeListener(this);
		lists.add(editList);
		lists.add(insertionList);
		lists.add(deletionList);
		lists.add(debugList);
		lists.add(navigationList);
		lists.add(focusList);
		lists.add(removeList);
		colors.add(new Color(199, 21, 133));
		colors.add(new Color(158, 0, 178));
		colors.add(new Color(79, 191, 10));
		colors.add(new Color(63, 0, 178));
		colors.add(new Color(10, 190, 201));
		colors.add(new Color(201, 24, 10));
		colors.add(new Color(235, 172, 10));
	}

	// called when an enqueued paint event for this component is dequeued
	public void paint(Graphics g) {
		super.paint(g); // clears the window
		Graphics2D g2 = (Graphics2D) g;

		// make graph background white
		g2.setColor(Color.WHITE);
		g2.fillRect(X_BORDER_GAP, Y_BORDER_GAP, getWidth() - X_BORDER_GAP * 2,
				getHeight() - (2 * Y_BORDER_GAP));
		g2.setColor(Color.BLACK);

		// create x and y axes
		g2.drawLine(X_BORDER_GAP, getHeight() - Y_BORDER_GAP, X_BORDER_GAP,
				Y_BORDER_GAP);
		g2.drawLine(X_BORDER_GAP, getHeight() - Y_BORDER_GAP, getWidth()
				- X_BORDER_GAP, getHeight() - Y_BORDER_GAP);

		// create line markers for y axis
		for (int i = 0; i < Y_HATCH_CNT; i++) {
			int x0 = X_BORDER_GAP;
			int x1 = getWidth() - X_BORDER_GAP;
			int y0 = getHeight()
					- (((i + 1) * (getHeight() - Y_BORDER_GAP * 2))
							/ Y_HATCH_CNT + Y_BORDER_GAP);
			int y1 = y0;
			g2.drawLine(x0, y0, x1, y1);

		}

		// create labels for y axis
		for (int i = 0; i < Y_HATCH_CNT + 1; i++) {
			int x0 = X_BORDER_GAP;
			int y0 = getHeight()
					- (((i) * (getHeight() - Y_BORDER_GAP * 2)) / Y_HATCH_CNT + Y_BORDER_GAP);
			String yLabel = (Integer.toString(i * 20) + "%");
			FontMetrics metrics = g2.getFontMetrics();
			int labelWidth = metrics.stringWidth(yLabel);
			g2.drawString(yLabel, x0 - labelWidth - 5,
					y0 + (metrics.getHeight() / 2) - 3);
		}

		// create line markers marks for x axis
		for (int i = 0; i < X_HATCH_CNT; i++) {
			int x0 = (i + 1) * (getWidth() - X_BORDER_GAP * 2) / (10 - 1)
					+ X_BORDER_GAP;
			int x1 = x0;
			int y0 = getHeight() - Y_BORDER_GAP;
			int y1 = y0 - GRAPH_POINT_WIDTH;
			g2.drawLine(x0, y0, x1, y1);
		}

		// create labels for x axis
		int index = 0;
		for (int i = counter.getStart(); i < counter.getEnd(); i = i
				+ roundUp(counter.getSize(), 10) / 10)  {
			if (i < insertionList.size() && i >= 0 && insertionList.size() > 0) {
				int x0 = (((index) * (getWidth() - X_BORDER_GAP * 2))
						/ X_HATCH_CNT + X_BORDER_GAP);
				int y0 = getHeight() - Y_BORDER_GAP;
				Date date = new Date(timeStampList.get(i));
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				String xLabel = sdf.format(date);
				FontMetrics metrics = g2.getFontMetrics();
				int labelWidth = metrics.stringWidth(xLabel);
				g2.drawString(xLabel, x0 - labelWidth / 2,
						y0 + metrics.getHeight() + 3);
				index++;
			} else {
				break;
			}
		}

		// draw currentTime line
		g2.setStroke(GRAPH_STROKE);
		int x0 = (counter.getCurrentTime() - counter.getStart())
				* (getWidth() - X_BORDER_GAP * 2) / (counter.getSize() - 1) + X_BORDER_GAP;
		int x1 = x0;
		int y0 = Y_BORDER_GAP;
		int y1 = getHeight() - Y_BORDER_GAP;
		g2.drawLine(x0, y0, x1, y1);

		// drawTitle
		// String title = "Statistics";
		// g2.drawString(title, getWidth() / 2, Y_BORDER_GAP / 2);

		// draw key
		FontMetrics metrics = g2.getFontMetrics();
		String sessionTime = ("Session Time");
		int labelWidth = metrics.stringWidth(sessionTime);
		g2.drawString(sessionTime, getWidth() / 2 - labelWidth / 2, getHeight()
				- Y_BORDER_GAP / 4 + 3);
		int spaceBefore = X_BORDER_GAP;
		g2.setStroke(GRAPH_STROKE);
		
		String edit = ("Edit");
		g2.drawString(edit, spaceBefore,
				Y_BORDER_GAP / 2 + (metrics.getHeight() / 2) - 3);
		labelWidth = metrics.stringWidth(edit);
		spaceBefore = spaceBefore + labelWidth + 5;
		g2.setColor(new Color(199, 21, 133));
		editKeyStart = spaceBefore;
		editKeyEnd = spaceBefore + 30;
		g2.drawLine(editKeyStart, Y_BORDER_GAP / 2, editKeyEnd,
				Y_BORDER_GAP / 2);
		g2.setColor(Color.black);
		spaceBefore = spaceBefore + 30 + 5;

		String insertion = ("Insertion");
		g2.drawString(insertion, spaceBefore,
				Y_BORDER_GAP / 2 + (metrics.getHeight() / 2) - 3);
		labelWidth = metrics.stringWidth(insertion);
		spaceBefore = spaceBefore + labelWidth + 5;
		g2.setColor(new Color(158, 0, 178));
		insertionKeyStart = spaceBefore;
		insertionKeyEnd = spaceBefore + 30;
		g2.drawLine(insertionKeyStart, Y_BORDER_GAP / 2, insertionKeyEnd,
				Y_BORDER_GAP / 2);
		g2.setColor(Color.black);
		spaceBefore = spaceBefore + 30 + 5;

		String deletion = ("Deletion");
		g2.drawString(deletion, spaceBefore,
				Y_BORDER_GAP / 2 + (metrics.getHeight() / 2) - 3);
		labelWidth = metrics.stringWidth(deletion);
		spaceBefore = spaceBefore + labelWidth + 5;
		g2.setColor(new Color(79, 191, 10));
		deletionKeyStart = spaceBefore;
		deletionKeyEnd = spaceBefore + 30;
		g2.drawLine(spaceBefore, Y_BORDER_GAP / 2, spaceBefore + 30,
				Y_BORDER_GAP / 2);
		g2.setColor(Color.black);
		spaceBefore = spaceBefore + 30 + 5;

		String debug = ("Debug");
		g2.drawString(debug, spaceBefore,
				Y_BORDER_GAP / 2 + (metrics.getHeight() / 2) - 3);
		labelWidth = metrics.stringWidth(debug);
		spaceBefore = spaceBefore + labelWidth + 5;
		g2.setColor(new Color(63, 0, 178));
		debugKeyStart = spaceBefore;
		debugKeyEnd = spaceBefore + 30;
		g2.drawLine(spaceBefore, Y_BORDER_GAP / 2, spaceBefore + 30,
				Y_BORDER_GAP / 2);
		g2.setColor(Color.black);
		spaceBefore = spaceBefore + 30 + 5;

		String navigation = ("Navigation");
		g2.drawString(navigation, spaceBefore,
				Y_BORDER_GAP / 2 + (metrics.getHeight() / 2) - 3);
		labelWidth = metrics.stringWidth(navigation);
		spaceBefore = spaceBefore + labelWidth + 5;
		g2.setColor(new Color(10, 190, 201));
		navigationKeyStart = spaceBefore;
		navigationKeyEnd = spaceBefore + 30;
		g2.drawLine(spaceBefore, Y_BORDER_GAP / 2, spaceBefore + 30,
				Y_BORDER_GAP / 2);
		g2.setColor(Color.black);
		spaceBefore = spaceBefore + 30 + 5;

		String focus = ("Focus");
		g2.drawString(focus, spaceBefore,
				Y_BORDER_GAP / 2 + (metrics.getHeight() / 2) - 3);
		labelWidth = metrics.stringWidth(focus);
		spaceBefore = spaceBefore + labelWidth + 5;
		g2.setColor(new Color(201, 24, 10));
		focusKeyStart = spaceBefore;
		focusKeyEnd = spaceBefore + 30;
		g2.drawLine(spaceBefore, Y_BORDER_GAP / 2, spaceBefore + 30,
				Y_BORDER_GAP / 2);
		g2.setColor(Color.black);
		spaceBefore = spaceBefore + 30 + 5;

		String remove = ("Remove");
		g2.drawString(remove, spaceBefore,
				Y_BORDER_GAP / 2 + (metrics.getHeight() / 2) - 3);
		labelWidth = metrics.stringWidth(remove);
		spaceBefore = spaceBefore + labelWidth + 5;
		g2.setColor(new Color(235, 172, 10));
		removeKeyStart = spaceBefore;
		removeKeyEnd = spaceBefore + 30;
		g2.drawLine(spaceBefore, Y_BORDER_GAP / 2, spaceBefore + 30,
				Y_BORDER_GAP / 2);

		// draw data points and lines
		for (int i = 0; i < colors.size(); i++) {
			drawDataPoints(g2, colors.get(i), lists.get(i));
			connectTheDots(g2, colors.get(i), lists.get(i));
		}

	}

	public void drawDataPoints(Graphics g2, Color color, List<Double> ratios) {

		g2.setColor(color);
		int xPos = 0;
		for (int i = counter.getStart(); i < counter.getEnd(); i++) {
			if (i < ratios.size() && i >= 0) {
				int x = (xPos * (getWidth() - X_BORDER_GAP * 2) / (counter.getSize() - 1) + X_BORDER_GAP)
						- GRAPH_POINT_WIDTH / 2;
				int y = (int) ((Y_MAX - ratios.get(i))
						* (getHeight() - Y_BORDER_GAP * 2) / (Y_MAX - 1)
						+ Y_BORDER_GAP - GRAPH_POINT_WIDTH / 2);
				int ovalW = GRAPH_POINT_WIDTH;
				int ovalH = GRAPH_POINT_WIDTH;
				g2.fillOval(x, y, ovalW, ovalH);
				xPos++;
			} else {
				break;
			}
		}
	}

	public void connectTheDots(Graphics g2, Color color, List<Double> ratios) {
		g2.setColor(color);
		int xPos = 0;
		for (int i = counter.getStart(); i < counter.getEnd() - 1; i++) {
			if (i < ratios.size() - 1 && i >= 0) {
				int x1 = (xPos * (getWidth() - X_BORDER_GAP * 2) / (counter.getSize() - 1) + X_BORDER_GAP);
				int y1 = (int) ((Y_MAX - ratios.get(i))
						* (getHeight() - Y_BORDER_GAP * 2) / (Y_MAX - 1) + Y_BORDER_GAP);
				int x2 = ((xPos + 1) * (getWidth() - X_BORDER_GAP * 2)
						/ (counter.getSize() - 1) + X_BORDER_GAP);
				int y2 = (int) ((Y_MAX - ratios.get(i + 1))
						* (getHeight() - Y_BORDER_GAP * 2) / (Y_MAX - 1) + Y_BORDER_GAP);
				g2.drawLine(x1, y1, x2, y2);
				xPos++;
			} else {
				break;
			}
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
		if ((event.getX() >= insertionKeyStart)
				&& (event.getX() <= insertionKeyEnd)
				&& (event.getY() <= ((Y_BORDER_GAP / 2) + 4))
				&& (event.getY() >= ((Y_BORDER_GAP / 2) - 4))) {
			int index = lists.indexOf(insertionList);
			if (!(index == lists.size() - 1)) {
				lists.set(index, lists.get(lists.size() - 1));
				lists.set(lists.size() - 1, insertionList);
				colors.set(index, colors.get(colors.size() - 1));
				colors.set(colors.size() - 1, new Color(158, 0, 178));
				repaint();
			}
		} else if ((event.getX() >= deletionKeyStart)
				&& (event.getX() <= deletionKeyEnd)
				&& (event.getY() <= ((Y_BORDER_GAP / 2) + 4))
				&& (event.getY() >= ((Y_BORDER_GAP / 2) - 4))) {
			int index = lists.indexOf(deletionList);
			if (!(index == lists.size() - 1)) {
				lists.set(index, lists.get(lists.size() - 1));
				lists.set(lists.size() - 1, deletionList);
				colors.set(index, colors.get(colors.size() - 1));
				colors.set(colors.size() - 1, new Color(79, 191, 10));
				repaint();
			}
		} else if ((event.getX() >= debugKeyStart)
				&& (event.getX() <= debugKeyEnd)
				&& (event.getY() <= ((Y_BORDER_GAP / 2) + 4))
				&& (event.getY() >= ((Y_BORDER_GAP / 2) - 4))) {
			int index = lists.indexOf(debugList);
			if (!(index == lists.size() - 1)) {
				lists.set(index, lists.get(lists.size() - 1));
				lists.set(lists.size() - 1, debugList);
				colors.set(index, colors.get(colors.size() - 1));
				colors.set(colors.size() - 1, new Color(63, 0, 178));
				repaint();
			}
		} else if ((event.getX() >= navigationKeyStart)
				&& (event.getX() <= navigationKeyEnd)
				&& (event.getY() <= ((Y_BORDER_GAP / 2) + 4))
				&& (event.getY() >= ((Y_BORDER_GAP / 2) - 4))) {
			int index = lists.indexOf(navigationList);
			if (!(index == lists.size() - 1)) {
				lists.set(index, lists.get(lists.size() - 1));
				lists.set(lists.size() - 1, navigationList);
				colors.set(index, colors.get(colors.size() - 1));
				colors.set(colors.size() - 1, new Color(10, 190, 201));
				repaint();
			}
		} else if ((event.getX() >= focusKeyStart)
				&& (event.getX() <= focusKeyEnd)
				&& (event.getY() <= ((Y_BORDER_GAP / 2) + 4))
				&& (event.getY() >= ((Y_BORDER_GAP / 2) - 4))) {
			int index = lists.indexOf(focusList);
			if (!(index == lists.size() - 1)) {
				lists.set(index, lists.get(lists.size() - 1));
				lists.set(lists.size() - 1, focusList);
				colors.set(index, colors.get(colors.size() - 1));
				colors.set(colors.size() - 1, new Color(201, 24, 10));
				repaint();
			}
		} else if ((event.getX() >= removeKeyStart)
				&& (event.getX() <= removeKeyEnd)
				&& (event.getY() <= ((Y_BORDER_GAP / 2) + 4))
				&& (event.getY() >= ((Y_BORDER_GAP / 2) - 4))) {
			int index = lists.indexOf(removeList);
			if (!(index == lists.size() - 1)) {
				lists.set(index, lists.get(lists.size() - 1));
				lists.set(lists.size() - 1, removeList);
				colors.set(index, colors.get(colors.size() - 1));
				colors.set(colors.size() - 1, new Color(235, 172, 10));
				repaint();
			}
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		RatioFileReader reader = new ARatioFileReader();
		ALineGraph aLineGraph = new ALineGraph(
				new APlayAndRewindCounter(reader), reader);
		frame.setContentPane(aLineGraph);
		frame.setSize(640, 400);
		frame.setVisible(true);
		reader.readFile("/Users/Duri/projects/difficultyPrediction/ratios.csv");
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equalsIgnoreCase("newRatioFeatures")) {
			newRatios((RatioFeatures) evt.getNewValue());
			repaint();
		} else if (evt.getPropertyName().equalsIgnoreCase("start") || evt.getPropertyName().equalsIgnoreCase("size") || evt.getPropertyName().equalsIgnoreCase("currentTime"))
			repaint();
	}

	@Override
	public void newRatios(RatioFeatures ratioFeatures) {
		if (ratioFeatures.getEditRatio() != 0
				&& ratioFeatures.getInsertionRatio() == 0) // kludge as the plug
															// in does not
															// provide insertion
															// ratio
			insertionList.add(ratioFeatures.getEditRatio());
		else
			insertionList.add(ratioFeatures.getInsertionRatio());
		editList.add(ratioFeatures.getEditRatio());
		deletionList.add(ratioFeatures.getDeletionRatio());
		debugList.add(ratioFeatures.getDebugRatio());
		navigationList.add(ratioFeatures.getNavigationRatio());
		focusList.add(ratioFeatures.getFocusRatio());
		removeList.add(ratioFeatures.getRemoveRatio());
		timeStampList.add(ratioFeatures.getSavedTimeStamp());
		repaint();
	}

	// int lastStatusIndex;
	// int lastAggregateStatusIndex;
	// int lastAgrregateStatus;
	// int lastStatus;
	// @Override
	// public void newStatus(String aStatus) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void newAggregatedStatus(String aStatus) {
	// // TODO Auto-generated method stub
	//
	// }
	// @Override
	// public void newStatus(int aStatus) {
	// lastStatus = aStatus;
	// predict
	//
	// }

	// @Override
	// public void newAggregatedStatus(int aStatus) {
	// // TODO Auto-generated method stub
	//
	// }

	public void setData(List<Double> newEditList, List<Double> newInsertionList,
			List<Double> newDeletionList, List<Double> newDebugList,
			List<Double> newNavigationList, List<Double> newFocusList,
			List<Double> newRemoveList, List<Long> newTimeStampList) {
		editList = newEditList;
		insertionList = newInsertionList;
		deletionList = newDeletionList;
		debugList = newDebugList;
		navigationList = newNavigationList;
		focusList = newFocusList;
		removeList = newRemoveList;
		timeStampList = newTimeStampList;
		repaint();
	}

	public List<List<Double>> getLists() {
		return lists;
	}
	
	public List<Double> getEditList() {
		return editList;
	}

	public List<Double> getInsertionList() {
		return insertionList;
	}

	public List<Double> getDeletionList() {
		return deletionList;
	}

	public List<Double> getDebugList() {
		return debugList;
	}

	public List<Double> getNavigationList() {
		return navigationList;
	}

	public List<Double> getRemoveList() {
		return removeList;
	}

	public List<Double> getFocusList() {
		return focusList;
	}
	
	public int getEditListIndex() {
		return editListIndex;
	}
	
	public void setEditListIndex(int newIndex) {
		editListIndex = newIndex;
	}

	public int getInsertionListIndex() {
		return insertionListIndex;
	}
	
	public void setInsertionListIndex(int newIndex) {
		insertionListIndex = newIndex;
	}

	public int getDeletionListIndex() {
		return deletionListIndex;
	}

	public void setDeletionListIndex(int newIndex) {
		deletionListIndex = newIndex;
	}

	public int getDebugListIndex() {
		return debugListIndex;
	}
	
	public void setDebugListIndex(int newIndex) {
		debugListIndex = newIndex;
	}

	public int getNavigationListIndex() {
		return navigationListIndex;
	}
	
	public void setNavigationListIndex(int newIndex) {
		navigationListIndex = newIndex;
	}
	
	public int getFocusListIndex() {
		return focusListIndex;
	}
	
	public void setFocusListIndex(int newIndex) {
		focusListIndex = newIndex;
	}

	public int getRemoveListIndex() {
		return removeListIndex;
	}
	
	public void setRemoveListIndex(int newIndex) {
		removeListIndex = newIndex;
	}

	public ArrayList<Color> getColors() {
		return colors;
	}

	@Override
	public RatioFileReader getRatioFileReader() {
		return ratioFileReader;
	}

	@Override
	public void setRatioFileReader(RatioFileReader ratioFileReader) {
		this.ratioFileReader = ratioFileReader;
	}

	@Override
	public int numSegments() {
		return insertionList.size();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		System.err.println("Reset not implemented");

	}
	
	private int roundUp(double val, double factor) {
		return (int) (Math.ceil(val / factor) * factor);
	}

}
