package util.trace.plugin;

import util.trace.TraceableInfo;
import util.trace.Tracer;


public class PluginStopped extends TraceableInfo {

	public PluginStopped(String aMessage, Object aFinder) {
		super(aMessage, aFinder);
	}
	
	public static PluginStopped newCase(String aMessage, Object aFinder) {
//		if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(PluginStopped.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(PluginStopped.class) + ") " +aMessage);
		if (shouldInstantiate(PluginStopped.class)) {
		PluginStopped retVal = new PluginStopped("", aFinder);
		retVal.announce();
		return retVal;
		}
		Tracer.info(aFinder, aMessage);
		Tracer.info(PluginStopped.class, aMessage);

		return null;
	}

	public static PluginStopped newCase(Object aFinder) {
		String aMessage = "";
		return newCase(aMessage, aFinder);
//		MacroRecordingStarted retVal = new MacroRecordingStarted("", aFinder);
//		retVal.announce();
//		return retVal;
	}

	
}
