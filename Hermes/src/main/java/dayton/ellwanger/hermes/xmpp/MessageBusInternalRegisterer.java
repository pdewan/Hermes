package dayton.ellwanger.hermes.xmpp;

import org.json.JSONObject;

public interface MessageBusInternalRegisterer {
//	public static final String TAGS_FIELD = "tags";
//	public static final String NO_TAGS = "none";

	void addTaggedJSONObjectListener(TaggedJSONListener aListener, String aRegexes);
	void notifyNewJSONMessage(JSONObject aJSONObject);

}
