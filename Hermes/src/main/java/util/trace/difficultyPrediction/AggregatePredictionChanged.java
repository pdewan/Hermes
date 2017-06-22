package util.trace.difficultyPrediction;

import util.trace.TraceableInfo;
import util.trace.Tracer;

public class AggregatePredictionChanged extends TraceableInfo{
	String prediction;
	String data;
	public AggregatePredictionChanged(String aMessage, String aPrediction, String aData,  Object aFinder) {
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
    public static AggregatePredictionChanged newCase (String aMessage, String aPrediction,  String aData, Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(AggregatePredictionChanged.class))
//      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(AggregatePredictionChanged.class) + ") " +aMessage);
    	if (shouldInstantiate(AggregatePredictionChanged.class)) {
    	AggregatePredictionChanged retVal = new AggregatePredictionChanged(aMessage, aPrediction, aData, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(AggregatePredictionChanged.class, aMessage);


    	return null;
    }
    public static AggregatePredictionChanged newCase (String aPrediction,  String aData,  Object aFinder) {
    	String aMessage = toString(aPrediction, aData);
    	return newCase(aMessage, aPrediction, aData, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aPrediction, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
