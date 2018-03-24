package util.trace.recorder;

import util.trace.TraceableInfo;
import util.trace.Tracer;

public class RemovedCommandFromBuffers extends TraceableInfo{
	String command;
	public RemovedCommandFromBuffers(String aMessage, String aCommand, String aDocNorNormal, String aDocAndNormal,  Object aFinder) {
		 super(aMessage, aFinder);
		 command = aCommand;
	}
	public String getCommand() {
		return command;
	}
	
	
    public static String toString(String aCommand, String aDocOrNormal, String aDocAndNormal) {
    	return  
    				aCommand + "->" + "(" + aDocOrNormal + "," + aDocAndNormal + ")";
    				
    }
    public static RemovedCommandFromBuffers newCase (String aMessage, String aCommand, String aDocOrNormal,   String aDocAndNormal,  Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(ExcludedCommand.class))
//      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(ExcludedCommand.class) + ") " +aMessage);
    	if (shouldInstantiate(RemovedCommandFromBuffers.class)) {
    	RemovedCommandFromBuffers retVal = new RemovedCommandFromBuffers(aMessage, aCommand, aDocOrNormal, aDocAndNormal,  aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(RemovedCommandFromBuffers.class, aMessage);


    	return null;
    }
    public static RemovedCommandFromBuffers newCase (String aCommand, String aDocOrNormal,  String aDocAndNormal,  Object aFinder) {
    	String aMessage = toString(aCommand, aDocOrNormal, aDocAndNormal);
    	return newCase(aMessage, aCommand, aDocOrNormal, aDocAndNormal, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aCommandName, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
