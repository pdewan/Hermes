package fluorite.commands;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import fluorite.model.EHEventRecorder;

public class BalloonClickedCommand extends AbstractCommand implements EHICommand {
	private static final String XML_MESSAGE = "message";
	private String message;

	public BalloonClickedCommand() {}
	
	public BalloonClickedCommand(String aString) {
		message = aString;
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
		dataMap.put(XML_MESSAGE, message);
		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "BalloonClickedCommand";
	}

	@Override
	public String getName() {
		return "BalloonClicked";
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

		if ((nodeList = commandElement.getElementsByTagName(XML_MESSAGE)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			message = textNode.getTextContent();
		}
	}
}
