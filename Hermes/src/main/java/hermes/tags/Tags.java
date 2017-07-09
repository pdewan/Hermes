package hermes.tags;

import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface Tags {
	 public static final String TAGS_FIELD = "tags";
	 public static final String NO_TAGS = "none";
	 public static String TYPE= "type";
	 
	 public static String TIME_TRACKER = "TIME_TRACKER";
	 public static String APPEND = "APPEND";
	 public static String FACEBOOK_POST = "FACEBOOK_POST";
	 public static String FILE_DATA_REQUEST = "FILE_DATA_REQUEST";
	 public static String HELP_MESSAGE = "HELP_MESSAGE"; 
	 public static String EDITOR_CONTENTS = "EDITOR_CONTENTS"; 
	 public static String DOCUMENT_CHANGE = "DOCUMENT_CHANGE"; 
	

	 
	 
	 /*
	  * JSON Fields, probbaly should go somewhere else in sepaate files in main plugin
	  * 
	  */
	 public static final String FILE_NAME = "filename"; // not camel case for legacy reasons

	 public static final String ABSOLUTE_FILE_NAME = "absoluteFileName";
	 public static final String RELATIVE_FILE_NAME = "relativeFileName";
	 
	 public static void putTags(JSONObject aMessageData, String... aTags) {
		 try {
			JSONArray aJSONArray = new JSONArray(aTags);
			aMessageData.put("tags", aJSONArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 public static boolean matchesTag(Pattern aTagsPattern, String tag) {
			return (aTagsPattern == null) ? false : aTagsPattern.matcher(tag).matches();
	}
		public static JSONArray toTags(JSONObject message){
			JSONArray tags = null;
			if(!message.has(TAGS_FIELD)) {
				tags = new JSONArray();
				tags.put(NO_TAGS);
			} else {
				try {
					tags = (JSONArray) message.get(TAGS_FIELD);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return tags;
		}
}
