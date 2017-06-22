package analyzer.ui.graphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import bus.uigen.ObjectEditor;
import bus.uigen.uiFrame;
//import bus.uigen.hermes.HermesObjectEditorProxy;

public class Driver {

	// TODO: Fix issue with preconditions and buttons
	// TODO: Error checking on files with incorrect format
	// TODO: Combine two keys
	// TODO: Scalable graph

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		RatioFileReader reader = new ARatioFileReader();

		PlayAndRewindCounter counter = new APlayAndRewindCounter(reader);
		ALineGraph lineGraph = new ALineGraph(counter, reader);
		AStatusBar statusBar = new AStatusBar(counter, reader);
		AWebDisplay webDisplay = new AWebDisplay(counter, reader);
		ADifficultyTypeDisplay difficultyTypeDisplay = new ADifficultyTypeDisplay(
				counter, reader);
		ALegend checkboxes = new ALegend(frame, difficultyTypeDisplay, statusBar,
				webDisplay, lineGraph);
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
		frame.add(statusBar, c);
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
		frame.add(lineGraph, c);
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
	}
}
