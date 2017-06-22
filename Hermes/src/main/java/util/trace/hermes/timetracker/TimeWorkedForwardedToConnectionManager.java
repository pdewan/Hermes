package util.trace.hermes.timetracker;



import util.trace.TraceableInfo;

public class TimeWorkedForwardedToConnectionManager extends TraceableInfo {
	public TimeWorkedForwardedToConnectionManager(String aMessage, Object aFinder,
			String aJSONObject) {

		super(aMessage, aFinder);
	}
	public static TimeWorkedForwardedToConnectionManager newCase(			
			Object aFinder,
			String aJSONObject
			) { 
		
		TimeWorkedForwardedToConnectionManager retVal = new TimeWorkedForwardedToConnectionManager(
				aJSONObject, aFinder, aJSONObject);
				
    	retVal.announce();
    	return retVal;
	}
}
