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
	String status = NORMAL;
	public final static String PASSED = "passed";
//	final static String FAIL_DECLINE = "fail_decline";
//	final static String FAIL_GROWTH = "fail_growth";
	public final static String TYPE = "type";
	public final static String TESTCASE = "testcase";
	public final static String STATUS = "status";
	public final static String NORMAL = "normal";
	public final static String DIFFICULTY = "difficulty";
	public final static String FIX = "fix";


	
	public LocalCheckCommand() {}
	
	public LocalCheckCommand(String event) {
		testcase = event.substring(0, event.indexOf("_"));
		type = event.substring(event.indexOf("_")+1);
		status = NORMAL;
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
		dataMap.put(STATUS, status);
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
	
	public String getStatus() {
		return status;
	}
	public String getTestcase() {
		return testcase;
	}
	public String getProgressType() {
		return type;
	}
	public void setStatus(String newVal) {
		status = newVal;
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
		if ((nodeList = commandElement.getElementsByTagName(STATUS)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			status = textNode.getTextContent();
		}
	}
}
