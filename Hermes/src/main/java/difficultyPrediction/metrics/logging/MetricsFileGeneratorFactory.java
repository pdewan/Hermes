package difficultyPrediction.metrics.logging;

public class MetricsFileGeneratorFactory {
	static MetricsFileGenerator singleton;
	
	public static void setSingleton(MetricsFileGenerator singleton) {
		MetricsFileGeneratorFactory.singleton = singleton;
	}

	public static void createSingleton() {
		singleton = new AMetricsFileGenerator();

	}
	
	public static MetricsFileGenerator getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}

}
