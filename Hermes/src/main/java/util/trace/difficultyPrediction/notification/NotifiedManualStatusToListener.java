package util.trace.difficultyPrediction.notification;

import difficultyPrediction.PluginEventListener;
import difficultyPrediction.statusManager.StatusListener;
import edu.cmu.scs.fluorite.commands.ICommand;
import util.trace.TraceableInfo;
import util.trace.Tracer;
import util.trace.recorder.ICommandInfo;

public class NotifiedManualStatusToListener extends TraceableInfo{
//	ICommand command;
	public NotifiedManualStatusToListener(String aMessage, String aStatus, int anIntStatus, StatusListener aListener, Object aFinder) {
		 super(aMessage,aFinder);
//		 command = aCommand;
	}
	
    public static String toString(String aStatus, int anIntStatus, StatusListener aListener) {
    	
    	return  
    				"(" + aStatus  + "," + anIntStatus +  ")" + "->" +  aListener;
    				
    }
    public static NotifiedManualStatusToListener newCase (String aMessage, String aStatus, int anIntStatus,  StatusListener aListener,  Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(ExcludedCommand.class))
//      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(ExcludedCommand.class) + ") " +aMessage);
    	if (shouldInstantiate(NotifiedManualStatusToListener.class)) {
    	NotifiedManualStatusToListener retVal = new NotifiedManualStatusToListener(aMessage, aStatus, anIntStatus, aListener,  aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(NotifiedManualStatusToListener.class, aMessage);


    	return null;
    }
    public static NotifiedManualStatusToListener newCase (String aStatus, int anIntStatus,  StatusListener aListener,  Object aFinder) {
    	String aMessage = toString(aStatus, anIntStatus, aListener);
    	return newCase(aMessage, aStatus, anIntStatus, aListener, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aCommandName, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
