package fluorite.recorders;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.ui.IEditorPart;

import fluorite.commands.EHRunCommand;

public class EHDebugEventSetRecorder extends EHBaseRecorder implements
		IDebugEventSetListener {

	private static EHDebugEventSetRecorder instance = null;

	public static EHDebugEventSetRecorder getInstance() {
		if (instance == null) {
			instance = new EHDebugEventSetRecorder();
		}

		return instance;
	}

	private EHDebugEventSetRecorder() {
		super();
	}

	@Override
	public void addListeners(IEditorPart editor) {
		// Do nothing.
	}

	@Override
	public void removeListeners(IEditorPart editor) {
		// Do nothing.
	}

	public void handleDebugEvents(DebugEvent[] debugEvents) {
		for (DebugEvent event : debugEvents) 
		{
			if (event.getKind() == DebugEvent.CREATE
					|| event.getKind() == DebugEvent.TERMINATE)
			{
				Object source = event.getSource();
				boolean terminate = event.getKind() == DebugEvent.TERMINATE;

				if (source instanceof IProcess) {
					IProcess process = (IProcess) source;
					ILaunchConfiguration config = process.getLaunch()
							.getLaunchConfiguration();

					if (config == null) {
						return;
					}

					@SuppressWarnings("rawtypes")
					Map attributes = null;
					try {
						attributes = config.getAttributes();
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// Retrieve the corresponding project name
					String projectName = (String) (attributes
							.get("org.eclipse.jdt.launching.PROJECT_ATTR"));

					getRecorder().recordCommand(
							new EHRunCommand(false, terminate, projectName, 0, false, false, false, false));

				} else if (source instanceof IDebugTarget) {
					getRecorder().recordCommand(
							new EHRunCommand(true, terminate, null, 0,false, false, false, false));
				}
			}
			
			if (event.getKind() ==DebugEvent.BREAKPOINT)
			{
				Object source = event.getSource();
				
				if (source instanceof IProcess) {
					IProcess process = (IProcess) source;
					ILaunchConfiguration config = process.getLaunch()
							.getLaunchConfiguration();

					if (config == null) {
						return;
					}

					@SuppressWarnings("rawtypes")
					Map attributes = null;
					try {
						attributes = config.getAttributes();
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// Retrieve the corresponding project name
					String projectName = (String) (attributes
							.get("org.eclipse.jdt.launching.PROJECT_ATTR"));
					
					getRecorder().recordCommand(
							new EHRunCommand(false, false, projectName, 0, true, false, false, false));
				}
				else if (source instanceof IDebugTarget) {
					getRecorder().recordCommand(
							new EHRunCommand(false, false, null,0, true, false, false, false));

				}
			}
			if (event.getKind() ==DebugEvent.STEP_END)
			{
				getRecorder().recordCommand(
							new EHRunCommand(false, false, null, 0, false, true, false, false));
				
			}
			if (event.getKind() ==DebugEvent.STEP_INTO)
			{
				getRecorder().recordCommand(
							new EHRunCommand(false, false, null, 0, false, false, true, false));
				
			}
				
			if (event.getKind() ==DebugEvent.STEP_RETURN)
			{
				getRecorder().recordCommand(
							new EHRunCommand(false, false, null, 0, false, false, false, true));
				
			}
			
		}
	}

}
