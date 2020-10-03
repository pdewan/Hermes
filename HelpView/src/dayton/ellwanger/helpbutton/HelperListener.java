package dayton.ellwanger.helpbutton;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

public interface HelperListener {
//	public void login(String email, String password);
	public void reply(String reply, String email, String password, String id) throws IOException;
	public void pull(String term, String course, String assign, String problem, String password, String regex, String langauge) throws IOException;
	public void createProject(JSONObject request);
}
