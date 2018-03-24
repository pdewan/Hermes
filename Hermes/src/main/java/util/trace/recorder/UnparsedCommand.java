package util.trace.recorder;

import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.commands.ICommand;
import fluorite.commands.EHICommand;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class UnparsedCommand extends ICommandInfo{
	public UnparsedCommand(String aMessage, EHICommand aCommand,  String anElement,  Object aFinder) {
		 super(aMessage, aCommand, 0, aFinder);
	}	
    
    public static UnparsedCommand newCase (String aMessage, EHICommand aCommand, String anElement,  Object aFinder) {

    	if (shouldInstantiate(UnparsedCommand.class)) {
    	UnparsedCommand retVal = new UnparsedCommand(aMessage, aCommand, anElement, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(UnparsedCommand.class, aMessage);


    	return null;
    }
    public static UnparsedCommand newCase (EHICommand aCommand, String anElement, Object aFinder) {
    	String aMessage = anElement + "<-" + aCommand;
    	return newCase(aMessage, aCommand,  anElement, aFinder);

    }
}
