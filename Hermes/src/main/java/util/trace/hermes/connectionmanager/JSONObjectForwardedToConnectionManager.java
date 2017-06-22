package util.trace.hermes.connectionmanager;



import util.trace.TraceableInfo;

public class JSONObjectForwardedToConnectionManager extends TraceableInfo {
	public JSONObjectForwardedToConnectionManager(String aMessage, Object aFinder,
			String aJSONObject) {

		super(aMessage, aFinder);
	}
	public static JSONObjectForwardedToConnectionManager newCase(			
			Object aFinder,
			String aJSONObject
			) { 
		
		JSONObjectForwardedToConnectionManager retVal = new JSONObjectForwardedToConnectionManager(
				aJSONObject, aFinder, aJSONObject);
				
    	retVal.announce();
    	return retVal;
	}
}
