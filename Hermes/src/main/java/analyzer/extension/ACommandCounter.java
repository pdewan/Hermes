package analyzer.extension;

import java.util.Date;

import analyzer.AnAnalyzer;
import analyzer.Analyzer;
import analyzer.AnalyzerListener;
import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import difficultyPrediction.DifficultyPredictionSettings;
import fluorite.commands.EHICommand;

public class ACommandCounter implements AnalyzerListener{

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startTimeStamp(long aStartTimeStamp) {
		
	}

	@Override
	public void finishParticipant(String anId, String aFolder) {
		
	}

	@Override
	public void newCorrectStatus(int aStatus) {
		
	}

	@Override
	public void newStoredCommand(EHICommand aNewCommand) {
		
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
