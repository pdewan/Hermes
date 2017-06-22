package difficultyPrediction.web;

public class WebBrowserAccessorFactory {
	static WebBrowserAccessor singleton;
	public static void createSingleton() {
//		boundsOutputter = new ADisplayBoundsPiper();
		singleton = new AWebBrowserAccessor();

	}
	public static WebBrowserAccessor getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}

}
