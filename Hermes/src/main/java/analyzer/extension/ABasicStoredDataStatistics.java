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

public class ABasicStoredDataStatistics implements AnalyzerListener{
	int numStoredPredictions;
	int numStoredDifficultyPredictions;
	int numStoredIndeterminatePredictions;

	int numStoredProgressPredictions;
	int numNoStoredPredictions;
	int numStatuses;
	int numDifficultyStatuses;
	int numSurmountableStatuses;
	int numInsurmountableStatuses;
	int numNullStatuses;
	int numProgressStatuses;
	int numInputCommands;
	int numInputCommandsAfterFirstPrediction; // minus first segment
	int numInputCommandsBeforeFirstPrediction;// plus first segment
	int numEvents;
//	boolean madePrediction;
	
	protected void initStatistics() {
		numEvents = 0;
		numStoredPredictions = 0;
		numStoredDifficultyPredictions = 0;
		numStoredProgressPredictions = 0;
		numStoredIndeterminatePredictions = 0;
		numStatuses = 0;
		numDifficultyStatuses = 0;
		numProgressStatuses = 0;
		numSurmountableStatuses = 0;
		numInsurmountableStatuses = 0;
		numNullStatuses = 0;
		numInputCommands = 0;	
		numInputCommandsAfterFirstPrediction = 0;
		numInputCommandsBeforeFirstPrediction = 0;
	}
	
	protected boolean isMadePediction() {
		return numStoredProgressPredictions > 0 || numStoredDifficultyPredictions > 0;
	}
	
	protected void printStatistics() {
		System.out.println("Num Commands:" + numInputCommands);
		System.out.println("Num Commands After First Prediction:" + numInputCommandsAfterFirstPrediction);
		System.out.println("Num Commands Before First Prediction:" + numInputCommandsBeforeFirstPrediction);
		System.out.println("Num Stored Predictions:" + numStoredPredictions);
		System.out.println("Num Stored Progress Predictions:" + numStoredProgressPredictions);
		System.out.println("Num Stored Difficulty Predictions:" + numStoredDifficultyPredictions);
		System.out.println("Num Stored Indeterminate Predictions:" + numStoredIndeterminatePredictions);
		System.out.println("Commands per stored indeterminate prediction:" + ((double) numInputCommandsBeforeFirstPrediction)/numStoredIndeterminatePredictions );
		System.out.println("Commands per stored non indeterninate prediction:" + ((double) numInputCommandsAfterFirstPrediction)/(numStoredDifficultyPredictions + numStoredProgressPredictions) );
		System.out.println("Commands per stored  prediction:" + ((double) numInputCommands)/(numStoredPredictions) );

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
		numStoredPredictions++;
		switch (aPredictionType) {
		case MakingProgress: 
			numStoredProgressPredictions++;
			break;
		case HavingDifficulty:
			numStoredDifficultyPredictions++;
			break;	
		case Indeterminate:
			numStoredIndeterminatePredictions++;
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
		} else {
			numInputCommandsBeforeFirstPrediction++;
		}
		
	}
	public static void main (String[] args) {
		 DifficultyPredictionSettings.setReplayMode(true);
			//
			 Analyzer analyzer = new AnAnalyzer();
			 AnalyzerListener analyzerListener = new ABasicStoredDataStatistics();
			 analyzer.addAnalyzerListener(analyzerListener);
			 OEFrame frame = ObjectEditor.edit(analyzer);
	}
}
