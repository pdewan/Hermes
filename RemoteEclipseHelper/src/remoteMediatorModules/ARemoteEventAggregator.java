/**
 * @author Nils Persson
 * @date 2018-Mar-29 1:31:45 PM 
 */
package remoteMediatorModules;

import java.util.List;

import difficultyPrediction.Mediator;
import difficultyPrediction.eventAggregation.AnEventAggregator;
import difficultyPrediction.eventAggregation.AnEventAggregatorDetails;
import fluorite.commands.EHICommand;
import remoteMessaging.RemoteMessageSender;
import remoteMessaging.RemoteMessageSenderFactory;
import remoteModuleSelection.MediatorModule;
import util.trace.Tracer;
import util.trace.difficultyPrediction.NewEventSegmentAggregation;

public class ARemoteEventAggregator extends AnEventAggregator implements RemoteEventAggregator{

	/**
	 * @param mediator
	 */
	public ARemoteEventAggregator(Mediator mediator) {
		super(mediator);
	}
	
	@Override 
	public void onEventsHandOff(List<EHICommand> genericActions) {
		if(super.mediator != null) {
			AnEventAggregatorDetails args = new AnEventAggregatorDetails(genericActions);
			args.startTimeStamp = super.getStartTimestamp();
			NewEventSegmentAggregation.newCase(args.toString(), this);
			
			RemoteMessageSenderFactory.getSingleton().sendMessage(args);
		}
	}
	
//	public void handOff(AnEventAggregatorDetails details){
//		// this used to be called in the handoff event call in the mediator
//		Tracer.info(this,"difficultyRobot.handoffevents");
//		
//		// locally would call performFeatureExtraction, remote must send details over XMPP
//		RemoteMessageSenderFactory.getSingleton().sendMessage(MediatorModule.EVENT_AGGREGATOR, details);
//	}

}
