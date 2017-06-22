package util.trace.difficultyPrediction.notification;

import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.featureExtraction.RatioFeaturesListener;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class RegisteredRatioListener extends TraceableInfo{
//	ICommand command;
	public RegisteredRatioListener(String aMessage,  RatioFeaturesListener aListener, Object aFinder) {
		 super(aMessage,aFinder);
//		 command = aCommand;
	}
	
    public static String toString(RatioFeaturesListener aListener) {
    	
    	return  
    				 "" + aListener;
    				
    }
    public static RegisteredRatioListener newCase (String aMessage, RatioFeaturesListener aListener,  Object aFinder) {

    	if (shouldInstantiate(RegisteredRatioListener.class)) {
    	RegisteredRatioListener retVal = new RegisteredRatioListener(aMessage,  aListener,  aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(RegisteredRatioListener.class, aMessage);


    	return null;
    }
    public static RegisteredRatioListener newCase ( RatioFeaturesListener aListener,  Object aFinder) {
    	String aMessage = toString(aListener);
    	return newCase(aMessage, aListener, aFinder);

    }
}
