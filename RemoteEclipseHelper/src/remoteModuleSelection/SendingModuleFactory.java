/**
 * @author Nils Persson
 * @date 2018-Apr-19 8:27:21 PM 
 */
package remoteModuleSelection;

import difficultyPrediction.Mediator;
import remoteMediatorModules.ARemoteEventAggregator;
import remoteMediatorModules.RemoteEventAggregator;

/**
 * creates and returns singleton object for keeping track
 * of the module that sends data over the xmpp connection
 */
public class SendingModuleFactory {
	static SendingModule singleton;
	public static void createSingleton() {
		singleton = new ASendingModule();
	}
	
	public static SendingModule getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}
}
