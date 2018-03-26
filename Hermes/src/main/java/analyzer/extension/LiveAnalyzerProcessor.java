package analyzer.extension;

import analyzer.ParticipantTimeLine;

public interface LiveAnalyzerProcessor extends RatioFileGenerator{
	ParticipantTimeLine getParticipantTimeLine();

}
