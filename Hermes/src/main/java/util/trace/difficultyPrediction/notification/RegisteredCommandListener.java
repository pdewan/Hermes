package util.trace.difficultyPrediction.notification;

import difficultyPrediction.PluginEventListener;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class RegisteredCommandListener extends TraceableInfo{
//	ICommand command;
	public RegisteredCommandListener(String aMessage,  PluginEventListener aListener, Object aFinder) {
		 super(aMessage,aFinder);
//		 command = aCommand;
	}
	
    public static String toString(PluginEventListener aListener) {
    	
    	return  
    				 "" + aListener;
    				
    }
    public static RegisteredCommandListener newCase (String aMessage, PluginEventListener aListener,  Object aFinder) {

    	if (shouldInstantiate(RegisteredCommandListener.class)) {
    	RegisteredCommandListener retVal = new RegisteredCommandListener(aMessage,  aListener,  aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(RegisteredCommandListener.class, aMessage);


    	return null;
    }
    public static RegisteredCommandListener newCase ( PluginEventListener aListener,  Object aFinder) {
    	String aMessage = toString(aListener);
    	return newCase(aMessage, aListener, aFinder);

    }
}
