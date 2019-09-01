package hermes.json;

import java.util.Date;

import dayton.ellwanger.hermes.xmpp.ConnectionManager;
import hermes.tags.Tags;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 *Json librray cannot be imporyted by multiple projects, so others can use this class for JSON
 *
 */
public class JSONProxy {
	
	   public static JSONObject toJSONObject(Object[][] aPairs, String ...aTags) {
			JSONObject messageData = new JSONObject();
				for (Object[] aPair:aPairs) {
					messageData.put((String) aPair[0], aPair[1]);
				}
				Tags.putTags(messageData, aTags);
			return messageData;
				
			
	   }
	   public static void notifyNewJSONMessage(Object[][] aPairs, String ...aTags) {
			JSONObject aJSONObject = JSONProxy.toJSONObject(aPairs, aTags);
			ConnectionManager.getInstance().notifyNewJSONMessage(aJSONObject);
		}
	   public static void sendJSONObject (Object[][] aPairs, String ...aTags) {
			if(ConnectionManager.getInstance() != null) {
				try {
				
				ConnectionManager.getInstance().sendMessage(toJSONObject(aPairs, aTags));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

}
