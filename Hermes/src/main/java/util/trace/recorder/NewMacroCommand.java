package util.trace.recorder;

import util.trace.TraceableInfo;
import util.trace.Tracer;

public class NewMacroCommand extends TraceableInfo{
	String commandName;
	long relativeTimeStamp;		
	public NewMacroCommand(String aMessage, String aCommandName, long aRelativeTimeStamp, Object aFinder) {
		 super(aMessage, aFinder);
		 commandName = aCommandName;
		 relativeTimeStamp = aRelativeTimeStamp;		 
	}
	public String getCommandName() {
		return commandName;
	}
	public long getRelativeTimeStamp() {
		return relativeTimeStamp;
	}
	
    public static String toString(String aCommandName, long aRelativeTimeStamp) {
    	return("(" + 
    				aCommandName 
    				+ "," + aRelativeTimeStamp + ")");
    }
    public static NewMacroCommand newCase (String aMessage, String aCommandName, long aRelativeTimeStamp, Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(NewMacroCommand.class))
//    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println(Tracer.infoPrintBody(MacroRecordingStarted.class) + ") " + aMessage);
    	if (shouldInstantiate(NewMacroCommand.class)) {
    	NewMacroCommand retVal = new NewMacroCommand(aMessage, aCommandName, aRelativeTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(NewMacroCommand.class, aMessage);

    	return null;
    }
    public static NewMacroCommand newCase (String aCommandName, long aRelativeTimeStamp, Object aFinder) {
    	String aMessage = toString(aCommandName, aRelativeTimeStamp);
    	return newCase(aMessage, aCommandName, aRelativeTimeStamp, aFinder);
//    	NewMacroCommand retVal = new NewMacroCommand(aMessage, aCommandName, aRelativeTimeStamp, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
