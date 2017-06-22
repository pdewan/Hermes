package util.trace.difficultyPrediction;

import util.trace.TraceableInfo;
import util.trace.Tracer;


public class CommandIgnoredBecauseQueueFull extends TraceableInfo {

	public CommandIgnoredBecauseQueueFull(String aMessage, Object aFinder) {
		super(aMessage, aFinder);
	}
	
	public static CommandIgnoredBecauseQueueFull newCase(String aMessage, Object aFinder) {
//		if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(CommandIgnoredBecauseQueueFull.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(CommandIgnoredBecauseQueueFull.class) + ") " +aMessage);
		if (shouldInstantiate(CommandIgnoredBecauseQueueFull.class)) {
		CommandIgnoredBecauseQueueFull retVal = new CommandIgnoredBecauseQueueFull("", aFinder);
		retVal.announce();
		return retVal;
		}
		Tracer.info(aFinder, aMessage);

		return null;
	}

	public static CommandIgnoredBecauseQueueFull newCase(Object aFinder) {
		String aMessage = "";
		return newCase(aMessage, aFinder);
//		MacroRecordingStarted retVal = new MacroRecordingStarted("", aFinder);
//		retVal.announce();
//		return retVal;
	}

	
}
