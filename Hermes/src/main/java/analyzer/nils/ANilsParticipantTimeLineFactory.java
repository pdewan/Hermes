/**
 * @author Nils Persson
 * @date 2018-Oct-24 6:15:36 PM 
 */
package analyzer.nils;

import analyzer.ParticipantTimeLine;
import analyzer.ParticipantTimeLineFactory;

/**
 * 
 */
public class ANilsParticipantTimeLineFactory implements ParticipantTimeLineFactory {

	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLineFactory#createParticipantTimeLine()
	 */
	@Override
	public NilsParticipantTimeLine createParticipantTimeLine() {
		System.out.println("************* NILS PARTICIPANT FACTORY SET ****************");
		return new ANilsParticipantTimeLine();
	}

}
