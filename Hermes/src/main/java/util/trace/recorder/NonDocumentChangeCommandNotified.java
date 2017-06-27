package util.trace.recorder;

import edu.cmu.scs.fluorite.commands.ICommand;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class NonDocumentChangeCommandNotified extends ICommandInfo{
	public NonDocumentChangeCommandNotified(String aMessage, ICommand aCommand, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aCommand, aStartTimeStamp, aFinder);
	}	
    
    public static NonDocumentChangeCommandNotified newCase (String aMessage, ICommand aCommand, long aStartTimeStamp,  Object aFinder) {

    	if (shouldInstantiate(NonDocumentChangeCommandNotified.class)) {
    	NonDocumentChangeCommandNotified retVal = new NonDocumentChangeCommandNotified(aMessage, aCommand, aStartTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(NonDocumentChangeCommandNotified.class, aMessage);


    	return null;
    }
    public static NonDocumentChangeCommandNotified newCase (ICommand aCommand, long aStartTimestamp,  Object aFinder) {
    	String aMessage = toString(aCommand, aStartTimestamp);
    	return newCase(aMessage, aCommand, aStartTimestamp, aFinder);

    }
}
