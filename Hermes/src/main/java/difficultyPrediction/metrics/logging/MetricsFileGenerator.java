package difficultyPrediction.metrics.logging;

import analyzer.AnalyzerListener;
import analyzer.ParticipantTimeLine;
import difficultyPrediction.DifficultyPredictionEventListener;
import difficultyPrediction.featureExtraction.RatioFeaturesListener;
import difficultyPrediction.statusManager.StatusListener;
import fluorite.model.RecorderListener;

public interface MetricsFileGenerator extends StatusListener, RatioFeaturesListener, RecorderListener {


}
