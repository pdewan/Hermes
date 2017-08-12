package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cmu.scs.fluorite.commands.ICommand;
import fluorite.model.EHEventRecorder;

public class EHOutputProduced extends EHAbstractCommand implements EHICommand {

	public static final String XML_Output_Tag = "outputString";
	
	public EHOutputProduced()
	{
		
	}
	
	public EHOutputProduced(String aText)
	{
		outputText = aText;
	}
	
	protected String outputText;
	@Override
	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String getOutputText() {
		return outputText;
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getAttributesMap() {
		// TODO Auto-generated method stub
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("type", "Exception");
		//attrMap.put("text", mExceptionText);
		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		if (outputText != null)
			dataMap.put(XML_Output_Tag, outputText);

		return dataMap;
	}
	
	
	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		NodeList nodeList = null;

		if ((nodeList = commandElement.getElementsByTagName(XML_Output_Tag)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			outputText =textNode.getTextContent();
		}
	}

	@Override
	public String getCommandType() {
		// TODO Auto-generated method stub
		return "ExceptionCommand";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Exception";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return EHEventRecorder.MacroCommandCategory;
	}

	@Override
	public String getCategoryID() {
		// TODO Auto-generated method stub
		return EHEventRecorder.MacroCommandCategoryID;
	}

	@Override
	public boolean combine(ICommand anotherCommand) {
		// TODO Auto-generated method stub
		return false;
	}


}
