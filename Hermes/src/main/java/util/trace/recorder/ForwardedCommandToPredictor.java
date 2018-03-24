package util.trace.recorder;

import fluorite.commands.EHICommand;
import util.trace.Tracer;

public class ForwardedCommandToPredictor extends ICommandInfo{
	public ForwardedCommandToPredictor(String aMessage, EHICommand aCommand, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aCommand, aStartTimeStamp, aFinder);
	}	
    
    public static ForwardedCommandToPredictor newCase (String aMessage, EHICommand aCommand, long aStartTimeStamp,  Object aFinder) {

    	if (shouldInstantiate(ForwardedCommandToPredictor.class)) {
    	ForwardedCommandToPredictor retVal = new ForwardedCommandToPredictor(aMessage, aCommand, aStartTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(ForwardedCommandToPredictor.class, aMessage);


    	return null;
    }
    public static ForwardedCommandToPredictor newCase (EHICommand aCommand, long aStartTimestamp,  Object aFinder) {
    	String aMessage = toString(aCommand, aStartTimestamp);
    	return newCase(aMessage, aCommand, aStartTimestamp, aFinder);

    }
}
