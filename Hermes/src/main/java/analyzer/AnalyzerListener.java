package analyzer;

import fluorite.commands.EHICommand;
import fluorite.commands.PredictionType;
import fluorite.commands.Status;
import fluorite.commands.WebVisitCommand;

public interface AnalyzerListener extends BrowseHistoryListener{
	void newParticipant(String anId, String aFolder);
//	void newFeatures(RatioFeatures aFeatures);
	public void startTimestamp(long aStartTimeStamp);
	
	void finishParticipant(String anId, String aFolder);
//	void newCorrectStatus (int aStatus);
	void newStoredCommand(EHICommand aNewCommand, long aStartAbsoluteTime, long aDuration);
	void newStoredInputCommand(EHICommand aNewCommand, long aStartAbsoluteTime, long aDuration);
	void newCorrectStatus (Status aStatus, long aStartAbsoluteTime, long aDuration);
	void newPrediction(PredictionType aPredictionType, long aStartAbsoluteTime, long aDuration);
	void newWebVisit(WebVisitCommand aWebVisitCommand, long aStartAbsoluteTime,long aDuration);
}
