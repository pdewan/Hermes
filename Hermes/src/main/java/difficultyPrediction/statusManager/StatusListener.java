package difficultyPrediction.statusManager;

public interface StatusListener {
	void newStatus(String aStatus);
	void newAggregatedStatus(String aStatus);
	void newStatus(int aStatus);
	void newAggregatedStatus(int aStatus);
	void newManualStatus(String aStatus);
//	void newReplayedStatus(String aStatus);
	void newReplayedStatus(int aStatus);

}
