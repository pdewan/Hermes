package replayer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;

import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import fluorite.util.EHUtilities;
import util.misc.Common;

public class ALogReplayer {
	
	IProject lastProject;
	IEditorPart lastEditor;
	public static final String TEST_PROJECT_NAME = "DummyProj";
	public static final String TEST_PROJECT_LOCATION = "D:/TestReplay/DummyProject";
	public static final String TEST_FILE = "src/HelloWorld.java";

	
	
	protected void createOrGetProject(String aProjectName, String aLocation) {
		lastProject = EHUtilities.createProjectFromLocation(aProjectName, aLocation);
//		EHUtilities.createProjectFromFolder(aProjectName, aLocation);

	}
	public void createOrGetPredefinedProject() {
		createOrGetProject(TEST_PROJECT_NAME, TEST_PROJECT_LOCATION);
	}
	public void openEditorOfPredefinedFile() {
		openEditorFromSwingThread(TEST_FILE);	
	}
	public static void printWorkingDirectory() {
		 System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		 Path currentRelativePath = Paths.get("");
		 String s = currentRelativePath.toAbsolutePath().toString();
		 System.out.println("Current relative path is: " + s);
		 System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
		 
	}
	protected void addTextAndRefresh(String aFolderName, String aRelativeFileName, String aText) {
		String aFileName = aFolderName + "/" + aRelativeFileName;
		try {
			
		File aFile = new File(aFileName);
		if (!aFile.exists()) {
			return;
		}
		StringBuffer aPreviousText = Common.toStringBuffer(aFile);
//		aPreviousText.append(aText);
		Common.writeText(aFile, aPreviousText.toString() + aText);
		refreshFile(aRelativeFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void addTextAndRefreshPredefinedFile(String aText) {
		addTextAndRefresh (TEST_PROJECT_LOCATION, TEST_FILE, aText);
		
	}
//	public void openEditor(String aFileName) {
//		if (lastProject == null)
//			return;
//		lastEditor = EHUtilities.openEditor(lastProject, aFileName);
//		
//	}
	protected void openEditorInUIThread(String aFileName) {
		if (lastProject == null)
			return;
		
	 EHUtilities.openEditorInUIThread(lastProject, aFileName);
		
	}
	protected void openEditorFromSwingThread(String aFileName) {
		if (lastProject == null)
			return;
		Runnable newRunnable = new Runnable() {

			@Override
			public void run() {
				openEditorInUIThread(aFileName);
			}
			
		};
		Thread newThread = new Thread(newRunnable) ;
		newThread.start();
		
	}
	public void refreshPredefinedFile() {
		refreshFile(TEST_FILE);
	}
	protected void refreshFile(String aFileName) {
		if (lastProject == null) {
			return;
		}
		EHUtilities.refreshFile(lastProject, aFileName);	
//		Runnable aRunnable = new Runnable() {
//			@Override
//			public void run() {
//				EHUtilities.refreshFile(lastProject, aFileName);				
//			}			
//		};
//		(new Thread (aRunnable)).start();
		
	}
	
	public static OEFrame createUI() {
		return ObjectEditor.edit(new ALogReplayer());
	}
	public static void main (String[] args) {
		createUI();
	}

}
