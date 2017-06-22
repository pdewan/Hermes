package util.trace.hermes.helpbutton;



import util.trace.TraceableInfo;

public class DifficultyUpdateForwardedToConnectionManager extends TraceableInfo {
	public DifficultyUpdateForwardedToConnectionManager(String aMessage, Object aFinder,
			String aJSONObject) {

		super(aMessage, aFinder);
	}
	public static DifficultyUpdateForwardedToConnectionManager newCase(			
			Object aFinder,
			String aJSONObject
			) { 
		
		DifficultyUpdateForwardedToConnectionManager retVal = new DifficultyUpdateForwardedToConnectionManager(
				aJSONObject, aFinder, aJSONObject);
				
    	retVal.announce();
    	return retVal;
	}
}
