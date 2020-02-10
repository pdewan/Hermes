package analyzer.nils;

import difficultyPrediction.APredictionParameters;
import difficultyPrediction.eventAggregation.EventAggregationStrategy;
import difficultyPrediction.eventAggregation.EventAggregator;
import difficultyPrediction.eventAggregation.EventAggregatorArray;
import fluorite.commands.EHICommand;

public class AWindowAggregator implements EventAggregationStrategy {

	public static final int DEFAULT_MAX = 25; // why is this not 50
	// dfaiult ignore number is 50
	public static final int DEFAULT_IGNORE_NUM = 50;
	public static final boolean DEFAULT_IGNORE = true;
	int count = 0;

	public int m_numberOfEvents;
	public int maxNumberOfEvents;
	public int numberOfEventsToIgnore;
	public boolean ignoreEvents;
	
	public AWindowAggregator(String numberOfEvents) {
		m_numberOfEvents = Integer.parseInt(numberOfEvents);
		ignoreEvents = false;
	}

	public AWindowAggregator() {
		maxNumberOfEvents = DEFAULT_MAX;
		numberOfEventsToIgnore = DEFAULT_IGNORE_NUM;
		ignoreEvents = DEFAULT_IGNORE;
	}
	int numberOfEventsToIgnore() {
		
		return APredictionParameters.getInstance().getStartupLag();

}
	int maxNumberOfEvents() {
	
	return APredictionParameters.getInstance().getSegmentLength();

}
	public void performAggregation(EHICommand event,
			EventAggregator eventAggregator) {
		System.out.println("&&&&&&&&&&&&& " + count++);

		actions.addEvents(event);
//		System.out.println("Actions:" + actions);

		// Ignore the first few events a user sends then begin tracking, to
		// avoid
		// dealing with startup lag
		if (ignoreEvents) {
//			System.out
//					.println("(discrete chunks.performAggregation) Ignored events left = "
//							+ (numberOfEventsToIgnore() - actions.size()));
			if (actions.size() >= numberOfEventsToIgnore()) {
				actions.clear();
				ignoreEvents = false;
			}
		} else {
			if (actions.size() % maxNumberOfEvents() == 0) {
				eventAggregator.onEventsHandOff(actions);
//				actions.clear();
				actions.remove(0); // keep the window sliding
			}
		}

	}

	private EventAggregatorArray actions = new EventAggregatorArray();

	

}
