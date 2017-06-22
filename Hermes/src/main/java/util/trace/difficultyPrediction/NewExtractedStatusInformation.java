package util.trace.difficultyPrediction;

import util.trace.TraceableInfo;
import util.trace.Tracer;


public class NewExtractedStatusInformation extends TraceableInfo {
	String statusInformation;
	public NewExtractedStatusInformation(String aMessage, String aStatusInformation, Object aFinder) {
		super(aMessage, aFinder);
	}
	
	
	public String getStatusInformation() {
		return statusInformation;
	}


	public static NewExtractedStatusInformation newCase(String aMessage, String aStatusInformation, Object aFinder) {
//		if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(NewExtractedStatusInformation.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(NewExtractedStatusInformation.class) + ") " +aMessage);
		if (shouldInstantiate(NewExtractedStatusInformation.class)) {
		NewExtractedStatusInformation retVal = new NewExtractedStatusInformation(aMessage, aStatusInformation, aFinder);
		retVal.announce();
		return retVal;
		}
		Tracer.info(aFinder, aMessage);
		return null;
	}

	public static NewExtractedStatusInformation newCase(String aStatusInformation, Object aFinder) {
		String aMessage = aStatusInformation.toString();
		return newCase(aMessage, aStatusInformation, aFinder);
//		MacroRecordingStarted retVal = new MacroRecordingStarted("", aFinder);
//		retVal.announce();
//		return retVal;
	}

	
}
