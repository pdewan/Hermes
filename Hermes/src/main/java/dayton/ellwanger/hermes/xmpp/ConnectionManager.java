package dayton.ellwanger.hermes.xmpp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.ui.editors.text.EditorsUI;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dayton.ellwanger.hermes.ExtensionManager;
import dayton.ellwanger.hermes.preferences.Preferences;
import hermes.proxy.JSONProxy;
import hermes.tags.Tags;
import util.trace.xmpp.XMPPPacketSent;


public class ConnectionManager implements 
	ConnectionListener, StanzaListener, StanzaFilter, MessageBusInternalRegisterer, Tags {
	
	protected static ConnectionManager instance;
	private AbstractXMPPConnection connection;
	private ConnectionState connectionState;
	private List<ConnectStateListener> stateListeners;
	private List<MessageListener> messageListeners;
	private String serverID;
	private String xmppID;
	private String internalXmppID;
	private String xmppUsername;
	private String internalXmppUsername;
	private String xmppPassword;
	private String hostname;

	protected List<MessageBusInternalRegistration> messageBusInternalRegistrations = new ArrayList();
	
	
	private ConnectionManager() {
		stateListeners = new LinkedList<ConnectStateListener>();
		messageListeners = new LinkedList<MessageListener>();
		connectionState = ConnectionState.DISCONNECTED;
	}
	
	public static ConnectionManager init() {
		instance = new ConnectionManager();
		ExtensionManager.initiateConnectionListeners();
		ExtensionManager.initiateMessageListeners();
		return instance;
	}
	
	public static ConnectionManager getInstance() {
		if(instance == null) {
			return init();
		} else {
			return instance;
		}
	}
	

	public void connectAction() {
		switch(connectionState) {
		case CONNECTED:
			disconnect();
			break;
		case DISCONNECTED:
			connect();
			break;
		default:
			break;
		}
	}
	
	public ConnectionState getConnectionState() {
		return connectionState;
	}
	
	public void doConnect() {
		if(connectionState == ConnectionState.DISCONNECTED) {
			connect();
		}
	}
	
	public void sendMessage(JSONObject messageData) {
		
		try {
			messageData.put("from", internalXmppID);
		} catch (Exception ex) {ex.printStackTrace();}

		//System.out.println(messageData.toString());
		if(connectionState == ConnectionState.CONNECTED) { 
			Message message = new Message(serverID);
			message.setBody(messageData.toString());
			try {
				System.out.println(message);
				XMPPPacketSent.newCase(this, message.toString());
				connection.sendStanza(message);
			} catch (NotConnectedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public String getInstructorID() {
		return serverID;
	}
	
	public String getXMPPID() {
		return xmppID;
	}
	
	public String getInternalXMPPID() {
		return internalXmppID;
	}
	
	public boolean isGoogle() {
		return Preferences.getPreference(Preferences.DOMAIN).toLowerCase().equals("gmail");
	}
	
	//Here for chat plugins, like ECF, to set up their own connection to manage chats
	public String getXMPPPassword() {
		return xmppPassword;
	}
	
	public String getHostname() {
		return hostname;
	}

	private void createAccount() {
		try {
			AccountManager.sensitiveOperationOverInsecureConnectionDefault(true);
			AccountManager accountManager = AccountManager.getInstance(connection);
			accountManager.createAccount(this.xmppUsername, this.xmppPassword);
			accountManager.createAccount(this.internalXmppUsername, this.xmppPassword);
			EditorsUI.getPreferenceStore().setValue(Preferences.CREATE, false);
		} catch (XMPPErrorException xmppErrorException) {
			if(xmppErrorException.getXMPPError().getCondition() == XMPPError.Condition.conflict) {
				//Need a different username
			} 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void connect() {
		setState(ConnectionState.CONNECTING);
		//Needs to be true, or when ECF chat message begins, error is thrown
		JivePropertiesManager.setJavaObjectEnabled(true);
		serverID = Preferences.getPreference(Preferences.MESSAGE_BUS);
		//usernames must be all lower-space
		xmppUsername = Preferences.getPreference(Preferences.USERNAME).toLowerCase();
		internalXmppUsername = xmppUsername + "_internal";
		String domain = Preferences.getPreference(Preferences.DOMAIN).toLowerCase();
		xmppID = xmppUsername + "@" + domain;
		internalXmppID = internalXmppUsername + "@" + domain;
		hostname = Preferences.getPreference(Preferences.HOST);
		xmppPassword = Preferences.getPreference(Preferences.PASSWORD);
		try {
			XMPPTCPConnectionConfiguration.Builder configBuilder = 
					XMPPTCPConnectionConfiguration.builder();
			configBuilder.setServiceName(domain);
			configBuilder.setHost(hostname);
			if(!EditorsUI.getPreferenceStore().getBoolean(Preferences.SECURITY))
				configBuilder.setSecurityMode(SecurityMode.disabled);
			connection = new XMPPTCPConnection(configBuilder.build());
			connection.addConnectionListener(this);
			connection.connect();
			if(EditorsUI.getPreferenceStore().getBoolean(Preferences.CREATE))
				createAccount();
			connection.login(internalXmppUsername, xmppPassword);
			System.out.println("XMPP Connected");
			setState(ConnectionState.CONNECTED);
			connection.addSyncStanzaListener(this, this);
		} catch (Exception ex) {
			ex.printStackTrace();
			setState(ConnectionState.DISCONNECTED);
		}
	}
	
	private void disconnect() {
		setState(ConnectionState.DISCONNECTING);
		connection.disconnect();
	}
	
	private void setState(ConnectionState state) {
		connectionState = state;
		alertStateListeners();
	}
	
	private void alertStateListeners() {
		for(ConnectStateListener l : stateListeners) {
			l.stateChanged(connectionState);
		}
	}
	
	public void addStateListener(ConnectStateListener l) {
		stateListeners.add(l);
		l.stateChanged(connectionState);
	}
	
	public void removeStateListener(ConnectStateListener l) {
		stateListeners.remove(l);
	}
	
	public void addMessageListener(MessageListener l) {
		messageListeners.add(l);
	}
	
	public void removeMessageListener(MessageListener l) {
		messageListeners.remove(l);
	}
	
	public void alertMessageListeners(String from, String message) {
		for(MessageListener l : messageListeners) {
			l.messageReceieved(from, message);
		}
	}
	
	@Override
	public void connectionClosed() {
		setState(ConnectionState.DISCONNECTED);
	}

	@Override
	public void connectionClosedOnError(Exception arg0) {
		setState(ConnectionState.DISCONNECTED);
	}

	@Override
	public void reconnectingIn(int arg0) {
		setState(ConnectionState.CONNECTING);
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
		setState(ConnectionState.DISCONNECTED);
	}

	@Override
	public void reconnectionSuccessful() {
		//setState(ConnectionState.CONNECTED);
	}

	@Override
	public void connected(XMPPConnection connection) {
		//setState(ConnectionState.CONNECTED);
	}

	@Override
	public void authenticated(XMPPConnection connection, boolean resumed) {}
	
	@Override
	public boolean accept(Stanza stanza) {
		return stanza instanceof Message;
	}
	
	@Override
	public void processPacket(Stanza stanza) throws NotConnectedException {
		Message message = (Message) stanza;
		String body = message.getBody();
		if(body == null) {
			body = "";
		}
		String user = stanza.getFrom().split("/")[0];
		alertMessageListeners(user, body);
		try {
			JSONObject json = new JSONObject(message.getBody());
		} catch (Exception ex) {
			//Received invalid JSON
		}
	}
//	public boolean matchesTag(Pattern aTagsPattern, String tag) {
//		return (aTagsPattern == null) ? false : aTagsPattern.matcher(tag).matches();
//	}
//	public void notifyNewJSONMessage(Object[][] aPairs, String ...aTags) {
//		JSONObject aJSONObject = JSonProxy.toJSONObject(aPairs, aTags);
//		notifyNewJSONMessage(aJSONObject);
//	}
	@Override
	public void notifyNewJSONMessage(JSONObject aJSONObject) {
		JSONArray aTags = Tags.toTags(aJSONObject);
		for (MessageBusInternalRegistration aRegistration:messageBusInternalRegistrations) {
			Pattern aTagsPattern = aRegistration.getTagsPattern();
			TaggedJSONListener aListener = aRegistration.getTaggedJSONListener();	
			for (int i = 0; i < aTags.length(); i++) {
				String aTag;
				try {
					aTag = aTags.getString(i);
					if(Tags.matchesTag(aTagsPattern, aTag)) {
						aListener.newJSONObject(aJSONObject);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
//	public static JSONArray toTags(JSONObject message){
//		JSONArray tags = null;
//		if(!message.has(TAGS_FIELD)) {
//			tags = new JSONArray();
//			tags.put(NO_TAGS);
//		} else {
//			try {
//				tags = (JSONArray) message.get(TAGS_FIELD);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//		return tags;
//	}

	@Override
	public void addTaggedJSONObjectListener(TaggedJSONListener aListener, String aRegex) {
		MessageBusInternalRegistration aRegistration = new AMessageBusInternalRegistration(aListener, aRegex);
		messageBusInternalRegistrations.add(aRegistration);
		
		
	}
	
}