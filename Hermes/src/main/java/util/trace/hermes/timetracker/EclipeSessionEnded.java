package util.trace.hermes.timetracker;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.trace.TraceableInfo;

public class EclipeSessionEnded extends TraceableInfo {
	public EclipeSessionEnded(String aMessage, long aTimeStamp, long aDuration, Object aFinder
			) {
		super(aMessage, aFinder);
	}
	public static EclipeSessionEnded newCase(			
			Object aFinder,
			long aTimestamp,
			long aDuration
			) { 
//		long aTimeStamp = aStartTimestamp + aLastCommandTimestamp;
		Date aDate = new Date(aTimestamp);
		Date aDurationDate = new Date(aDuration);
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		String formatted = df.format(aDurationDate);
		String aMessage = "(" + aDate.toString() + "," + formatted + ")";
		EclipeSessionEnded retVal = new EclipeSessionEnded(aMessage,
				aTimestamp, aDuration, aFinder);				
    	retVal.announce();
    	return retVal;
	}
}
