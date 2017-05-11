package dayton.ellwanger.hermes.ui;

import org.eclipse.swt.widgets.Display;

import dayton.ellwanger.hermes.xmpp.ConnectStateListener;
import dayton.ellwanger.hermes.xmpp.ConnectionManager;
import dayton.ellwanger.hermes.xmpp.ConnectionState;

public class StatusViewController implements ConnectButtonListener, ConnectStateListener {

	private StatusView view;
	private ConnectionManager connector;
	
	public StatusViewController(StatusView view) {
		this.view = view;
		view.addConnectListener(this);
		connector = ConnectionManager.getInstance();
		connector.addStateListener(this);
	}

	@Override
	public void connectButtonPressed() {
		connector.connectAction();
	}

	@Override
	public void stateChanged(ConnectionState newState) {
		Display.getDefault().asyncExec(new ViewUpdater(newState));
	}
	
	
	class ViewUpdater implements Runnable {
		
		ConnectionState state;
		
		public ViewUpdater(ConnectionState state) {
			this.state = state;
		}
		
		public void run() {
			view.setConnectButtonText(state.getOppositeAction());
		}
		
	}
	
}
