package edu.cmu.scs.fluorite.recorders;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;

import edu.cmu.scs.fluorite.commands.ShellBoundsCommand;
import edu.cmu.scs.fluorite.model.EventRecorder;

public class ShellBoundsRecorder extends BaseRecorder implements Listener {
	
	private static ShellBoundsRecorder _instance = null;
	
	public static ShellBoundsRecorder getInstance() {
		if (_instance == null) {
			_instance = new ShellBoundsRecorder();
		}
		
		return _instance;
	}

	@Override
	public void addListeners(IEditorPart editor) {
	}

	@Override
	public void removeListeners(IEditorPart editor) {
	}

	@Override
	public void handleEvent(Event event) {
		if (event.widget instanceof Shell) {
			logShellSize((Shell) event.widget);
		}
	}
	
	public void logShellSize(Shell shell) {
		EventRecorder anEventRecorder = getRecorder();
		if (anEventRecorder != null) {
			anEventRecorder.recordCommand(new ShellBoundsCommand(shell.getBounds()));
		}
//		getRecorder().recordCommand(new ShellBoundsCommand(shell.getBounds()));
	}

}
