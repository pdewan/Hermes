package analyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import org.joda.time.DateTime;

import difficultyPrediction.Mediator;
import difficultyPrediction.PluginEventListener;
import difficultyPrediction.StatusInformation;
import difficultyPrediction.eventAggregation.AnEventAggregator;
import difficultyPrediction.eventAggregation.AnEventAggregatorDetails;
import difficultyPrediction.eventAggregation.EventAggregator;
import difficultyPrediction.featureExtraction.BarrierListener;
import difficultyPrediction.featureExtraction.ExtractRatiosBasedOnNumberOfEventsAndBasedOnTime;
import difficultyPrediction.featureExtraction.RatioBasedFeatureExtractor;
import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.featureExtraction.RatioFeaturesListener;
import difficultyPrediction.featureExtraction.WebLinkListener;
import difficultyPrediction.predictionManagement.APredictionManagerDetails;
import difficultyPrediction.predictionManagement.PredictionManager;
import difficultyPrediction.statusManager.StatusListener;
import difficultyPrediction.statusManager.StatusManager;
import difficultyPrediction.statusManager.StatusManagerDetails;
import fluorite.commands.EHICommand;

public class AnalyzerMediator implements Mediator {
	FeatureExtractorAnalyzer featureExtractorAnalyzer;
	File file;
	String participantId;
	String dataFolder;
	public AnalyzerMediator(String aSpreadsheetFolder, String aParticipantId) {
		participantId = aParticipantId;
		dataFolder = aSpreadsheetFolder;
		featureExtractorAnalyzer = new FeatureExtractorAnalyzer(this);
		featureExtractorAnalyzer.featureExtractionStrategy = new ExtractRatiosBasedOnNumberOfEventsAndBasedOnTime();
//		 file = new File("/users/jasoncarter/filename.txt");
//		 file = new File(MainConsoleUI.PARTICIPANT_INFORMATION_DIRECTORY + "filename.txt");
		 file = new File(dataFolder+ "filename.txt");

		 if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}

	@Override
	public void eventAggregator_HandOffEvents(AnEventAggregator aggregator,
			AnEventAggregatorDetails details) {
		System.out.println("events have been aggregated");

		this.featureExtractorAnalyzer.performFeatureExtraction(details.actions,
				featureExtractorAnalyzer, details.startTimeStamp);

	}

	@Override
	public void featureExtractor_HandOffFeatures(RatioBasedFeatureExtractor extractor,
			RatioFeatures details) {

		System.out.println("Insertion ratio:" + details.getInsertionRatio());
		System.out.println("Deletion ratio:" + details.getDeletionRatio());
		System.out.println("Debug ratio:" + details.getDebugRatio());
		System.out.println("Navigation ratio:" + details.getNavigationRatio());
		System.out.println("Focus ratio:" + details.getFocusRatio());
		System.out.println("Remove ratio:" + details.getRemoveRatio());
		System.out.println("features have been computed");
		
		java.util.Date time=new java.util.Date((long)details.getSavedTimeStamp());
		Calendar mydate = Calendar.getInstance();
		mydate.setTimeInMillis(details.getSavedTimeStamp());
		
		//mydate.get(Calendar.HOUR)
		//mydate.get(Calendar.MINUTE)
		//mydate.get(Calendar.SECOND)
		DateTime timestamp = new DateTime(details.getSavedTimeStamp());
		//timestamp.get(timestamp)
		
		System.out.println(timestamp.toString("MM-dd-yyyy H:mm:ss"));
		try
		{
//		    String filename= "/users/jasoncarter/filename.csv";
		    String filename = dataFolder + "ratios.csv";
		    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
		   
		    fw.write(""+ details.getInsertionRatio());
		    fw.write(",");
		    fw.write("" + details.getDeletionRatio());
		    fw.write(",");
		    fw.write("" + details.getDebugRatio());
		    fw.write(",");
		    fw.write("" + details.getNavigationRatio());
		    fw.write(",");
		    fw.write("" + details.getFocusRatio());
		    fw.write(",");
			fw.write("" + details.getRemoveRatio());
			fw.write(",");
			fw.write("" + timestamp.toString("MM-dd-yyyy H:mm:ss"));
			fw.write("\n");
		    fw.close();
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
		
		

	}
	
	

	@Override
	public void predictionManager_HandOffPrediction(PredictionManager manager,
			APredictionManagerDetails details) {
		// not used

	}

	@Override
	public void statusManager_HandOffStatus(StatusManager manager,
			StatusManagerDetails details) {
		// not used

	}

	@Override
	public void processEvent(EHICommand e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EventAggregator getEventAggregator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEventAggregator(EventAggregator eventAggregator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RatioBasedFeatureExtractor getFeatureExtractor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFeatureExtractor(RatioBasedFeatureExtractor featureExtractor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PredictionManager getPredictionManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPredictionManager(PredictionManager predictionManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StatusManager getStatusManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStatusManager(StatusManager statusManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StatusInformation getStatusInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStatusInformation(StatusInformation statusInformation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addRatioFeaturesListener(
			RatioFeaturesListener aRatioFeaturesListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeRatioFeaturesListener(
			RatioFeaturesListener aRatioFeaturesListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addStatusListener(StatusListener aListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeStatusListener(StatusListener aListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyNewRatios(RatioFeatures aRatios) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyNewStatus(String aStatus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPluginEventListener(PluginEventListener aListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePluginEventListener(PluginEventListener aListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyStartCommand() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyStopCommand() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyNewCommand(EHICommand aCommand) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addWebLinkListener(WebLinkListener aListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeWebLinkListener(WebLinkListener aListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addBarrierListener(BarrierListener aListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeBarrierListener(BarrierListener aListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyNewReplayedStatus(int aStatus) {
		// TODO Auto-generated method stub
		
	}

	

}
