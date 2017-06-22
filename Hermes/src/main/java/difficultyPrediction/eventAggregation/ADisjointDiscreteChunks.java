package difficultyPrediction.eventAggregation;

import difficultyPrediction.APredictionParameters;
import fluorite.commands.EHICommand;

public class ADisjointDiscreteChunks implements EventAggregationStrategy {

	public static final int DEFAULT_MAX = 25; // why is this not 50
	// dfaiult ignore number is 50
	public static final int DEFAULT_IGNORE_NUM = 50;
	public static final boolean DEFAULT_IGNORE = true;

	public int maxNumberOfEvents;
	public int numberOfEventsToIgnore;
	public boolean ignoreEvents;

	public ADisjointDiscreteChunks() {
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
				actions.clear();
			}
		}

	}

	private EventAggregatorArray actions = new EventAggregatorArray();

	

}
