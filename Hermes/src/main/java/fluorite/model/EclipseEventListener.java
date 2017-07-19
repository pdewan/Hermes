package fluorite.model;

public interface EclipseEventListener extends RecorderListener {
	public void commandExecuted(String aCommandName, long aTimestamp);
	public void documentChanged(String aCommandName, long aTimestamp);
	public void documentChangeFinalized(long aTimestamp);
	
}
