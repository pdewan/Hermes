package analyzer.ui.graphics;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import bus.uigen.ObjectEditor;

public class AutoTextInCustomFrameWithCustomDrawing {

	public static void main(String[] args) {
		JFrame f = new JFrame("ArenaTracker 2.0");
		// finalize
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// done
		f.setVisible(true);
		f.setSize(200, 300);


		 SquaringCounterWithButtons counter = new
		 SquaringCounterWithButtons();
		 BarChartDrawingPanel circleDrawingPanel = new
		 BarChartDrawingPanel(counter);
		 JFrame frame= new JFrame();
		 frame.setLayout(new GridLayout(2, 0));
		 JPanel textPanel = new JPanel();
		 frame.add(textPanel);
		 frame.add(circleDrawingPanel);
//		 uiFrame editor = ObjectEditor.createOEFrame(frame);
		 ObjectEditor.editInMainContainer(counter, textPanel);

		
		 frame.setSize(300, 400);
		 frame.setVisible(true);
//		 HermesObjectEditorProxy.editInMainContainer(counter, textPanel);


	}

}
