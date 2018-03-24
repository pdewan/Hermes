package util.trace.recorder;

import util.trace.TraceableInfo;
import util.trace.Tracer;

public class ExcludedCommand extends TraceableInfo{
	public ExcludedCommand(String aMessage, String aCommand, Object aFinder) {
		 super(aMessage, aFinder);
	}	
    
    public static ExcludedCommand newCase (String aMessage, String aCommand, Object aFinder) {

    	if (shouldInstantiate(ExcludedCommand.class)) {
    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aCommand,  aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(ExcludedCommand.class, aMessage);


    	return null;
    }
    public static ExcludedCommand newCase (String aCommand,  Object aFinder) {
    	String aMessage = aCommand;
    	return newCase(aMessage, aCommand, aFinder);

    }
}
