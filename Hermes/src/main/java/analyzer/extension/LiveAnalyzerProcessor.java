package analyzer.extension;

import analyzer.ParticipantTimeLine;

public interface LiveAnalyzerProcessor extends AnalyzerProcessor{
	ParticipantTimeLine getParticipantTimeLine();

}
