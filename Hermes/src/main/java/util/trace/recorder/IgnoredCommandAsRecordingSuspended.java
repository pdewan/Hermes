package util.trace.recorder;

import fluorite.commands.EHICommand;
import util.trace.Tracer;

public class IgnoredCommandAsRecordingSuspended extends ICommandInfo{
	public IgnoredCommandAsRecordingSuspended(String aMessage, EHICommand aCommand, int aCommandNumber, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aCommand, aStartTimeStamp, aFinder);
	}	
    
    public static IgnoredCommandAsRecordingSuspended newCase (String aMessage, EHICommand aCommand, int aCommandNumber, long aStartTimeStamp,  Object aFinder) {

    	if (shouldInstantiate(IgnoredCommandAsRecordingSuspended.class)) {
    	IgnoredCommandAsRecordingSuspended retVal = new IgnoredCommandAsRecordingSuspended(aMessage, aCommand,  aCommandNumber, aStartTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(IgnoredCommandAsRecordingSuspended.class, aMessage);


    	return null;
    }
    public static IgnoredCommandAsRecordingSuspended newCase (EHICommand aCommand, int aCommandNumber, long aStartTimestamp,  Object aFinder) {
    	String aMessage = toString(aCommand,  aCommandNumber, aStartTimestamp);
    	return newCase(aMessage, aCommand,  aCommandNumber, aStartTimestamp, aFinder);

    }
}
