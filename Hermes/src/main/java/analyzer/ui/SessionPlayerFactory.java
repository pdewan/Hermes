package analyzer.ui;

import analyzer.ui.graphics.RatioFileReader;

public class SessionPlayerFactory {
	static GeneralizedPlayAndRewindCounter singleton;
	public static void createSingleton() {
//		singleton = new AMultiLevelAggregator();
		singleton = new AGeneralizedPlayAndRewindCounter();

	}
	public static void createSingleton(RatioFileReader reader) {
		singleton = new AGeneralizedPlayAndRewindCounter(reader);

	}
	public static GeneralizedPlayAndRewindCounter getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}

}
