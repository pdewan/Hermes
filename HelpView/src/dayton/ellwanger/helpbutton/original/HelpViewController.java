package dayton.ellwanger.helpbutton.original;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dayton.ellwanger.hermes.xmpp.ConnectionManager;
import util.trace.hermes.helpbutton.DifficultyUpdateForwardedToConnectionManager;
import util.trace.hermes.helpbutton.HelpInformationForwardedToConnectionManager;
import workspaceConnectionManager.WorkspaceConnectionManager;

public class HelpViewController implements HelpListener {

	public HelpViewController(HelpView view) {
		view.addHelpListener(this);
	}

	@Override
	public void help(String helpText) {
		if(ConnectionManager.getInstance() != null) {
			JSONObject messageData = new JSONObject();
			try {
				messageData.put("help", helpText);
				JSONArray tags = new JSONArray();
				tags.put("HELP_MESSAGE");
				messageData.put("tags", tags);
				messageData.put("filename", WorkspaceConnectionManager.getActiveDocumentName());
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
			HelpInformationForwardedToConnectionManager.newCase(this, messageData.toString());
//			JSONObjectForwardedToConnectionManager.newCase(this, messageData.toString());
			ConnectionManager.getInstance().sendMessage(messageData);
		}
	}

	@Override
	public void difficultyUpdate(int difficulty) {
		if(ConnectionManager.getInstance() != null) {
			JSONObject messageData = new JSONObject();
			try {
				messageData.put("difficulty", difficulty);
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
			DifficultyUpdateForwardedToConnectionManager.newCase(this, messageData.toString());
//			JSONObjectForwardedToConnectionManager.newCase(this, messageData.toString());

			ConnectionManager.getInstance().sendMessage(messageData);
		}		
	}

}
