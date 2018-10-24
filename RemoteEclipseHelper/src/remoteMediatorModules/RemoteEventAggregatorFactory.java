/**
 * @author Nils Persson
 * @date 2018-Apr-20 1:33:26 PM 
 */
package remoteMediatorModules;

import difficultyPrediction.DifficultyRobot;
import remoteMessaging.ARemoteMessageSender;
import remoteMessaging.RemoteMessageSender;

/**
 * 
 */
public class RemoteEventAggregatorFactory {
	static RemoteEventAggregator singleton;
	public static void createSingleton() {
		singleton = new ARemoteEventAggregator(DifficultyRobot.getInstance());
	}
	
	public static RemoteEventAggregator getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}
}
