package analyzer.ui;

//import context.saros.SarosAccessorFactory;
import javax.swing.WindowConstants;

import context.recording.AnAbstractDisplayBoundsOutputter;
import context.recording.RecorderFactory;
import analyzer.ui.balloons.ABalloonCreator;
import analyzer.ui.graphics.LineGraphComposer;
import analyzer.ui.text.AMultiLevelAggregator;
import analyzer.ui.video.ALocalScreenRecorderAndPlayer;
import analyzer.ui.video.LocalScreenPlayerFactory;
import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
//import bus.uigen.hermes.HermesObjectEditorProxy;
import difficultyPrediction.APredictionParameters;

public class APredictionController implements PredictionController {
	
	protected GeneralizedPlayAndRewindCounter player;
	
	public APredictionController() {
		player = SessionPlayerFactory.getSingleton();
	}

	@Override
	public void lineGraph() {
		LineGraphComposer.composeUI();
	}

	@Override
	public void multilevelAggregator() {
		AMultiLevelAggregator.createUI();		
	}

	@Override
	public void localScreenPlayer() {
 		LocalScreenPlayerFactory.getSingleton().createUI();		
	}
	@Override
	public void screenRecorder() {
 		RecorderFactory.getSingleton().createUI();		
	}

	@Override
	public void predictionParameters() {
		APredictionParameters.createUI();		
	}

	@Override
	public void balloonCreator() { 		
 		ABalloonCreator.createUI();	
	}

	@Override
	public GeneralizedPlayAndRewindCounter getPlayer() {
		// TODO Auto-generated method stub
		return player;
	}
	
	@Override
	public void accessSaros() {
//		SarosAccessorFactory.createSingleton();
	}
	
	static boolean uiCreated;
	
	public static void createUI() {		
		OEFrame oeFrame = ObjectEditor.edit(PredictionControllerFactory.getSingleton());
//		Object oeFrame = ObjectEditor.edit(PredictionControllerFactory.getSingleton());
//		HermesObjectEditorProxy.edit(PredictionControllerFactory.getSingleton(), 800, 150, 
//				WindowConstants.DISPOSE_ON_CLOSE, false);
		oeFrame.setSize(800, 150);
		oeFrame.getFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		oeFrame.setAutoExitEnabled(false);
	}
	
	public static void main(String[] args) {
		ObjectEditor.edit(new APredictionController());
	}
	
}
