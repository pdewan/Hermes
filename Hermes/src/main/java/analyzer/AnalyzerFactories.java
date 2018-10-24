package analyzer;

public class AnalyzerFactories {
	static ParticipantTimeLineFactory participantTimeLineFactory = new AParticipantTimelineFactory();
	public static ParticipantTimeLineFactory getParticipantTimeLineFactory() {
		return participantTimeLineFactory;
	}
	public static void setParticipantTimeLineFactory(ParticipantTimeLineFactory participantTimeLineFactory) {
		AnalyzerFactories.participantTimeLineFactory = participantTimeLineFactory;
	}
	public static ParticipantTimeLine createParticipantTimeLine() {
		return participantTimeLineFactory.createParticipantTimeLine();
	}

}
