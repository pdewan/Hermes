package util.trace.hermes.timetracker;



import java.util.Date;

import util.trace.TraceableInfo;

public class IdleCycleDetected extends TraceableInfo {
	public IdleCycleDetected(String aMessage, long aTimestamp, int aCycleNo, Object aFinder
			) {
		super(aMessage, aFinder);
	}
	public static IdleCycleDetected newCase(			
			Object aFinder,
			long aTimeStamp, 
			int aCycleNo
			) { 
//		long aTimeStamp = aStartTimestamp + aLastCommandTimestamp;
		Date aDate = new Date(aTimeStamp);
		Date aCurrentDate = new Date(System.currentTimeMillis());
		String aMessage = "(" + aCycleNo + "," + aDate.toString() + ".." + aCurrentDate + ")";
		IdleCycleDetected retVal = new IdleCycleDetected(aMessage,
				aTimeStamp,  aCycleNo, aFinder);				
    	retVal.announce();
    	return retVal;
	}
}
