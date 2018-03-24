package util.trace.recorder;

import fluorite.commands.EHBaseDocumentChangeEvent;
import util.trace.Tracer;

public class DocumentChangeCommandNotified extends ICommandInfo{
	public DocumentChangeCommandNotified(String aMessage, EHBaseDocumentChangeEvent aCommand, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aCommand, aStartTimeStamp, aFinder);
	}	
    
    public static DocumentChangeCommandNotified newCase (String aMessage, EHBaseDocumentChangeEvent aCommand, long aStartTimeStamp,  Object aFinder) {

    	if (shouldInstantiate(DocumentChangeCommandNotified.class)) {
    	DocumentChangeCommandNotified retVal = new DocumentChangeCommandNotified(aMessage, aCommand, aStartTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(DocumentChangeCommandNotified.class, aMessage);


    	return null;
    }
    public static DocumentChangeCommandNotified newCase (EHBaseDocumentChangeEvent aCommand, long aStartTimestamp,  Object aFinder) {
    	String aMessage = toString(aCommand, aStartTimestamp);
    	return newCase(aMessage, aCommand, aStartTimestamp, aFinder);

    }
}
