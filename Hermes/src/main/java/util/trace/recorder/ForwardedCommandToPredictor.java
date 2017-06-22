package util.trace.recorder;

import edu.cmu.scs.fluorite.commands.ICommand;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class ForwardedCommandToPredictor extends ICommandInfo{
	public ForwardedCommandToPredictor(String aMessage, ICommand aCommand, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aCommand, aStartTimeStamp, aFinder);
	}	
    
    public static ForwardedCommandToPredictor newCase (String aMessage, ICommand aCommand, long aStartTimeStamp,  Object aFinder) {

    	if (shouldInstantiate(ForwardedCommandToPredictor.class)) {
    	ForwardedCommandToPredictor retVal = new ForwardedCommandToPredictor(aMessage, aCommand, aStartTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(ForwardedCommandToPredictor.class, aMessage);


    	return null;
    }
    public static ForwardedCommandToPredictor newCase (ICommand aCommand, long aStartTimestamp,  Object aFinder) {
    	String aMessage = toString(aCommand, aStartTimestamp);
    	return newCase(aMessage, aCommand, aStartTimestamp, aFinder);

    }
}
