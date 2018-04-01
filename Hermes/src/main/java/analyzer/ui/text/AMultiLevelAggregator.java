package analyzer.ui.text;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import analyzer.AParticipantTimeLine;
import analyzer.ARatioFileReplayer;
import analyzer.ATimeStampComputer;
import analyzer.AnalyzerFactory;
import analyzer.RatioFilePlayerFactory;
import analyzer.TimeStampComputerFactory;
import analyzer.WebLink;
import analyzer.extension.ARatioFileGenerator;
import bus.uigen.OEFrame;
//import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
//import bus.uigen.hermes.HermesALabelBeanModelProxy;
//import bus.uigen.hermes.HermesAListenableVectorProxy;
//import bus.uigen.hermes.LabelBeanModel;
//import bus.uigen.hermes.HermesObjectEditorProxy;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.DifficultyRobot;
import difficultyPrediction.MultiLevelAggregator;
import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.metrics.RatioCalculator;
import difficultyPrediction.metrics.RatioCalculatorSelector;
import difficultyPrediction.statusManager.StatusAggregationDiscreteChunks;
import fluorite.commands.DifficultyCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.PredictionCommand;
import fluorite.commands.PredictionType;
import fluorite.commands.Status;
import fluorite.commands.WebVisitCommand;
import fluorite.model.StatusConsts;
import util.annotations.Column;
import util.annotations.ComponentHeight;
import util.annotations.PreferredWidgetClass;
import util.annotations.Row;
import util.annotations.Visible;
import util.models.ALabelBeanModel;
import util.models.AListenableVector;
import util.models.LabelBeanModel;
import util.trace.difficultyPrediction.AggregatePredictionChanged;
import util.trace.difficultyPrediction.PredictionChanged;

public class AMultiLevelAggregator implements MultiLevelAggregator{
	static OEFrame oeFrame;
//	static Object oeFrame;

//	protected List<ICommand> commands = new ArrayList();
	protected List<RatioFeatures> features = new ArrayList();
	protected List<String> predictions = new ArrayList();
	protected String aggregatedStatus = "";
	protected String manualStatus = "";
	protected String manualBarrier = "";
	protected int correctStatusInt = ARatioFileGenerator.toInt(PredictionType.Indeterminate);
	protected String correctStatus = 	AParticipantTimeLine.statusIntToString(correctStatusInt);


	static RatioCalculator ratioCalculator;
	static MultiLevelAggregator instance;
	protected StringBuffer commandsBuffer = new StringBuffer();
	protected StringBuffer ratiosBuffer = new StringBuffer();
	protected StringBuffer predictionsBuffer = new StringBuffer();
	protected PropertyChangeSupport propertyChangeSupport;
	protected List<LabelBeanModel> webLinks = new AListenableVector<>();
//	protected List<HermesLabelBeanModelProxy> webLinks = new HermesAListenableVectorProxy<>();

	protected int numWebLinks;
	
	
	


	public AMultiLevelAggregator() {
//		DifficultyRobot.getInstance().addRatioFeaturesListener(this);
		
		// need to remove the code duplication below by having a single object that gets bound to
		// either the robot or RatioFilePlayer
		
		// for live or replayed prediction events
		DifficultyRobot.getInstance().addStatusListener(this);
		DifficultyRobot.getInstance().addPluginEventListener(this);
		DifficultyRobot.getInstance().addRatioFeaturesListener(this);
		DifficultyRobot.getInstance().addWebLinkListener(this);
		DifficultyRobot.getInstance().addBarrierListener(this);
		
		// for rati file relay
		RatioFilePlayerFactory.getSingleton().addStatusListener(this);
		RatioFilePlayerFactory.getSingleton().addPluginEventListener(this);
		RatioFilePlayerFactory.getSingleton().addRatioFeaturesListener(this);
		RatioFilePlayerFactory.getSingleton().addWebLinkListener(this);
		RatioFilePlayerFactory.getSingleton().addBarrierListener(this);
		
		// when ana analyzer is repredicting
		AnalyzerFactory.getSingleton().addAnalyzerListener(this);

		
//		ratioCalculator = APercentageCalculator.getInstance();
		ratioCalculator = RatioCalculatorSelector.getRatioCalculator();
//		webLinks.add(new ALabelBeanModel(Common.toBlueColoredUnderlinedHrefHTML("https://www.google.com", "google")));
		webLinks.add(new ALabelBeanModel(""));

		numWebLinks = 0;
		propertyChangeSupport = new PropertyChangeSupport(this);

	}
	
	
	@Override
    @Visible(false)
	public void reset() {
		aggregatedStatus = StatusConsts.INDETERMINATE;
		oldAggregateStatus = StatusConsts.INDETERMINATE;
		setBarrier("");
		features.clear();
		predictions.clear();
		commandsBuffer.setLength(0);
		ratiosBuffer.setLength(0);
		predictionsBuffer.setLength(0);	
		clearWebLinks();
	}
	public static String toString(DifficultyCommand aCommand) {
		if (aCommand.getStatus() == null)
			return "";
		return aCommand.getStatus().toString();		
	}
	// is called after each segment to reset previous value
	protected void setManualStatus(String newValue) {
		String oldStatus = manualStatus;
		manualStatus = newValue;		
		propertyChangeSupport.firePropertyChange("ManualStatus", oldStatus, manualStatus);
//		setCorrectStatus(newValue);
	}
	protected void setCorrectStatus(String newValue) {
		int i = 0;
		setCorrectStatus(StatusAggregationDiscreteChunks.statusStringToInt(newValue));
	}

	
	protected void setCorrectStatus(int newValue) {
		String newStringValue = AParticipantTimeLine.statusIntToString(newValue);
		String oldCorrectStatus = correctStatus;
		correctStatus = newStringValue;
		correctStatusInt = newValue;
		propertyChangeSupport.firePropertyChange("CorrectStatus", oldCorrectStatus, correctStatus);
		
	}

	@Override
    @Visible(false)
	public void newCommand(EHICommand newCommand) {
		// we wil get this as aggregated status as
		// prediction command does not seem to be in sync wuth aggregated status
		if (newCommand instanceof PredictionCommand && 
				DifficultyPredictionSettings.isReplayMode() &&  // implied by the latter
				DifficultyPredictionSettings.isReplayRatioFiles() ) { // this implies the former
			// this should happen in live mode
			PredictionCommand aPredictionCommand = (PredictionCommand) newCommand;
			System.out.println("received prediction command:" + newCommand);
			if (aPredictionCommand.getPredictionType() != PredictionType.MakingProgress) {
				System.out.println("Not making progress");
			}
			// new aggregate status should be enough, for some reason this is not in sync with ratio file generated
//			newReplayedStatus(AnAnalyzerProcessor.toInt(((PredictionCommand) newCommand).getPredictionType()));
			
		}
		if (newCommand instanceof DifficultyCommand) {
//			if (!AnalyzerFactory.getSingleton().getAnalyzerParameters().isReplayOutputFiles()) {
				DifficultyCommand aDifficultyStatusCommand = (DifficultyCommand) newCommand;
				TimeStampComputerFactory.getSingleton().computeTimestamp(newCommand); // so that start time can be reset
			setManualStatus(toString((DifficultyCommand) newCommand)); // should this not be done always regardless of replay output files
			setCorrectStatus(ARatioFileGenerator.toInt(aDifficultyStatusCommand.getStatus()));

//			}

		} else {
		maybeClearNonAggregatedStatus();
//		commands.add(newCommand);
		commandsBuffer.append(toClassifiedString(newCommand) + "\n");
		propertyChangeSupport.firePropertyChange("Segment", "", commandsBuffer.toString());
		}	
	}

	@Override
	public void newManualStatus(String newValue) {
		setManualStatus(newValue);
	}

	@Override
    @Visible(false)
	public void commandProcessingStarted() {
		reset();
		propertyChangeSupport.firePropertyChange("this", "", this);

		
	}

	@Override
    @Visible(false)
	public void commandProcessingStopped() {
//		reset();
//		propertyChangeSupport.firePropertyChange("this", "", this);

		
	}
	String oldStatus = "";
	@Override
    @Visible(false)
	public void newStatus(String aStatus) {
		if (aStatus == null) {
			System.out.println ("null status!");
			return;
		}
		if (!aStatus.equals(oldStatus)) {
			PredictionChanged.newCase(aStatus, getRatios(), this);
			oldStatus = aStatus;
		}
		predictions.add(aStatus);
		predictionsBuffer.append(aStatus + "\n");
		propertyChangeSupport.firePropertyChange("Predictions", "", predictionsBuffer.toString());
		if (!DifficultyPredictionSettings.isReplayMode()) {
			setCorrectStatus(aStatus);
		}
		
	}
	String oldAggregateStatus = "";
	boolean lastEventWasAggregatedStatus;
	@Override
    @Visible(false)
	public void newAggregatedStatus(String aStatus) {
		lastEventWasAggregatedStatus = true; // at next command clear data other than aggregated status
		if (!aStatus.equals(oldAggregateStatus)) {
			AggregatePredictionChanged.newCase(aStatus, getRatios(), this);
			aggregatedStatus = aStatus;
			propertyChangeSupport.firePropertyChange("AggregatedStatus", oldAggregateStatus, aStatus);
			oldAggregateStatus = aStatus;		

		}
		setCorrectStatus(aStatus);
//		predictions.clear();
//		predictionsBuffer.setLength(0);
//		ratiosBuffer.setLength(0);
//		commandsBuffer.setLength(0);
		
	}
    @Visible(false)
	public void maybeClearNonAggregatedStatus() {
		if (!lastEventWasAggregatedStatus) 
			return;
		lastEventWasAggregatedStatus = false;
		predictions.clear();
		predictionsBuffer.setLength(0);
		ratiosBuffer.setLength(0);
		commandsBuffer.setLength(0);
		if (!AnalyzerFactory.getSingleton().getAnalyzerParameters().isReplayOutputFiles()) {
		setManualStatus("");
		setBarrier("");
		}
		clearWebLinks();
		
	}
    
    protected void clearWebLinks() {
    	for (LabelBeanModel aWebLink: webLinks) {
    		aWebLink.setText("");
//    		aWebLink.setSearchString("");
//    		aWebLink.setUrlString("");
    	}
    	numWebLinks=0;
    }

	@Override
    @Visible(false)
	public void newStatus(int aStatus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Visible(false)
	public void newAggregatedStatus(int newStatus) {
		 newAggregatedStatus(ARatioFileReplayer.toStringStatus(newStatus));
		
	}
	@Override
//	@Visible(false)
	public void newRatios(RatioFeatures newVal) {
		features.add(newVal);
//		commands.clear();	
//		commandsBuffer.setLength(0);
		commandsBuffer.append("\n");
		ratiosBuffer.append(newVal + "\n");
		propertyChangeSupport.firePropertyChange("Ratios", "", ratiosBuffer.toString());
//		setManualStatus("");
//		setBarrier("");

	}
	@Row(0)
	@Override
	public String getManualBarrier() {
		return manualBarrier;
	}
	@Row(1)
	@Column(0)
	@Override
	public String getManualStatus() {
		return manualStatus;
	}
	@Row(2)
//	@Row(1)
//	@Column(1)
	@Override
	public String getCorrectStatus() {
		return correctStatus;
	}
//	@Override
//	public void setManualStatus(String newVal) {
//		this.manualStatus = newVal;
//	}
	
//	@Override
//	public void setBarrier(String newVal) {
//		this.barrier = newVal;
//	}
	@Row(3)
//	@Row(1)
//	@Column(2)
	@Override
	public String getAggregatedStatus() {
		return aggregatedStatus;
	}
    @Visible(false)
	protected  String toClassifiedString (EHICommand aCommand) {
		String featureName = ratioCalculator.getFeatureName(aCommand);
		return 
				featureName + " " + ATimeStampComputer.toDateString(TimeStampComputerFactory.getSingleton().computeTimestamp(aCommand)) + 
				" (" + aCommand + " )";
	}
	@Row(4)
	@PreferredWidgetClass(JTextArea.class)
	@ComponentHeight(80)
	@Override
	public String getPredictions() {
		return predictionsBuffer.toString();
	}
	@Row(5)
	@PreferredWidgetClass(JTextArea.class)
	@ComponentHeight(100)
	@Override
	public String getRatios() {
		return ratiosBuffer.toString();
	}
	@Row(6)	
	@Override
	public List<LabelBeanModel> getWebLinks() {
		return webLinks;
	}
//	
//	public void setWebLinks(List<LabelBeanModel> newVal) {
//		webLinks = newVal;
//	}


//	public void setWebLinks(List<WebLink> webLinks) {
//		this.webLinks = webLinks;
//	}
//	@Row(4)
//	@ComponentHeight(100)
//	public String getWebSearch() {
//		return webSearch;
//	}
//
//
//	public void setWebSearch(String webSearch) {
//		this.webSearch = webSearch;
//	}
//
//	@Row(5)
//	@ComponentHeight(100)
//	public String getWebURL() {
//		return webURL;
//	}
//
//
//	public void setWebURL(String webURL) {
//		this.webURL = webURL;
//	}
	@Row(7)
	@PreferredWidgetClass(JTextArea.class)
	@ComponentHeight(200)
	public String getSegment() {
		return commandsBuffer.toString();
	}
	
	
	
	
	@Visible(false)
	public static MultiLevelAggregator getInstance() {
		return AggregatorFactory.getSingleton();
		
//		if (instance == null) {
//			instance = new AMultiLevelAggregator();
//			
//		}
//		return instance;
	}
	
	@Override
	public void newWebLink(WebLink aWebLink) {		
		if (numWebLinks < webLinks.size()) {
			try {
//			webLinks.get(numWebLinks).setSearchString(aWebLink.getSearchString());
//			webLinks.get(numWebLinks).setUrlString(aWebLink.getUrlString());
			webLinks.get(numWebLinks).setText(aWebLink.getClickableLink().getText());
			} catch (Exception e) {
				webLinks.get(numWebLinks).setText(e.getMessage());
			}
		} else {
			webLinks.add(new ALabelBeanModel(aWebLink.getClickableLink().getText()));
		}		
	}
	
	
	@Override
	public void newWebLinks(List<WebLink> aWebLinks) {	
		if (aWebLinks == null || aWebLinks.isEmpty())
			return;
		for (WebLink aWebLink:aWebLinks) {
		   newWebLink(aWebLink);
	    }	
	}
	
	public static void createUI() {
		if (oeFrame != null) {
			getInstance().reset();
			return;
		}
		 oeFrame = ObjectEditor.edit(getInstance());
//		 oeFrame = HermesObjectEditorProxy.edit(getInstance(), 700, 700, WindowConstants.DISPOSE_ON_CLOSE,
//				 false);
		oeFrame.setSize(700, 700);
		oeFrame.getFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		oeFrame.setAutoExitEnabled(false);
//		HermesObjectEditorProxy.edit(getInstance(), 700, 700, )
	}

	

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
		
	}
	@Override
	public void newBarrier(String newValue) {
		setBarrier(newValue);
//		String oldValue = manualBarrier;
//		manualBarrier = newValue;
//		propertyChangeSupport.firePropertyChange("barrier", oldValue, newValue);		
	}
	
	 void setBarrier(String newValue) {
		String oldValue = manualBarrier;
		manualBarrier = newValue;
		propertyChangeSupport.firePropertyChange("barrier", oldValue, newValue);		
	}
	
	
	
//	@Override
//	public void reset() {
//		// TODO Auto-generated method stub
//		System.err.println("Reset not implemented");
//		
//	}
	
	


	@Override
	public void newParticipant(String anId, String aFolder) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void startTimestamp(long aStartTimeStamp) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void finishParticipant(String anId, String aFolder) {
		// TODO Auto-generated method stub
		
	}


	


	@Override
	public void newBrowseLine(String aLine) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void newBrowseEntries(Date aDate, String aSearchString, String aURL) {
		// TODO Auto-generated method stub
		
	}
	


	@Override
	public void newReplayedStatus(int aStatus) {
//		setCorrectStatus(aStatus);
		newAggregatedStatus(aStatus);
	}
//	@Override
//	// from analyzer thread,not very nteresting, need it from runnable thread
//	public void newCorrectStatus(int aStatus) {
////		System.out.println ("New Status:" + aStatus);
////		correctStatusInt = aStatus;
////		String oldStatusString = correctStatus;
////		correctStatus = AParticipantTimeLine.statusIntToString(aStatus);
////		propertyChangeSupport.firePropertyChange("CorrectStatus", oldStatusString, correctStatus);
//
//	}

	@Override
	public void finishedBrowserLines() {
		// TODO Auto-generated method stub
		
	}

	public static void main (String[] args) {
//		ObjectEditor.edit(AMultiLevelAggregator.getInstance());
		createUI();
	}



	

	@Override
	public void newCorrectStatus(DifficultyCommand newCommand, Status aStatus, long aStartRelativeTime, long aDuration) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void newPrediction(PredictionCommand newParam, PredictionType aPredictionType, long aStartRelativeTime, long aDuration) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void newStoredCommand(EHICommand aNewCommand, long aStartRelativeTime, long aDuration) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void newStoredInputCommand(EHICommand aNewCommand, long aStartRelativeTime, long aDuration) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void newWebVisit(WebVisitCommand aWebVisitCommand, long aStartAbsoluteTime, long aDuration) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void newBrowserCommands(List<WebVisitCommand> aCommands) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void experimentStartTimestamp(long aStartTimeStamp) {
		// TODO Auto-generated method stub
		
	}


	

	


	
	

}
