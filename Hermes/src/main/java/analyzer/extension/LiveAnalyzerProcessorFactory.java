package analyzer.extension;

public class LiveAnalyzerProcessorFactory {
	static LiveAnalyzerProcessor singleton;
	public static void createSingleton() {
//		singleton = new AMultiLevelAggregator();
		singleton = new ALiveRatioFileGenerator();

	}
	
	public static LiveAnalyzerProcessor getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}

}
