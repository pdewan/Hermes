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
public class RemotePredictionManagerFactory {
	static RemotePredictionManager singleton;
//	public static void createSingleton() {
//		singleton = new ARemotePredictionManager(DifficultyRobot.getInstance());
//	}
//	
//	public static RemotePredictionManager getSingleton() {
//		if (singleton == null)
//			createSingleton();
//		return singleton;
//	}
}
