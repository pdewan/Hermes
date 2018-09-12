package analyzer.extension;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import analyzer.AParticipantTimeLine;
import analyzer.AnAnalyzer;
import analyzer.Analyzer;
import analyzer.AnalyzerListener;
import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import difficultyPrediction.DifficultyPredictionSettings;
import fluorite.commands.DifficultyCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.PredictionCommand;
import fluorite.commands.PredictionType;
import fluorite.commands.ShellCommand;
import fluorite.commands.Status;
import fluorite.commands.WebVisitCommand;

public class ABasicStoredDataStatistics implements AnalyzerListener {
	int numStoredPredictions;
	double totalStoredPredictions;
	int numStoredDifficultyPredictions;
	protected double totalStoredDifficultyPredictions;
	protected double totalStoredProgressPredictions;
	
	int numStoredIndeterminatePredictions;
	double totalStoredIndeterminatePredictions;
	

	int numStoredProgressPredictions;
	int numNoStoredPredictions;
	int numStatuses;
	int numDifficultyStatuses;
	int numSurmountableStatuses;
	double totalSurmountableStatuses;
	int numStoredDifficultiesWithWebEpisodes;
	double totalStoredDifficultiesWithWebEpisodes;
	int numStoredProgressWithWebEpisodes;
	int numProgressWithWebEpisodes;
	double totalProgressWithWebEpisodes;
	int numPredictedDifficultiesWithWebEpisodes;
	double totalPredictedDifficultiesWithWebEpisodes;
	int numDifficultiesWithWebEpisodes;
	double totalDifficultiesWithWebEpisodes;
	int numDifficultiesNoWebAccess;
	double totalDifficultiesNoWebAccess;

	int numCorrectionsOfDifficultyWithWebEpisodes;
	double totalCorectionsToProgressWithWebEpisodes;
	int numDifficultyConfirmationsWithWebEpisodes;
	int numSurmountableStatusesWithWebEpisodes;
	double totalSurmountableStatusesWithWebEpisodes;
	int numInsurmountableStatuses;
	double totalInsurmountableStatuses;
	int numInsurmountableStatusesWithWebEpisodes;
	double totalInsurmountableStatusesWithWebEpisodes;
	int numCorrectPredictions;
	protected double totalCorrectPredictions;
	protected int numDifficulties;
	protected int numProgresses;
	protected int numIndeterminates;
	protected double totalIndeterminates;
	int numNullStatuses;
	protected int numProgressStatuses;
	int numInputCommands;
	int numInputCommandsAfterFirstPrediction; // minus first segment
	int numInputCommandsBeforeFirstPrediction;// plus first segment
	int numEvents;
	long startTimestamp;
	long experimentStartTimestamp;
	long lastTimestamp;
	EHICommand lastNonWebCommand;
	EHICommand lastCommand;
	protected boolean firstCommandAfterStart = true;
	protected int numWebVisits;
	protected int numWebVisitsBeforeSurmountableDificulties;
	protected int numWebVisitsBeforeInsurmuntableDifficulties;
	protected int numWebVisitsBeforeProgressInference;
	protected double numWebVisitsBeforeProgress;
	protected double averageWebVisitsBeforeProgress;
	protected double totalWebVisitsBeforeProgress;

	protected int numWebVisitsBeforeDifficultyInferences;
	protected int numWebVisitsBeforeFalseDifficultyInferences;	
	protected int numWebVisitsBeforeFalseProgressInferences;	

	protected int numWebVisitsBeforeDuplicateDifficultyInferences;

	protected double numWebVisitsBeforeDifficulties;
	protected double averageWebVisitsBeforeDifficulties;
	protected double totalWebVisitsBeforeDifficulties;

	protected int numLostFocus;
	protected int numGainedFocus;
	protected int numWebEpisodes;
	protected int numWebEpisodesBeforeSurmountableDifficulties;
	protected int numWebEpisodesBeforeInsurmountableDifficulties;
	protected long timeOnWebVisits;
	protected boolean lastPredictionWasDifficulty;
	protected boolean lastPredictionWasIndeterminate;
	
	protected int numSurmountableInIndeterminatePeriod = 0;
	protected double totalSurmountableInIndeterminatePeriod = 0;
	
	protected int numInsurmountableInIndeterminatePeriod = 0;
	protected double totalInsurmountableInIndeterminatePeriod = 0;
	
	protected int numProgressInIndeterminatePeriod = 0;
	protected double totalProgressInIndeterminatePeriod = 0;
	
	protected int numCorrectionsOfDifficulty;
	protected double totalProgressCorrections;
	
	protected int numCorrectionsOfProgress;
	protected double totalDifficultyCorrections;
	
	protected int numProgressConfirmations; // not really used
	protected double totalProgressConfirmations;
	
	protected int numDifficultyConfirmations;
	protected double totalDifficultyConfirmations;
	
	

	long maxTimeBetweenCommands;
	protected boolean writeFile;
	protected int numParticipants;
	protected int numWebVisitsSinceLastPrediction;
	protected int numWebEpisodesSinceLastPrediction;
	protected int numSurmountableWithoutWebAccesses;
	protected int numInsurmountableWithoutWebAccesses;
	
	protected double totalSurmountableWithoutWebAccesses;
	protected double totalInsurmountableWithoutWebAccesses;
	
//	protected double numWebAccessesInSurmountable;
//	protected double numWebAccessesInInsurmountable;
	protected double numWebAccessesInInferredSurmountable;
	protected double numWebAccessesInProgress;
	
	
	
	protected Set<String> ignoreParticipants = new HashSet<String>(Arrays.asList(
			new String[]{"33"}));

	// boolean madePrediction;

	

	protected void initStatistics() {
		numParticipants++;
		numEvents = 0;
		numStoredPredictions = 0;
		numStoredDifficultyPredictions = 0;
		numStoredProgressPredictions = 0;
		numStoredIndeterminatePredictions = 0;
		numStatuses = 0;
		numDifficultyStatuses = 0;
		numProgressStatuses = 0;
		numSurmountableStatuses = 0;
		numSurmountableStatusesWithWebEpisodes = 0;
		numInsurmountableStatuses = 0;
		numInsurmountableStatusesWithWebEpisodes = 0;
		numStoredDifficultiesWithWebEpisodes = 0;
		numCorrectionsOfDifficultyWithWebEpisodes = 0;
		numDifficultyConfirmationsWithWebEpisodes = 0;
		numStoredProgressWithWebEpisodes = 0;
		numProgressWithWebEpisodes = 0;
		numNullStatuses = 0;
		numInputCommands = 0;
		numInputCommandsAfterFirstPrediction = 0;
		numInputCommandsBeforeFirstPrediction = 0;
		experimentStartTimestamp = 0;
		maxTimeBetweenCommands = 0;
		lastNonWebCommand = null;
		lastCommand = null;
		numWebVisits = 0;
		numWebVisitsBeforeInsurmuntableDifficulties = 0;
		numWebVisitsBeforeSurmountableDificulties = 0;
		numWebVisitsBeforeProgressInference = 0;
		numWebVisitsBeforeDifficultyInferences = 0;
		numWebVisitsBeforeDifficulties = 0; //not really needed
		numWebVisitsBeforeFalseDifficultyInferences = 0;
		numWebVisitsBeforeFalseProgressInferences = 0;
		numWebVisitsBeforeDuplicateDifficultyInferences = 0;
		numWebVisitsSinceLastPrediction = 0;
		numLostFocus = 0;
		numGainedFocus = 0;
		numWebEpisodes = 0;
		numWebEpisodesBeforeSurmountableDifficulties = 0;
		numWebEpisodesBeforeInsurmountableDifficulties = 0;
		numWebEpisodesSinceLastPrediction = 0;
		numInsurmountableWithoutWebAccesses = 0;
		numSurmountableWithoutWebAccesses = 0;
		
		timeOnWebVisits = 0;
		lastPredictionWasDifficulty = false;
		lastPredictionWasIndeterminate = false;

		
		numCorrectionsOfDifficulty = 0;
		numProgressConfirmations = 0;
		
		numCorrectionsOfProgress = 0;
		numDifficultyConfirmations = 0;
		
		numSurmountableInIndeterminatePeriod = 0;
		
		numInsurmountableInIndeterminatePeriod = 0;
		
		numProgressInIndeterminatePeriod = 0;
		

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
		// System.out.println("Num Stored Difficulty Predictions:" +
		// numStoredDifficultyPredictions);
		System.out.println("Num Stored Indeterminate Predictions:" + numStoredIndeterminatePredictions);
		System.out.println("Commands per stored indeterminate prediction:"
				+ ((double) numInputCommandsBeforeFirstPrediction) / numStoredIndeterminatePredictions);
		System.out.println(
				"Commands per stored non indeterninate prediction:" + ((double) numInputCommandsAfterFirstPrediction)
						/ (numStoredDifficultyPredictions + numStoredProgressPredictions));
		System.out.println("Commands per stored  prediction:" + ((double) numInputCommands) / (numStoredPredictions));

		System.out.println("Num Statuses:" + numStatuses);
		System.out.println("Num Difficulty Statuses:" + numDifficultyStatuses);
		// System.out.println("Num Surmountable Statuses:" +
		// numSurmountableStatuses);
		// System.out.println("Num Insurmountable Statuses:" +
		// numInsurmountableStatuses);
		// System.out.println("Num Progress Statuses:" + numProgressStatuses);

		System.out.println("Experiment start: " + new Date(experimentStartTimestamp));
		System.out.println("Experiment end: " + new Date(lastTimestamp));
		long anExperimentTime = lastTimestamp - experimentStartTimestamp;
		System.out.println("Experiment duration: " + AnAnalyzer.convertMillSecondsToHMmSs(anExperimentTime));
		System.out.println("Web visit duration: " + AnAnalyzer.convertMillSecondsToHMmSs(timeOnWebVisits));
		double aWebVisitFraction = ((double) timeOnWebVisits) / anExperimentTime;
		double anInputTime = anExperimentTime - timeOnWebVisits;
		System.out.println("Web visit time fraction: " + aWebVisitFraction);
		long aWebEpisodeDuration = Math.round(((double) timeOnWebVisits) / numWebEpisodes);
		System.out.println("Web episode duration:" + AnAnalyzer.convertMillSecondsToHMmSs(aWebEpisodeDuration));
		long aTimeBetweenPredictions = Math.round(((double) anExperimentTime) / numStoredPredictions);
		System.out
				.println("Time between predictions: " + AnAnalyzer.convertMillSecondsToHMmSs(aTimeBetweenPredictions));
		long aTimeBetweenCommands = Math.round(((double) anExperimentTime) / numInputCommands);
		System.out.println(
				"Time between 300 commands: " + AnAnalyzer.convertMillSecondsToHMmSs(aTimeBetweenCommands * 300));
		long aTimeBetweenWebEpisodes = Math.round(((double) anExperimentTime) / numWebEpisodes);
		System.out
				.println("Time between web episodes: " + AnAnalyzer.convertMillSecondsToHMmSs(aTimeBetweenWebEpisodes));
		System.out.println("Max time between commands: " + maxTimeBetweenCommands);
		System.out.println("Number of lost focus:" + numLostFocus);
		// System.out.println("Number of gained focus:" + numGainedFocus);
		System.out.println("Number of web visits:" + numWebVisits);
		System.out.println("Number of web episodes:" + numWebEpisodes);
		double aWebEpisodeLength = ((double) numWebVisits) / numWebEpisodes;
		System.out.println("Number of web visits per episode:" + aWebEpisodeLength);
		double aCommandsBetweenWebEpisodes = ((double) numInputCommands) / numWebEpisodes;
		System.out.println("Commands between web episodes:" + aCommandsBetweenWebEpisodes);
		System.out.println("Time for 60 commands:"
				+ AnAnalyzer.convertMillSecondsToHMmSs((Math.round(anInputTime / anInputTime))));
		double aTimeBetweenWebVisits = ((double) anExperimentTime) / numWebVisits;
		System.out.println("Time between web visits:" + aTimeBetweenWebVisits);

		// double aTimeBetweenWebEpisodes = ((double)
		// anExperimentTime)/numWebEpisodes;
		// System.out.println("Time between web episodes:" +
		// aTimeBetweenWebEpisodes);

		double aNumWebVisitsPerFocusChange = ((double) numWebVisits) / numGainedFocus;
		System.out.println("Number of web visits per focus:" + aNumWebVisitsPerFocusChange);
		System.out.println("Num Surmountable Statuses:" + numSurmountableStatuses);
		System.out.println("Num Insurmountable Statuses:" + numInsurmountableStatuses);
		System.out.println("Num Progress Statuses:" + numProgressStatuses);
		System.out.println("Num Stored Difficulty Predictions:" + numStoredDifficultyPredictions);

	}

	long experimentTime;
	double webVisitFraction;
	long inputTime;
	long webEpisodeDuration;
	long timeBetweenPredictions;
	long timeBetweenCommands;
	long timeBetweenWebEpisodes;
	double webEpisodeLength;
	double commandsBetweenWebEpisodes;
	double timeBetweenWebVisits;
	double numWebVisitsPerFocusChange;
	double numWebEpisodesPerFocusChange;
	
	protected double totalExperimentTime;
	protected double totalWebEpisodes;
	protected double totalFocuses;
	protected double totalWebVisits;
	protected double totalDifficulties;
	protected double totalProgresses;
	protected double averageWebVisitsSurmountable;
	protected double averageWebVisitsInsurmountable;
	protected double averageWebVisitsInferredProgress;
	protected double averageWebVisitsInferredDifficulty;
	
	protected double totalWebVisitsSurmountable;
	protected double totalWebVisitsInsurmountable;
	protected double totalWebVisitsInferredProgress;
	protected double totalWebVisitsInferredDifficulty;
	public static double round2DecimalPlaces(double aNum) {
		return Math.round(100.0 * aNum)/100.0;
	}
	protected void computeDerivedStatistics() {

		experimentTime = lastTimestamp - experimentStartTimestamp;

		webVisitFraction = ((double) timeOnWebVisits) / experimentTime;
		inputTime = experimentTime - timeOnWebVisits;
		webEpisodeDuration = Math.round(((double) timeOnWebVisits) / numWebEpisodes);
		timeBetweenPredictions = Math.round(((double) experimentTime) / numStoredPredictions);
		timeBetweenCommands = Math.round(((double) experimentTime) / numInputCommands);
		timeBetweenWebEpisodes = Math.round(((double) experimentTime) / numWebEpisodes);

		webEpisodeLength = ((double) numWebVisits) / numWebEpisodes;
		commandsBetweenWebEpisodes = ((double) numInputCommands) / numWebEpisodes;

		timeBetweenWebVisits = ((double) experimentTime) / numWebVisits;
		numWebVisitsPerFocusChange = ((double) numWebVisits) / numGainedFocus;
		numWebEpisodesPerFocusChange = ((double) numWebEpisodes) / numGainedFocus;
		numDifficultyStatuses = numSurmountableStatuses + numInsurmountableStatuses;
		numCorrectPredictions = numStoredDifficultyPredictions - numCorrectionsOfDifficulty;
		totalCorrectPredictions += numCorrectPredictions;
		
		totalProgressCorrections += numCorrectionsOfDifficulty;
		
		totalProgressConfirmations += numProgressConfirmations;
		
		totalDifficultyCorrections += numCorrectionsOfProgress;
		
		totalDifficultyConfirmations += numDifficultyConfirmations;
		
		numDifficulties = numDifficultyStatuses + 
						numStoredDifficultyPredictions 
						- numCorrectionsOfDifficulty
						- numDifficultyConfirmations; 
		numPredictedDifficultiesWithWebEpisodes = 
				numStoredDifficultiesWithWebEpisodes 
				- numCorrectionsOfDifficultyWithWebEpisodes 
				- numDifficultyConfirmationsWithWebEpisodes ;
		totalPredictedDifficultiesWithWebEpisodes += numPredictedDifficultiesWithWebEpisodes;
		numDifficultiesWithWebEpisodes =
				numSurmountableStatusesWithWebEpisodes +
				numInsurmountableStatusesWithWebEpisodes +
				numPredictedDifficultiesWithWebEpisodes;
//				numStoredDifficultiesWithWebEpisodes 
//				- numCorrectionsToProgressWithWebEpisodes 
//				- numDifficultyConfirmationsWithWebEpisodes;
		numWebVisitsBeforeDifficulties =
				numWebVisitsBeforeInsurmuntableDifficulties
				+ numWebVisitsBeforeSurmountableDificulties
				+numWebVisitsBeforeDifficultyInferences
				+numWebVisitsBeforeFalseProgressInferences
				- numWebVisitsBeforeDuplicateDifficultyInferences;
		
		averageWebVisitsBeforeDifficulties = round2DecimalPlaces(numWebVisitsBeforeDifficulties/numDifficulties);
		
		totalWebVisitsBeforeDifficulties += numWebVisitsBeforeDifficulties;		
			
		numDifficultiesNoWebAccess =  (numDifficulties - numDifficultiesWithWebEpisodes);
		totalDifficultiesNoWebAccess += numDifficultiesNoWebAccess;
		
		numProgresses = numProgressStatuses
						+ numStoredProgressPredictions
						- numCorrectionsOfProgress
						- numProgressConfirmations;	
		numProgressWithWebEpisodes = numStoredProgressWithWebEpisodes 
						-numSurmountableStatusesWithWebEpisodes
						- numInsurmountableStatusesWithWebEpisodes
						+ numDifficultyConfirmationsWithWebEpisodes;
		totalProgressWithWebEpisodes += numProgressWithWebEpisodes;
		numIndeterminates = numStoredIndeterminatePredictions 
				- numSurmountableInIndeterminatePeriod
				- numInsurmountableInIndeterminatePeriod
				-numProgressInIndeterminatePeriod;
		numWebVisitsBeforeProgress = numWebVisitsBeforeProgressInference +
				numWebVisitsBeforeFalseDifficultyInferences;
		averageWebVisitsBeforeProgress = round2DecimalPlaces(numWebVisitsBeforeProgress/numProgresses);
		totalWebVisitsBeforeProgress += numWebVisitsBeforeProgress;
		
		if (numProgresses + numDifficulties != numStoredDifficultyPredictions + numStoredProgressPredictions) {
			System.err.println("Assertion failed regarding num difficulties and numProgresses");
		}
																					
		
		averageWebVisitsSurmountable = ((double) numWebVisitsBeforeSurmountableDificulties)/numSurmountableStatuses;
		averageWebVisitsInsurmountable = ((double) numWebVisitsBeforeInsurmuntableDifficulties)/numInsurmountableStatuses;
		averageWebVisitsInferredProgress = ((double) numWebVisitsBeforeProgressInference)/numStoredProgressPredictions;
		averageWebVisitsInferredDifficulty = ((double) numWebVisitsBeforeDifficultyInferences)/numStoredDifficultyPredictions;
		
		totalWebVisitsSurmountable += numWebVisitsBeforeSurmountableDificulties;
		totalWebVisitsInsurmountable += numWebVisitsBeforeInsurmuntableDifficulties;
		totalWebVisitsInferredProgress += numWebVisitsBeforeProgressInference;
		totalWebVisitsInferredDifficulty += numWebVisitsBeforeDifficultyInferences;
		
		totalStoredDifficultyPredictions += numStoredDifficultyPredictions;
		totalStoredProgressPredictions += numStoredProgressPredictions;
		
		totalSurmountableStatuses += numSurmountableStatuses;
		totalInsurmountableStatuses += numInsurmountableStatuses;
		
		totalSurmountableWithoutWebAccesses += numSurmountableWithoutWebAccesses;
		totalInsurmountableWithoutWebAccesses += numInsurmountableWithoutWebAccesses;
		
		totalStoredPredictions += numStoredPredictions;
		
		totalStoredIndeterminatePredictions += numStoredIndeterminatePredictions;
		
		totalInsurmountableInIndeterminatePeriod += numInsurmountableInIndeterminatePeriod;
		totalSurmountableInIndeterminatePeriod += numSurmountableInIndeterminatePeriod;
		totalProgressInIndeterminatePeriod+= numProgressInIndeterminatePeriod;

//		totalWebVisitsInsurmountable += numInsurmountableWithoutWebAccesses;		
		
//		totalWebVisitsInsurmountable += numWebV;		

		totalExperimentTime += experimentTime;																			// corrected
		totalDifficulties += numDifficulties;
		totalProgresses += numProgresses;
		totalIndeterminates += numIndeterminates;
		totalWebVisits += numWebVisits;
		totalWebEpisodes += numWebEpisodes;
		totalFocuses += numGainedFocus;
		
		totalInsurmountableStatusesWithWebEpisodes += numInsurmountableStatusesWithWebEpisodes;
		totalSurmountableStatusesWithWebEpisodes += numSurmountableStatusesWithWebEpisodes;
		totalStoredDifficultiesWithWebEpisodes += numStoredDifficultiesWithWebEpisodes++;
		totalCorectionsToProgressWithWebEpisodes += numCorrectionsOfDifficultyWithWebEpisodes;
		totalDifficultiesWithWebEpisodes += numDifficultiesWithWebEpisodes;

	}

	@Override
	public void newBrowseLine(String aLine) {

	}

	@Override
	public void newBrowseEntries(Date aDate, String aSearchString, String aURL) {
		// System.out.println(aDate + " Search string: " + aSearchString + " URL
		// " + aURL);

	}
	protected void maybeWriteAverageStatistics() {
		
	}
	protected void maybeWriteTotalStatistics() {
		
	}


	@Override
	public void finishedBrowserLines() {		
		maybeWriteAverageStatistics();
		maybeWriteTotalStatistics();

	}

	@Override
	public void newParticipant(String anId, String aFolder) {
		if (aFolder == null || ignoreParticipants.contains(anId))
			return;
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
		System.out.println("Start time:" + aDate);
		long aTimeFromDate = aDate.getTime();
		if (startTimestamp != aTimeFromDate) {
			System.out.println("Conversion error");
		}

	}

	protected void maybeWriteParticipantStatistics(String anId, String aFolder) {

	}

	@Override
	public void finishParticipant(String anId, String aFolder) {
		if (aFolder == null || ignoreParticipants.contains(anId))
			return;
			;
		computeDerivedStatistics();
		printStatistics();
		maybeWriteParticipantStatistics(anId, aFolder);
	}

	protected Date dateFromAbsoluteTime(long aAbsoluteTime) {
		return new Date(aAbsoluteTime);
	}

	// @Override
	// public void newCorrectStatus(int aStatus) {
	//// numCorrections++;
	//// switch (aStatus) {
	//// case AParticipantTimeLine.SURMOUNTABLE_INT:
	//// numDifficultyStatuses++;
	//// break;
	//// case AParticipantTimeLine.INSURMOUNTABLE_INT:
	//// numDifficultyStatuses++;
	//// break;
	//// case AParticipantTimeLine.PROGRESS_INT:
	//// numProgressPredictions++;
	//// break;
	//// }
	////
	// }
	protected void newCorrectDifficultyStatus(DifficultyCommand newCommand, Date aDate, boolean surmountable, boolean confirm) {
		System.out.println("Date:" + aDate + " Surmountable?:" + surmountable + " confirm previus prediction?" + confirm);
	}
	protected void newDifficultyCorrection(DifficultyCommand newCommand, Date aDate) {
		System.out.println("Date:" + aDate + " correcting previous difficulty prediction");
	}
	@Override
	public void newCorrectStatus(DifficultyCommand newCommand, Status aStatus, long aStartAbsoluteTime,
			long aDuration) {
		Date aDate = new Date(aStartAbsoluteTime);
		numStatuses++;
		if (aStatus == null) {
			numNullStatuses++;
			return;
		}
		switch (aStatus) {
		case Surmountable:
			numSurmountableStatuses++;
//			numDifficultyStatuses++;
			System.out.println(aDate + " Surmountable " + newCommand);
			if (numWebVisitsSinceLastPrediction > 0) {
				numSurmountableStatusesWithWebEpisodes++;
			} else {
				numSurmountableWithoutWebAccesses++;
			}
			numWebEpisodesBeforeSurmountableDifficulties += numWebEpisodesBeforeSurmountableDifficulties;
			if (numWebVisitsSinceLastPrediction > 0) // for debugging			
			numWebVisitsBeforeSurmountableDificulties += numWebVisitsSinceLastPrediction;
			System.out.println("numWebVisitsSinceLastPrediction " + numWebVisitsSinceLastPrediction);
			if (lastPredictionWasDifficulty) {
				newCorrectDifficultyStatus(newCommand, aDate, true, true);
				numDifficultyConfirmations++;
				if (numWebVisitsSinceLastPrediction > 0) {
					numDifficultyConfirmationsWithWebEpisodes++;
					numWebVisitsBeforeDuplicateDifficultyInferences += numWebVisitsSinceLastPrediction;

				}
			} 
			
			else if (lastPredictionWasIndeterminate) {
				numSurmountableInIndeterminatePeriod++;
				newCorrectDifficultyStatus(newCommand, aDate, true, false);
			}
			
			else  {
				numCorrectionsOfProgress++;
				numWebVisitsBeforeFalseProgressInferences += numWebVisitsSinceLastPrediction; 
				newCorrectDifficultyStatus(newCommand, aDate, true, false);

			}
			
			break;
		case Insurmountable:
			numInsurmountableStatuses++;
			if (numWebVisitsSinceLastPrediction > 0) {
				numInsurmountableStatusesWithWebEpisodes++;
			} else {
				numInsurmountableWithoutWebAccesses++;
			}
			if (numWebVisitsSinceLastPrediction > 0) // for debugging
			numWebEpisodesBeforeInsurmountableDifficulties += numWebEpisodesSinceLastPrediction;
			if (numWebVisitsSinceLastPrediction > 0) // for debugging
			numWebVisitsBeforeInsurmuntableDifficulties += numWebVisitsSinceLastPrediction;
//			numDifficultyStatuses++;
			System.out.println(aDate + "Insurmountable " + newCommand);
			System.out.println("numWebVisitsSinceLastPrediction " + numWebVisitsSinceLastPrediction);
			if (lastPredictionWasDifficulty) {
				newCorrectDifficultyStatus(newCommand, aDate, false, true);
				numDifficultyConfirmations++;
				if (numWebVisitsSinceLastPrediction > 0) {
					numDifficultyConfirmationsWithWebEpisodes++;
				}
				numWebVisitsBeforeDuplicateDifficultyInferences += numWebVisitsSinceLastPrediction;
			} else if  (lastPredictionWasIndeterminate) {
				newCorrectDifficultyStatus(newCommand, aDate, false, false);
				numInsurmountableInIndeterminatePeriod++;
			} else {
				newCorrectDifficultyStatus(newCommand, aDate, false, false);
				numCorrectionsOfProgress++;
				numWebVisitsBeforeFalseProgressInferences += numWebVisitsSinceLastPrediction; 
			}
			

			break;

		case Making_Progress:
			System.out.println(aDate + "Progress  " + newCommand);
			numProgressStatuses++;
			if (numWebVisitsSinceLastPrediction > 0) // for debugging
				numWebVisitsBeforeProgressInference += numWebVisitsSinceLastPrediction;
			if (lastPredictionWasDifficulty) {
				newDifficultyCorrection(newCommand, aDate);
				numCorrectionsOfDifficulty++;
				numWebVisitsBeforeFalseDifficultyInferences += numWebVisitsSinceLastPrediction;
				if (numWebVisitsSinceLastPrediction > 0) {
					numCorrectionsOfDifficultyWithWebEpisodes--; 
				}
			} else if (lastPredictionWasIndeterminate) {
				numProgressInIndeterminatePeriod++;
			} else {
			
				numProgressConfirmations++;
			}
			
	
		}
	}
	
	protected void newDifficultyPrediction(PredictionCommand newParam, Date aDate) {
		System.out.println("Difficulty predicted at " + aDate);
	}

	@Override
	public void newPrediction(PredictionCommand newParam, PredictionType aPredictionType, long aStartAbsoluteTime,
			long aDuration) {
		Date aDate = dateFromAbsoluteTime(aStartAbsoluteTime);
		numStoredPredictions++;
		switch (aPredictionType) {
		case MakingProgress:
			if (numWebVisitsSinceLastPrediction > 0) {
				numStoredProgressWithWebEpisodes++; // assuming this is not cancelled
			} 
			lastPredictionWasIndeterminate = false;

			numStoredProgressPredictions++;
			lastPredictionWasDifficulty = false;
			if (numWebVisitsSinceLastPrediction > 0) // for debugging 	
			numWebVisitsBeforeProgressInference += numWebVisitsSinceLastPrediction;
			break;
		case HavingDifficulty:
			if (numWebVisitsSinceLastPrediction > 0) {
				numStoredDifficultiesWithWebEpisodes++; // assuming this is not cancelled
			} 
			lastPredictionWasIndeterminate = false;
			numStoredDifficultyPredictions++;
			System.out.println(dateFromAbsoluteTime(aStartAbsoluteTime) + " Difficulty prediction");
			newDifficultyPrediction(newParam, aDate);
			lastPredictionWasDifficulty = true;
			if (numWebVisitsSinceLastPrediction > 0) // for debugging
			numWebVisitsBeforeDifficultyInferences += numWebVisitsSinceLastPrediction;
			break;
		case Indeterminate:
			lastPredictionWasIndeterminate = true;

			numStoredIndeterminatePredictions++;
			lastPredictionWasDifficulty = false;
			break;
		}
		numWebVisitsSinceLastPrediction = 0;
		numWebEpisodesSinceLastPrediction = 0;
	}

	protected void computeMaxTimeBetweenCommands(long aStartAbsoluteTime, EHICommand aCommand) {
		long aTimeSinceLastCommand = aStartAbsoluteTime - lastTimestamp;
		maxTimeBetweenCommands = Math.max(aTimeSinceLastCommand, maxTimeBetweenCommands);
		if (maxTimeBetweenCommands == aTimeSinceLastCommand) {
			Date aDate = new Date(aStartAbsoluteTime);
			System.out.println(aDate + " New max time:" + maxTimeBetweenCommands + " new command" + aCommand);
			System.out.println(dateFromAbsoluteTime(lastTimestamp) + " Previous command:" + lastNonWebCommand);
			// System.out.println("New Command:"+ aCommand);
		}

	}

	protected void computeAndPrintInputCommandStatistics(EHICommand aNewCommand, long aStartAbsoluteTime,
			long aDuration) {
		numEvents++;
		computeMaxTimeBetweenCommands(aStartAbsoluteTime, aNewCommand);
		lastTimestamp = aStartAbsoluteTime + aDuration;
		if (lastCommand instanceof WebVisitCommand) {
			// System.out.println(dateFromAbsoluteTime(aStartAbsoluteTime) + "
			// Returning from web visit:" + aNewCommand);
		}
		lastNonWebCommand = aNewCommand;
		lastCommand = aNewCommand;

		if (isMadePediction()) {
			numInputCommandsAfterFirstPrediction++;
		} else {
			numInputCommandsBeforeFirstPrediction++;
		}
		if (aNewCommand instanceof ShellCommand) {
			// System.out.println(dateFromAbsoluteTime(aStartAbsoluteTime) + " "
			// + aNewCommand);
			if (ShellCommand.isFocusGain(aNewCommand)) {
				numGainedFocus++;
			} else if (ShellCommand.isFocusLost(aNewCommand)) {
				numLostFocus++;
			}
		}
	}

	@Override
	public void newStoredCommand(EHICommand aNewCommand, long aStartAbsoluteTime, long aDuration) {
		if (aNewCommand.getTimestamp() == 0) {
			return;
		}
		// computeInputCommandStatistics(aNewCommand, aStartAbsoluteTime,
		// aDuration);
	}

	@Override
	public void newStoredInputCommand(EHICommand aNewCommand, long aStartAbsoluteTime, long aDuration) {
		numInputCommands++;
		if (isMadePediction()) {
			numInputCommandsAfterFirstPrediction++;
		} else {
			numInputCommandsBeforeFirstPrediction++;
		}

		computeAndPrintInputCommandStatistics(aNewCommand, aStartAbsoluteTime, aDuration);

	}

	@Override
	public void newWebVisit(WebVisitCommand aWebVisitCommand, long aStartAbsoluteTime, long aDuration) {
		numWebVisits++;
		numWebVisitsSinceLastPrediction++;
		Date aDate = new Date(aStartAbsoluteTime);

		if (!(lastCommand instanceof WebVisitCommand)) {
			numWebEpisodes++;
			numWebEpisodesSinceLastPrediction++;
			// System.out.println(aDate + "Last non web command before web
			// visit:" + lastCommand );
		}
		lastCommand = aWebVisitCommand;
		// System.out.println(aDate + " " + "WebVisit->" +
		// aWebVisitCommand.getSearchString() + ":" +
		// aWebVisitCommand.getUrl());
		String aSearchString = aWebVisitCommand.getSearchString();
		if (aSearchString.startsWith("http:")) {
			return;
		}
		System.out.println(aDate + " " + "SearchedWebVisit->" + aWebVisitCommand.getSearchString() + ":"
				+ aWebVisitCommand.getUrl());

	}

	@Override
	public void newBrowserCommands(List<WebVisitCommand> aCommands) {
		timeOnWebVisits = 0;
		for (WebVisitCommand aCommand : aCommands) {
			timeOnWebVisits += AnAnalyzer.duration(aCommand);

		}
	}

	@Override
	public void experimentStartTimestamp(long aStartTimeStamp) {
		// TODO Auto-generated method stub

	}
	public boolean isWriteFile() {
		return writeFile;
	}

	public void setWriteFile(boolean writeFile) {
		this.writeFile = writeFile;
	}

	public static void main(String[] args) {
		DifficultyPredictionSettings.setReplayMode(true);
		//
		Analyzer analyzer = new AnAnalyzer();
		AnalyzerListener analyzerListener = new ABasicStoredDataStatistics();
		analyzer.loadDirectory();
		analyzer.getAnalyzerParameters().getParticipants().setValue("17");
		analyzer.addAnalyzerListener(analyzerListener);
		analyzer.getAnalyzerParameters().replayLogs();
		// OEFrame frame = ObjectEditor.edit(analyzer);
	}

	@Override
	public void replayFinished() {
		maybeWriteAverageStatistics();
		maybeWriteTotalStatistics();
		
	}

	@Override
	public void replayStarted() {
		// TODO Auto-generated method stub
		
	}
}
