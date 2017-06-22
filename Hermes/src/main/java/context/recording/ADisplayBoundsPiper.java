package context.recording;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Shell;

import config.HelperConfigurationManagerFactory;
import util.annotations.LayoutName;
import util.annotations.Visible;
import util.pipe.ConsoleModel;
import util.remote.ProcessExecer;
import bus.uigen.attributes.AttributeNames;
import bus.uigen.misc.OEMisc;

@LayoutName(AttributeNames.GRID_BAG_LAYOUT)
public class ADisplayBoundsPiper extends AnAbstractDisplayBoundsOutputter implements  DisplayBoundsOutputter, PropertyChangeListener {
	/*
	public static final String RECORDER_JAVA_PATH = "D:/Program Files/Java/jdk1.7.0_51/bin/java";
	public static final String RECORDER_CLASS_PATH = "D:/dewan_backup/Java/eclipse/workspace/FileExample/bin";
	public static final String RECORDER_MAIN_CLASS = "InputAndOutput";
	*/
	
	public static final String WORKSPACE_PATH = "/Users/nicholasdillon/Documents/UNC/Research/WorkspaceIUI/VLCj/";
	public static final String RECORDER_JAVA_PATH = "/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/bin/java"; 
	public static final String RECORDER_CLASS_PATH = WORKSPACE_PATH + "bin:"
			+ WORKSPACE_PATH + "jna-3.5.2.jar:"
			+ WORKSPACE_PATH + "logback-classic-1.1.2.jar:"
			+ WORKSPACE_PATH + "logback-core-1.1.2.jar:"
			+ WORKSPACE_PATH + "lwjgl.jar:"
			+ WORKSPACE_PATH + "platform-3.5.2.jar:"
			+ WORKSPACE_PATH + "vlcj-3.0.1.jar:"
			+ WORKSPACE_PATH + "vlcj-3.0.1-tests.jar:"
			+ WORKSPACE_PATH + "vlcj-3.0.1-test-sources.jar:"
			+ WORKSPACE_PATH + "vlcj-3.0.1-sources.jar:"
			+ WORKSPACE_PATH + "vlcj-3.0.1-javadoc.jar";
	public static final String RECORDER_MAIN_CLASS = "VLCplayer";
	
	/*
	public static final String RECORDER_JAVA_PATH = "/Library/Java/JavaVirtualMachines/jdk1.7.0_10.jdk/Contents/Home/bin/java";
	public static final String RECORDER_CLASS_PATH = "/Users/nicholasdillon/Documents/UNC/Research/WorkspaceIUI/Record20secs/bin:"
			+ "/Users/nicholasdillon/Documents/UNC/Research/WorkspaceIUI/Record20secs/slf4j-api-1.7.7.jar:"
			+ "/Users/nicholasdillon/Documents/UNC/Research/WorkspaceIUI/Record20secs/slf4j-simple-1.7.7.jar:" 
			+ "/Users/nicholasdillon/Documents/UNC/Research/WorkspaceIUI/Record20secs/xuggle-xuggler-5.4.jar";
	public static final String RECORDER_MAIN_CLASS = "Record20secs";
	*/
	
	public static final String[] RECORDER_LAUNCHING_COMMAND = 
		{RECORDER_JAVA_PATH, "-cp" ,  RECORDER_CLASS_PATH, RECORDER_MAIN_CLASS};
	
//	ProcessExecer processExecer;
//	ConsoleModel consoleModel;
	
//	public ADisplayBoundsPiper() {
//		display = Display.getCurrent();
//		display.addListener(SWT.RESIZE, this);
////		startRecorder(RECORDER_LAUNCHING_COMMAND);
////		listenToRecorderIOEvents();
//		
//	}	
//	@Override
//	public void connectToDisplayAndRecorder() {
//		listenToDisplayEvents();
//		connectToRecorder();
//	
//	}
//	@Override
	@Visible(false)
	public void connectToExternalProgram() {
//		launchRecorder(RECORDER_LAUNCHING_COMMAND);	
		System.err.println("Calling connect to recorder");
		launch();
		listenToRecorderIOEvents();
	}
	public String configuredJavaPath() {
		return HelperConfigurationManagerFactory.getSingleton().getRecorderJavaPath();
	}
	@Override
	@Visible(false)
	public String[] launchCommand() {
		return new String[] {getJavaPath(), "-cp",RECORDER_CLASS_PATH, RECORDER_MAIN_CLASS}; 
	}
//	@Override
//	public void listenToDisplayEvents() {
//		System.out.println("Shell " + display.getActiveShell());
////		display.getActiveShell().addListener(SWT.RESIZE, this);
//		Shell[] shells = display.getShells();
//		for (Shell shell:shells) {
//			
//			shell.addListener(SWT.RESIZE, this);
//			shell.addControlListener(this);
//			System.out.println("Shell " + shell);
//
//			System.out.println("Shell bounds " + shell.getBounds());
//		}
////		display.addListener(SWT.RESIZE, this);
//
//	}
	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#startRecorder(java.lang.String)
	 */
//	@Visible(false)
//	public void launchRecorder() {
//		launchRecorder(RECORDER_LAUNCHING_COMMAND);
//
//	}
////	@Override
//	@Visible(false)
//	public void launchRecorder(String[] aCommand) {
//		processExecer = OEMisc.runWithProcessExecer(aCommand);
//		System.err.println(processExecer);
//		consoleModel = processExecer.getConsoleModel();
//	}
	
	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#listenToRecorderIOEvents()
	 */
//	@Override
	@Visible(false)
	public void listenToRecorderIOEvents() {
		processExecer.consoleModel().addPropertyChangeListener(this);
	}
	
	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#boundsToString()
	 */
//	@Override
//	public String boundsToString() {
//		if (display == null) return "";
//		Shell aShell = display.getActiveShell(); // can be null, dangerous!
//		if (aShell == null) return "";
//		return aShell.getBounds().toString();
//	}
//	
//	@Override
//	public String boundsToString(Shell aShell) {
//		
//		return aShell.getBounds().toString();
//	}
	
	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#updateRecorder()
	 */
//	@Override
//	public void updateRecorder() {
//		System.out.println("Active shell:" + boundsToString());
//		if (processExecer != null)
//		processExecer.consoleModel().setInput(boundsToString());
//	}
	@Override
	@Visible(false)
	public void updateRecorder(Shell aShell) {
		System.out.println("Updated shell:" + boundsToString(aShell));
		if (processExecer != null) {
		processExecer.consoleModel().setInput(boundsToString(aShell));
		}
	}
	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#handleEvent(org.eclipse.swt.widgets.Event)
	 */
//	@Override
//	public void handleEvent(Event event) {
//	
//		updateRecorder((Shell) event.widget);
//		
//	}
	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
	}
//	@Override
//	public void controlMoved(ControlEvent e) {
//		Shell aShell = (Shell)e.getSource();
////		System.out.println ("Changed shell " + boundsToString ((Shell)e.getSource()));
//		updateRecorder(aShell);
//		// TODO Auto-generated method stub
//		
//	}
//	@Override
//	public void controlResized(ControlEvent e) {
//		Shell aShell = (Shell)e.getSource();
//
//		System.out.println ("Changed shell " + aShell);
//		updateRecorder(aShell);
//
//
//		// TODO Auto-generated method stub
//		
//	}

	

}
