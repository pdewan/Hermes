package analyzer;

import org.joda.time.DateTime;

import fluorite.commands.EHICommand;

public class AResettingTimeStampComputer implements TimeStampComputer {
	
	long startTimeStamp = 0;
	public AResettingTimeStampComputer() {
		
	}
	@Override
	public void reset() {
		startTimeStamp = 0;
	}
	// This is confusing, is this teh relative or absolute time stamp. When called by performFeatureExtraction
	// it seems to be relative. When called by others, not sure
	public long computeTimestamp(EHICommand aCommand) {
		long aTimeStamp2 = aCommand.getTimestamp2();
		if (aTimeStamp2 > startTimeStamp) {
//				System.out.println ("TS 2 " + aTimeStamp2);			
		
			startTimeStamp = aTimeStamp2;
			System.out.println("time stamp 2 " + aTimeStamp2 + " > start time " + startTimeStamp);
		}
		return aCommand.getTimestamp() + startTimeStamp;
			
	}
	public static String toDateString(long aTimeStamp) {
		DateTime dateTime = new DateTime(aTimeStamp);
		return dateTime.toString("MM-dd-yyyy H:mm:ss");
	}

}
