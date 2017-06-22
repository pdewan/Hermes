package util.trace.hermes.helpbutton;



import util.trace.TraceableInfo;

public class HelpInformationForwardedToConnectionManager extends TraceableInfo {
	public HelpInformationForwardedToConnectionManager(String aMessage, Object aFinder,
			String aJSONObject) {

		super(aMessage, aFinder);
	}
	public static HelpInformationForwardedToConnectionManager newCase(			
			Object aFinder,
			String aJSONObject
			) { 
		
		HelpInformationForwardedToConnectionManager retVal = new HelpInformationForwardedToConnectionManager(
				aJSONObject, aFinder, aJSONObject);
				
    	retVal.announce();
    	return retVal;
	}
}
