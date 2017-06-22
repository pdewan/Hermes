package analyzer;

import difficultyPrediction.PredictionParametersSetterSelector;
import difficultyPrediction.eventAggregation.EventAggregationStrategy;
import difficultyPrediction.eventAggregation.EventAggregator;
import difficultyPrediction.eventAggregation.EventAggregatorArray;
import fluorite.commands.EHICommand;

public class DiscreteChunksAnalyzer implements EventAggregationStrategy {

	public DiscreteChunksAnalyzer() {

	}

	int m_numberOfEvents;
	private boolean ignoreEvents;
	private int numberOfEventsToIgnore = PredictionParametersSetterSelector.getSingleton().getStartupLag();
	private EventAggregatorArray actions = new EventAggregatorArray();
	
	public DiscreteChunksAnalyzer(String numberOfEvents) {
		m_numberOfEvents = Integer.parseInt(numberOfEvents);
		ignoreEvents = false;
	}
	
//	int numberOfEvents() {
//		return APredictionParameters.getInstance().getSegmentLength();
////		return m_numberOfEvents;
//	}
//	int numberOfEventsToIgnore() {
//		return APredictionParameters.getInstance().getSegmentLength();
//
//	}

	@Override
	public void performAggregation(EHICommand event,
			EventAggregator eventAggregator) {
		actions.addEvents(event);
		if (ignoreEvents) {
			System.out
					.println("(discrete chunks.performAggregation) Ignored events left = "
							+ (numberOfEventsToIgnore - actions.size()));
			if (actions.size() >= numberOfEventsToIgnore) {
				actions.clear();
				ignoreEvents = false;
			}
		} else {
			if (actions.size() % m_numberOfEvents == 0) {
//			if (actions.size() % numberOfEvents() == 0) {

//				NewEventSegment.newCase(this);
				
				eventAggregator.onEventsHandOff(actions);
				//do something with events here
				actions.clear();
			}
		}

	}

}
