package util.trace.recorder;

import fluorite.commands.EHICommand;
import util.trace.Tracer;

public class NonDocumentChangeCommandNotified extends ICommandInfo{
	public NonDocumentChangeCommandNotified(String aMessage, EHICommand aCommand, int aCommandNumber, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aCommand, aStartTimeStamp, aFinder);
	}	
    
    public static NonDocumentChangeCommandNotified newCase (String aMessage, EHICommand aCommand, int aCommandNumber,  long aStartTimeStamp,  Object aFinder) {

    	if (shouldInstantiate(NonDocumentChangeCommandNotified.class)) {
    	NonDocumentChangeCommandNotified retVal = new NonDocumentChangeCommandNotified(aMessage, aCommand,  aCommandNumber,  aStartTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(NonDocumentChangeCommandNotified.class, aMessage);


    	return null;
    }
    public static NonDocumentChangeCommandNotified newCase (EHICommand aCommand, int aCommandNumber, long aStartTimestamp,  Object aFinder) {
    	String aMessage = toString(aCommand,  aCommandNumber, aStartTimestamp);
    	return newCase(aMessage, aCommand,  aCommandNumber,  aStartTimestamp, aFinder);

    }
}
