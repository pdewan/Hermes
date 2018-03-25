package fluorite.commands;

import java.util.Set;

public class LibrariesRemoved extends EclipseCommand {
	String newLibraries;

	public LibrariesRemoved(String commandId, Set<String> aNewLibraries) {
		super(commandId);
	}
	public String getNewLibraries() {
		return newLibraries;
	}
	public void setNewLibraries(String newLibraries) {
		this.newLibraries = newLibraries;
	}
	

}
