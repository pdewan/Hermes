package fluorite.model;

public interface RecorderListener {
	void eventRecordingStarted(long aStartTimestamp);
	void eventRecordingEnded();

}
