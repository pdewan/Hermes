package replayer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;

import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import fluorite.util.EHUtilities;
import util.annotations.Visible;
import util.misc.Common;

public class ALogReplayer implements IExecutionListener {
	
	IProject lastProject;
	IEditorPart lastEditor;
	public static final String TEST_PROJECT_NAME = "DummyProj";
	public static final String TEST_PROJECT_LOCATION = "D:/TestReplay/DummyProject";
	public static final String TEST_FILE = "src/HelloWorld.java";
	String[] commands = new String[] {
			IWorkbenchCommandConstants.EDIT_COPY,
			IWorkbenchCommandConstants.EDIT_CONTENT_ASSIST,
			IWorkbenchCommandConstants.FILE_RENAME

			

	};
	
	String javaVersion = "JavaSE-1.8";

	public void listenToCommands() { 
		ICommandService commandService = EHUtilities.getCommandService();
		if (commandService == null) {
			System.out.println("No command service");
		}
		try {
			for (String aCommandName: commands) {				
			
//		Command aCommand = commandService.getCommand("com.foo.the.command").executeWithChecks(new ExecutionEvent());
				Command aCommand = commandService.getCommand(aCommandName);
				aCommand.addExecutionListener(this);
			}
		

		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	@Visible(false)
	public String getJavaVersion() {
		return javaVersion;
	}
	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}
	public void createOrGetProject(String aProjectName, String aLocation) {
		lastProject = EHUtilities.createProjectFromLocation(aProjectName, aLocation, javaVersion);
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
	@Visible(false)
	@Override
	public void notHandled(String commandId, NotHandledException exception) {
		// TODO Auto-generated method stub
		
	}
	@Visible(false)
	@Override
	public void postExecuteFailure(String commandId, ExecutionException exception) {
		System.out.println("Post execution failure:" + commandId);

		
	}
	@Visible(false)
	@Override
	public void postExecuteSuccess(String commandId, Object returnValue) {
		System.out.println("Post execution success:" + returnValue);
		
	}
	@Visible(false)
	@Override
	public void preExecute(String commandId, ExecutionEvent event) {
		System.out.println("Pre execution success:" + event);
		
	}

}