package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ExceptionCommand extends OutputProduced implements EHICommand {
	public static final String XML_Exception_Tag = "exceptionString";
	public static final String XML_Language_Tag = "language";
	protected String language;
//	public static final String XML_Output_Tag = "outputString";
//	
	public ExceptionCommand()
	{
		
	}
	
	public ExceptionCommand(String aText, String language)
	{
		super(aText);
		this.language = language;
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

	//Commenting this out
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
			dataMap.put(XML_Exception_Tag, outputText);
		if (language != null) 
			dataMap.put(XML_Language_Tag, language);
		return dataMap;
	}
	
	
	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		NodeList nodeList = null;

		if ((nodeList = commandElement.getElementsByTagName(XML_Exception_Tag)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			outputText =textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_Language_Tag)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			language =textNode.getTextContent();
		} else {
			language = "java";
		}
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
		return "ExceptionCommand";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Exception";
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
