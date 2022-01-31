package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CompilationEventCommand extends AbstractCommand {
	
	private boolean success = false;
	
	public CompilationEventCommand() {}
	
	public CompilationEventCommand(boolean success) {
		// TODO Auto-generated constructor stub
		this.success = success;
	}

	@Override
	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getAttributesMap() {
		// TODO Auto-generated method stub
		return new HashMap<String, String>();
	}

	@Override
	public Map<String, String> getDataMap() {
		// TODO Auto-generated method stub
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("success", success+"");
		return dataMap;
	}

	@Override
	public String getCommandType() {
		// TODO Auto-generated method stub
		return "CompilationEventCommand";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "CompilationEvent";
	}

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		String value = null;
		NodeList nodeList = null;
		
		if ((nodeList = commandElement.getElementsByTagName("success")).getLength() > 0) {
			Node textNode = nodeList.item(0);
			value = textNode.getTextContent();
			success = Boolean.parseBoolean(value);
		}
		else {
			success = false;
		}
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategoryID() {
		// TODO Auto-generated method stub
		return null;
	}

}
