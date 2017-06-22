package util.trace.recorder;

import java.util.Calendar;

import org.apache.commons.configuration.ConfigurationFactory.AdditionalConfigurationData;

import edu.cmu.scs.fluorite.commands.ICommand;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class ICommandInfo extends TraceableInfo{
	ICommand command;
	public ICommandInfo(String aMessage, ICommand aCommand, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aFinder);
		 command = aCommand;
	}
	public ICommand getCommand() {
		return command;
	}
	
	
    public static String toString(ICommand aCommand, long aStartTimeStamp) {
    	long timestamp = Calendar.getInstance().getTime().getTime();
		timestamp -= aStartTimeStamp;
    	return  
    				aCommand + "(" + aStartTimeStamp + "," + timestamp + ")";
    				
    }
    
}
