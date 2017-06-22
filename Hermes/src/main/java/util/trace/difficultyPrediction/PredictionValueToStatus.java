package util.trace.difficultyPrediction;

import util.trace.TraceableInfo;
import util.trace.Tracer;


public class PredictionValueToStatus extends TraceableInfo {

	public PredictionValueToStatus(String aMessage, Object aFinder) {
		super(aMessage, aFinder);
	}
	
	public static PredictionValueToStatus newCase(String aMessage, Object aFinder) {
//		if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(PredictionValueToStatus.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(PredictionValueToStatus.class) + ") " +aMessage);
		if (shouldInstantiate(PredictionValueToStatus.class)) {
		PredictionValueToStatus retVal = new PredictionValueToStatus("", aFinder);
		retVal.announce();
		return retVal;
		}
		Tracer.info(aFinder, aMessage);

		return null;
	}

	public static PredictionValueToStatus newCase(Object aFinder) {
		String aMessage = "";
		return newCase(aMessage, aFinder);
//		MacroRecordingStarted retVal = new MacroRecordingStarted("", aFinder);
//		retVal.announce();
//		return retVal;
	}

	
}
