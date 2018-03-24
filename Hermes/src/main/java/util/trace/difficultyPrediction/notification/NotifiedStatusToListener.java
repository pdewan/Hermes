package util.trace.difficultyPrediction.notification;

import difficultyPrediction.statusManager.StatusListener;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class NotifiedStatusToListener extends TraceableInfo{
//	ICommand command;
	public NotifiedStatusToListener(String aMessage, String aStatus, int anIntStatus, StatusListener aListener, Object aFinder) {
		 super(aMessage,aFinder);
//		 command = aCommand;
	}
	
    public static String toString(String aStatus, int anIntStatus, StatusListener aListener) {
    	
    	return  
    				"(" + aStatus  + "," + anIntStatus +  ")" + "->" +  aListener;
    				
    }
    public static NotifiedStatusToListener newCase (String aMessage, String aStatus, int anIntStatus,  StatusListener aListener,  Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(ExcludedCommand.class))
//      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(ExcludedCommand.class) + ") " +aMessage);
    	if (shouldInstantiate(NotifiedStatusToListener.class)) {
    	NotifiedStatusToListener retVal = new NotifiedStatusToListener(aMessage, aStatus, anIntStatus, aListener,  aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(NotifiedStatusToListener.class, aMessage);


    	return null;
    }
    public static NotifiedStatusToListener newCase (String aStatus, int anIntStatus,  StatusListener aListener,  Object aFinder) {
    	String aMessage = toString(aStatus, anIntStatus, aListener);
    	return newCase(aMessage, aStatus, anIntStatus, aListener, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aCommandName, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
