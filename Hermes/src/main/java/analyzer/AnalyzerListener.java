package analyzer;

import fluorite.commands.EHICommand;
import fluorite.commands.PredictionType;
import fluorite.commands.Status;

public interface AnalyzerListener extends BrowseHistoryListener{
	void newParticipant(String anId, String aFolder);
//	void newFeatures(RatioFeatures aFeatures);
	public void startTimestamp(long aStartTimeStamp);
	
	void finishParticipant(String anId, String aFolder);
//	void newCorrectStatus (int aStatus);
	void newStoredCommand(EHICommand aNewCommand);
	void newStoredInputCommand(EHICommand aNewCommand);
	void newCorrectStatus (Status aStatus, long aStartRelativeTime, long aDuration);
	void newPrediction(PredictionType aPredictionType, long aStartRelativeTime, long aDuration);

}
