package util.trace.hermes.timetracker;



import java.util.Date;

import util.trace.TraceableInfo;

public class ActivitySessionStarted extends TraceableInfo {
	public ActivitySessionStarted(String aMessage, long aTimestamp, Object aFinder
			) {
		super(aMessage, aFinder);
	}
	public static ActivitySessionStarted newCase(			
			Object aFinder,
			long aTimestamp
			) { 
//		long aTimeStamp = aStartTimestamp + aLastCommandTimestamp;
		Date aDate = new Date(aTimestamp);
		String aMessage = "(" + aDate.toString() + "," + aTimestamp + ")";
		ActivitySessionStarted retVal = new ActivitySessionStarted(aMessage,
				aTimestamp,  aFinder);				
    	retVal.announce();
    	return retVal;
	}
}
