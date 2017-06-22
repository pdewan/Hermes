package context.recording;

import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import bus.uigen.models.FileSetterModel;

public interface DisplayBoundsOutputter extends Listener,  ControlListener {

//	public abstract void startRecorder(String aCommand[]);

//	public abstract void listenToRecorderIOEvents();
	public void connectToDisplay();


	public abstract String boundsToString();

//	public abstract void updateRecorder();

	public abstract void handleEvent(Event event);

//	public abstract void propertyChange(PropertyChangeEvent evt);

	void start();
	
	String[] launchCommand();
	String configuredJavaPath();

	void connectToExternalProgram();

	void listenToDisplayEvents();

	void updateRecorder(Shell aShell);

	String boundsToString(Shell aShell);
	
	void createUI();

	boolean isStarted();


	FileSetterModel getJavaLocationSetter();

}