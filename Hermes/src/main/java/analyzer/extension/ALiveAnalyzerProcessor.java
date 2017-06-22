package analyzer.extension;

import difficultyPrediction.DifficultyPredictionSettings;
import analyzer.AnalyzerFactory;
import analyzer.ParticipantTimeLine;

public class ALiveAnalyzerProcessor extends AnAnalyzerProcessor implements LiveAnalyzerProcessor {
    public static final String LIVE_USER_NAME = "Live User";
    public static final String DUMMY_FOLDER_NAME = "Live Dummy Folder";
    public ALiveAnalyzerProcessor() {
    	if (DifficultyPredictionSettings.isReplayMode()) {
    		AnalyzerFactory.getSingleton().addAnalyzerListener(this);
    	} else {
    		newParticipant(LIVE_USER_NAME, DUMMY_FOLDER_NAME);
    	}
    }
//	@Override
//	public ParticipantTimeLine getParticipantTimeLine() {
//		return participantTimeLine;
//	}

}
