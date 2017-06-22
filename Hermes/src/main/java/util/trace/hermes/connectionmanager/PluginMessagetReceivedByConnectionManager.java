package util.trace.hermes.connectionmanager;



import util.trace.TraceableInfo;

public class PluginMessagetReceivedByConnectionManager extends TraceableInfo {
	public PluginMessagetReceivedByConnectionManager(String aMessage, Object aFinder,
			String aJSONObject) {

		super(aMessage, aFinder);
	}
	public static PluginMessagetReceivedByConnectionManager newCase(			
			Object aFinder,
			String aJSONObject
			) { 
		
		PluginMessagetReceivedByConnectionManager retVal = new PluginMessagetReceivedByConnectionManager(
				aJSONObject, aFinder, aJSONObject);
				
    	retVal.announce();
    	return retVal;
	}
}
