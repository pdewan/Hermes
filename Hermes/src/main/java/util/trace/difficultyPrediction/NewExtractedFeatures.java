package util.trace.difficultyPrediction;

import util.trace.TraceableInfo;
import util.trace.Tracer;


public class NewExtractedFeatures extends TraceableInfo {
	String ratioFeatures;
	public NewExtractedFeatures(String aMessage, String aRatioFeatures, Object aFinder) {
		super(aMessage, aFinder);
	}
	
	
	public String getRatioFeatures() {
		return ratioFeatures;
	}


	public static NewExtractedFeatures newCase(String aMessage, String aRatioFeatures, Object aFinder) {
//		if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(NewExtractedFeatures.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(NewExtractedFeatures.class) + ") " +aMessage);
		if (shouldInstantiate(NewExtractedFeatures.class)) {
		NewExtractedFeatures retVal = new NewExtractedFeatures(aMessage, aRatioFeatures, aFinder);
		retVal.announce();
		return retVal;
		}
		Tracer.info(aFinder, aMessage);
		return null;
	}

	public static NewExtractedFeatures newCase(String aRatioFeatures, Object aFinder) {
		String aMessage = aRatioFeatures.toString();
		return newCase(aMessage, aRatioFeatures, aFinder);

	}

	
}
