package analyzer;

import fluorite.commands.DifficultyCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.PredictionCommand;
import fluorite.commands.PredictionType;
import fluorite.commands.Status;
import fluorite.commands.WebVisitCommand;

public interface AnalyzerListener extends BrowseHistoryListener{
	void newParticipant(String anId, String aFolder);
//	void newFeatures(RatioFeatures aFeatures);
	public void startTimestamp(long aStartTimeStamp);
	public void experimentStartTimestamp(long aStartTimeStamp);
	public void replayFinished();
	public void replayStarted();

	
	void finishParticipant(String anId, String aFolder);
//	void newCorrectStatus (int aStatus);
	void newStoredCommand(EHICommand aNewCommand, long aStartAbsoluteTime, long aDuration);
	void newStoredInputCommand(EHICommand aNewCommand, long aStartAbsoluteTime, long aDuration);
	void newCorrectStatus (DifficultyCommand newCommand, Status aStatus, long aStartAbsoluteTime, long aDuration);
	void newPrediction(PredictionCommand newParam, PredictionType aPredictionType, long aStartAbsoluteTime, long aDuration);
	void newWebVisit(WebVisitCommand aWebVisitCommand, long aStartAbsoluteTime,long aDuration);
}
