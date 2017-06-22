package util.trace.difficultyPrediction.notification;

import difficultyPrediction.PluginEventListener;
import difficultyPrediction.statusManager.StatusListener;
import edu.cmu.scs.fluorite.commands.ICommand;
import util.trace.TraceableInfo;
import util.trace.Tracer;
import util.trace.recorder.ICommandInfo;

public class NotifiedReplayedStatusToListener extends TraceableInfo{
//	ICommand command;
	public NotifiedReplayedStatusToListener(String aMessage, int anIntStatus, StatusListener aListener, Object aFinder) {
		 super(aMessage,aFinder);
//		 command = aCommand;
	}
	
    public static String toString(int anIntStatus, StatusListener aListener) {
    	
    	return  
    				 anIntStatus + "->" +  aListener;
    				
    }
    public static NotifiedReplayedStatusToListener newCase (String aMessage, int anIntStatus,  StatusListener aListener,  Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(ExcludedCommand.class))
//      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(ExcludedCommand.class) + ") " +aMessage);
    	if (shouldInstantiate(NotifiedReplayedStatusToListener.class)) {
    	NotifiedReplayedStatusToListener retVal = new NotifiedReplayedStatusToListener(aMessage, anIntStatus, aListener,  aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(NotifiedReplayedStatusToListener.class, aMessage);


    	return null;
    }
    public static NotifiedReplayedStatusToListener newCase ( int anIntStatus,  StatusListener aListener,  Object aFinder) {
    	String aMessage = toString(anIntStatus, aListener);
    	return newCase(aMessage,anIntStatus, aListener, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aCommandName, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
