package analyzer.ui.video;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import util.annotations.Row;
import util.annotations.Visible;
import util.pipe.ConsoleModel;
import util.remote.ProcessExecer;
import analyzer.ui.GeneralizedPlayAndRewindCounter;
import analyzer.ui.SessionPlayerFactory;
import analyzer.ui.graphics.PlayAndRewindCounter;
import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import bus.uigen.misc.OEMisc;
import context.recording.ADisplayBoundsPiper;

public class ALocalScreenRecorderAndPlayer extends ADisplayBoundsPiper implements LocalScreenRecorderAndPlayer{
	
//	ProcessExecer processExecer;
//	ConsoleModel consoleModel;
	protected GeneralizedPlayAndRewindCounter player;
//	protected boolean connected;
	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	public ALocalScreenRecorderAndPlayer() {
		//super();
		player = SessionPlayerFactory.getSingleton();
		player.addPropertyChangeListener(this);
	}
	
//	@Override
//	public void start() {
//		super.start();
////		connected = true;
////		propertyChangeSupport.firePropertyChange("connected", null, true);
//	}
//	@Visible(false)
//	public void connectToExternalProgram() {
//		launch();
////		launchRecorder(RECORDER_LAUNCHING_COMMAND);		
//		listenToRecorderIOEvents();
//	}
//	@Visible(false)
//	public void launchRecorder(String[] aCommand) {
//		processExecer = OEMisc.runWithProcessExecer(aCommand);
//		System.err.println("Starting recorder " + processExecer);
//		consoleModel = processExecer.getConsoleModel();
//	}
//	@Visible(false)
//	public void listenToRecorderIOEvents() {
//		processExecer.consoleModel().addPropertyChangeListener(this);
//	}
	
//	public boolean isConnected() {
//		return connected;
//	}
	
	@Visible(false)
	public PlayAndRewindCounter getPlayer() {
		return player;
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (!isStarted()) return;
		if (player.isPlayBack() && evt.getPropertyName().equalsIgnoreCase("currentTime")) {
			seek (player.getCurrentWallTime());		// +player.getAbsoluteStartTime()
		}
	}
	@Override
	public void seek(long aTime) {
		propertyChangeSupport.firePropertyChange("wallTime", null, player.getCurrentWallTime());

		
	}
	@Override
	public void play() {
		// TODO Auto-generated method stub
		processExecer.consoleModel().setInput("play");
	}
	
	
	
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		processExecer.consoleModel().setInput("pause");
	}
	
	@Override
	@Row(2)
	public long getWallTime() {
		// TODO Auto-generated method stub
		return player.getCurrentWallTime();
	}
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener arg0) {
		propertyChangeSupport.addPropertyChangeListener(arg0);
	}
	public void handleEvent(Event event) {
		
//		updateRecorder((Shell) event.widget);
		
	}
//	@Visible(false)
//	public void createUI() {
//		OEFrame oeFrame = ObjectEditor.edit(LocalScreenPlayerFactory.getSingleton());
//		oeFrame.setSize(250, 150);
//	}
}