package util.trace.recorder;

import org.w3c.dom.Element;

import fluorite.commands.EHICommand;
import util.trace.Tracer;

public class ParsedCommand extends ICommandInfo{
	public ParsedCommand(String aMessage, EHICommand aCommand,  Element anElement,  Object aFinder) {
		 super(aMessage, aCommand, 0, aFinder);
	}	
    
    public static ParsedCommand newCase (String aMessage, EHICommand aCommand, Element anElement,  Object aFinder) {

    	if (shouldInstantiate(ParsedCommand.class)) {
    	ParsedCommand retVal = new ParsedCommand(aMessage, aCommand, anElement, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(ParsedCommand.class, aMessage);


    	return null;
    }
    public static ParsedCommand newCase (EHICommand aCommand, Element anElement, Object aFinder) {
    	String aMessage = anElement + "->" + aCommand;
    	return newCase(aMessage, aCommand,  anElement, aFinder);

    }
}
