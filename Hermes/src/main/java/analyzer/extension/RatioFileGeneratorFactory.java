package analyzer.extension;

public class RatioFileGeneratorFactory {
	static RatioFileGenerator singleton;
	
	public static void setSingleton(RatioFileGenerator singleton) {
		RatioFileGeneratorFactory.singleton = singleton;
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
