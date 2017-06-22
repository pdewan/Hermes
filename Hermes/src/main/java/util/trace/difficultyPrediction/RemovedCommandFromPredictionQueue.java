package util.trace.difficultyPrediction;

import edu.cmu.scs.fluorite.commands.ICommand;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class RemovedCommandFromPredictionQueue extends TraceableInfo{
	ICommand command;
	public RemovedCommandFromPredictionQueue(String aMessage, ICommand aCommand, String aPredictionQ, Object aFinder) {
		 super(aMessage, aFinder);
		 command = aCommand;
	}
	public ICommand getCommand() {
		return command;
	}
	
    public static String toString(ICommand aCommand, String aQueue) {
    	return  
    				aCommand.getClass().getSimpleName() + ":" + aCommand.getName() + "(" + aCommand.getTimestamp() + ")" +  "->" + "(" + aQueue + ")";
    				
    }
    public static RemovedCommandFromPredictionQueue newCase (String aMessage, ICommand aCommand, String aQueue,  Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(ExcludedCommand.class))
//      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(ExcludedCommand.class) + ") " +aMessage);
    	if (shouldInstantiate(RemovedCommandFromPredictionQueue.class)) {
    	RemovedCommandFromPredictionQueue retVal = new RemovedCommandFromPredictionQueue(aMessage, aCommand, aQueue,  aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(RemovedCommandFromPredictionQueue.class, aMessage);


    	return null;
    }
    public static RemovedCommandFromPredictionQueue newCase (ICommand aCommand, String aQueue,  Object aFinder) {
    	String aMessage = toString(aCommand, aQueue);
    	return newCase(aMessage, aCommand, aQueue, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aCommandName, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
