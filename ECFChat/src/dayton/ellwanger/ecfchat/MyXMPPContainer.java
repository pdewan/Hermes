package dayton.ellwanger.ecfchat;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.internal.provider.xmpp.smack.ECFConnection;
import org.eclipse.ecf.provider.comm.ConnectionCreateException;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.xmpp.XMPPContainer;
import org.jivesoftware.smack.XMPPConnection;

/**
 * @since 3.0
 */
public class MyXMPPContainer extends XMPPContainer {

	XMPPConnection myXMPPConnection;
	private String hostname;
	
	public MyXMPPContainer(String hostname) throws Exception {
		super();
		this.hostname = hostname;
	}
	
	public void connect(ID remote, IConnectContext joinContext)
			throws ContainerConnectException {
		super.connect(remote, joinContext);
	}

	protected ISynchAsynchConnection createConnection(ID remoteSpace,
			Object data) throws ConnectionCreateException {
		final boolean google = isGoogle(remoteSpace);
		ECFConnection ecfConnection = new ECFConnection(google, getConnectNamespace(), receiver);
		return ecfConnection;
	}
}