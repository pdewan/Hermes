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
public class RemoteFeatureExtractorFactory {
	static RemoteFeatureExtractor singleton;
	public static void createSingleton() {
		singleton = new ARemoteFeatureExtractor(DifficultyRobot.getInstance());
	}
	
	public static RemoteFeatureExtractor getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}
}
