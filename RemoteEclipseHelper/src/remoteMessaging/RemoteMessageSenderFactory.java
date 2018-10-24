/**
 * @author Nils Persson
 * @date 2018-Mar-29 9:20:17 PM 
 */
package remoteMessaging;

import difficultyPrediction.Mediator;
import remoteMediatorModules.ARemoteEventAggregator;
import remoteMediatorModules.RemoteEventAggregator;

/**
 * 
 */
public class RemoteMessageSenderFactory {
	static RemoteMessageSender singleton;
	public static void createSingleton() {
		singleton = new ARemoteMessageSender();
	}
	
	public static RemoteMessageSender getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}
}
