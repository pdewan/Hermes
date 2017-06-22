package analyzer.extension;

public class FileReplayAnalyzerProcessorFactory {
	static LiveAnalyzerProcessor singleton;
	public static void createSingleton() {
//		singleton = new AMultiLevelAggregator();
		singleton = new AFileReplayAnalyzerProcessor();

	}
	
	public static LiveAnalyzerProcessor getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}

}
