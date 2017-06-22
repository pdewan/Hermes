package util.trace.difficultyPrediction.notification;


import difficultyPrediction.statusManager.StatusListener;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class RegisteredStatusListener extends TraceableInfo{
//	ICommand command;
	public RegisteredStatusListener(String aMessage,  StatusListener aListener, Object aFinder) {
		 super(aMessage,aFinder);
//		 command = aCommand;
	}
	
    public static String toString(StatusListener aListener) {
    	
    	return  
    				 "" + aListener;
    				
    }
    public static RegisteredStatusListener newCase (String aMessage, StatusListener aListener,  Object aFinder) {

    	if (shouldInstantiate(RegisteredStatusListener.class)) {
    	RegisteredStatusListener retVal = new RegisteredStatusListener(aMessage,  aListener,  aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(RegisteredStatusListener.class, aMessage);


    	return null;
    }
    public static RegisteredStatusListener newCase ( StatusListener aListener,  Object aFinder) {
    	String aMessage = toString(aListener);
    	return newCase(aMessage, aListener, aFinder);

    }
}
