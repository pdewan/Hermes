package analyzer;

public class AParticipantTimelineFactory implements ParticipantTimeLineFactory {
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLineFactory#createParticipantTimeLine()
	 */
	@Override
	public ParticipantTimeLine createParticipantTimeLine() {
		return new AParticipantTimeLine();
	}

}
