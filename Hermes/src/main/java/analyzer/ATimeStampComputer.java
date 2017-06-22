package analyzer;

import org.joda.time.DateTime;

import fluorite.commands.EHICommand;

public class ATimeStampComputer implements TimeStampComputer {
	long startTimeStamp = 0;
	public ATimeStampComputer() {
		
	}
	@Override
	public void reset() {
		startTimeStamp = 0;
	}
	public long computeTimestamp(EHICommand aCommand) {
		long aTimeStamp2 = aCommand.getTimestamp2();
		if (aTimeStamp2 > startTimeStamp) {
//				System.out.println ("TS 2 " + aTimeStamp2);			
		
			startTimeStamp = aTimeStamp2;
		}
		return aCommand.getTimestamp() + startTimeStamp;
			
	}
	public static String toDateString(long aTimeStamp) {
		DateTime dateTime = new DateTime(aTimeStamp);
		return dateTime.toString("MM-dd-yyyy H:mm:ss");
	}

}
