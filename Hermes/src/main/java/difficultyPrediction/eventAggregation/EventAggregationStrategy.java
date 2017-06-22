package difficultyPrediction.eventAggregation;

import fluorite.commands.EHICommand;


public interface EventAggregationStrategy {
	public void performAggregation(EHICommand event, EventAggregator eventAggregator);
}
