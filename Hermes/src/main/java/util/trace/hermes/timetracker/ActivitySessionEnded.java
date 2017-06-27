package util.trace.hermes.timetracker;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DurationFormatUtils;

import util.trace.TraceableInfo;

public class ActivitySessionEnded extends TraceableInfo {
	public ActivitySessionEnded(String aMessage, long aTimestamp, long aDuration, Object aFinder
			) {
		super(aMessage, aFinder);
	}
	public static ActivitySessionEnded newCase(			
			Object aFinder,
			long aTimestamp,
			long aDuration
			) { 
//		long aTimeStamp = aStartTimestamp + aLastCommandTimestamp;
		Date aDate = new Date(aTimestamp);
//		DurationFormatUtils.formatDuration(timeInMS, "HH:mm:ss,SSS");
		// New date object from millis
		Date aDurationDate = new Date(aDuration);
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		String formatted = df.format(aDurationDate);
		String aMessage = "(" + aDate.toString() + "," + formatted + ")";
		ActivitySessionEnded retVal = new ActivitySessionEnded(aMessage,
				aTimestamp, aDuration,  aFinder);				
    	retVal.announce();
    	return retVal;
	}
}
