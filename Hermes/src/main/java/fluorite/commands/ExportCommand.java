package fluorite.commands;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import fluorite.model.EHEventRecorder;

public class ExportCommand extends AbstractCommand implements EHICommand {
	private static final String XML_PROJECT = "project";
	private static final String XML_SUCCESS = "success";
	private String projectName;
	private boolean success;
	
	public ExportCommand() {}
	
	public ExportCommand(String project, boolean success) {
		projectName = project;
		this.success = success;
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
		attrMap.put(XML_PROJECT, projectName);
		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put(XML_SUCCESS, success+"");
		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "ExportCommand";
	}

	@Override
	public String getName() {
		return "Export";
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

		if ((nodeList = commandElement.getElementsByTagName(XML_PROJECT)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			projectName = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_SUCCESS)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			success = Boolean.getBoolean(textNode.getTextContent());
		}
	}
}
