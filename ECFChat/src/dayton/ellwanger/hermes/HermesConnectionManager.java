package dayton.ellwanger.hermes;

import org.eclipse.swt.widgets.Display;

import dayton.ellwanger.ecfchat.ECFConnector;
import dayton.ellwanger.ecfchat.EditorSharer;
import dayton.ellwanger.hermes.preferences.Preferences;
import dayton.ellwanger.hermes.xmpp.ConnectStateListener;
import dayton.ellwanger.hermes.xmpp.ConnectionManager;
import dayton.ellwanger.hermes.xmpp.ConnectionState;
import dayton.ellwanger.hermes.xmpp.MessageListener;

public class HermesConnectionManager implements ConnectStateListener, MessageListener {
	
	EditorSharer editorSharer;
	ECFConnector connector;

	public HermesConnectionManager() {
		editorSharer = new EditorSharer();
		ConnectionManager.getInstance().addMessageListener(this);
	}
	
	@Override
	public void stateChanged(ConnectionState newState) {
		
		if(newState == ConnectionState.CONNECTED) {
			connect();
		}
		if(connector != null && newState == ConnectionState.DISCONNECTED) {
			
			connector.disconnect();
		}
	}
	
	private void connect() {
		connector = new ECFConnector();
		try {
			ConnectionManager connectionManager = ConnectionManager.getInstance();
			String xmppID = connectionManager.getXMPPID();
			//I don't know why ECF requires the ID to be in this format, but it does
			if(!connectionManager.isGoogle()) {
				xmppID += ";" + connectionManager.getHostname();
			}
			connector.connect(xmppID, connectionManager.getXMPPPassword(), connectionManager.getHostname());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void messageReceieved(String from, String message) {
		if(message.equalsIgnoreCase("share")) {
			// add dialogue to allow this
			//Do a check to make sure 'from' is an authorized user
			System.out.println("Share message");
			//System.out.println(connector.getEntryForUsername(Preferences.getPreference(Preferences.INSTRUCTOR)));
			System.out.println(connector.getEntryForUsername(from));
			//editorSharer.setShareWith(connector.getEntryForUsername(Preferences.getPreference(Preferences.INSTRUCTOR)));
			editorSharer.setShareWith(connector.getEntryForUsername(from));
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					editorSharer.shareEditor();
				}
			});
		}
	}

}
