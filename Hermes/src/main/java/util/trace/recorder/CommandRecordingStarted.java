package util.trace.recorder;

import util.trace.TraceableInfo;
import util.trace.Tracer;

public class CommandRecordingStarted extends TraceableInfo{
	long startTimestamp;
	public CommandRecordingStarted(String aMessage, long mStartTimestamp,  Object aFinder) {
		 super(aMessage, aFinder);
		 startTimestamp = mStartTimestamp;
	}
	public long getStartTimestamp() {
		return startTimestamp;
	}
	
	
   
    public static CommandRecordingStarted newCase (String aMessage, long aStartTimestamp,  Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(LogFileCreated.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(LogFileCreated.class) + ") " +aMessage);
    	if (shouldInstantiate(CommandRecordingStarted.class)) {
    	CommandRecordingStarted retVal = new CommandRecordingStarted(aMessage, aStartTimestamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);

    	return null;
    }
    public static CommandRecordingStarted newCase (long aStartTimestamp,  Object aFinder) {
    	String aMessage = "Start time:" + aStartTimestamp;
    	return newCase(aMessage, aStartTimestamp, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aFileName, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
