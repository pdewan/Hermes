package fluorite.util;

import org.eclipse.core.resources.IProject;

public class CurrentProjectHolder {
	static IProject project;

	public static IProject getProject() {
		return project;
	}

	public static void setProject(IProject project) {
		CurrentProjectHolder.project = project;
	}

}
