package context.recording;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.eclipse.swt.widgets.Shell;

import config.HelperConfigurationManagerFactory;
import bus.uigen.attributes.AttributeNames;
import bus.uigen.misc.OEMisc;
import bus.uigen.models.AFileSetterModel;
import bus.uigen.models.FileSetterModel;
import util.annotations.LayoutName;
import util.annotations.Row;
import util.annotations.Visible;
import util.misc.Common;
import util.pipe.ConsoleModel;
import util.remote.ProcessExecer;
@LayoutName(AttributeNames.GRID_BAG_LAYOUT)
public class ADisplayBoundsFileWriter extends AnAbstractDisplayBoundsOutputter implements  DisplayBoundsOutputter {
	
	public static final String WORKSPACE_PATH = "/Users/nicholasdillon/Documents/UNC/Research/WorkspaceIUI/Record20secs/";
	public static final String RECORDER_JAVA_PATH = "/Library/Java/JavaVirtualMachines/jdk1.7.0_10.jdk/Contents/Home/bin/java";
	public static final String RECORDER_CLASS_PATH = WORKSPACE_PATH + "bin:"
			+ WORKSPACE_PATH + "slf4j-api-1.7.7.jar:"
			+ WORKSPACE_PATH + "slf4j-simple-1.7.7.jar:" 
			+ WORKSPACE_PATH + "xuggle-xuggler-5.4.jar";
	public static final String RECORDER_MAIN_CLASS = "Record20secs";
	
	public static final String[] RECORDER_LAUNCHING_COMMAND = 
		{RECORDER_JAVA_PATH, "-cp" ,  RECORDER_CLASS_PATH, RECORDER_MAIN_CLASS};
	
//	ProcessExecer processExecer;
//	ConsoleModel consoleModel;
//	FileSetterModel recorderJava = new AFileSetterModel(JFileChooser.FILES_ONLY);
	public ADisplayBoundsFileWriter() {		
//		String aRecorderJavaPath = HelperConfigurationManagerFactory.getSingleton().getRecorderJavaPath();
//		if (aRecorderJavaPath != null && !aRecorderJavaPath.isEmpty())
//			recorderJava.setText(aRecorderJavaPath);
			
			
	}
	public String configuredJavaPath() {
		return HelperConfigurationManagerFactory.getSingleton().getRecorderJavaPath();
	}
	@Override
	@Visible(false)
	public String[] launchCommand() {
		return new String[] {getJavaPath(), "-cp",RECORDER_CLASS_PATH, RECORDER_MAIN_CLASS}; 
	}
//	@Row(0)
//	public FileSetterModel getJavaLocation() {
//		return recorderJava;		
//	}
//	@Visible(false)
//	public void setJavaLocation() {
//		System.out.println("Java 7 Location");
//	}
//	@Row(1)
//	public void start() {
//		super.start();
//	}
	
	@Visible(false)
	public void connectToExternalProgram() {
		// commenting out code added probably by Nick
		System.err.println("Calling connect to recorder");
		launch();
//		startRecorder(RECORDER_LAUNCHING_COMMAND);
		// Doesnt matter; just launch recorder and continuously write to file
		//listenToRecorderIOEvents();
	}
//	@Visible(false)
//	public void launch() {
//		launch(RECORDER_LAUNCHING_COMMAND);
//
//	}
//	@Visible(false)
//	public void launch(String[] aCommand) {	
//		// do not need this
////		processExecer = OEMisc.runWithProcessExecer(aCommand);
////		consoleModel = processExecer.getConsoleModel();
//		
//	}
	@Visible(false)
	@Override
	public void updateRecorder(Shell aShell) {
		String baseName = ADisplayBoundsFileWriter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		baseName = baseName.replace("%20", " "); // Replace %20's with spaces
		String outputFilename = baseName + "../../WorkspaceIUI/screencaptures/frame.txt";
		File screencaps = new File(baseName + "/../../WorkspaceIUI/screencaptures");
		if(!screencaps.exists()) {
			System.err.println("screencaptures directory does not exist... creating...");
			screencaps.mkdir();
		}
		String bounds = boundsToString(aShell);
		bounds = bounds.substring(bounds.indexOf('{')+1, bounds.length()-1);
		bounds = bounds.replace(',', ' ');
		System.out.println("Updated shell: " + bounds);
		try {
			Common.writeText(outputFilename, bounds);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Visible(false)
	public void createUI() {
		super.createUI();	
		oeFrame.setSize(500, 150);
		recorderJava.initFrame((JFrame) (oeFrame.getFrame().getPhysicalComponent()));		
	}

	
}