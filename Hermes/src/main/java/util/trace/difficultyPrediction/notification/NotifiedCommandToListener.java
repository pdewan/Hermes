package util.trace.difficultyPrediction.notification;

import difficultyPrediction.PluginEventListener;
import fluorite.commands.EHICommand;
import util.trace.TraceableInfo;
import util.trace.Tracer;
import util.trace.recorder.ICommandInfo;

public class NotifiedCommandToListener extends ICommandInfo{
//	ICommand command;
	public NotifiedCommandToListener(String aMessage, EHICommand aCommand, PluginEventListener aListener, Object aFinder) {
		 super(aMessage, aCommand, 0, aFinder);
//		 command = aCommand;
	}
	
    public static String toString(EHICommand aCommand, PluginEventListener aListener) {
    	
    	return  
    				aCommand + "(" + aCommand.getTimestamp() + ")" +  "->" + "(" + aListener + ")";
    				
    }
    public static NotifiedCommandToListener newCase (String aMessage, EHICommand aCommand, PluginEventListener aListener,  Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(ExcludedCommand.class))
//      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(ExcludedCommand.class) + ") " +aMessage);
    	if (shouldInstantiate(NotifiedCommandToListener.class)) {
    	NotifiedCommandToListener retVal = new NotifiedCommandToListener(aMessage, aCommand, aListener,  aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(NotifiedCommandToListener.class, aMessage);


    	return null;
    }
    public static NotifiedCommandToListener newCase (EHICommand aCommand, PluginEventListener aListener,  Object aFinder) {
    	String aMessage = toString(aCommand, aListener);
    	return newCase(aMessage, aCommand, aListener, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aCommandName, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
