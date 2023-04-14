package fluorite.commands;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fluorite.model.EHEventRecorder;

public class AnnotationCommand extends AbstractCommand implements EHICommand {
	private String annotation;
	private static final String XML_ANNOTATION = "annotation";

	
	public AnnotationCommand() {}

	public AnnotationCommand(String annotation) {
		this.annotation = annotation;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setmComment(String annotation) {
		this.annotation = annotation;
	}

	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}

	public void dump() {
		// TODO Auto-generated method stub

	}

	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put(XML_ANNOTATION, annotation);
		return dataMap;
	}

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		NodeList nodeList = null;

		if ((nodeList = commandElement.getElementsByTagName(XML_ANNOTATION)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			annotation = textNode.getTextContent();
		}
	}

	public String getCommandType() {
		return "AnnotationCommand";
	}


	public String getName() {
		return "AnnotationCommand";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCategory() {
		return EHEventRecorder.XML_Command_Tag;
	}

	public String getCategoryID() {
		return EHEventRecorder.XML_Command_Tag;
	}

	public boolean combine(EHICommand anotherCommand) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, String> getAttributesMap() {
		// TODO Auto-generated method stub
		return new HashMap<String, String>();
	}

}
