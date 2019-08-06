package fluorite.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

public class CurrentFileHolder {
	static IFile file;

	public static IFile getIFile() {
		return file;
	}

	public static void setFile(IFile newVal) {
		if (newVal == null) {
			return;
		}
		file = newVal;
	}

}
