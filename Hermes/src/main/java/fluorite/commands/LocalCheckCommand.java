package fluorite.commands;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import fluorite.model.EHEventRecorder;

public class LocalCheckCommand extends AbstractCommand implements EHICommand{
	String testcase, type;
	final static String PASSED = "passed";
//	final static String FAIL_DECLINE = "fail_decline";
//	final static String FAIL_GROWTH = "fail_growth";
	final static String TYPE = "type";
	final static String TESTCASE = "testcase";
	
	public LocalCheckCommand() {}
	
	public LocalCheckCommand(String event) {
		testcase = event.substring(0, event.indexOf("_"));
		type = event.substring(event.indexOf("_")+1);
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
		dataMap.put(TESTCASE, testcase);
		dataMap.put(TYPE, type);
		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "LocalCheckCommand";
	}

	@Override
	public String getName() {
		return "LocalCheck";
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

		if ((nodeList = commandElement.getElementsByTagName(TESTCASE)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			testcase = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(TYPE)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			type = textNode.getTextContent();
		}
	}
}
