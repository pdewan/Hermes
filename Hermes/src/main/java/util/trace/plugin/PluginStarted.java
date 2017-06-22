package util.trace.plugin;

import util.trace.TraceableInfo;
import util.trace.Tracer;


public class PluginStarted extends TraceableInfo {

	public PluginStarted(String aMessage, Object aFinder) {
		super(aMessage, aFinder);
	}
	
	public static PluginStarted newCase(String aMessage, Object aFinder) {
//		if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(PluginStarted.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(PluginStarted.class) + ") " +aMessage);
		if (shouldInstantiate(PluginStarted.class)) {
		PluginStarted retVal = new PluginStarted("", aFinder);
		retVal.announce();
		return retVal;
		}
		Tracer.info(aFinder, aMessage);
		Tracer.info(PluginStarted.class, aMessage);


		return null;
	}

	public static PluginStarted newCase(Object aFinder) {
		String aMessage = "";
		return newCase(aMessage, aFinder);
//		MacroRecordingStarted retVal = new MacroRecordingStarted("", aFinder);
//		retVal.announce();
//		return retVal;
	}

	
}
