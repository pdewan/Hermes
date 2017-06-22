package util.trace.difficultyPrediction.analyzer;

import util.trace.TraceableInfo;
import util.trace.Tracer;


public class AnalyzerPredictionStartNotification extends TraceableInfo {

	public AnalyzerPredictionStartNotification(String aMessage, Object aFinder) {
		super(aMessage, aFinder);
	}
	
	public static AnalyzerPredictionStartNotification newCase(String aMessage, Object aFinder) {
//		if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(PluginStarted.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(PluginStarted.class) + ") " +aMessage);
		if (shouldInstantiate(AnalyzerPredictionStartNotification.class)) {
		AnalyzerPredictionStartNotification retVal = new AnalyzerPredictionStartNotification("", aFinder);
		retVal.announce();
		return retVal;
		}
		Tracer.info(aFinder, aMessage);
		Tracer.info(AnalyzerPredictionStartNotification.class, aMessage);


		return null;
	}

	public static AnalyzerPredictionStartNotification newCase(Object aFinder) {
		String aMessage = "";
		return newCase(aMessage, aFinder);
//		MacroRecordingStarted retVal = new MacroRecordingStarted("", aFinder);
//		retVal.announce();
//		return retVal;
	}

	
}
