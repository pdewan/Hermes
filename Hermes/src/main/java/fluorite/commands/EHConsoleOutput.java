package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

public class EHConsoleOutput extends EHOutputProduced implements EHICommand {

//	public static final String XML_Output_Tag = "outputString";
//	
	public EHConsoleOutput()
	{
		
	}
	
	public EHConsoleOutput(String aText)
	{
		super(aText);
//		outputText = aText;
	}
	
//	protected String outputText;
//	@Override
//	public boolean execute(IEditorPart target) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//	
//	public String getOutputText() {
//		return outputText;
//	}
//
//	@Override
//	public void dump() {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public Map<String, String> getAttributesMap() {
		// TODO Auto-generated method stub
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("type", "Exception");
		//attrMap.put("text", mExceptionText);
		return attrMap;
	}

//	@Override
//	public Map<String, String> getDataMap() {
//		Map<String, String> dataMap = new HashMap<String, String>();
//		if (outputText != null)
//			dataMap.put(XML_Output_Tag, outputText);
//
//		return dataMap;
//	}
	
	
//	@Override
//	public void createFrom(Element commandElement) {
//		super.createFrom(commandElement);
//		
//		NodeList nodeList = null;
//
//		if ((nodeList = commandElement.getElementsByTagName(XML_Output_Tag)).getLength() > 0) {
//			Node textNode = nodeList.item(0);
//			outputText =textNode.getTextContent();
//		}
//	}

	@Override
	public String getCommandType() {
		// TODO Auto-generated method stub
		return "EHExceptionCommand";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "EHException";
	}

//	@Override
//	public String getDescription() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getCategory() {
//		// TODO Auto-generated method stub
//		return EHEventRecorder.MacroCommandCategory;
//	}
//
//	@Override
//	public String getCategoryID() {
//		// TODO Auto-generated method stub
//		return EHEventRecorder.MacroCommandCategoryID;
//	}
//
//	@Override
//	public boolean combine(ICommand anotherCommand) {
//		// TODO Auto-generated method stub
//		return false;
//	}


}
