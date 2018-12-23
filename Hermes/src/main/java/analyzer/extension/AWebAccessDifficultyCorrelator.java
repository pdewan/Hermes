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
	
	public AWebAccessDifficultyCorrelator(Analyzer anAnalyzer) {
		super(anAnalyzer);
		
	}
	
//	public static double round2DecimalPlaces(double aNum) {
//		return Math.round(100.0 * aNum)/100.0;
//	}
	public void newParticipant(String anId, String aFolder) {
		super.newParticipant(anId, aFolder);
		if (isWriteFile() && AnAnalyzer.ALL_PARTICIPANTS.equals(anId)) {

//		if (DifficultyPredictionSettings.isNewRatioFiles() && AnAnalyzer.ALL_PARTICIPANTS.equals(anId)) {
			outputFileName = computeOutputFileName();
//			writeFile = true;
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
		if (outputFile == null) {
			return;
		}
		try {
			Common.writeText(outputFile, HEADER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected String computeOutputFileName() {
//		return  AnAnalyzer.PARTICIPANT_DIRECTORY + AnAnalyzer.OUTPUT_DATA + WEB_ACCESS_FIlE_NAME;
		return  analyzer.getParticipantsFolderName() + AnAnalyzer.OUTPUT_DATA + WEB_ACCESS_FIlE_NAME;

	}
	public static final String HEADER = 
			"Id,Duration MS, Duration, Web Visits,Web Episodes,Focus,Insurmountable,Surmountable,Predicted, Corrected, Difficulties, DifficultiesNoWebAccess, InsurmNoWebAccess, SurmNoWebAccess, WebAcessInsurm, WebAccessSurm, WebAccessProgress, WebAccessDiff, NumProgresses, NumImdterminates, ProgressIndeterminate, SurmountableIndeterminate, InsurmountableIndeterminate, Predictions, DifficultiesWithWebEpisodes,SurmountableWithWebEpisodes, InsumountableWithWebEpisodes, PredictedWithWebEpisodes, WebAccessDifficulty, WebAccessProgress, NormalWithWebEpisodes";
	@Override
	protected void maybeWriteParticipantStatistics(String anId, String aFolder) {
		if (outputFile == null) {
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
				numCorrectionsOfDifficulty + "," +
				numDifficulties + "," +
				numDifficultiesNoWebAccess + "," +
				numInsurmountableWithoutWebAccesses + "," +
				numSurmountableWithoutWebAccesses + "," +
				
				round2DecimalPlaces(averageWebVisitsInsurmountable) + "," +
				round2DecimalPlaces(averageWebVisitsSurmountable) + "," +
				round2DecimalPlaces(averageWebVisitsInferredProgress) + "," +
				round2DecimalPlaces(averageWebVisitsInferredDifficulty) + "," +
//
//				numWebVisitsBeforeInsurmuntableDifficulties + "," +
//				numWebVisitsBeforeSurmountableDificulties+ "," +
//				numWebVisitsBeforeProgressInference + "," +
//				numWebVisitsBeforeDifficultyInferences + "," +

				numProgresses +  "," +
				numIndeterminates  +  "," +
				numProgressInIndeterminatePeriod + "," +
				numSurmountableInIndeterminatePeriod + "," +
				numInsurmountableInIndeterminatePeriod + "," +
				numStoredPredictions + "," +
				numDifficultiesWithWebEpisodes + "," +
				numSurmountableStatusesWithWebEpisodes + "," +
				numInsurmountableStatusesWithWebEpisodes + "," +
				numPredictedDifficultiesWithWebEpisodes + "," +
//				numWebVisitsBeforeDifficulties + "," +
				round2DecimalPlaces(averageWebVisitsBeforeDifficulties) + "," +
//				numWebVisitsBeforeProgress;
				round2DecimalPlaces(averageWebVisitsBeforeProgress) + "," +
				numProgressWithWebEpisodes;

		try {
			Common.appendText(outputFile, aRow);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	protected void maybeWriteAverageStatistics() {
		String aRow = 
				"average" + "," +
				totalExperimentTime/numParticipants+ "," +
				AnAnalyzer.convertMillSecondsToHMmSs(Math.round(totalExperimentTime/numParticipants))+ "," +
				round2DecimalPlaces(totalWebVisits/numParticipants) + "," +
				round2DecimalPlaces(totalWebEpisodes/numParticipants) + "," +
				totalFocuses/numParticipants + "," +
//				round2DecimalPlaces(numWebVisitsPerFocusChange) + "," +
//				round2DecimalPlaces(numWebEpisodesPerFocusChange) + "," +
				round2DecimalPlaces(totalInsurmountableStatuses/numParticipants) + "," +
				round2DecimalPlaces(totalSurmountableStatuses/numParticipants) + "," +
				round2DecimalPlaces(totalStoredDifficultyPredictions/numParticipants) + "," +
				round2DecimalPlaces(totalProgressCorrections/numParticipants) + "," + 
//				totalDifficulties/numParticipants + "," +
//				-numProgressCorrections + "," +
				round2DecimalPlaces(totalDifficulties/numParticipants) + "," +
				round2DecimalPlaces(totalDifficultiesNoWebAccess/totalDifficulties) + "," +

				round2DecimalPlaces(totalInsurmountableWithoutWebAccesses/totalInsurmountableStatuses) + "," +
				round2DecimalPlaces(totalSurmountableWithoutWebAccesses/totalSurmountableStatuses) + "," +
				round2DecimalPlaces(totalWebVisitsInsurmountable/totalInsurmountableStatuses) + "," +
				round2DecimalPlaces(totalWebVisitsSurmountable/totalSurmountableStatuses) + "," +
				round2DecimalPlaces(totalWebVisitsInferredProgress/totalStoredProgressPredictions) + "," +
				round2DecimalPlaces(totalWebVisitsInferredDifficulty/totalStoredDifficultyPredictions) + "," +
				round2DecimalPlaces(totalProgresses/numParticipants) +  "," +
				round2DecimalPlaces(totalIndeterminates/numParticipants) +  "," +
				round2DecimalPlaces(totalProgressInIndeterminatePeriod/numParticipants) +  "," +
				round2DecimalPlaces(totalSurmountableInIndeterminatePeriod/numParticipants)  + "," +
				round2DecimalPlaces(totalInsurmountableInIndeterminatePeriod/numParticipants) + "," +
				round2DecimalPlaces(totalStoredPredictions/numParticipants) + "," +
				round2DecimalPlaces(totalDifficultiesWithWebEpisodes/numParticipants) + "," +
				round2DecimalPlaces(totalSurmountableStatusesWithWebEpisodes/numParticipants) + "," +
				round2DecimalPlaces(totalInsurmountableStatusesWithWebEpisodes/numParticipants) + "," +
				round2DecimalPlaces(totalPredictedDifficultiesWithWebEpisodes/numParticipants) + "," +
				round2DecimalPlaces(totalWebVisitsBeforeDifficulties/totalDifficulties) + "," +
				round2DecimalPlaces(totalWebVisitsBeforeProgress/totalProgresses) + "," +
				round2DecimalPlaces(totalProgressWithWebEpisodes/numParticipants);
		try {
			Common.appendText(outputFile, aRow);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	protected void maybeWriteTotalStatistics() {
		String aRow = 
				"total" + "," +
				totalExperimentTime+ "," +
				AnAnalyzer.convertMillSecondsToHMmSs(Math.round(totalExperimentTime))+ "," +
				round2DecimalPlaces(totalWebVisits) + "," +
				round2DecimalPlaces(totalWebEpisodes) + "," +
				totalFocuses + "," +
//				round2DecimalPlaces(numWebVisitsPerFocusChange) + "," +
//				round2DecimalPlaces(numWebEpisodesPerFocusChange) + "," +
				round2DecimalPlaces(totalInsurmountableStatuses) + "," +
				round2DecimalPlaces(totalSurmountableStatuses) + "," +
				round2DecimalPlaces(totalStoredDifficultyPredictions) + "," +
				round2DecimalPlaces(totalProgressCorrections) + "," + 
//				totalDifficulties + "," +
//				-numProgressCorrections + "," +
				round2DecimalPlaces(totalDifficulties) + "," +
				round2DecimalPlaces(totalDifficultiesNoWebAccess/totalDifficulties) + "," +

				round2DecimalPlaces(totalInsurmountableWithoutWebAccesses/totalInsurmountableStatuses) + "," +
				round2DecimalPlaces(totalSurmountableWithoutWebAccesses/totalSurmountableStatuses) + "," +
				round2DecimalPlaces(totalWebVisitsInsurmountable/totalInsurmountableStatuses) + "," +
				round2DecimalPlaces(totalWebVisitsSurmountable/totalSurmountableStatuses) + "," +
				round2DecimalPlaces(totalWebVisitsInferredProgress/totalStoredProgressPredictions) + "," +
				round2DecimalPlaces(totalWebVisitsInferredDifficulty/totalStoredDifficultyPredictions) + "," +
				round2DecimalPlaces(totalProgresses) +  "," +
				round2DecimalPlaces(totalIndeterminates) +  "," +
				round2DecimalPlaces(totalProgressInIndeterminatePeriod) +  "," +
				round2DecimalPlaces(totalSurmountableInIndeterminatePeriod)  + "," +
				round2DecimalPlaces(totalInsurmountableInIndeterminatePeriod) + "," +
				round2DecimalPlaces(totalStoredPredictions) + "," +
				round2DecimalPlaces(totalDifficultiesWithWebEpisodes) + "," +
				round2DecimalPlaces(totalSurmountableStatusesWithWebEpisodes) + "," +
				round2DecimalPlaces(totalInsurmountableStatusesWithWebEpisodes) + "," +
				round2DecimalPlaces(totalPredictedDifficultiesWithWebEpisodes) + "," +
				totalWebVisitsBeforeDifficulties + "," +
				totalWebVisitsBeforeProgress + "," +
				totalProgressWithWebEpisodes;
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
			 AWebAccessDifficultyCorrelator analyzerListener = new AWebAccessDifficultyCorrelator(analyzer);
			 analyzer.loadDirectory();
//			 analyzer.getAnalyzerParameters().setNewOutputFiles(true);
			 analyzerListener.setWriteFile(true);
//			 analyzerListener.setWriteFile(false);
			 analyzer.getAnalyzerParameters().getParticipants().setValue("All");
//			 analyzer.getAnalyzerParameters().getParticipants().setValue("16");
			 analyzer.addAnalyzerListener(analyzerListener);
			 analyzer.getAnalyzerParameters().replayLogs();
//			 OEFrame frame = ObjectEditor.edit(analyzer);
	}

}
