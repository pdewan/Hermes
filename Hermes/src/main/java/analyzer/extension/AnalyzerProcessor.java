package analyzer.extension;

import analyzer.AnalyzerListener;
import analyzer.ParticipantTimeLine;
import difficultyPrediction.DifficultyPredictionEventListener;

public interface AnalyzerProcessor extends AnalyzerListener, DifficultyPredictionEventListener {
	ParticipantTimeLine getParticipantTimeLine();


}
