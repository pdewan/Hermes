package util.trace.recorder;

import java.util.LinkedList;

import util.trace.TraceableInfo;
import util.trace.Tracer;

public class PendingCommandsLogBegin extends TraceableInfo{
	LinkedList commands;
	public PendingCommandsLogBegin(String aMessage, LinkedList aCommands,  Object aFinder) {
		 super(aMessage, aFinder);
		 commands = aCommands;
	}
	public LinkedList getCommands() {
		return commands;
	}
	
	
    public static String toString(LinkedList aCommands) {
    	return("(" + 
//    				aCommands.toString() + 
 			"commands" +
    				")");
    }
    public static PendingCommandsLogBegin newCase (String aMessage, LinkedList aCommands,  Object aFinder) {
//    	try {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(MacroCommandsLogBegin.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(MacroCommandsLogBegin.class) + ") " +aMessage);
//    	} catch (Exception e) {
//    		System.out.println("MacroCommandsLogBegin: " + e);
//    	}
    	if (shouldInstantiate(PendingCommandsLogBegin.class)) {
    	PendingCommandsLogBegin retVal = new PendingCommandsLogBegin(aMessage, aCommands, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);

    	return null;
    }
    public static PendingCommandsLogBegin newCase (LinkedList aCommands,  Object aFinder) {
    	String aMessage = toString(aCommands);
    	return newCase(aMessage, aCommands, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aCommands, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
