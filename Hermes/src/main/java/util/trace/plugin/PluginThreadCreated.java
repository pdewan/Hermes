package util.trace.plugin;

import util.trace.Tracer;
import util.trace.session.ThreadCreated;

public class PluginThreadCreated extends ThreadCreated{
//	String threadName;
	public PluginThreadCreated(String aMessage, String aCommandName,  Object aFinder) {
		super(aMessage, "", aCommandName, aFinder);
//		 super(aMessage, aFinder);
//		 threadName = aThreadName;
	}
//	public String getThreadName() {
//		return threadName;
//	}
//	
//	
//    public static String toString(String aThreadName) {
//    	return("(" + 
//    				aThreadName + 
//    				")");
//    }
    public static PluginThreadCreated newCase (String aMessage, String aThreadName,  Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(PluginThreadCreated.class))
//      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(PluginThreadCreated.class) + ") " +aMessage);
    	if (shouldInstantiate(PluginThreadCreated.class)) {
    	PluginThreadCreated retVal = new PluginThreadCreated(aMessage, aThreadName, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(PluginThreadCreated.class, aMessage);


    	return null;
    }
    public static PluginThreadCreated newCase (String aThreadName,  Object aFinder) {
    	String aMessage = toString(aThreadName);
    	return newCase(aMessage, aThreadName, aFinder);
//    	ExcludedThread retVal = new ExcludedThread(aMessage, aThreadName, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
