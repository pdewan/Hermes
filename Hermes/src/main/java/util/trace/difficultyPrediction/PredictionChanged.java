package util.trace.difficultyPrediction;

import util.trace.TraceableInfo;
import util.trace.Tracer;

public class PredictionChanged extends TraceableInfo{
	String prediction;
	String data;
	public PredictionChanged(String aMessage, String aPrediction, String aData,  Object aFinder) {
		 super(aMessage, aFinder);
		 prediction = aPrediction;
		 data = aData;
	}
	public String getPrediction() {
		return prediction;
	}
	public String getData() {
		return data;
	}
	
	
    public static String toString(String aPrediction, String aData) {
    	return("(" + 
    				aPrediction + "," + aData.substring(0, Math.min(60, aData.length())) + "..." +
    				")");
    }
    public static PredictionChanged newCase (String aMessage, String aPrediction,  String aData, Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(PredictionChanged.class))
//      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(PredictionChanged.class) + ") " +aMessage);
    	if (shouldInstantiate(PredictionChanged.class)) {
    	PredictionChanged retVal = new PredictionChanged(aMessage, aPrediction, aData, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(PredictionChanged.class, aMessage);


    	return null;
    }
    public static PredictionChanged newCase (String aPrediction,  String aData,  Object aFinder) {
    	String aMessage = toString(aPrediction, aData);
    	return newCase(aMessage, aPrediction, aData, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aPrediction, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
