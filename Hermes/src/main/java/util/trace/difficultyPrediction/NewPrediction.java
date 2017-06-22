package util.trace.difficultyPrediction;

import util.trace.TraceableInfo;
import util.trace.Tracer;

public class NewPrediction extends TraceableInfo{
	double prediction;
	public NewPrediction(String aMessage, double aPrediction,  Object aFinder) {
		 super(aMessage, aFinder);
		 prediction = aPrediction;
	}
	public double getFileName() {
		return prediction;
	}
	
	
    public static String toString(double aPrediction) {
    	return("(" + 
    				aPrediction + 
    				")");
    }
    public static NewPrediction newCase (String aMessage, double aPrediction,  Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(NewPrediction.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(NewPrediction.class) + ") " +aMessage);
    	if (shouldInstantiate(NewPrediction.class)) {
    	NewPrediction retVal = new NewPrediction(aMessage, aPrediction, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);

    	return null;
    }
    public static NewPrediction newCase (double aPrediction,  Object aFinder) {
    	String aMessage = toString(aPrediction);
    	return newCase(aMessage, aPrediction, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aPrediction, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
