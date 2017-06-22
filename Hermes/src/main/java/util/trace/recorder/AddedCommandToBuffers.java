package util.trace.recorder;

import org.apache.commons.configuration.ConfigurationFactory.AdditionalConfigurationData;

import edu.cmu.scs.fluorite.commands.ICommand;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class AddedCommandToBuffers extends TraceableInfo{
	ICommand command;
	public AddedCommandToBuffers(String aMessage, ICommand aCommand, String aDocNorNormal, String aDocAndNormal,  Object aFinder) {
		 super(aMessage, aFinder);
		 command = aCommand;
	}
	public ICommand getCommand() {
		return command;
	}
	
	
    public static String toString(ICommand aCommand, String aDocOrNormal, String aDocAndNormal) {
    	return  
    				aCommand + "," + aCommand.getTimestamp() + "->" + "(" + aDocOrNormal + "," + aDocAndNormal + ")";
    				
    }
    public static AddedCommandToBuffers newCase (String aMessage, ICommand aCommand, String aDocOrNormal,   String aDocAndNormal,  Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(ExcludedCommand.class))
//      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(ExcludedCommand.class) + ") " +aMessage);
    	if (shouldInstantiate(AddedCommandToBuffers.class)) {
    	AddedCommandToBuffers retVal = new AddedCommandToBuffers(aMessage, aCommand, aDocOrNormal, aDocAndNormal,  aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(AddedCommandToBuffers.class, aMessage);


    	return null;
    }
    public static AddedCommandToBuffers newCase (ICommand aCommand, String aDocOrNormal,  String aDocAndNormal,  Object aFinder) {
    	String aMessage = toString(aCommand, aDocOrNormal, aDocAndNormal);
    	return newCase(aMessage, aCommand, aDocOrNormal, aDocAndNormal, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aCommandName, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
