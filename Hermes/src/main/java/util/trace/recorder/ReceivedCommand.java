package util.trace.recorder;

import edu.cmu.scs.fluorite.commands.ICommand;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class ReceivedCommand extends ICommandInfo{
	public ReceivedCommand(String aMessage, ICommand aCommand, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aCommand, aStartTimeStamp, aFinder);
	}	
    
    public static ReceivedCommand newCase (String aMessage, ICommand aCommand, long aStartTimeStamp,  Object aFinder) {

    	if (shouldInstantiate(ReceivedCommand.class)) {
    	ReceivedCommand retVal = new ReceivedCommand(aMessage, aCommand, aStartTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(ReceivedCommand.class, aMessage);


    	return null;
    }
    public static ReceivedCommand newCase (ICommand aCommand, long aStartTimestamp,  Object aFinder) {
    	String aMessage = toString(aCommand, aStartTimestamp);
    	return newCase(aMessage, aCommand, aStartTimestamp, aFinder);

    }
}
