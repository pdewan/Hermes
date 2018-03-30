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
import fluorite.commands.WebVisitCommand;

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
	
	long maxTimeBetweenCommands;
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
		maxTimeBetweenCommands = 0;
		
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
		long anExperimentTime = lastTimestamp - experimentStartTimestamp;
		System.out.println("Experiment duration: " + AnAnalyzer.convertMillSecondsToHMmSs(anExperimentTime));
		long aTimeBetweenPredictions = Math.round(((double) anExperimentTime)/numStoredPredictions);
		System.out.println("Time between predictions: " + AnAnalyzer.convertMillSecondsToHMmSs(aTimeBetweenPredictions) );
		long aTimeBetweenCommands = Math.round(((double) anExperimentTime)/numInputCommands);
		System.out.println("Milliseconds between commands: " + aTimeBetweenCommands );
		System.out.println("Max time between commands: " + maxTimeBetweenCommands);

		
	}
	
	
	@Override
	public void newBrowseLine(String aLine) {
		
	}

	@Override
	public void newBrowseEntries(Date aDate, String aSearchString, String aURL) {
		System.out.println(aDate + " Search string: " + aSearchString + " URL " + aURL);
		
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
		lastTimestamp = aStartTimeStamp;

		startTimestamp = aStartTimeStamp;
		Date aDate = new Date(aStartTimeStamp);
		System.out.println("Start time:" + aDate );
		long aTimeFromDate = aDate.getTime();
		if (startTimestamp != aTimeFromDate) {
			System.out.println("Conversion error");
		}
		
	}

	@Override
	public void finishParticipant(String anId, String aFolder) {
		printStatistics();		
		
	}
	
	protected Date dateFromAbsoluteTime (long aAbsoluteTime) {
		return new Date(aAbsoluteTime);
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
	public void newCorrectStatus(Status aStatus, long aStartAbsoluteTime, long aDuration) {
		numStatuses++;
		if (aStatus == null) {
			numNullStatuses++;
			return;
		}
		switch (aStatus) {
		case Surmountable:
			numSurmountableStatuses++;
			numDifficultyStatuses++;
			System.out.println("Surmountable time:" + dateFromAbsoluteTime(aStartAbsoluteTime));
			break;
		case Insurmountable:
			numInsurmountableStatuses++;
			numDifficultyStatuses++;
			System.out.println("Insurmountable time:" + dateFromAbsoluteTime(aStartAbsoluteTime));

		case Making_Progress:
			numProgressStatuses++;
		}		
	}

	@Override
	public void newPrediction(PredictionType aPredictionType, long aStartAbsoluteTime, long aDuration) {
		numStoredPredictions++;
		switch (aPredictionType) {
		case MakingProgress: 
			numStoredProgressPredictions++;
			break;
		case HavingDifficulty:
			numStoredDifficultyPredictions++;
			System.out.println("Difficulty prediction time:" + dateFromAbsoluteTime(aStartAbsoluteTime));

			break;	
		case Indeterminate:
			numStoredIndeterminatePredictions++;
			break;
		}
	}
	
	protected void computeMaxTimeBetweenCommands(EHICommand aCommand) {
		long aTimeSinceLastCommand = startTimestamp + aCommand.getTimestamp() - lastTimestamp;
		maxTimeBetweenCommands = Math.max(aTimeSinceLastCommand, maxTimeBetweenCommands);
		if (maxTimeBetweenCommands == aTimeSinceLastCommand ) {
			System.out.println("New max time:" + maxTimeBetweenCommands);
		}
		
	}

	@Override
	public void newStoredCommand(EHICommand aNewCommand, long aStartAbsoluteTime, long aDuration) {
		if (aNewCommand.getTimestamp() == 0) {
			return;
		}
		numEvents++;
		computeMaxTimeBetweenCommands(aNewCommand);
		lastTimestamp = aStartAbsoluteTime + aDuration;
	}

	@Override
	public void newStoredInputCommand(EHICommand aNewCommand, long aStartAbsoluteTime, long aDuration) {
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

	@Override
	public void newWebVisit(WebVisitCommand aWebVisitCommand, long aStartAbsoluteTime, long aDuration) {
		Date aDate = new Date(aStartAbsoluteTime);
		System.out.println("WebVisit->" + aDate + ":" + aWebVisitCommand.getSearchString() + ":" + aWebVisitCommand.getUrl());
		
	}
}
