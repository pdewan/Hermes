package context.recording;

import analyzer.ui.video.ALocalScreenRecorderAndPlayer;

public class RecorderFactory {
	static DisplayBoundsOutputter singleton;
	public static void createSingleton() {
//		boundsOutputter = new ALocalScreenRecorderAndPlayer();
//		singleton = new ADisplayBoundsFileWriter();
		singleton = new ADisplayBoundsPiper();
		
		// do not connect by default
//		boundsOutputter.connectToDisplayAndRecorder();
	}
	public static DisplayBoundsOutputter getSingleton() {
		if (singleton == null)
			createSingleton();
		return singleton;
	}

}