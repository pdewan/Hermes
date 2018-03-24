package analyzer.ui.video;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Event;

import config.HelperConfigurationManagerFactory;
import util.annotations.Visible;
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
