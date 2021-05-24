package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fluorite.model.EHEventRecorder;

public class ShowStatCommand extends AbstractCommand implements EHICommand{
	private String type;
	private static final String XML_TYPE = "type";
	
	public ShowStatCommand() {}
	
	public ShowStatCommand(String type) {
		this.type = type;
	}
	
	public boolean execute(IEditorPart target) {
		return false;
	}

	public void dump() {
	}
	
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put(XML_TYPE, type);
		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "ShowStatCommand";
	}

	@Override
	public String getName() {
		return "GetHelp";
	}

	@Override
	public String getDescription() {
		return null;
	}

	public String getCategory() {
		return EHEventRecorder.UserMacroCategoryName;
	}

	public String getCategoryID() {
		return EHEventRecorder.UserMacroCategoryID;
	}
	
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		NodeList nodeList = null;

		if ((nodeList = commandElement.getElementsByTagName(XML_TYPE)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			type = textNode.getTextContent();
		}
	}

	public Map<String, String> getAttributesMap() {
		return new HashMap<>();
	}

}
