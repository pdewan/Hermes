package util.trace.difficultyPrediction;

import edu.cmu.scs.fluorite.commands.ICommand;
import fluorite.commands.EHICommand;
import util.trace.TraceableInfo;
import util.trace.Tracer;
import util.trace.recorder.ICommandInfo;

public class AddedCommandToPredictionQueue extends ICommandInfo{
	EHICommand command;
	public AddedCommandToPredictionQueue(String aMessage, EHICommand aCommand, String aPredictionQ, Object aFinder) {
		 super(aMessage, aCommand, 0, aFinder);
		 command = aCommand;
	}
	
    public static String toString(EHICommand aCommand, String aQueue) {
    	
    	return  
    				aCommand + "(" + aCommand.getTimestamp() + ")" +  "->" + "(" + aQueue + ")";
    				
    }
    public static AddedCommandToPredictionQueue newCase (String aMessage, EHICommand aCommand, String aQueue,  Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(ExcludedCommand.class))
//      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(ExcludedCommand.class) + ") " +aMessage);
    	if (shouldInstantiate(AddedCommandToPredictionQueue.class)) {
    	AddedCommandToPredictionQueue retVal = new AddedCommandToPredictionQueue(aMessage, aCommand, aQueue,  aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(AddedCommandToPredictionQueue.class, aMessage);


    	return null;
    }
    public static AddedCommandToPredictionQueue newCase (EHICommand aCommand, String aQueue,  Object aFinder) {
    	String aMessage = toString(aCommand, aQueue);
    	return newCase(aMessage, aCommand, aQueue, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aCommandName, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
