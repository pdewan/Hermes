package analyzer.extension;

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
import config.HelperConfigurationManagerFactory;
import dayton.ellwanger.hermes.HermesActivator;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.metrics.CommandCategory;
import difficultyPrediction.metrics.RatioCalculatorSelector;
import fluorite.model.EHEventRecorder;
import fluorite.util.CurrentProjectHolder;
import fluorite.util.EHUtilities;
/**
 * Subclasses by RationFileReader so this is not really live, it has the option of being live
 * @author dewan
 *
 */
public class ALiveRatioFileGenerator extends ARatioFileGenerator implements LiveAnalyzerProcessor {
    public static final String LIVE_USER_NAME = "Live User";
    public static final String DUMMY_FOLDER_NAME = "Live Dummy Folder";
	public static final String WORKSPACE_RATIOS_FOLDER_NAME = "Metrics";
	public static final String PROJECT_RATIOS_FOLDER_NAME = "Logs/Metrics";
	protected PrintWriter workspaceOut; 
	protected Map<String, PrintWriter> projectToPrintWriter = new HashMap<>();

    public ALiveRatioFileGenerator() {
    	if (DifficultyPredictionSettings.isReplayMode()) {
    		AnalyzerFactory.getSingleton().addAnalyzerListener(this);
    	} else {
    		newParticipant(LIVE_USER_NAME, DUMMY_FOLDER_NAME);
    		fillHeader(headerStringBuffer);
    		
    	}
    }
    protected PrintWriter workspaceOut() {
    	try {
    	if (workspaceOut == null) {
    		workspaceOut = createAndInitializsPrintWriter(workspaceMetrcsFileName());
//    		FileWriter fw = new FileWriter(workspaceMetrcsFileName(), true);
//    		BufferedWriter bw = new BufferedWriter(fw);
//        	workspaceOut = new PrintWriter(bw);
    	}
    	return workspaceOut;
    	} catch (Exception e){    		
    		e.printStackTrace();
    		return null;
    	}
    }
    protected void logHeader(PrintWriter aPrintwriter, StringBuffer aStringBuffer) {
//    	if(aStringBuffer.length() == 0) {
//    		appendFeatureHeader(aStringBuffer);
//    	}
    	aPrintwriter.println(aStringBuffer);
    	aPrintwriter.flush();
    }
    protected PrintWriter createAndInitializsPrintWriter(String aFileName) {
    	try {
//    	if (workspaceOut == null) {
    		FileWriter fw = new FileWriter(aFileName, true);
    		BufferedWriter bw = new BufferedWriter(fw);
        	PrintWriter anOut = new PrintWriter(bw);
//        	metricsStringBuffer.setLength(0);
//        	appendFeatureHeader(metricsStringBuffer);
        	logMetrics(anOut, headerStringBuffer);
//        	metricsStringBuffer.setLength(0);
        	
//    	}
    	return anOut;
    	} catch (Exception e){    		
    		e.printStackTrace();
    		return null;
    	}
    }
    protected List<String> computedFeatureNames;
    protected List<String> computedFeatureNames() {
    	if (computedFeatureNames == null) {
    		computedFeatureNames =RatioCalculatorSelector.getRatioCalculator().getComputedFeatureNames();
    	}
    	return computedFeatureNames;
    }
    /**
     * Called last so we do not have to put a trailing command
     */
    protected void appendWebEpilogHeader(StringBuffer aStringBuffer) {
//    	aStringBuffer.append("time, numVisits,  title, URL");
    	aStringBuffer.append("Web Page Visits");
    	
    }
    protected void appendWebEpilogValues(StringBuffer aStringBuffer, RatioFeatures aRatioFeatures) {
//    	aStringBuffer.append("time, numVisits,  title, URL");
    	aStringBuffer.append(aRatioFeatures.getPageVisits());
    	
    }
    protected void appendPrologHeade(StringBuffer aStringBuffer) {
    	aStringBuffer.append("Date, File, StartTime, ElapsedTime, BusyTime, ");
    	
    }
    static Date date = new Date();
    static Date toDate(long aUnixTime) {
    	date.setTime(aUnixTime);
    	return date;
    }
    protected void appendrPrologValues(StringBuffer aStringBuffer, RatioFeatures aRatioFeatures) {
    	aStringBuffer.append(
    			toDate(aRatioFeatures.getUnixStartTime()) + "," +
    			aRatioFeatures.getFileName() + ", " +
    			aRatioFeatures.getUnixStartTime() + ", " +
    			aRatioFeatures.getElapsedTime() + ", " +
    			aRatioFeatures.getEstimatedBusyTime()
    			);
    	
    }
  
    protected void appendFeatureNames(StringBuffer aStringBuffer) {
//    	boolean isFirst = true;
    	
    	for (String aFeatureName:computedFeatureNames()) {
//    		if (!isFirst) {
//    			aStringBuffer.append(",");
//
//    		} else {
//    			isFirst = false;
//    		}
			aStringBuffer.append(aFeatureName);
			aStringBuffer.append(",");

    	}
    	 
    }
    protected void appendFeatureValues(StringBuffer aStringBuffer, RatioFeatures aRatioFeatures) {
//    	boolean isFirst = true;
    	
    	for (String aFeatureName:computedFeatureNames()) {
    		Double aFeatureValue = (Double) aRatioFeatures.getFeature(aFeatureName);
//    		if (!isFirst) {
//    			aStringBuffer.append(",");
//    		} else {
//    			isFirst = false;
//    		}
			aStringBuffer.append(aFeatureValue);
			aStringBuffer.append(",");
    	}    	 
    }
    
    protected void fillHeader(StringBuffer aStringBuffer) {
    	appendPrologHeade(aStringBuffer);
    	appendFeatureNames(aStringBuffer);
    	appendWebEpilogHeader(aStringBuffer);
//    	boolean isFirst = true;
//    	
//    	for (String aFeatureName:computedFeatureNames()) {
//    		if (!isFirst) {
//    			aStringBuffer.append(",");
//
//    		} else {
//    			isFirst = false;
//    		}
//			aStringBuffer.append(aFeatureName);
//
//    	}
    	 
    }
    protected void fillValues(StringBuffer aStringBuffer, RatioFeatures aRatioFeatures) {
    	appendrPrologValues(aStringBuffer, aRatioFeatures);
    	appendFeatureValues(aStringBuffer, aRatioFeatures);
    	appendWebEpilogValues(aStringBuffer, aRatioFeatures);

    }
    
    
//    protected void appendToWorkspaceRat(String aFileName, String aText) {
//    	try(FileWriter fw = new FileWriter("myfile.txt", true);
//    		    BufferedWriter bw = new BufferedWriter(fw);
//    		    PrintWriter out = new PrintWriter(bw))
//    		{
//    		    out.println("the text");
//    		    //more code
//    		    out.println("more text");
//    		    //more code
//    		} catch (IOException e) {
//    		    //exception handling left as an exercise for the reader
//    		}
//    }
    protected void appendToWorkspace(String aFileName, String aText) {
    	workspaceOut().println(aText);
//    	try(FileWriter fw = new FileWriter("myfile.txt", true);
//    		    BufferedWriter bw = new BufferedWriter(fw);
//    		    PrintWriter out = new PrintWriter(bw))
//    		{
//    		    out.println("the text");
//    		    //more code
//    		    out.println("more text");
//    		    //more code
//    		} catch (IOException e) {
//    		    //exception handling left as an exercise for the reader
//    		}
    }
    
    protected boolean preSaveRatioFile() {
		return HelperConfigurationManagerFactory.getSingleton().isLogRatio();
	}
    /**
     * This data does not exist in live mode so we do nothing
     */
    public void addStuckData(ParticipantTimeLine l) {
    	
    }
//	@Override
//	public ParticipantTimeLine getParticipantTimeLine() {
//		return participantTimeLine;
//	}
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
//    	IProject aProject = EHUtilities.getCurrentProject();
    	IProject aProject = CurrentProjectHolder.getProject();

    	if (aProject == null) {
			return null;
		}
    	String aProjectName = aProject.getName();
    	PrintWriter aProjectOut = projectToPrintWriter.get(aProjectName);
		if (aProjectOut == null) {
			String aProjectFileName = projectMetricsFileName();
			aProjectOut = createAndInitializsPrintWriter(aProjectFileName);
			projectToPrintWriter.put(aProjectName, aProjectOut);
			
		}
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
		File aMetricsFolderLocation = new File(aProjectFolderLocation, PROJECT_RATIOS_FOLDER_NAME );
		if (!aMetricsFolderLocation.exists()) {
			if (!aMetricsFolderLocation.mkdirs()) {
				return null;
			}
		}
		
		return metricsFileName(aMetricsFolderLocation);
    }
    @Override
    protected String workspaceMetrcsFileName()  {
		try {
			File logFolder = HermesActivator.getDefault().getStateLocation().append(WORKSPACE_RATIOS_FOLDER_NAME)
					.toFile();
			if (!logFolder.exists()) {
				if (!logFolder.mkdirs()) {
					return null;
//					throw new Exception("Could not make log directory!");
				}
			}
			return metricsFileName(logFolder);
//			long aStartTimestamp = EHEventRecorder.getInstance().getStartTimestamp();
//			File outputFile = new File(logFolder, EHEventRecorder.getUniqueMacroNameByTimestamp(aStartTimestamp, false));
//
//			return outputFile.getAbsolutePath();
		} catch (Exception e) {
			return null;
		}
	}
//    protected boolean headerSaved = false;
    StringBuffer metricsStringBuffer = new StringBuffer();
    StringBuffer headerStringBuffer = new StringBuffer();
    
    protected void logMetrics(PrintWriter anOut, StringBuffer aStringBuffer) {
    	if (anOut == null) {
    		return;
    	}
    	anOut.println(aStringBuffer);
    	anOut.flush();
    	
    }
    
    @Override
	public void newRatios(RatioFeatures newVal) {
    	super.newRatios(newVal);
    	if (HelperConfigurationManagerFactory.getSingleton().isSaveEachRatio()) {
    		
//    		if (!headerSaved) {
//    			appendFeatureHeader(stringBuffer);
////    			aStringBuffer.append("\n");
//    		}
    		metricsStringBuffer.setLength(0);
    		metricsStringBuffer.append("\n");
//    		workspaceOut().print(stringBuffer);
//    		appendFeatureValues (metricsStringBuffer, newVal);
    		fillValues(metricsStringBuffer, newVal);
    		logMetrics(workspaceOut(), metricsStringBuffer);
    		logMetrics(projectOut(), metricsStringBuffer);
    		metricsStringBuffer.setLength(0); // twice for safety
    		
    	}
    	
    }

}
