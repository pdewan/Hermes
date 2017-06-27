package util.trace.recorder;

import edu.cmu.scs.fluorite.commands.ICommand;
import fluorite.commands.EHBaseDocumentChangeEvent;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class DocumentChangeFinalizedEventNotified extends ICommandInfo{
	public DocumentChangeFinalizedEventNotified(String aMessage, EHBaseDocumentChangeEvent aCommand, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aCommand, aStartTimeStamp, aFinder);
	}	
    
    public static DocumentChangeFinalizedEventNotified newCase (String aMessage, EHBaseDocumentChangeEvent aCommand, long aStartTimeStamp,  Object aFinder) {

    	if (shouldInstantiate(DocumentChangeFinalizedEventNotified.class)) {
    	DocumentChangeFinalizedEventNotified retVal = new DocumentChangeFinalizedEventNotified(aMessage, aCommand, aStartTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(DocumentChangeFinalizedEventNotified.class, aMessage);


    	return null;
    }
    public static DocumentChangeFinalizedEventNotified newCase (EHBaseDocumentChangeEvent aCommand, long aStartTimestamp,  Object aFinder) {
    	String aMessage = toString(aCommand, aStartTimestamp);
    	return newCase(aMessage, aCommand, aStartTimestamp, aFinder);

    }
}
