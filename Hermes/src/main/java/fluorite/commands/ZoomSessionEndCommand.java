package fluorite.commands;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import fluorite.model.EHEventRecorder;

public class ZoomSessionEndCommand extends ZoomChatCommand {
	protected String sessionStartTime;
	protected String sessionEndTime;

	final static String SESSION_START_TIME = "session_start_time";
	final static String SESSION_END_TIME = "session_end_time";
	
	public ZoomSessionEndCommand() {}
	
	public ZoomSessionEndCommand(String sessionStartTime, String sessionEndTime) {
		this.sessionStartTime = sessionStartTime;
		this.sessionEndTime = sessionEndTime;
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
		dataMap.put(SESSION_START_TIME, sessionStartTime);
		dataMap.put(SESSION_END_TIME, sessionEndTime);
		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "ZoomSessionEndCommand";
	}

	@Override
	public String getName() {
		return "ZoomSessionEnd";
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
		
		if ((nodeList = commandElement.getElementsByTagName(SESSION_START_TIME)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			sessionStartTime = textNode.getTextContent();
		}
		
		if ((nodeList = commandElement.getElementsByTagName(SESSION_END_TIME)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			sessionEndTime = textNode.getTextContent();
		}
	}
}
