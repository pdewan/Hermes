package util.trace.recorder;

import util.trace.TraceableInfo;
import util.trace.Tracer;

public class CombinedCommand extends TraceableInfo{
	String command;
	public CombinedCommand(String aMessage, String aCommand, String aLastCommand, Object aFinder) {
		 super(aMessage, aFinder);
		 command = aCommand;
	}
	public String getCommand() {
		return command;
	}
	
	
    public static String toString(String aCommand, String aLastCommand) {
    	return("(" + 
    				aCommand + ","  + aLastCommand +
    				")");
    }
    public static CombinedCommand newCase (String aMessage, String aCommand, String aLastCommand,  Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(ExcludedCommand.class))
//      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(ExcludedCommand.class) + ") " +aMessage);
    	if (shouldInstantiate(CombinedCommand.class)) {
    	CombinedCommand retVal = new CombinedCommand(aMessage, aCommand, aLastCommand, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(CombinedCommand.class, aMessage);


    	return null;
    }
    public static CombinedCommand newCase (String aCommand, String aLastCommand,  Object aFinder) {
    	String aMessage = toString(aCommand, aLastCommand);
    	return newCase(aMessage, aCommand, aLastCommand, aFinder);
    }
}
