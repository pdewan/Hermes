package dayton.ellwanger.hermes.xmpp;

import java.util.regex.Pattern;

public class AMessageBusInternalRegistration implements MessageBusInternalRegistration{
	protected Pattern tagsPattern;
	protected TaggedJSONListener taggedJSONListener;
	 
	public AMessageBusInternalRegistration(TaggedJSONListener aTaggedJSONListener, String aRegex) {
		super();
		this.tagsPattern = Pattern.compile(aRegex);;
		this.taggedJSONListener = aTaggedJSONListener;
	}

	@Override
	public Pattern getTagsPattern() {
		return tagsPattern;
	}

	@Override
	public TaggedJSONListener getTaggedJSONListener() {
		return taggedJSONListener;
	}

}
