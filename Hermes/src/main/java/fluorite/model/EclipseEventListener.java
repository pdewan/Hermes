package fluorite.model;

public interface EclipseEventListener extends RecorderListener {
	public void commandExecuted(long aTimestamp);
	public void documentChanged(long aTimestamp);
	public void documentChangeFinalized(long aTimestamp);
	
}
