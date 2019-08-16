package difficultyPrediction.metrics.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import analyzer.AnalyzerFactory;
import analyzer.ParticipantTimeLine;
import analyzer.extension.ARatioFileGenerator;
import analyzer.extension.LiveAnalyzerProcessor;
import config.HelperConfigurationManagerFactory;
import dayton.ellwanger.hermes.HermesActivator;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.DifficultyRobot;
import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.metrics.RatioCalculatorSelector;
import fluorite.commands.DifficultyCommand;
import fluorite.model.EHEventRecorder;
import fluorite.util.CurrentProjectHolder;
import fluorite.util.EHUtilities;

/**
 * Subclasses by RationFileReader so this is not really live, it has the option
 * of being live
 * 
 * @author dewan
 *
 */
public class AMetricsFileGenerator implements MetricsFileGenerator {
;
	public static final String WORKSPACE_RATIOS_FOLDER_NAME = "Metrics";
	public static final String PROJECT_RATIOS_FOLDER_NAME = "Logs/Metrics";
	protected PrintWriter workspaceOut;
	protected Map<String, PrintWriter> projectToPrintWriter = new HashMap<>();
	protected RatioFeatures lastRatioFeatures;
	protected int ratioNumber = 0; // the superclass time line probbaly has this
									// info derivable
	protected boolean doNotWaitForPredictions = false;

	public AMetricsFileGenerator() {
//		if (DifficultyPredictionSettings.isReplayMode()) {
//			AnalyzerFactory.getSingleton().addAnalyzerListener(this);
//		} else {
//			newParticipant(LIVE_USER_NAME, DUMMY_FOLDER_NAME);
//			fillHeader(headerStringBuffer);
//
//		}
		fillHeader(headerStringBuffer);
		DifficultyRobot.getInstance().addRatioFeaturesListener(this);

		DifficultyRobot.getInstance().addStatusListener(this);

	}

//	@Override
//	public void newParticipant(String anId, String aFolder) {
//		super.newParticipant(anId, aFolder);
//		DifficultyRobot.getInstance().addStatusListener(this);
//	}

	protected PrintWriter workspaceOut() {
		try {
			if (workspaceOut == null) {
				workspaceOut = createAndInitializsPrintWriter(workspaceMetrcsFileName());
				// FileWriter fw = new FileWriter(workspaceMetrcsFileName(),
				// true);
				// BufferedWriter bw = new BufferedWriter(fw);
				// workspaceOut = new PrintWriter(bw);
			}
			return workspaceOut;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void logHeader(PrintWriter aPrintwriter, StringBuffer aStringBuffer) {
		// if(aStringBuffer.length() == 0) {
		// appendFeatureHeader(aStringBuffer);
		// }
		aPrintwriter.println(aStringBuffer);
		aPrintwriter.flush();
	}
	protected Map<PrintWriter, File> outToFile = new HashMap<>();
	protected PrintWriter createAndInitializsPrintWriter(String aFileName) {
		try {
			// if (workspaceOut == null) {
			FileWriter fw = new FileWriter(aFileName, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter anOut = new PrintWriter(bw);
			File aFile = new File(aFileName);
			outToFile.put(anOut, aFile);
			// metricsStringBuffer.setLength(0);
			// appendFeatureHeader(metricsStringBuffer);
			logMetrics(anOut, headerStringBuffer);
//			File aFile = new File(aFileName);
//			outToFile.put(anOut, aFile);
			// metricsStringBuffer.setLength(0);

			// }
			return anOut;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected List<String> computedFeatureNames;

	protected List<String> computedFeatureNames() {
		if (computedFeatureNames == null) {
			computedFeatureNames = RatioCalculatorSelector.getRatioCalculator().getComputedFeatureNames();
		}
		return computedFeatureNames;
	}

	/**
	 * Called last so we do not have to put a trailing command
	 */
	protected void appendWebEpilogHeader(StringBuffer aStringBuffer) {
		// aStringBuffer.append("time, numVisits, title, URL");
		aStringBuffer.append("Web Page Visits");

	}

	protected void appendWebEpilogValues(StringBuffer aStringBuffer, RatioFeatures aRatioFeatures) {
		// aStringBuffer.append("time, numVisits, title, URL");
		aStringBuffer.append(aRatioFeatures.getPageVisits());

	}

	protected void appendPrologHeade(StringBuffer aStringBuffer) {
		aStringBuffer.append("Id,Prediction,Date,File,StartTime,ElapsedTime,BusyTime,Commands,");

	}

	static Date date = new Date();

	static Date toDate(long aUnixTime) {
		date.setTime(aUnixTime);
		return date;
	}

	protected void appendPrologPredictionAndRatios(StringBuffer aStringBuffer, RatioFeatures aRatioFeatures, String aStatus) {
		aStringBuffer.append(ratioNumber + "," + aStatus + "," + toDate(aRatioFeatures.getUnixStartTime()) + ","
				+ aRatioFeatures.getFileName() + "," + aRatioFeatures.getUnixStartTime() + ","
				+ aRatioFeatures.getElapsedTime() + "," + aRatioFeatures.getEstimatedBusyTime() + "," + aRatioFeatures.getCommandString() + ",");

	}
	protected void appendPrologAggregateStatus(StringBuffer aStringBuffer, RatioFeatures aRatioFeatures, String aStatus) {
		aStringBuffer.append( "," + aStatus + "," + toDate(aRatioFeatures.getUnixStartTime()) + ","
				+ aRatioFeatures.getFileName() + ", " + aRatioFeatures.getUnixStartTime());

	}
	/**
	 * Do not really need aRatioFeatures	
	 */
	protected void appendPrologManualStatus(StringBuffer aStringBuffer, RatioFeatures aRatioFeatures, DifficultyCommand aDifficultyCommand) {
		long aCurrentTime = aDifficultyCommand.getTimestamp() + EHEventRecorder.getInstance().getStartTimestamp();
		String aStatusString = toString(aDifficultyCommand);
		aStringBuffer.append("MS" + "," + aStatusString + "," + toDate(aCurrentTime) + ","
				+ aRatioFeatures.getFileName() + ", " + aCurrentTime);

	}

	protected void appendFeatureNames(StringBuffer aStringBuffer) {
		// boolean isFirst = true;

		for (String aFeatureName : computedFeatureNames()) {
			// if (!isFirst) {
			// aStringBuffer.append(",");
			//
			// } else {
			// isFirst = false;
			// }
			aStringBuffer.append(aFeatureName);
			aStringBuffer.append(",");

		}

	}

	protected void appendFeatureValues(StringBuffer aStringBuffer, RatioFeatures aRatioFeatures) {
		// boolean isFirst = true;

		for (String aFeatureName : computedFeatureNames()) {
			Double aFeatureValue = (Double) aRatioFeatures.getFeature(aFeatureName);
			// if (!isFirst) {
			// aStringBuffer.append(",");
			// } else {
			// isFirst = false;
			// }
			aStringBuffer.append(aFeatureValue);
			aStringBuffer.append(",");
		}
	}

	protected void fillHeader(StringBuffer aStringBuffer) {
		appendPrologHeade(aStringBuffer);
		appendFeatureNames(aStringBuffer);
		appendWebEpilogHeader(aStringBuffer);
		// boolean isFirst = true;
		//
		// for (String aFeatureName:computedFeatureNames()) {
		// if (!isFirst) {
		// aStringBuffer.append(",");
		//
		// } else {
		// isFirst = false;
		// }
		// aStringBuffer.append(aFeatureName);
		//
		// }

	}

	protected void fillStatusAndFeatures(StringBuffer aStringBuffer, RatioFeatures aRatioFeatures, String aStatus) {
		appendPrologPredictionAndRatios(aStringBuffer, aRatioFeatures, aStatus);
		appendFeatureValues(aStringBuffer, aRatioFeatures);
		appendWebEpilogValues(aStringBuffer, aRatioFeatures);

	}

	// protected void appendToWorkspaceRat(String aFileName, String aText) {
	// try(FileWriter fw = new FileWriter("myfile.txt", true);
	// BufferedWriter bw = new BufferedWriter(fw);
	// PrintWriter out = new PrintWriter(bw))
	// {
	// out.println("the text");
	// //more code
	// out.println("more text");
	// //more code
	// } catch (IOException e) {
	// //exception handling left as an exercise for the reader
	// }
	// }
	protected void appendToWorkspace(String aFileName, String aText) {
		workspaceOut().println(aText);
		// try(FileWriter fw = new FileWriter("myfile.txt", true);
		// BufferedWriter bw = new BufferedWriter(fw);
		// PrintWriter out = new PrintWriter(bw))
		// {
		// out.println("the text");
		// //more code
		// out.println("more text");
		// //more code
		// } catch (IOException e) {
		// //exception handling left as an exercise for the reader
		// }
	}

	protected boolean preSaveRatioFile() {
		return HelperConfigurationManagerFactory.getSingleton().isLogRatio();
	}

	/**
	 * This data does not exist in live mode so we do nothing
	 */
	public void addStuckData(ParticipantTimeLine l) {

	}

	// @Override
	// public ParticipantTimeLine getParticipantTimeLine() {
	// return participantTimeLine;
	// }
	public static String getUniqueMacroNameByTimestamp(long timestamp, boolean autosave) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
		return "Metrics" + format.format(new Date(timestamp)) + (autosave ? "-Autosave" : "") + ".csv";
	}

	protected String metricsFileName(File aLogFolder) {
		long aStartTimestamp = EHEventRecorder.getInstance().getStartTimestamp();
		File outputFile = new File(aLogFolder, getUniqueMacroNameByTimestamp(aStartTimestamp, false));

		return outputFile.getAbsolutePath();
	}

	protected PrintWriter projectOut() {
		// IProject aProject = EHUtilities.getCurrentProject();
		IProject aProject = CurrentProjectHolder.getProject();

		if (aProject == null) {
			return null;
		}
		String aProjectName = aProject.getName();
		PrintWriter aProjectOut = projectToPrintWriter.get(aProjectName);
		if (aProjectOut == null) {
			aProjectOut = createProjectOut(aProjectName);
//			String aProjectFileName = projectMetricsFileName();
//			aProjectOut = createAndInitializsPrintWriter(aProjectFileName);
//			// let us do this each time since print writer may become stale
//			projectToPrintWriter.put(aProjectName, aProjectOut);

		}
		return aProjectOut;
	}
	protected PrintWriter createProjectOut(String aProjectName) {
		String aProjectFileName = projectMetricsFileName();
		PrintWriter aProjectOut = createAndInitializsPrintWriter(aProjectFileName);
		// let us do this each time since print writer may become stale
		projectToPrintWriter.put(aProjectName, aProjectOut);
		return aProjectOut;
	}

	protected String projectMetricsFileName() {
		IProject aProject = CurrentProjectHolder.getProject();
		if (aProject == null) {
			return null;
		}
		String aProjectName = aProject.getName();
		IPath aProjectLocation = aProject.getLocation();
		File aProjectFolderLocation = aProjectLocation.toFile();
		File aMetricsFolderLocation = new File(aProjectFolderLocation, PROJECT_RATIOS_FOLDER_NAME);
		if (!aMetricsFolderLocation.exists()) {
			if (!aMetricsFolderLocation.mkdirs()) {
				return null;
			}
		}

		return metricsFileName(aMetricsFolderLocation);
	}

	protected String workspaceMetrcsFileName() {
		try {
			File logFolder = HermesActivator.getDefault().getStateLocation().append(WORKSPACE_RATIOS_FOLDER_NAME)
					.toFile();
			if (!logFolder.exists()) {
				if (!logFolder.mkdirs()) {
					return null;
					// throw new Exception("Could not make log directory!");
				}
			}
			return metricsFileName(logFolder);
			// long aStartTimestamp =
			// EHEventRecorder.getInstance().getStartTimestamp();
			// File outputFile = new File(logFolder,
			// EHEventRecorder.getUniqueMacroNameByTimestamp(aStartTimestamp,
			// false));
			//
			// return outputFile.getAbsolutePath();
		} catch (Exception e) {
			return null;
		}
	}

	// protected boolean headerSaved = false;
	StringBuffer metricsStringBuffer = new StringBuffer();
	StringBuffer headerStringBuffer = new StringBuffer();

	protected void logMetrics(PrintWriter anOut, StringBuffer aStringBuffer) {
		if (anOut == null) {
			return;
		}
		File aFile = outToFile.get(anOut);
//		long aPostChangeTime = aFile.lastModified();
//		if (aFile == null) {
//			doLogMetrics(anOut, aStringBuffer);
//			return;
//		}
		long aPreChangeTime = aFile.lastModified();
//		anOut.println(aStringBuffer);
//		anOut.flush();
		doLogMetrics(anOut, aStringBuffer);
		

//		logMetrics(workspaceOut(), metricsStringBuffer);
//		logMetrics(anOut, metricsStringBuffer);
		long aPostChangeTime = aFile.lastModified();
		if (aPostChangeTime == aPreChangeTime) {
			handleStaleLogMetrics(anOut, aStringBuffer);
		}
	}
	protected void doLogMetrics(PrintWriter anOut, StringBuffer aStringBuffer) {
//		if (anOut == null) {
//			return;
//		}
//		File aFile = outToFile.get(anOut);
//		long aPostChangeTime = aFile.lastModified();
//		long aPreChangeTime = aFile.lastModified();
		anOut.println(aStringBuffer);
		anOut.flush();
		

//		logMetrics(workspaceOut(), metricsStringBuffer);
//		logMetrics(anOut, metricsStringBuffer);
//		long aPostChangeTime = aFile.lastModified();
//		if (aPostChangeTime == aPreChangeTime) {
//			handleStaleLogMetrics(anOut, aStringBuffer);
//		}
	}
	
	protected void handleStaleLogMetrics(PrintWriter anOut, StringBuffer aStringBuffer) {
		try {
		if (anOut == workspaceOut) {
			workspaceOut = null;
			workspaceOut = workspaceOut();
			doLogMetrics(anOut, aStringBuffer); // do not want infinite recursion
		} else {
			createProjectOut(CurrentProjectHolder.getProject().getName());
			doLogMetrics(anOut, aStringBuffer);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fillValues(String aStatus, String anAggregateStatus, DifficultyCommand aManualStatus) {
		if (HelperConfigurationManagerFactory.getSingleton().isSaveEachRatio()) {

			// if (!headerSaved) {
			// appendFeatureHeader(stringBuffer);
			//// aStringBuffer.append("\n");
			// }
			metricsStringBuffer.setLength(0);
//			metricsStringBuffer.append("\n");
			// workspaceOut().print(stringBuffer);
			// appendFeatureValues (metricsStringBuffer, newVal);
			if (aStatus != null) {
			fillStatusAndFeatures(metricsStringBuffer, lastRatioFeatures, aStatus);
			} else if (anAggregateStatus != null) {
				appendPrologAggregateStatus(metricsStringBuffer, lastRatioFeatures, anAggregateStatus);
			} else if (aManualStatus != null) {
				appendPrologManualStatus(metricsStringBuffer, lastRatioFeatures, aManualStatus);

			}
			PrintWriter aWorkspaceOut = workspaceOut();
			File aFile = outToFile.get(aWorkspaceOut);
			long aPreChangeTime = aFile.lastModified();
//			logMetrics(workspaceOut(), metricsStringBuffer);
			logMetrics(aWorkspaceOut, metricsStringBuffer);
			long aPostChangeTime = aFile.lastModified();
			if (aPostChangeTime == aPreChangeTime) {
				
			}

			PrintWriter aProjectOut = projectOut();

//			logMetrics(projectOut(), metricsStringBuffer);
			logMetrics(aProjectOut, metricsStringBuffer);

			metricsStringBuffer.setLength(0); // twice for safety

		}
	}
	static final String emptyString = "";
	@Override
	public void newRatios(RatioFeatures newVal) {
		lastRatioFeatures = newVal;
		ratioNumber++;
		if (doNotWaitForPredictions) {
			fillValues(emptyString, null, null);
		}
		// if
		// (HelperConfigurationManagerFactory.getSingleton().isSaveEachRatio())
		// {
		//
		//// if (!headerSaved) {
		//// appendFeatureHeader(stringBuffer);
		////// aStringBuffer.append("\n");
		//// }
		// metricsStringBuffer.setLength(0);
		// metricsStringBuffer.append("\n");
		//// workspaceOut().print(stringBuffer);
		//// appendFeatureValues (metricsStringBuffer, newVal);
		// fillValues(metricsStringBuffer, newVal);
		// logMetrics(workspaceOut(), metricsStringBuffer);
		// logMetrics(projectOut(), metricsStringBuffer);
		// metricsStringBuffer.setLength(0); // twice for safety
		//
		// }

	}

	@Override
	public void newStatus(String aStatus) {
		if (doNotWaitForPredictions)
			return;
		fillValues(aStatus, null, null);
	}

	@Override
	public void newAggregatedStatus(String anAggregateStatus) {
		fillValues(null, anAggregateStatus, null);
	}

	@Override
	public void newStatus(int aStatus) {

	}

	@Override
	public void newAggregatedStatus(int aStatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public void newManualStatus(String aManualStatus) {
		

	}

	@Override
	public void newReplayedStatus(int aStatus) {
		// TODO Auto-generated method stub

	}
	
	protected String toString(DifficultyCommand aCommand) {
		switch  (aCommand.getStatus()) {
		case Insurmountable:
			return "YES_I";
		case Surmountable:
			return "YES_S";
		
		case Making_Progress:
			return "NO";
		}
		return "";
	}
	// need a table
	@Override
	public void newManualStatus(DifficultyCommand aCommand) {
//		String aStatusString;
//		switch  (aCommand.getStatus()) {
//		case Insurmountable:
//			aStatusString = "YES_I";
//			break;
//		case Surmountable:
//			aStatusString = "YES_S";
//			break;
//		
//		case Making_Progress:
//			aStatusString = "NO";
//			break;
//		}
		fillValues(null, null, aCommand);

	}

	@Override
	public void modelBuilt(boolean newVal, Exception e) {
		if (!newVal) {
			fillValues("", null, null); // get the last ratio
			fillValues(null, e.getMessage(), null);
			doNotWaitForPredictions = true;

		}
	}

	@Override
	public void predictionError(Exception e) {
		fillValues("", null, null); // get the last ratio
		fillValues(null, e.getMessage(), null);
		doNotWaitForPredictions = true;
		
	}

}
