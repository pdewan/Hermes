package context.recording;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
//import bus.uigen.hermes.HermesObjectEditorProxy;
import bus.uigen.misc.OEMisc;
import bus.uigen.models.AFileSetterModel;
import bus.uigen.models.FileSetterModel;
import util.annotations.Row;
import util.annotations.Visible;
import util.pipe.ConsoleModel;
import util.remote.ProcessExecer;


public abstract class AnAbstractDisplayBoundsOutputter implements  DisplayBoundsOutputter {
	// should really be specified in a config file
//	public static final String RECORDER_JAVA_PATH = "D:/Program Files/Java/jdk1.7.0_51/bin/java";
//	public static final String RECORDER_CLASS_PATH = "D:/dewan_backup/Java/eclipse/workspace/FileExample/bin";
//	public static final String RECORDER_MAIN_CLASS = "InputAndOutput";
////	public static final String RECORDER_LAUNCHING_COMMAND = RECORDER_JAVA_PATH + 
////									" " + "-cp" + " " + RECORDER_CLASS_PATH +
////									" " + RECORDER_MAIN_CLASS;
//	public static final String[] RECORDER_LAUNCHING_COMMAND = {RECORDER_JAVA_PATH, 
//			"-cp" ,  RECORDER_CLASS_PATH,
//			RECORDER_MAIN_CLASS};
	Display display;
	protected OEFrame oeFrame;
//	protected Object oeFrame;

	protected boolean started;
	FileSetterModel recorderJava = new AFileSetterModel(JFileChooser.FILES_ONLY);
	protected ProcessExecer processExecer;
	protected ConsoleModel consoleModel;


	//	ProcessExecer processExecer;
//	ConsoleModel consoleModel;
	public AnAbstractDisplayBoundsOutputter() {
//		display = Display.getCurrent();
//		display.addListener(SWT.RESIZE, this);
//		startRecorder(RECORDER_LAUNCHING_COMMAND);
//		listenToRecorderIOEvents();
//		String aJavaPath = HelperConfigurationManagerFactory.getSingleton().getRecorderJavaPath();
		String aJavaPath = configuredJavaPath();

		if (aJavaPath != null && !aJavaPath.isEmpty())
			recorderJava.setText(aJavaPath);
		
	}
	
//	public String configuredJavaPath() {
//		return HelperConfigurationManagerFactory.getSingleton().getRecorderJavaPath();
//	}
	
	protected String getJavaPath() {
		return recorderJava.getText();
	}
	@Visible(false)
	public void launch() {
		launch(launchCommand());

	}
	
	@Visible(false)
	public void launch(String[] aCommand) {	
		// do not need this
		processExecer = OEMisc.runWithProcessExecer(aCommand);
		consoleModel = processExecer.getConsoleModel();
		
	}
	public boolean preStart() {
		return !started;
	}
	@Row(0)
	@Override
	public FileSetterModel getJavaLocationSetter() {
		return recorderJava;		
	}
	@Visible(false)
	public void setJavaLocation() {
		System.out.println("Java 7 Location");
	}
	@Override
	@Row(1)
	public void start() {
//		connectToDisplay();
//		listenToDisplayEvents();
		connectToExternalProgram();
		started = true;	
	}
	@Visible(false)
	public void connectToDisplay() {
		if (display != null)
			return;
		display = Display.getCurrent();
		if (display == null)
			return;
//			display = Display.getDefault();
//		if (display == null)
//			return;
		display.addListener(SWT.RESIZE, this);
		listenToDisplayEvents();
		
	}
	@Visible(false)
	@Override
	public void listenToDisplayEvents() {
		
		System.out.println("Shell " + display.getActiveShell());
//		display.getActiveShell().addListener(SWT.RESIZE, this);
		Shell[] shells = display.getShells();
		for (Shell shell:shells) {
			
			shell.addListener(SWT.RESIZE, this);
			shell.addControlListener(this);
			System.out.println("Shell " + shell);

			System.out.println("Shell bounds " + shell.getBounds());
		}
//		display.addListener(SWT.RESIZE, this);

	}
	
	
	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#listenToRecorderIOEvents()
	 */

	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#boundsToString()
	 */
	@Visible(false)
	@Override
	public String boundsToString() {
		if (display == null) return "";
		Shell aShell = display.getActiveShell(); // can be null, dangerous!
		if (aShell == null) return "";
		return aShell.getBounds().toString();
	}
	@Visible(false)
	@Override
	public String boundsToString(Shell aShell) {
		
		return aShell.getBounds().toString();
	}
	
	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#updateRecorder()
	 */
//	@Override
//	public void updateRecorder() {
//		System.out.println("Active shell:" + boundsToString());
//		if (processExecer != null)
//		processExecer.consoleModel().setInput(boundsToString());
//	}
	
	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	@Visible(false)
	@Override
	public void handleEvent(Event event) {
	
		updateRecorder((Shell) event.widget);
		
	}
	@Visible(false)
	@Override
	public void controlMoved(ControlEvent e) {
		Shell aShell = (Shell)e.getSource();
//		System.out.println ("Changed shell " + boundsToString ((Shell)e.getSource()));
		updateRecorder(aShell);
		// TODO Auto-generated method stub
		
	}
	@Visible(false)
	@Override
	public void controlResized(ControlEvent e) {
		Shell aShell = (Shell)e.getSource();

		System.out.println ("Changed shell " + aShell);
		updateRecorder(aShell);


		// TODO Auto-generated method stub
		
	}
	@Override
	@Visible(false)
	public boolean isStarted() {
		return started;
	}
	@Visible(false)
	public void createUI() {
		DisplayBoundsOutputter aRecorder = RecorderFactory.getSingleton();
		oeFrame = ObjectEditor.edit(aRecorder);
		oeFrame.setSize(350, 150);
		aRecorder.getJavaLocationSetter().initFrame((JFrame) oeFrame.getFrame().getPhysicalComponent());

		
//		oeFrame = HermesObjectEditorProxy.edit(aRecorder, 350, 150);
//		oeFrame.setSize(350, 150);
//		aRecorder.getJavaLocationSetter().initFrame((JFrame) oeFrame);
	}
	

}
