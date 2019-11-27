package util.trace.recorder;

import fluorite.commands.BaseDocumentChangeEvent;
import util.trace.Tracer;

public class DocumentChangeCommandNotified extends ICommandInfo{
	public DocumentChangeCommandNotified(String aMessage, BaseDocumentChangeEvent aCommand, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aCommand, aStartTimeStamp, aFinder);
	}	
    
    public static DocumentChangeCommandNotified newCase (String aMessage, BaseDocumentChangeEvent aCommand, int aCommandNumber, long aStartTimeStamp,  Object aFinder) {

    	if (shouldInstantiate(DocumentChangeCommandNotified.class)) {
    	DocumentChangeCommandNotified retVal = new DocumentChangeCommandNotified(aMessage, aCommand, aStartTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(DocumentChangeCommandNotified.class, aMessage);


    	return null;
    }
    public static DocumentChangeCommandNotified newCase (BaseDocumentChangeEvent aCommand, int aCommandNumber, long aStartTimestamp,  Object aFinder) {
    	String aMessage = toString(aCommand,  aCommandNumber, aStartTimestamp);
    	return newCase(aMessage, aCommand,  aCommandNumber, aStartTimestamp, aFinder);

    }
}
