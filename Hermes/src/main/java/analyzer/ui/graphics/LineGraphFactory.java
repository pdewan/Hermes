package analyzer.ui.graphics;

import analyzer.ui.SessionPlayerFactory;

public class LineGraphFactory {
	static LineGraph singleton;
	public static void createSingleton() {
//		boundsOutputter = new ADisplayBoundsPiper();
		singleton = new ALineGraph(SessionPlayerFactory.getSingleton(), new ARatioFileReader());

	}
	public static LineGraph getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}

}
