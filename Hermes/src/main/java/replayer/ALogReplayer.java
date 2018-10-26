package replayer;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;

import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import fluorite.util.EHUtilities;
import util.misc.Common;

public class ALogReplayer {
	
	IProject lastProject;
	IEditorPart lastEditor;
	
	public void createOrGetProject(String aProjectName, String aLocation) {
		lastProject = EHUtilities.createProjectFromLocation(aProjectName, aLocation);
//		EHUtilities.createProjectFromFolder(aProjectName, aLocation);

	}
	public void addTextAndRefresh(String aFileName, String aText) {
		try {
		File aFile = new File(aFileName);
		if (!aFile.exists()) {
			return;
		}
		StringBuffer aPreviousText = Common.toStringBuffer(aFile);
		aPreviousText.append(aText);
		Common.writeText(aFile, aText);
		refreshFile(aFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
//	public void openEditor(String aFileName) {
//		if (lastProject == null)
//			return;
//		lastEditor = EHUtilities.openEditor(lastProject, aFileName);
//		
//	}
	public void openEditorInUIThread(String aFileName) {
		if (lastProject == null)
			return;
	 EHUtilities.openEditorInUIThread(lastProject, aFileName);
		
	}
	public void refreshFile(String aFileName) {
		if (lastProject == null) {
			return;
		}
		EHUtilities.refreshFile(lastProject, aFileName);
	}
	
	public static OEFrame createUI() {
		return ObjectEditor.edit(new ALogReplayer());
	}

}
