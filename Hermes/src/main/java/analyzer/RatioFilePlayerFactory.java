package analyzer;

public class RatioFilePlayerFactory {
	static RatioFilePlayer singleton;
	public static RatioFilePlayer getSingleton() {
		if (singleton == null) {
			singleton = new ARatioFileReplayer();
		}
		return singleton;
	}

}
