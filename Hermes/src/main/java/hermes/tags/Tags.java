package hermes.tags;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface Tags {
	 String TIME_TRACKER = "TIME_TRACKER";
	 public static void putTags(JSONObject aMessageData, String... aTags) {
		 try {
			JSONArray aJSONArray = new JSONArray(aTags);
			aMessageData.put("tags", aJSONArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
}
