package dayton.ellwanger.consolelistener;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.TextConsole;
import org.json.JSONArray;
import org.json.JSONObject;

import dayton.ellwanger.hermes.xmpp.ConnectStateListener;
import dayton.ellwanger.hermes.xmpp.ConnectionState;
import dayton.ellwanger.hermes.xmpp.ConnectionManager;

public class HermesConnectionManager implements ConnectStateListener, IDocumentListener, IConsoleListener {
	
	private String workspaceString;
	private String consoleString;
	private ContentSender sender;
	
	public HermesConnectionManager() {
		workspaceString = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString().replace("/",".");
	}
	
	@Override
	public void stateChanged(ConnectionState newState) {
		if(newState == ConnectionState.CONNECTED) {
			ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(this);
		}
	}


	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {}


	@Override
	public void documentChanged(DocumentEvent event) {
		consoleString += event.getText();
		if(sender == null) {
			sender = new ContentSender();
			(new Thread(sender)).start();
		} else {
			sender.send = false;
		}
	}
	
	private void sendConsole() {
		if(ConnectionManager.getInstance() != null) {
			JSONObject messageData = new JSONObject();
			try {
				messageData.put("filename", workspaceString + "/console");
				messageData.put("append", consoleString.replace('\r', ' '));
				consoleString = "";
				JSONArray tags = new JSONArray();
				tags.put("APPEND");
				messageData.put("tags", tags);
			} catch (Exception ex) {}
			ConnectionManager.getInstance().sendMessage(messageData);
		}
	}


	@Override
	public void consolesAdded(IConsole[] consoles) {
		for(IConsole c : consoles) {
			if(c instanceof TextConsole) {
				((TextConsole) c).getDocument().addDocumentListener(this);
			}
		}
	}


	@Override
	public void consolesRemoved(IConsole[] consoles) {}
	
	class ContentSender implements Runnable {

		boolean send;
		int sleepDelay = 10000;

		public ContentSender() {
			send = false;
		}

		public void run() {
			while(!send) {
				send = true;
				try {
					Thread.sleep(sleepDelay);
				} catch (Exception ex) {ex.printStackTrace();}
			}
			sendConsole();
		}

	}
	
}