package util.trace.recorder;

import java.util.Calendar;

import org.apache.commons.configuration.ConfigurationFactory.AdditionalConfigurationData;

import edu.cmu.scs.fluorite.commands.ICommand;
import fluorite.commands.EHICommand;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class ICommandInfo extends TraceableInfo{
	EHICommand command;
	public ICommandInfo(String aMessage, EHICommand aCommand, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aFinder);
		 command = aCommand;
	}
	public EHICommand getCommand() {
		return command;
	}
	
	
    public static String toString(EHICommand aCommand, long aStartTimeStamp) {
    	long timestamp = Calendar.getInstance().getTime().getTime();
		timestamp -= aStartTimeStamp;
    	return  
    				aCommand + "(" + aStartTimeStamp + "," + timestamp + ")";
    				
    }
    
}
