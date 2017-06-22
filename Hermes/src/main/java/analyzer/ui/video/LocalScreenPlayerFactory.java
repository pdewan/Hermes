package analyzer.ui.video;

public class LocalScreenPlayerFactory {
	static LocalScreenRecorderAndPlayer singleton;
	public static void createSingleton() {
//		singleton = new AMultiLevelAggregator();
//		singleton = new ALocalScreenRecorderAndPlayer();
		singleton = new ALocalScreenPlayer();

	}
	
	public static LocalScreenRecorderAndPlayer getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}


}
