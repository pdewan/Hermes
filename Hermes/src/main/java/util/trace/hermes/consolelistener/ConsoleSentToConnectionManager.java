package util.trace.hermes.consolelistener;



import util.trace.TraceableInfo;

public class ConsoleSentToConnectionManager extends TraceableInfo {
	public ConsoleSentToConnectionManager(String aMessage, Object aFinder,
			String aJSONObject) {

		super(aMessage, aFinder);
	}
	public static ConsoleSentToConnectionManager newCase(			
			Object aFinder,
			String aJSONObject
			) { 
		
		ConsoleSentToConnectionManager retVal = new ConsoleSentToConnectionManager(
				aJSONObject, aFinder, aJSONObject);
				
    	retVal.announce();
    	return retVal;
	}
}
