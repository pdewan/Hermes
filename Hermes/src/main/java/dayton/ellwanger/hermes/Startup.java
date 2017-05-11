package dayton.ellwanger.hermes;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.editors.text.EditorsUI;

import dayton.ellwanger.hermes.preferences.Preferences;
import dayton.ellwanger.hermes.xmpp.ConnectionManager;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		ExtensionManager.earlyStartExtensions();
		if(EditorsUI.getPreferenceStore().getBoolean(Preferences.AUTOLOGIN)) {
			ConnectionManager.getInstance().doConnect();
		}
	}

}
