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
	long startTimestamp;
	long experimentStartTimestamp;
	long lastTimestamp;
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
		experimentStartTimestamp = 0;
		lastTimestamp = 0;
		
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
		System.out.println("Experiment start: " + new Date(experimentStartTimestamp));
		System.out.println("Experiment end: " + new Date (lastTimestamp));
		System.out.println("Experiment duration: " + AnAnalyzer.convertMillSecondsToHMmSs(lastTimestamp - experimentStartTimestamp));
		
		
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
	public void startTimestamp(long aStartTimeStamp) {
		if (experimentStartTimestamp == 0 && aStartTimeStamp != 0) {
			experimentStartTimestamp = aStartTimeStamp;
			System.out.println("Experiment Start");
		}
		startTimestamp = aStartTimeStamp;
		Date aDate = new Date(aStartTimeStamp);
		System.out.println("Start time:" + aDate );
		
	}

	@Override
	public void finishParticipant(String anId, String aFolder) {
		printStatistics();		
		
	}
	
	protected Date dateFromRelativeTime (long aRelativeTime) {
		return new Date(startTimestamp + aRelativeTime);
	}

//	@Override
//	public void newCorrectStatus(int aStatus) {
////		numCorrections++;
////		switch (aStatus) {
////		case	AParticipantTimeLine.SURMOUNTABLE_INT:
////				numDifficultyStatuses++;
////				break;
////		case AParticipantTimeLine.INSURMOUNTABLE_INT:
////			numDifficultyStatuses++;
////			break;
////		case AParticipantTimeLine.PROGRESS_INT:
////			numProgressPredictions++;
////			break;
////		}
////		
//	}
	@Override
	public void newCorrectStatus(Status aStatus, long aStartRelativeTime, long aDuration) {
		numStatuses++;
		if (aStatus == null) {
			numNullStatuses++;
			return;
		}
		switch (aStatus) {
		case Surmountable:
			numSurmountableStatuses++;
			numDifficultyStatuses++;
			System.out.println("Surmountable time:" + dateFromRelativeTime(aStartRelativeTime));
			break;
		case Insurmountable:
			numInsurmountableStatuses++;
			numDifficultyStatuses++;
			System.out.println("Insurmountable time:" + dateFromRelativeTime(aStartRelativeTime));

		case Making_Progress:
			numProgressStatuses++;
		}		
	}

	@Override
	public void newPrediction(PredictionType aPredictionType, long aStartRelativeTime, long aDuration) {
		numStoredPredictions++;
		switch (aPredictionType) {
		case MakingProgress: 
			numStoredProgressPredictions++;
			break;
		case HavingDifficulty:
			numStoredDifficultyPredictions++;
			System.out.println("Difficulty prediction time:" + dateFromRelativeTime(aStartRelativeTime));

			break;	
		case Indeterminate:
			numStoredIndeterminatePredictions++;
			break;
		}
	}

	@Override
	public void newStoredCommand(EHICommand aNewCommand) {
		numEvents++;
		lastTimestamp = startTimestamp + aNewCommand.getTimestamp() + AnAnalyzer.duration(aNewCommand);
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
			 analyzer.loadDirectory();
			 analyzer.getAnalyzerParameters().getParticipants().setValue("16");
			 analyzer.addAnalyzerListener(analyzerListener);
			 analyzer.getAnalyzerParameters().replayLogs();
//			 OEFrame frame = ObjectEditor.edit(analyzer);
	}
}
