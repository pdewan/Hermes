package difficultyPrediction;


//import main.Server;
import java.util.ArrayList;
import java.util.List;

import analyzer.WebLink;
import difficultyPrediction.featureExtraction.BarrierListener;
import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.featureExtraction.RatioFeaturesListener;
import difficultyPrediction.featureExtraction.WebLinkListener;
import difficultyPrediction.statusManager.StatusAggregationDiscreteChunks;
import difficultyPrediction.statusManager.StatusListener;
import fluorite.commands.DifficultyCommand;
import fluorite.commands.EHICommand;
import util.GlobalRunnableExecutor;
import util.trace.difficultyPrediction.notification.NotifiedManualStatusToListener;
import util.trace.difficultyPrediction.notification.NotifiedRatiosToListener;
import util.trace.difficultyPrediction.notification.NotifiedReplayedStatusToListener;
import util.trace.difficultyPrediction.notification.NotifiedStatusToListener;
import util.trace.difficultyPrediction.notification.RegisteredCommandListener;
import util.trace.difficultyPrediction.notification.RegisteredRatioListener;
import util.trace.difficultyPrediction.notification.RegisteredStatusListener;

public class AMediatorRegistrar implements MediatorRegistrar {	
	//Server server;
//	static Mediator  instance;
	List<StatusListener> statusListeners = new ArrayList();
	List<WebLinkListener> webLinkListeners = new ArrayList();
	List<RatioFeaturesListener> ratioFeaturesListeners = new ArrayList<>();
	List<BarrierListener> barrierListeners = new ArrayList<>();
	String id = "";
////	EventAggregator eventAggregator;
//	RatioBasedFeatureExtractor featureExtractor;
//	PredictionManager predictionManager;
//	StatusManager statusManager;
//	private StatusInformation statusInformation;
	List<PluginEventListener> listeners = new ArrayList();
	

	
	public AMediatorRegistrar(String id) {
		this.id = id;
//		eventAggregator = new AnEventAggregator(this);
////		eventAggregator.eventAggregationStrategy = new ADisjointDiscreteChunks();
//		eventAggregator.setEventAggregationStrategy(new ADisjointDiscreteChunks());
//
//		
//		featureExtractor = new ARatioBasedFeatureExtractor(this);
//		featureExtractor.setFeatureExtractionStrategy(new ExtractRatiosBasedOnNumberOfEvents());
//		
//		predictionManager = new APredictionManager(this);
//		predictionManager.setPredictionStrategy(new DecisionTreeModel(predictionManager));
//		
//		statusManager = new StatusManager(this);
//		statusManager.strategy = new StatusAggregationDiscreteChunks(statusManager);
		
		//this.server=server;
	}
	
	
//	//Aggregate events using aggregator class
//	// this should fire plugin events
//	@Override
//	public void processEvent(ICommand e) {
//		NewCommand.newCase(this);
//		notifyNewCommand(e);
////		Tracer.info(this, "difficultyRobot.processEvent");
//
////			eventAggregator.eventAggregationStrategy.performAggregation(e, eventAggregator);
//			eventAggregator.getEventAggregationStrategy().performAggregation(e, eventAggregator);
//
//	}
//	
//	//Takes aggregated events and uses a separate thread to compute features
//	public void eventAggregator_HandOffEvents(AnEventAggregator aggregator,
//			AnEventAggregatorDetails details) {
//
//		Tracer.info(this,"difficultyRobot.handoffevents");
//		
//		this.featureExtractor.getFeatureExtractionStrategy().performFeatureExtraction(details.actions, featureExtractor);
//
//		
//	}
//
//
//	@Override
//	public void featureExtractor_HandOffFeatures(RatioBasedFeatureExtractor extractor,
//			RatioFeatures details) {
//		Tracer.info(this, "difficultyRobot.featureExtractor");
//		statusInformation = new AStatusInformation();
//		statusInformation.setEditRatio(details.getEditRatio());
//	
//		statusInformation.setDebugRatio(details.getDebugRatio());
//		statusInformation.setNavigationRatio(details.getNavigationRatio());
//		statusInformation.setRemoveRatio(details.getRemoveRatio());
//		statusInformation.setFocusRatio(details.getFocusRatio());
//		NewExtractedStatusInformation.newCase(statusInformation, this);
//		//notifyNewRatios(details);
////		ADifficultyPredictionPluginEventProcessor.getInstance().notifyNewRatios(details);
//		notifyNewRatios(details);
//		AnAnalyzer.maybeRecordFeatures(details);
//
//		this.predictionManager.getPredictionStrategy().predictSituation(details.getEditRatio(), details.getDebugRatio(), details.getNavigationRatio(), details.getFocusRatio(), details.getRemoveRatio());
////		NewPrediction.newCase(this);
//
//	}
//
//
//	@Override
//	public void predictionManager_HandOffPrediction(PredictionManager manager,
//			APredictionManagerDetails details) {
//		StatusAggregationStarted.newCase(this);
//		Tracer.info(this, "difficultyRobot.handOffPrediction");
//		statusInformation.setPredictedClass("Prediction");
//		statusInformation.setPrediction(details.predictionValue);
//		statusInformation.setTimeStamp(new Date(Calendar.getInstance().getTimeInMillis()));
//		statusInformation.setStatusKind(StatusKind.PREDICTION_MADE);
//		statusInformation.setUserId(this.id);
//		statusInformation.setUserName(this.id);
//		notifyNewStatus(details.predictionValue);
//		this.statusManager.strategy.aggregateStatuses(details.predictionValue);
//	}
//
//
//	@Override
//	public void statusManager_HandOffStatus(StatusManager manager,
//			StatusManagerDetails details) {
//		Tracer.info(this, "difficultyRobot.handOffStatus");
//		 StatusPrediction statusPrediction = new StatusPrediction();
//         statusPrediction.timeStamp = new Date(Calendar.getInstance().getTimeInMillis());
//         statusPrediction.prediction = details.predictionValue;
//         statusPrediction.userId = this.id;
//         statusPrediction.userName = this.id;
//         statusPrediction.statusKind = StatusKind.PREDICTION_MADE;
//         notifyNewAggregateStatus(details.predictionValue);
//       
//       
//         saveToLog(details);
//         
//         
//         //I'm not sure I need this
////         DifficultyPredictionEventArgs eventArgs = new DifficultyPredictionEventArgs();
////         eventArgs.predictionValue = details.predictionValue;
////         eventArgs.id = this.id;
////         server.robot_handOffResultsFromDifficultyRobot(this, eventArgs);
//	}
//	
//	
//	public void saveToLog(StatusManagerDetails prediction)
//    {
//		Tracer.info(this, "Saving to log:" + prediction);
//		PredictionValueToStatus.newCase(this);
//		 PredictionType predictionType = PredictionType.MakingProgress;
//		 if(prediction.predictionValue.equals("NO"))
//         {
//        	 predictionType = PredictionType.MakingProgress;
//         }
//         if(prediction.predictionValue.equals("TIE"))
//         {
//        	 predictionType = PredictionType.Indeterminate;
//         }
//         if(prediction.predictionValue.equals("YES"))
//         {
//        	 predictionType = PredictionType.HavingDifficulty;
//         }
//    
//         PredictionCommand predictionCommand = new PredictionCommand(predictionType);
//         EventRecorder.getInstance().recordCommand(predictionCommand);
//    }


	public synchronized String getId() {
		return id;
	}


	public synchronized void setId(String id) {
		this.id = id;
	}


//	public EventAggregator getEventAggregator() {
//		return eventAggregator;
//	}
//
//
//	public void setEventAggregator(EventAggregator eventAggregator) {
//		this.eventAggregator = eventAggregator;
//	}
//
//
//	public RatioBasedFeatureExtractor getFeatureExtractor() {
//		return featureExtractor;
//	}
//
//
//	public void setFeatureExtractor(RatioBasedFeatureExtractor featureExtractor) {
//		this.featureExtractor = featureExtractor;
//	}
//
//
//	public PredictionManager getPredictionManager() {
//		return predictionManager;
//	}
//
//
//	public void setPredictionManager(PredictionManager predictionManager) {
//		this.predictionManager = predictionManager;
//	}
//
//
//	public StatusManager getStatusManager() {
//		return statusManager;
//	}
//
//
//	public void setStatusManager(StatusManager statusManager) {
//		this.statusManager = statusManager;
//	}
//
//
//	public StatusInformation getStatusInformation() {
//		return statusInformation;
//	}
//
//
//	public void setStatusInformation(StatusInformation statusInformation) {
//		this.statusInformation = statusInformation;
//	}
	
	public synchronized void addRatioFeaturesListener(RatioFeaturesListener aRatioFeaturesListener) {
		ratioFeaturesListeners.add(aRatioFeaturesListener);
		RegisteredRatioListener.newCase(aRatioFeaturesListener, this);
	}
	
	public synchronized void removeRatioFeaturesListener(RatioFeaturesListener aRatioFeaturesListener) {
		ratioFeaturesListeners.remove(aRatioFeaturesListener);
	}
	
	public synchronized void addStatusListener(StatusListener aListener) {
		statusListeners.add(aListener);
		RegisteredStatusListener.newCase(aListener, this);
	}	
	public void removeStatusListener(StatusListener aListener) {
		statusListeners.remove(aListener);
	}
	@Override
	public void addWebLinkListener(WebLinkListener aListener) {
		webLinkListeners.add(aListener);
		
	}
	@Override
	public void removeWebLinkListener(WebLinkListener aListener) {
		webLinkListeners.remove(aListener);
		
	}
	@Override
	public void addBarrierListener(BarrierListener aListener) {
		barrierListeners.add(aListener);
		
	}
	@Override
	public synchronized void removeBarrierListener(BarrierListener aListener) {
		barrierListeners.remove(aListener);
		
	}
		
	public synchronized void  notifyNewRatios(RatioFeatures aRatios) {
		for (RatioFeaturesListener aListener:ratioFeaturesListeners) {
			NotifiedRatiosToListener.newCase(aRatios, aListener, this);
			aListener.newRatios(aRatios);
		}
	}
	
	public synchronized void  notifyNewBarrier(String newVal) {
		for (BarrierListener aListener:barrierListeners) {
			aListener.newBarrier(newVal);
		}
	}
	
	@Override
	public synchronized void  notifyNewStatus(String aStatus) {
		int anIntStatus = StatusAggregationDiscreteChunks.statusStringToInt(aStatus);
		for (StatusListener aListener:statusListeners) {
			NotifiedStatusToListener.newCase(aStatus, anIntStatus, aListener, this);
			aListener.newStatus(aStatus);
			aListener.newStatus(anIntStatus);
//			aListener.newStatus(StatusAggregationDiscreteChunks.statusStringToInt(aStatus));
		}
	}
	@Override
	public synchronized void  notifyNewReplayedStatus(int aStatus) {
		

		for (StatusListener aListener:statusListeners) {
			NotifiedReplayedStatusToListener.newCase(aStatus, aListener, this);
			aListener.newReplayedStatus(aStatus);
//			aListener.newReplayedStatus(StatusAggregationDiscreteChunks.statusStringToInt(aStatus));
		}
	}
	public synchronized void  notifyNewManualStatus(String aStatus) {
		int anIntStatus = StatusAggregationDiscreteChunks.statusStringToInt(aStatus);

		for (StatusListener aListener:statusListeners) {
			NotifiedManualStatusToListener.newCase(aStatus, anIntStatus, aListener, this);
			aListener.newManualStatus(aStatus);
		}
	}
	@Override
	public void notifyNewManualStatus(DifficultyCommand aCommand) {

		for (StatusListener aListener:statusListeners) {
			aListener.newManualStatus(aCommand);
		}
	}
	public synchronized void  notifyNewWebLinks(List<WebLink> aWebLinks) {
		for (WebLinkListener aListener:webLinkListeners) {
//			for (WebLink aWebLink:aWebLinks) {
//				aListener.newWebLink(aWebLink);
//			}
			aListener.newWebLinks(aWebLinks);
		}
	}
	
//	public void  notifyNewIntStatus(int aStatus) {
//		for (StatusListener aListener:statusListeners) {
//			aListener.newStatus(aStatus);
//		}
//	}
	
	public synchronized void  notifyNewAggregateStatus(int aStatus) {
		for (StatusListener aListener:statusListeners) {
			aListener.newAggregatedStatus(aStatus);
		}
	}
	
	public synchronized void  notifyNewAggregateStatus(String aStatus) {
		for (StatusListener aListener:statusListeners) {
			aListener.newAggregatedStatus(aStatus);
			aListener.newAggregatedStatus(StatusAggregationDiscreteChunks.statusStringToInt(aStatus));
		}
	}
	
	@Override
	public synchronized void addPluginEventListener(PluginEventListener aListener){
		listeners.add(aListener);
		RegisteredCommandListener.newCase(aListener, this);
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.DifficultyPredictionPluginEventProcessor#addRemovePredictionEventListener(difficultyPrediction.DifficultyPredictionEventListener)
	 */
	@Override
	public synchronized void removePluginEventListener(PluginEventListener aListener){
		listeners.remove(aListener);
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.DifficultyPredictionPluginEventProcessor#notifyStart()
	 */
	@Override
	public synchronized void notifyStartCommand() {
		for (PluginEventListener aListener:listeners) {
			aListener.commandProcessingStarted();
		}
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.DifficultyPredictionPluginEventProcessor#notifyStop()
	 */
	@Override
	public synchronized void notifyStopCommand() {
		for (PluginEventListener aListener:listeners) {
			aListener.commandProcessingStopped();
		}
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.DifficultyPredictionPluginEventProcessor#notifyRecordCommand(edu.cmu.scs.fluorite.commands.ICommand)
	 */
	@Override
	public synchronized void notifyNewCommand(EHICommand aCommand) {
		for (PluginEventListener aListener:listeners) {
			// let us not block the prediction runnable on vagaries of the listeners
//			GlobalRunnableExecutor.getSingleton().asyncExecute(
//					new ANewCommandNotifier(aListener, aCommand));
			// for some  reason OE cannot keep up with async execution
			GlobalRunnableExecutor.getSingleton().syncExecute(
					new ANewCommandNotifier(aListener, aCommand));
		
			
		}
	}




	
	
	
//	public static Mediator getInstance() {
//		if (instance == null)
//			instance = new AMediatorRegistrar(""); // not sure what the id is actually used for
//		return instance;
//	}
	
}
