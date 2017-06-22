package util.trace.recorder;

import edu.cmu.scs.fluorite.commands.ICommand;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class IgnoredCommandAsRecordingSuspended extends ICommandInfo{
	public IgnoredCommandAsRecordingSuspended(String aMessage, ICommand aCommand, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aCommand, aStartTimeStamp, aFinder);
	}	
    
    public static IgnoredCommandAsRecordingSuspended newCase (String aMessage, ICommand aCommand, long aStartTimeStamp,  Object aFinder) {

    	if (shouldInstantiate(IgnoredCommandAsRecordingSuspended.class)) {
    	IgnoredCommandAsRecordingSuspended retVal = new IgnoredCommandAsRecordingSuspended(aMessage, aCommand, aStartTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(IgnoredCommandAsRecordingSuspended.class, aMessage);


    	return null;
    }
    public static IgnoredCommandAsRecordingSuspended newCase (ICommand aCommand, long aStartTimestamp,  Object aFinder) {
    	String aMessage = toString(aCommand, aStartTimestamp);
    	return newCase(aMessage, aCommand, aStartTimestamp, aFinder);

    }
}
