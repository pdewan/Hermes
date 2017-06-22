package util.trace.difficultyPrediction.notification;

import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.featureExtraction.RatioFeaturesListener;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class NotifiedRatiosToListener extends TraceableInfo{
//	ICommand command;
	public NotifiedRatiosToListener(String aMessage, RatioFeatures aRatioFeatures, RatioFeaturesListener aListener, Object aFinder) {
		 super(aMessage,aFinder);
//		 command = aCommand;
	}
	
    public static String toString(RatioFeatures aRatioFeatures, RatioFeaturesListener aListener) {
    	
    	return  
    				 aRatioFeatures + "->" +  aListener;
    				
    }
    public static NotifiedRatiosToListener newCase (String aMessage, RatioFeatures aRatioFeatures,  RatioFeaturesListener aListener,  Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(ExcludedCommand.class))
//      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(ExcludedCommand.class) + ") " +aMessage);
    	if (shouldInstantiate(NotifiedRatiosToListener.class)) {
    	NotifiedRatiosToListener retVal = new NotifiedRatiosToListener(aMessage, aRatioFeatures, aListener,  aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(NotifiedRatiosToListener.class, aMessage);


    	return null;
    }
    public static NotifiedRatiosToListener newCase ( RatioFeatures aRatioFeatures,  RatioFeaturesListener aListener,  Object aFinder) {
    	String aMessage = toString(aRatioFeatures, aListener);
    	return newCase(aMessage,aRatioFeatures, aListener, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aCommandName, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
