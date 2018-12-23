package analyzer.ui.balloons;

import analyzer.Resettable;
import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
//import bus.uigen.hermes.HermesObjectEditorProxy;
import difficultyPrediction.ADifficultyPredictionRunnable;
import util.annotations.Column;
import util.annotations.ComponentWidth;
import util.annotations.Row;

public class ABalloonCreator implements Resettable{
	static ABalloonCreator instance;
	static OEFrame frame;
//	static Object frame;
	String status = "";
	@Row(2)
	@ComponentWidth(325)
	public String getStatus() {
		return status;
	}

	public void setStatus(String newVal) {
		ADifficultyPredictionRunnable.getOrCreateInstance().asyncShowStatusInBallonTip(newVal);		
		
	}
	
	
	@Row(0)
	@Column(0)
	public void localDifficulty() {
//		setStatus("Facing Difficulty");
		setStatus("You are facing difficulty");

	}
	@Row(0)
	@Column(1)
	public void remoteDifficulty() {
//		setStatus("Remote difficulty detected");
		setStatus("Alice is facing dfficulty");
	}
	@Row(1)
	@Column(0)
	public void difficultyCommunicated() {
		setStatus("Difficulty communicated to collaborators");
	}
	@Row(1)
	@Column(1)
	public void difficultyResolved() {
		setStatus("In main method, it is String[] args and not String args");
	}
	@Row(1)
	@Column(2)
	public void difficulty2Resolved() {
		setStatus("Use '+' instead of ',' to concatenate two strings with System");
	}
	public static void createUI() {
		if (frame != null) {
//			instance.reset();
			getInstance().reset();
			return;
		}
//		OEFrame frame = ObjectEditor.edit(new ABalloonCreator());
//		instance = new ABalloonCreator();
//	    frame = ObjectEditor.edit(instance);
//		frame = HermesObjectEditorProxy.edit(getInstance(), 400, 150);

		frame = ObjectEditor.edit(getInstance());
		frame.setSize(400, 150);
	}
	public static ABalloonCreator getInstance() {
		if (instance == null)
			instance = new ABalloonCreator();
		return instance;
	}
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		System.err.println("Reset not implemented");
		
	}
	public static void main (String[] args) {
		createUI();
		
	}
	
	

	

}
