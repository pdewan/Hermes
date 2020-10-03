package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fluorite.model.EHEventRecorder;

public class GetHelpCommand extends AbstractCommand{
	private String email, course, assign, errorType, errorMessage, problem, term, requestID, output, help;
	private boolean success;
	private static final String XML_EMAIL = "email";
	private static final String XML_COURSE = "course";
	private static final String XML_ASSIGN = "assignment";
	private static final String XML_ERRORTYPE = "error-type";
	private static final String XML_PROBLEM = "problem";
	private static final String XML_TERM = "term";
	private static final String XML_ID = "requestID";
	private static final String XML_OUTPUT = "output";
	private static final String XML_ERRORMESSAGE = "error-message";
	private static final String XML_SUCCESS = "success";
	private static final String XML_HELP = "help";
	
	public GetHelpCommand() {}
	
	public GetHelpCommand(String email, String course, String assign, String errorType, String errorMessage, String problem, String term, String requestID, String output, String help, boolean success) {
		this.email = email;
		this.course = course;
		this.assign = assign;
		this.errorType = errorType;
		this.errorMessage = errorMessage;
		this.problem = problem;
		this.term = term;
		this.output = output;
		this.requestID = requestID;
		this.success = success;
		this.help = help;
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
		attrMap.put(XML_ID, requestID);
		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put(XML_SUCCESS, success+""); 
		dataMap.put(XML_EMAIL, email);
		dataMap.put(XML_TERM, term);
		dataMap.put(XML_COURSE, course);
		dataMap.put(XML_ASSIGN, assign);
		dataMap.put(XML_PROBLEM, problem);
		dataMap.put(XML_ERRORTYPE, errorType);
		dataMap.put(XML_ERRORMESSAGE, errorMessage);
		dataMap.put(XML_OUTPUT, output);
		dataMap.put(XML_HELP, help);
		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "GetHelpCommand";
	}

	@Override
	public String getName() {
		return "GetHelp";
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

		if ((nodeList = commandElement.getElementsByTagName(XML_ASSIGN)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			assign = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_COURSE)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			course =textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_EMAIL)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			email = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_ERRORMESSAGE)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			errorMessage =textNode.getTextContent();
		}if ((nodeList = commandElement.getElementsByTagName(XML_ERRORTYPE)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			errorType = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_HELP)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			help =textNode.getTextContent();
		}if ((nodeList = commandElement.getElementsByTagName(XML_ID)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			requestID = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_OUTPUT)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			output =textNode.getTextContent();
		}if ((nodeList = commandElement.getElementsByTagName(XML_PROBLEM)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			problem = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_SUCCESS)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			success = Boolean.getBoolean(textNode.getTextContent());
		}if ((nodeList = commandElement.getElementsByTagName(XML_TERM)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			term = textNode.getTextContent();
		}
	}

}
