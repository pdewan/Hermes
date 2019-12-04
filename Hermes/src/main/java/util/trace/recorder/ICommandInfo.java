package util.trace.recorder;

import java.util.Calendar;

import fluorite.commands.EHICommand;
import util.trace.TraceableInfo;

public class ICommandInfo extends TraceableInfo{
	EHICommand command;
	public ICommandInfo(String aMessage, EHICommand aCommand, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aFinder);
		 command = aCommand;
	}
	public EHICommand getCommand() {
		return command;
	}
	
	
    public static String toString(EHICommand aCommand, int aCommandNumber, long aStartTimeStamp) {
    	long timestamp = Calendar.getInstance().getTime().getTime();
		timestamp -= aStartTimeStamp;
    	return  
    				aCommandNumber + ":" + aCommand + "(" + aStartTimeStamp + "," + timestamp + ")";
    				
    }
    
}
