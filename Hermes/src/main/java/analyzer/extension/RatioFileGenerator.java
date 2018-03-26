package analyzer.extension;

import analyzer.AnalyzerListener;
import analyzer.ParticipantTimeLine;
import difficultyPrediction.DifficultyPredictionEventListener;

public interface RatioFileGenerator extends AnalyzerListener, DifficultyPredictionEventListener {
	ParticipantTimeLine getParticipantTimeLine();


}
