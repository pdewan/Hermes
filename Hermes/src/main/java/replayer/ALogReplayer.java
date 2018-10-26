package replayer;

import java.io.File;

import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import fluorite.util.EHUtilities;
import util.misc.Common;

public class ALogReplayer {
	
	public void createProject(String aProjectName, String aLocation) {
		EHUtilities.createProjectFromLocation(aProjectName, aLocation);
//		EHUtilities.createProjectFromFolder(aProjectName, aLocation);

	}
	public void addText(String aFileName, String aText) {
		try {
		File aFile = new File(aFileName);
		if (!aFile.exists()) {
			return;
		}
		StringBuffer aPreviousText = Common.toStringBuffer(aFile);
		aPreviousText.append(aText);
		Common.writeText(aFile, aText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static OEFrame createUI() {
		return ObjectEditor.edit(new ALogReplayer());
	}

}
