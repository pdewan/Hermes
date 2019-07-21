package analyzer.extension;

import analyzer.AnalyzerFactory;
import difficultyPrediction.DifficultyPredictionSettings;
/**
 * Subclasses by RationFileReader so this is not really live, it has the option of being live
 * @author dewan
 *
 */
public class ALiveRatioFileGenerator extends ARatioFileGenerator implements LiveAnalyzerProcessor {
    public static final String LIVE_USER_NAME = "Live User";
    public static final String DUMMY_FOLDER_NAME = "Live Dummy Folder";
    public ALiveRatioFileGenerator() {
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
