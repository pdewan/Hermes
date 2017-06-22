package util.trace.recorder;

import edu.cmu.scs.fluorite.commands.ICommand;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class CommandLoggingInitiated extends ICommandInfo{
	public CommandLoggingInitiated(String aMessage, ICommand aCommand, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aCommand, aStartTimeStamp, aFinder);
	}	
    
    public static CommandLoggingInitiated newCase (String aMessage, ICommand aCommand, long aStartTimeStamp,  Object aFinder) {

    	if (shouldInstantiate(CommandLoggingInitiated.class)) {
    	CommandLoggingInitiated retVal = new CommandLoggingInitiated(aMessage, aCommand, aStartTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(CommandLoggingInitiated.class, aMessage);


    	return null;
    }
    public static CommandLoggingInitiated newCase (ICommand aCommand, long aStartTimestamp,  Object aFinder) {
    	String aMessage = toString(aCommand, aStartTimestamp);
    	return newCase(aMessage, aCommand, aStartTimestamp, aFinder);

    }
}