package analyzer.extension;

public class RepredictingAnalyzerProcessorFactory {
	static AnalyzerProcessor singleton;
	
	public static void setSingleton(AnalyzerProcessor singleton) {
		RepredictingAnalyzerProcessorFactory.singleton = singleton;
	}

	public static void createSingleton() {
		singleton = new AnAnalyzerProcessor();

	}
	
	public static AnalyzerProcessor getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}

}
