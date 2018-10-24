/**
 * @author Nils Persson
 * @date 2018-Apr-19 8:26:39 PM 
 */
package remoteModuleSelection;

/**
 * 
 */
public interface SendingModule {
	public void setModule(MediatorModule mod);
	public MediatorModule getModule();
}
