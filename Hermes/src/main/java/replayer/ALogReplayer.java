package replayer;

import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import fluorite.util.EHUtilities;

public class ALogReplayer {
	
	public void createProject(String aProjectName, String aLocation) {
		EHUtilities.createProjectFromLocation(aProjectName, aLocation);
	}
	
	public static OEFrame createUI() {
		return ObjectEditor.edit(new ALogReplayer());
	}

}
