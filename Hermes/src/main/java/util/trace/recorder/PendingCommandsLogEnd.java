package util.trace.recorder;

import java.util.LinkedList;

import util.trace.TraceableInfo;
import util.trace.Tracer;

public class PendingCommandsLogEnd extends TraceableInfo{
	LinkedList commands;
	public PendingCommandsLogEnd(String aMessage, LinkedList aCommands,  Object aFinder) {
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
    public static PendingCommandsLogEnd newCase (String aMessage, LinkedList aCommands,  Object aFinder) {
//    	try {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(MacroCommandsLogEnd.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(MacroCommandsLogEnd.class) + ") " +aMessage);
//    	} catch (Exception e) {
//    		System.out.println("MacroCommandsLogEnd" + e); // console disposed
//    	}
    	if (shouldInstantiate(PendingCommandsLogEnd.class)) {
    	PendingCommandsLogEnd retVal = new PendingCommandsLogEnd(aMessage, aCommands, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);

    	return null;
    }
    public static PendingCommandsLogEnd newCase (LinkedList aCommands,  Object aFinder) {
    	String aMessage = toString(aCommands);
    	return newCase(aMessage, aCommands, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aCommands, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
