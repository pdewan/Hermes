package util.trace.recorder;

import edu.cmu.scs.fluorite.commands.ICommand;
import fluorite.commands.EHICommand;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class NonDocumentChangeCommandExecuted extends ICommandInfo{
	public NonDocumentChangeCommandExecuted(String aMessage, EHICommand aCommand, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aCommand, aStartTimeStamp, aFinder);
	}	
    
    public static NonDocumentChangeCommandExecuted newCase (String aMessage, EHICommand aCommand, long aStartTimeStamp,  Object aFinder) {

    	if (shouldInstantiate(NonDocumentChangeCommandExecuted.class)) {
    	NonDocumentChangeCommandExecuted retVal = new NonDocumentChangeCommandExecuted(aMessage, aCommand, aStartTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(NonDocumentChangeCommandExecuted.class, aMessage);


    	return null;
    }
    public static NonDocumentChangeCommandExecuted newCase (EHICommand aCommand, long aStartTimestamp,  Object aFinder) {
    	String aMessage = toString(aCommand, aStartTimestamp);
    	return newCase(aMessage, aCommand, aStartTimestamp, aFinder);

    }
}
