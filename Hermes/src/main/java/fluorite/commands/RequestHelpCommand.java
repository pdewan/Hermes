package fluorite.commands;

import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RequestHelpCommand extends GetHelpCommand{
	private static final String XML_DIFFICULTY = "difficulty";
	private int diffculty;
	
	public RequestHelpCommand() {}
	
	public RequestHelpCommand(String email, String course, String assign, String errorType, String errorMessage,
			String problem, String term, String requestID, String output, String help, boolean success, int difficulty) {
		super(email, course, assign, errorType, errorMessage, problem, term, requestID, output, help, success);
		this.diffculty = difficulty;
	}
	
	@Override
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = super.getDataMap();
		dataMap.put(XML_DIFFICULTY, diffculty+"");
		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "RequestHelpCommand";
	}

	@Override
	public String getName() {
		return "RequestHelp";
	}
	
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		NodeList nodeList = null;
		if ((nodeList = commandElement.getElementsByTagName(XML_DIFFICULTY)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			diffculty = Integer.getInteger(textNode.getTextContent());
		}
	}
}
