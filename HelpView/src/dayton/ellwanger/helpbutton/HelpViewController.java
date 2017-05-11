package dayton.ellwanger.helpbutton;

import org.json.JSONException;
import org.json.JSONObject;

import dayton.ellwanger.hermes.xmpp.ConnectionManager;

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
			} catch (JSONException ex) {}
			ConnectionManager.getInstance().sendMessage(messageData);
		}
	}

	@Override
	public void difficultyUpdate(int difficulty) {
		if(ConnectionManager.getInstance() != null) {
			JSONObject messageData = new JSONObject();
			try {
				messageData.put("difficulty", difficulty);
			} catch (JSONException ex) {}
			ConnectionManager.getInstance().sendMessage(messageData);
		}		
	}

}
