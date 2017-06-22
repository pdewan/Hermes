package util.trace.difficultyPrediction;

import util.trace.TraceableInfo;
import util.trace.Tracer;


public class NewEventSegmentAggregation extends TraceableInfo {
	String eventAggregatorDetails;
	public NewEventSegmentAggregation(String aMessage, String aEventAggregatorDetails, Object aFinder) {
		super(aMessage, aFinder);
	}
	
	
	public String getEventAggregatorDetails() {
		return eventAggregatorDetails;
	}


	public static NewEventSegmentAggregation newCase(String aMessage, String aEventAggregatorDetails, Object aFinder) {
//		if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(NewEventSegmentAggregation.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(NewEventSegmentAggregation.class) + ") " +aMessage);
		if (shouldInstantiate(NewEventSegmentAggregation.class)) {
			NewEventSegmentAggregation retVal = new NewEventSegmentAggregation(aMessage, aEventAggregatorDetails, aFinder);
		retVal.announce();
		return retVal;
		}
		Tracer.info(aFinder, aMessage);
		return null;
	}

	public static NewEventSegmentAggregation newCase(String aEventAggregatorDetails, Object aFinder) {
		String aMessage = aEventAggregatorDetails.toString();
		return newCase(aMessage, aEventAggregatorDetails, aFinder);
//		MacroRecordingStarted retVal = new MacroRecordingStarted("", aFinder);
//		retVal.announce();
//		return retVal;
	}

	
}
