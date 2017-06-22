package util.trace.difficultyPrediction.analyzer;

import util.trace.TraceableInfo;
import util.trace.Tracer;


public class AnalyzerPredictionStopNotification extends TraceableInfo {

	public AnalyzerPredictionStopNotification(String aMessage, Object aFinder) {
		super(aMessage, aFinder);
	}
	
	public static AnalyzerPredictionStopNotification newCase(String aMessage, Object aFinder) {
//		if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(PluginStarted.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(PluginStarted.class) + ") " +aMessage);
		if (shouldInstantiate(AnalyzerPredictionStopNotification.class)) {
		AnalyzerPredictionStopNotification retVal = new AnalyzerPredictionStopNotification("", aFinder);
		retVal.announce();
		return retVal;
		}
		Tracer.info(aFinder, aMessage);
		Tracer.info(AnalyzerPredictionStopNotification.class, aMessage);


		return null;
	}

	public static AnalyzerPredictionStopNotification newCase(Object aFinder) {
		String aMessage = "";
		return newCase(aMessage, aFinder);
//		MacroRecordingStarted retVal = new MacroRecordingStarted("", aFinder);
//		retVal.announce();
//		return retVal;
	}

	
}
