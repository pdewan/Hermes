package util.trace.recorder;

import util.trace.TraceableInfo;
import util.trace.Tracer;

public class NewFileSnapshot extends TraceableInfo{
	String fileName;
	String fileContents;
	public NewFileSnapshot(String aMessage, String aFileName, String aFileContents,  Object aFinder) {
		 super(aMessage, aFinder);
		 fileName = aFileName;
		 fileContents = aFileContents;
	}
	public String getFileName() {
		return fileName;
	}
	public String getFileContents() {
		return fileName;
	}
	
	
    public static String toString(String aFileName, String aFileContents) {
    	return("(" + 
    				aFileName + "," + aFileContents.substring(0, Math.min(60, aFileContents.length())) + "..." +
    				")");
    }
    public static NewFileSnapshot newCase (String aMessage, String aFileName,  String aFileContents, Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(NewFileSnapshot.class))
//      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(NewFileSnapshot.class) + ") " +aMessage);
    	if (shouldInstantiate(NewFileSnapshot.class)) {
    	NewFileSnapshot retVal = new NewFileSnapshot(aMessage, aFileName, aFileContents, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(NewFileSnapshot.class, aMessage);


    	return null;
    }
    public static NewFileSnapshot newCase (String aFileName,  String aFileContents,  Object aFinder) {
    	String aMessage = toString(aFileName, aFileContents);
    	return newCase(aMessage, aFileName, aFileContents, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aFileName, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
