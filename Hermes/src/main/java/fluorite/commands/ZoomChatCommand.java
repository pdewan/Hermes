package fluorite.commands;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import fluorite.model.EHEventRecorder;

public class ZoomChatCommand extends AbstractCommand implements EHICommand{
	protected String chat;
	protected String speaker;
	protected String sessionStartTime;
	protected String speakTime;
	final static String CHAT = "chat";
	final static String SPEAKER = "speaker";
	final static String SESSION_START_TIME = "session_start_time";
	final static String SPEAK_TIME = "speak_time";
	
	public ZoomChatCommand() {}
	
	public ZoomChatCommand(String speaker, String sessionStartTime, String speakTime) {
		this(speaker, "", sessionStartTime, speakTime);
	}
	
	public ZoomChatCommand(String speaker, String chat, String sessionStartTime, String speakTime) {
		this.speaker = speaker;
		this.chat = chat;
		this.sessionStartTime = sessionStartTime;
		this.speakTime = speakTime;
	}
	
	public void setChat(String chat) {
		this.chat = chat;
	}
	
	@Override
	public boolean execute(IEditorPart target) {
		return false;
	}

	@Override
	public void dump() {
	}

	@Override
	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put(SPEAKER, speaker);
		dataMap.put(CHAT, chat);
		dataMap.put(SESSION_START_TIME, sessionStartTime);
		dataMap.put(SPEAK_TIME, speakTime);
		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "ZoomChatCommand";
	}

	@Override
	public String getName() {
		return "ZoomChat";
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getCategory() {
		return EHEventRecorder.UserMacroCategoryName;
	}

	@Override
	public String getCategoryID() {
		return EHEventRecorder.UserMacroCategoryID;
	}
	
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		NodeList nodeList = null;

		if ((nodeList = commandElement.getElementsByTagName(SPEAKER)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			speaker = textNode.getTextContent();
		}
		
		if ((nodeList = commandElement.getElementsByTagName(CHAT)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			chat = textNode.getTextContent();
		}
		
		if ((nodeList = commandElement.getElementsByTagName(SESSION_START_TIME)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			sessionStartTime = textNode.getTextContent();
		}
		
		if ((nodeList = commandElement.getElementsByTagName(SPEAK_TIME)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			speakTime = textNode.getTextContent();
		}
	}
}
