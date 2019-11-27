package util.trace.recorder;

import fluorite.commands.EHICommand;
import util.trace.Tracer;

public class CommandLoggingInitiated extends ICommandInfo{
	public CommandLoggingInitiated(String aMessage, EHICommand aCommand, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aCommand, aStartTimeStamp, aFinder);
	}	
    
    public static CommandLoggingInitiated newCase (String aMessage, EHICommand aCommand, int aCommandNumber, long aStartTimeStamp,  Object aFinder) {

    	if (shouldInstantiate(CommandLoggingInitiated.class)) {
    	CommandLoggingInitiated retVal = new CommandLoggingInitiated(aMessage, aCommand, aStartTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(CommandLoggingInitiated.class, aMessage);


    	return null;
    }
    public static CommandLoggingInitiated newCase (EHICommand aCommand, int aCommandNumber, long aStartTimestamp,  Object aFinder) {
    	String aMessage = toString(aCommand, aCommandNumber, aStartTimestamp);
    	return newCase(aMessage, aCommand, aCommandNumber, aStartTimestamp, aFinder);

    }
}
