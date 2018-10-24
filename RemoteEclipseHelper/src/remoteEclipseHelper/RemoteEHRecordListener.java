/**
 * @author Nils Persson
 *
 */
package remoteEclipseHelper;

import difficultyPrediction.DifficultyRobot;
import difficultyPrediction.Mediator;
import difficultyPrediction.eventAggregation.AnEventAggregatorDetails;
import difficultyPrediction.eventAggregation.EventAggregationStrategy;
import fluorite.model.EHEventRecorder;
import fluorite.model.RecorderListener;
import remoteMediatorModules.ARemoteEventAggregator;
import remoteMediatorModules.RemoteEventAggregatorFactory;
import remoteModuleSelection.MediatorModule;
import remoteModuleSelection.RemoteModuleSelector;
import remoteModuleSelection.SendingModuleFactory;


public class RemoteEHRecordListener implements RecorderListener{
	protected Mediator mediator;
	protected boolean connectedRemote; // true if EA local and rest of pipeline remote

	public RemoteEHRecordListener() {
		// remove after hermes is setup to handle this
		RemoteModuleSelector.setRemoteModule(MediatorModule.EVENT_AGGREGATOR);
				
		EHEventRecorder.getInstance().addRecorderListener(this);
	}

	/* (non-Javadoc)
	 * @see fluorite.model.RecorderListener#eventRecordingStarted(long)
	 */
	@Override
	public void eventRecordingStarted(long aStartTimestamp) {
		mediator = DifficultyRobot.getInstance();
		switch(SendingModuleFactory.getSingleton().getModule()){
			case EVENT_AGGREGATOR:
				EventAggregationStrategy strat = mediator.getEventAggregator().getEventAggregationStrategy();
				mediator.setEventAggregator(RemoteEventAggregatorFactory.getSingleton());
				mediator.getEventAggregator().setEventAggregationStrategy(strat);
				break;
			case FEATURE_EXTRACTOR:
				break;
			case PREDICTION_MANAGER:
				break;
			case STATUS_AGGREGATOR:
				break;
		}
	}

	/* (non-Javadoc)
	 * @see fluorite.model.RecorderListener#eventRecordingEnded()
	 */
	@Override
	public void eventRecordingEnded() {
		
	}

}
