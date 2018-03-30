package difficultyPrediction.eventAggregation;

import java.util.List;

import difficultyPrediction.Mediator;
import fluorite.commands.EHICommand;
import util.trace.difficultyPrediction.NewEventSegmentAggregation;


public class AnEventAggregator implements EventAggregator {
	
	private long startTimestamp = 0;
	public long getStartTimestamp()
	{
		return startTimestamp;
	}
	
	public void setStartTimeStamp(long startTimeStamp)
	{
		this.startTimestamp = startTimeStamp;
	}
	
	public AnEventAggregator(Mediator mediator) {
		this.mediator = mediator;
	}
	
	protected Mediator mediator;
	 EventAggregationStrategy eventAggregationStrategy;
	
	
	public void setEventAggregationStrategy(EventAggregationStrategy strat) {
		this.eventAggregationStrategy = strat;
	}
	
	public EventAggregationStrategy getEventAggregationStrategy() {
		return eventAggregationStrategy;
	}
	
	public void onEventsHandOff(List<EHICommand> genericActions) {
		if(mediator != null) {
			AnEventAggregatorDetails args = new AnEventAggregatorDetails(genericActions);
			args.startTimeStamp = this.startTimestamp;
			NewEventSegmentAggregation.newCase(args.toString(), this);
			mediator.eventAggregator_HandOffEvents(this, args);
		}
	}
}
