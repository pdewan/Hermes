package fluorite.commands;

import java.util.Set;

public class EHLibrariesRemoved extends EHEclipseCommand {
	String newLibraries;

	public EHLibrariesRemoved(String commandId, Set<String> aNewLibraries) {
		super(commandId);
	}
	public String getNewLibraries() {
		return newLibraries;
	}
	public void setNewLibraries(String newLibraries) {
		this.newLibraries = newLibraries;
	}
	

}
