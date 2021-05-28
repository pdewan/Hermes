package fluorite.commands;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import fluorite.model.EHEventRecorder;

public class BalloonCommand extends AbstractCommand implements EHICommand {
	private static final String XML_MESSAGE = "message";
	private static final String XML_WORK_TIME = "workTime";
	private static final String XML_FINE_GRAINED_WORK_TIME = "fineGrainedWorkTime";
	private static final String XML_START_TIME = "startTime";
	private static final String XML_END_TIME = "endTime";
	private static final String XML_INSERT = "insert";
	private static final String XML_DELETE = "delete";
	private String message;
	private long workTime, fineGrainedWorkTime, startTime, endTime;
	private int insert, delete;

	public BalloonCommand() {}
	
	public BalloonCommand(String aString, long aWorkTime, long aFineGrainedWorkTime, long aStartTime, long anEndTime, int insertNum, int deleteNum) {
		message = aString;
		workTime = aWorkTime;
		fineGrainedWorkTime = aFineGrainedWorkTime;
		startTime = aStartTime;
		endTime = anEndTime;
		insert = insertNum;
		delete = deleteNum;
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
		attrMap.put(XML_MESSAGE, message);
		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put(XML_WORK_TIME, workTime+"");
		dataMap.put(XML_FINE_GRAINED_WORK_TIME, fineGrainedWorkTime+"");
		dataMap.put(XML_START_TIME, startTime+"");
		dataMap.put(XML_END_TIME, endTime+"");
		dataMap.put(XML_INSERT, insert+"");
		dataMap.put(XML_DELETE, delete+"");
		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "BalloonCommand";
	}

	@Override
	public String getName() {
		return "Balloon";
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
		if ((nodeList = commandElement.getElementsByTagName(XML_WORK_TIME)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			workTime = Long.parseLong(textNode.getTextContent());
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_FINE_GRAINED_WORK_TIME)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			fineGrainedWorkTime = Long.parseLong(textNode.getTextContent());
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_START_TIME)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			startTime = Long.parseLong(textNode.getTextContent());
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_END_TIME)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			endTime = Long.parseLong(textNode.getTextContent());
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_INSERT)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			insert = Integer.parseInt(textNode.getTextContent());
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_DELETE)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			delete = Integer.parseInt(textNode.getTextContent());
		}
		
	}
}
