package analyzer;

import org.joda.time.DateTime;

import fluorite.commands.EHICommand;

public class ADirectTimeStampComputer implements TimeStampComputer {
//	long startTimeStamp = 0;
	public ADirectTimeStampComputer() {
		
	}
	@Override
	public void reset() {
//		startTimeStamp = 0;
	}
	public long computeTimestamp(EHICommand aCommand) {
		
		return aCommand.getTimestamp();
			
	}
	public static String toDateString(long aTimeStamp) {
		DateTime dateTime = new DateTime(aTimeStamp);
		return dateTime.toString("MM-dd-yyyy H:mm:ss");
	}

}
