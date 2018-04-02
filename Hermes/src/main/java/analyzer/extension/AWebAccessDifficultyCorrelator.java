package analyzer.extension;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import analyzer.AnAnalyzer;
import analyzer.Analyzer;
import analyzer.AnalyzerListener;
import difficultyPrediction.DifficultyPredictionSettings;
import util.misc.Common;

public class AWebAccessDifficultyCorrelator extends ABasicStoredDataStatistics {
	public static final String WEB_ACCESS_FIlE_NAME = "allWebAccesses.csv";
	protected String outputFileName;
	protected File outputFile;
	protected boolean writeFile;
	
	public AWebAccessDifficultyCorrelator() {
		
	}
	
	public static double round2DecimalPlaces(double aNum) {
		return Math.round(100.0 * aNum)/100.0;
	}
	public void newParticipant(String anId, String aFolder) {
		super.newParticipant(anId, aFolder);
		if (DifficultyPredictionSettings.isNewRatioFiles() && AnAnalyzer.ALL_PARTICIPANTS.equals(anId)) {
			outputFileName = computeOutputFileName();
			writeFile = true;
			outputFile = new File(outputFileName);
			if (!outputFile.exists()) {
				try {
					outputFile.createNewFile();
				} catch (IOException e) {					
					e.printStackTrace();
				}
			}
			maybeWriteHeader();
		}
		
	}
	public void finishParticipant(String anId, String aFolder) {
		super.finishParticipant(anId, aFolder);
		
	}
	
	protected void maybeWriteHeader() {
		if (!writeFile) {
			return;
		}
		try {
			Common.writeText(outputFile, HEADER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected String computeOutputFileName() {
		return  AnAnalyzer.PARTICIPANT_DIRECTORY + AnAnalyzer.OUTPUT_DATA + WEB_ACCESS_FIlE_NAME;
	}
	public static final String HEADER = 
			"Id,Duration MS, Dureation, Web Accesses,Web Access Per Focus,Web Episodes Per Focus,Insurmountable,Surmountable,Predicted, Corrected, Difficulties";
	@Override
	protected void maybeWriteStatistics(String anId, String aFolder) {
		if (!writeFile) {
			return;
		}
		String aRow = 
				anId + "," +
				experimentTime+ "," +
				AnAnalyzer.convertMillSecondsToHMmSs(experimentTime)+ "," +
				numWebVisits + "," +
				numWebEpisodes + "," +
				numGainedFocus + "," +
//				round2DecimalPlaces(numWebVisitsPerFocusChange) + "," +
//				round2DecimalPlaces(numWebEpisodesPerFocusChange) + "," +
				numInsurmountableStatuses + "," +
				numSurmountableStatuses + "," +
				numStoredDifficultyPredictions + "," +
				-numProgressCorrections + "," +
				numDifficulties;
		try {
			Common.appendText(outputFile, aRow);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	protected void printStatistics() {		

		System.out.println("Experiment duration: " + AnAnalyzer.convertMillSecondsToHMmSs(experimentTime));
		System.out.println("Number of web visits:" + numWebVisits);		
		System.out.println("Number of web visits per focus:" + numWebVisitsPerFocusChange);
		System.out.println("Number of web episodes per focus:" + numWebEpisodesPerFocusChange);
		System.out.println("Num Surmountable Statuses:" + numSurmountableStatuses);
		System.out.println("Num Insurmountable Statuses:" + numInsurmountableStatuses);
		System.out.println("Num Difficulty Predictions:" + numStoredDifficultyPredictions);
	}
	
	public static void main (String[] args) {
		 DifficultyPredictionSettings.setReplayMode(true);
			//
			 Analyzer analyzer = new AnAnalyzer();
			 AnalyzerListener analyzerListener = new AWebAccessDifficultyCorrelator();
			 analyzer.loadDirectory();
//			 analyzer.getAnalyzerParameters().setNewOutputFiles(true);
			 analyzer.getAnalyzerParameters().getParticipants().setValue("All");
			 analyzer.addAnalyzerListener(analyzerListener);
			 analyzer.getAnalyzerParameters().replayLogs();
//			 OEFrame frame = ObjectEditor.edit(analyzer);
	}

}
