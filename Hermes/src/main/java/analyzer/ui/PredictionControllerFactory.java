package analyzer.ui;


public class PredictionControllerFactory {
	static PredictionController singleton;
	public static void createSingleton() {
		singleton = new APredictionController();
	}	
	public static PredictionController getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}
}
