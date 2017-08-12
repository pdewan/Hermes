package dayton.ellwanger.hermes.xmpp;

import java.util.regex.Pattern;

import org.json.JSONArray;

public interface MessageBusInternalRegistration {
	Pattern getTagsPattern();
	TaggedJSONListener getTaggedJSONListener();

}
