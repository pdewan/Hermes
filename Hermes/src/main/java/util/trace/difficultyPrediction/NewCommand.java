package util.trace.difficultyPrediction;

import util.trace.TraceableInfo;
import util.trace.Tracer;


public class NewCommand extends TraceableInfo {

	public NewCommand(String aMessage, Object aFinder) {
		super(aMessage, aFinder);
	}
	
	public static NewCommand newCase(String aMessage, Object aFinder) {
//		if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(NewCommand.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(NewCommand.class) + ") " +aMessage);
		if (shouldInstantiate(NewCommand.class)) {
		NewCommand retVal = new NewCommand("", aFinder);
		retVal.announce();
		return retVal;
		}
		Tracer.info(aFinder, aMessage);

		return null;
	}

	public static NewCommand newCase(Object aFinder) {
		String aMessage = "";
		return newCase(aMessage, aFinder);
//		MacroRecordingStarted retVal = new MacroRecordingStarted("", aFinder);
//		retVal.announce();
//		return retVal;
	}

	
}
