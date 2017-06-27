package util.trace.hermes.timetracker;



import java.util.Date;

import util.trace.TraceableInfo;

public class EclipeSessionStarted extends TraceableInfo {
	public EclipeSessionStarted(String aMessage, long aStartTimestamp, Object aFinder
			) {
		super(aMessage, aFinder);
	}
	public static EclipeSessionStarted newCase(			
			Object aFinder,
			long aStartTimestamp
			) { 
		Date aDate = new Date(aStartTimestamp);
		String aMessage = "(" + aDate.toString() + "," + aStartTimestamp + ")";
		EclipeSessionStarted retVal = new EclipeSessionStarted(aMessage,
				aStartTimestamp, aFinder);				
    	retVal.announce();
    	return retVal;
	}
}
