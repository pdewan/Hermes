package util.trace.hermes.workspacelistener;



import util.trace.TraceableInfo;

public class FileForwardedToConnectionManager extends TraceableInfo {
	public FileForwardedToConnectionManager(String aMessage, Object aFinder,
			String aJSONObject) {

		super(aMessage, aFinder);
	}
	public static FileForwardedToConnectionManager newCase(			
			Object aFinder,
			String aJSONObject
			) { 
		
		FileForwardedToConnectionManager retVal = new FileForwardedToConnectionManager(
				aJSONObject, aFinder, aJSONObject);
				
    	retVal.announce();
    	return retVal;
	}
}
