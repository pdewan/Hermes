package dayton.ellwanger.helpbutton;

import org.json.JSONObject;

public interface HelperListener {
//	public void login(String email, String password);
	public void reply(String reply, String email, String password, String id);
	public void pull(String term, String course, String assign, String problem, String password, String regex, String langauge);
	public void createProject(JSONObject request);
}
