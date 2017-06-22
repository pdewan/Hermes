package analyzer.extension;

public class LiveAnalyzerProcessorFactory {
	static LiveAnalyzerProcessor singleton;
	public static void createSingleton() {
//		singleton = new AMultiLevelAggregator();
		singleton = new ALiveAnalyzerProcessor();

	}
	
	public static LiveAnalyzerProcessor getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}

}
