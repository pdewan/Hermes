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
import analyzer.ui.GeneralizedPlayAndRewindCounter;
import analyzer.ui.SessionPlayerFactory;
import analyzer.ui.graphics.PlayAndRewindCounter;
import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import config.HelperConfigurationManagerFactory;
import context.recording.ADisplayBoundsPiper;
//bad hierarchy, just removing recording stuff from superclass, but that is what you get if there is no multiple inheirtance
public class ALocalScreenPlayer extends ALocalScreenRecorderAndPlayer {
	
	public String configuredJavaPath() {
		return HelperConfigurationManagerFactory.getSingleton().getPlayerJavaPath();
	}
	@Override
	@Visible(false)
	public void listenToDisplayEvents() {
	
	}
	
//	@Override
//	@Row(1)
//	public void start() {
//		super.start();
//		
//	}
	
	@Visible(false)
	public void handleEvent(Event event) {
		
//		updateRecorder((Shell) event.widget);
		
	}
	@Visible(false)
	public void controlMoved(ControlEvent e) {
//		Shell aShell = (Shell)e.getSource();
////		System.out.println ("Changed shell " + boundsToString ((Shell)e.getSource()));
//		updateRecorder(aShell);
//		// TODO Auto-generated method stub
		
	}
	

}
