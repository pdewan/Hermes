/**
 * @author Nils Persson
 * @date 2018-Apr-19 8:27:02 PM 
 */
package remoteModuleSelection;

import difficultyPrediction.eventAggregation.AnEventAggregatorDetails;

/**
 * Class to keep track of the module sending data over xmpp connection
 */
public class ASendingModule implements SendingModule{
	private MediatorModule module;
	
	public void setModule(MediatorModule mod){
		module = mod;
	}
	
	public MediatorModule getModule(){
		return module;
	} 
}
