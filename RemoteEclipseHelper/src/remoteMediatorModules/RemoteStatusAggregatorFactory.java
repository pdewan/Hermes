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
public class RemoteStatusAggregatorFactory {
	static RemoteStatusAggregator singleton;
//	public static void createSingleton() {
//		singleton = new ARemoteStatusManager(DifficultyRobot.getInstance());
//	}
//	
//	public static RemoteStatusManager getSingleton() {
//		if (singleton == null)
//			createSingleton();
//		return singleton;
//	}
}
