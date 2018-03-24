package dayton.ellwanger.hermes.xmpp;

import java.util.regex.Pattern;

public interface MessageBusInternalRegistration {
	Pattern getTagsPattern();
	TaggedJSONListener getTaggedJSONListener();

}
