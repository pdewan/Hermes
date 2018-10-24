/**
 * @author Nils Persson
 * @date 2018-Apr-19 9:08:54 PM 
 */
package remoteModuleSelection;

/**
 * Selector for factory of the remote module used to send data over xmpp
 */
public class RemoteModuleSelector {
	public static void setRemoteModule(MediatorModule mod){
		SendingModuleFactory.getSingleton().setModule(mod);
	}
}
