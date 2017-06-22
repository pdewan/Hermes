package analyzer.ui.graphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import util.misc.ThreadSupport;
import bus.uigen.ObjectEditor;
import bus.uigen.uiFrame;
//import bus.uigen.hermes.HermesObjectEditorProxy;
import difficultyPrediction.featureExtraction.ARatioFeatures;
import difficultyPrediction.featureExtraction.RatioFeatures;

public class LineGraphComposer {

	// TODO: Fix issue with preconditions and buttons
	// TODO: Error checking on files with incorrect format
	// TODO: Combine two keys
	// TODO: Scalable graph
	static LineGraph lineGraph;
	static StatusBar statusBar;
	static PlayAndRewindCounter counter;
	static JFrame frame;
	
	public static StatusBar getStatusBar() {
		if (statusBar == null) {
			composeUI();
		}
		return statusBar;
	}

//	public static void setStatusBar(StatusBar statusBar) {
//		LineGraphComposer.statusBar = statusBar;
//	}
	static void resetModels() {
		counter.reset();
		lineGraph.reset();
		statusBar.reset();
		counter.reset();
	}
	public static LineGraph  composeUI() {
		if (frame != null) {
			resetModels();
			return lineGraph;
		}
//		JFrame frame = new JFrame();
		 frame = new JFrame();

		RatioFileReader reader = new ARatioFileReader();

//		PlayAndRewindCounter counter = new APlayAndRewindCounter(reader);
		counter = new APlayAndRewindCounter(reader);

		ALineGraph aLineGraph = new ALineGraph(counter, reader);
		lineGraph = aLineGraph;
		AStatusBar aStatusBar = new AStatusBar(counter, reader);
		statusBar = aStatusBar;
		AWebDisplay webDisplay = new AWebDisplay(counter, reader);
		ADifficultyTypeDisplay difficultyTypeDisplay = new ADifficultyTypeDisplay(
				counter, reader);
		Legend checkboxes = new ALegend(frame, difficultyTypeDisplay, aStatusBar,
				webDisplay, aLineGraph);
		frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		JPanel counterPanel = new JPanel();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0; // request any extra horizontal space
		frame.add(counterPanel, c);
		JPanel readerPanel = new JPanel();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0; // request any extra horizontal space
		frame.add(readerPanel, c);
		JPanel checkboxPanel = new JPanel();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1.0; // request any extra horizontal space
		frame.add(checkboxPanel, c);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0; // request any extra horizontal space
		c.weighty = .1;
		c.gridx = 0;
		c.gridy = 3;
		frame.add(difficultyTypeDisplay, c);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0; // request any extra horizontal space
		c.weighty = .25;
		c.gridx = 0;
		c.gridy = 4;
		frame.add(aStatusBar, c);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0; // request any extra horizontal space
		c.weighty = .1;
		c.gridx = 0;
		c.gridy = 5;
		frame.add(webDisplay, c);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0; // request any extra vertical space
		c.weightx = 1.0; // request any extra horizontal space
		c.gridx = 0;
		c.gridy = 6;
		frame.add(aLineGraph, c);
//		uiFrame editor = ObjectEditor.createOEFrame(frame);
		ObjectEditor.editInMainContainer(counter, counterPanel);
		ObjectEditor.editInMainContainer(reader, readerPanel);
		ObjectEditor.editInMainContainer(checkboxes, checkboxPanel);
		
//		HermesObjectEditorProxy.editInMainContainer(counter, counterPanel);
//		HermesObjectEditorProxy.editInMainContainer(reader, readerPanel);
//		HermesObjectEditorProxy.editInMainContainer(checkboxes, checkboxPanel);
		
//		reader.readFile("data/ratios1.csv");

		// finalize
		frame.pack();
		frame.setVisible(true);
		frame.setSize(840, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return aLineGraph;
		
	}
	
	public static LineGraph getLineGraph() {
		if (lineGraph == null) {
			lineGraph = composeUI();
		}
		return lineGraph;
	}

	public static void main(String[] args) {
		LineGraph aLineGraph = composeUI();
		RatioFileReader aReader = aLineGraph.getRatioFileReader();
		RatioFeatures ratioFeatures = new ARatioFeatures();
		ratioFeatures.setDebugRatio(20.0);
		ratioFeatures.setInsertionRatio(50.0);
		ratioFeatures.setSavedTimeStamp(System.currentTimeMillis());
		aLineGraph.newRatios(ratioFeatures);
		ThreadSupport.sleep (1000);
		ratioFeatures.setDebugRatio(50.0);
		ratioFeatures.setInsertionRatio(20.0);
		ratioFeatures.setSavedTimeStamp(System.currentTimeMillis());
		aLineGraph.newRatios(ratioFeatures);

//		aReader.readFile("data/ratios1.csv");
	}
}
