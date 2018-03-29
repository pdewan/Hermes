package analyzer.extension;

import java.util.Date;

import analyzer.AParticipantTimeLine;
import analyzer.AnAnalyzer;
import analyzer.Analyzer;
import analyzer.AnalyzerListener;
import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import difficultyPrediction.DifficultyPredictionSettings;
import fluorite.commands.EHICommand;
import fluorite.commands.PredictionType;
import fluorite.commands.Status;

public class ACommandCounter implements AnalyzerListener{
	int numPredictions;
	int numDifficultyPredictions;
	int numIndeterminatePredictions;

	int numProgressPredictions;
	int numNoPredictions;
	int numStatuses;
	int numDifficultyStatuses;
	int numSurmountableStatuses;
	int numInsurmountableStatuses;
	int numNullStatuses;
	int numProgressStatuses;
	int numInputCommands;
	int numInputCommandsAfterFirstPrediction; // minus first segment
	int numEvents;
//	boolean madePrediction;
	
	protected void initStatistics() {
		numEvents = 0;
		numPredictions = 0;
		numDifficultyPredictions = 0;
		numProgressPredictions = 0;
		numIndeterminatePredictions = 0;
		numStatuses = 0;
		numDifficultyStatuses = 0;
		numProgressStatuses = 0;
		numSurmountableStatuses = 0;
		numInsurmountableStatuses = 0;
		numNullStatuses = 0;
		numInputCommands = 0;	
		numInputCommandsAfterFirstPrediction = 0;
	}
	
	protected boolean isMadePediction() {
		return numProgressPredictions > 0 || numDifficultyPredictions > 0;
	}
	
	protected void printStatistics() {
		System.out.println("Num Commands:" + numInputCommands);
		System.out.println("Num Commands After First Prediction:" + numInputCommandsAfterFirstPrediction);
		System.out.println("Num Commands Before First Prediction:" + (numInputCommands - numInputCommandsAfterFirstPrediction));
		System.out.println("Num Predictions:" + numPredictions);
		System.out.println("Num Progress Predictions:" + numProgressPredictions);
		System.out.println("Num Difficulty Predictions:" + numDifficultyPredictions);
		System.out.println("Num Indeterminate Predictions:" + (numPredictions - (numDifficultyPredictions + numProgressPredictions)));
		System.out.println("Num Statuses:" + numStatuses);
		System.out.println("Num Difficulty Statuses:" + numDifficultyStatuses);
		System.out.println("Num Surmountable Statuses:" + numSurmountableStatuses);
		System.out.println("Num Insurmountable Statuses:" + numInsurmountableStatuses);
		
	}
	
	
	@Override
	public void newBrowseLine(String aLine) {
		
	}

	@Override
	public void newBrowseEntries(Date aDate, String aSearchString, String aURL) {
		
	}

	@Override
	public void finishedBrowserLines() {
		
	}

	@Override
	public void newParticipant(String anId, String aFolder) {
		initStatistics();
	}

	@Override
	public void startTimeStamp(long aStartTimeStamp) {
		
	}

	@Override
	public void finishParticipant(String anId, String aFolder) {
		printStatistics();		
		
	}

	@Override
	public void newCorrectStatus(int aStatus) {
//		numCorrections++;
//		switch (aStatus) {
//		case	AParticipantTimeLine.SURMOUNTABLE_INT:
//				numDifficultyStatuses++;
//				break;
//		case AParticipantTimeLine.INSURMOUNTABLE_INT:
//			numDifficultyStatuses++;
//			break;
//		case AParticipantTimeLine.PROGRESS_INT:
//			numProgressPredictions++;
//			break;
//		}
//		
	}
	@Override
	public void newCorrectStatus(Status aStatus) {
		numStatuses++;
		if (aStatus == null) {
			numNullStatuses++;
			return;
		}
		switch (aStatus) {
		case Surmountable:
			numSurmountableStatuses++;
			numDifficultyStatuses++;
			break;
		case Insurmountable:
			numInsurmountableStatuses++;
			numDifficultyStatuses++;
		case Making_Progress:
			numProgressStatuses++;
		}		
	}

	@Override
	public void newPrediction(PredictionType aPredictionType) {
		numPredictions++;
		switch (aPredictionType) {
		case MakingProgress: 
			numPredictions++;
			numProgressPredictions++;
			break;
		case HavingDifficulty:
			numPredictions++;
			numDifficultyPredictions++;
			break;	
		case Indeterminate:
			break;
		}
	}

	@Override
	public void newStoredCommand(EHICommand aNewCommand) {
		numEvents++;
	}

	@Override
	public void newStoredInputCommand(EHICommand aNewCommand) {
		numInputCommands++;
		if (isMadePediction()) {
			numInputCommandsAfterFirstPrediction++;
		}
		
	}
	public static void main (String[] args) {
		 DifficultyPredictionSettings.setReplayMode(true);
			//
			 Analyzer analyzer = new AnAnalyzer();
			 AnalyzerListener analyzerListener = new ACommandCounter();
			 analyzer.addAnalyzerListener(analyzerListener);
			 OEFrame frame = ObjectEditor.edit(analyzer);
	}
}
