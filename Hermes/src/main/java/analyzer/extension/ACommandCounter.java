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

public class ACommandCounter implements AnalyzerListener{
	int numPredictions;
	int numDifficultyPredictions;

	int numProgressPredictions;
	int numNoPredictions;
	int numCorrections;
	int numDifficultyStatuses;
	int numSurmountableStatuses;
	int numInsurmountableStatuses;
	int numProgressCorrections;
	int numInputCommands;
	int numEvents;
	
	protected void initStatistics() {
		numEvents = 0;
		numPredictions = 0;
		numDifficultyPredictions = 0;
		numProgressPredictions = 0;
		numCorrections = 0;
		numDifficultyStatuses = 0;
		numProgressCorrections = 0;
		numInputCommands = 0;		
	}
	
	protected void printStatistics() {
		System.out.println("Num Commands:" + numInputCommands);
		System.out.println("Num Predictions:" + numPredictions);
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
		
		
	}

	@Override
	public void newCorrectStatus(int aStatus) {
		numCorrections++;
		switch (aStatus) {
		case	AParticipantTimeLine.SURMOUNTABLE_INT:
				numDifficultyStatuses++;
				break;
		case AParticipantTimeLine.INSURMOUNTABLE_INT:
			numDifficultyStatuses++;
			break;
		case AParticipantTimeLine.PROGRESS_INT:
			numProgressPredictions++;
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
