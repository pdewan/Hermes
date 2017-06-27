package util.trace.hermes.timetracker;



import java.util.Date;

import util.trace.TraceableInfo;

public class ActivityDetected extends TraceableInfo {
	public ActivityDetected(String aMessage, long aTimestamp, Object aFinder
			) {
		super(aMessage, aFinder);
	}
	public static ActivityDetected newCase(			
			Object aFinder,
			long aTimeStamp
			) { 
//		long aTimeStamp = aStartTimestamp + aLastCommandTimestamp;
		Date aDate = new Date(aTimeStamp);
		String aMessage = "(" + aDate.toString() + "," + aTimeStamp + ")";
		ActivityDetected retVal = new ActivityDetected(aMessage,
				aTimeStamp,  aFinder);				
    	retVal.announce();
    	return retVal;
	}
}
