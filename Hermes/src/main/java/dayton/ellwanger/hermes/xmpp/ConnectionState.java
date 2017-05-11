package dayton.ellwanger.hermes.xmpp;

public enum ConnectionState {

	CONNECTED("Connected", "Disconnect"), 
	CONNECTING("Connecting...", "Connecting..."), 
	DISCONNECTED("Disconnected", "Connect"), 
	DISCONNECTING("Disconnecting...", "Disconnecting...");
	
	private String stateString;
	private String oppositeAction;
	
	ConnectionState(String stateString, String oppositeAction) {
		this.stateString = stateString;
		this.oppositeAction = oppositeAction;
	}
	
	public String getState() {
		return stateString;
	}
	
	public String getOppositeAction() {
		return oppositeAction;
	}
	
}
