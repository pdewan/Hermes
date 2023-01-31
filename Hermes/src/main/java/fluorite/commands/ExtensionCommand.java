package fluorite.commands;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import fluorite.model.EHEventRecorder;

public class ExtensionCommand extends AbstractCommand implements EHICommand{
	String type, field1 = "", field2 = "", field3 = "";
	final String TYPE = "type";
	final String FIELD1 = "field1";
	final String FIELD2 = "field2";
	final String FIELD3 = "field3";


	
	public ExtensionCommand() {}
	
	public ExtensionCommand(String aType, String aField1, String aField2, String aField3) {
		type = aType;
		if (field1 != null) {
		field1 = aField1;
		}
		if (field2 != null) {
		field2 = aField2;
		}
		if (field3 != null) {
		field3 = aField3;
		}
		
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
		dataMap.put(TYPE, type );

		dataMap.put(FIELD1, field1);
		dataMap.put(FIELD2, field2);
		dataMap.put(FIELD3, field3);
		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "ExtensionCommand";
	}

	@Override
	public String getName() {
		return "Extension";
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
	
	public String getType() {
		return type;
	}
	public String getField1() {
		return field1;
	}
	public String getField2() {
		return field2;
	}
	public String getField3() {
		return field3;
	}
	public void setType(String newVal) {
		type = newVal;
	}
	public void setField1(String newVal) {
		field1 = newVal;
	}
	public void setField2(String newVal) {
		field2 = newVal;
	}
	public void setField3(String newVal) {
		field3 = newVal;
	}
	
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		NodeList nodeList = null;
		if ((nodeList = commandElement.getElementsByTagName(TYPE)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			type = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(FIELD1)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			field1 = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(FIELD2)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			field2 = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(FIELD3)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			field3 = textNode.getTextContent();
		}
		
	}
}
