package util.trace.recorder;

import fluorite.commands.EHICommand;
import util.trace.Tracer;

public class NonDocumentChangeCommandExecuted extends ICommandInfo{
	public NonDocumentChangeCommandExecuted(String aMessage, EHICommand aCommand, int aCommandNumber, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aCommand, aStartTimeStamp, aFinder);
	}	
    
    public static NonDocumentChangeCommandExecuted newCase (String aMessage, EHICommand aCommand, int aCommandNumber, long aStartTimeStamp,  Object aFinder) {

    	if (shouldInstantiate(NonDocumentChangeCommandExecuted.class)) {
    	NonDocumentChangeCommandExecuted retVal = new NonDocumentChangeCommandExecuted(aMessage, aCommand,  aCommandNumber, aStartTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(NonDocumentChangeCommandExecuted.class, aMessage);


    	return null;
    }
    public static NonDocumentChangeCommandExecuted newCase (EHICommand aCommand, int aCommandNumber, long aStartTimestamp,  Object aFinder) {
    	String aMessage = toString(aCommand,  aCommandNumber, aStartTimestamp);
    	return newCase(aMessage, aCommand,  aCommandNumber, aStartTimestamp, aFinder);

    }
}
