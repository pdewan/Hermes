package analyzer.extension;

public class RepredictingAnalyzerProcessorFactory {
	static RatioFileGenerator singleton;
	
	public static void setSingleton(RatioFileGenerator singleton) {
		RepredictingAnalyzerProcessorFactory.singleton = singleton;
	}

	public static void createSingleton() {
		singleton = new ARatioFileGenerator();

	}
	
	public static RatioFileGenerator getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}

}
