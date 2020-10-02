package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import hermes.proxy.Diff_Match_Patch_Proxy;

public class ConsoleOutput extends OutputProduced implements EHICommand {

//	public static final String XML_Output_Tag = "outputString";
//	
	protected String lastOutput = null; 
	protected String diff = "null";
	protected boolean overflow = false;
	protected static int limit = 200;
	
	public ConsoleOutput(){}
	
	public ConsoleOutput(String aText, String lastOutput){
//		super(aText);
		outputText = boundInfiniteLoop(aText);
//		outputText = aText;
		this.lastOutput = lastOutput; 
		if (lastOutput != null) {
			diff = Diff_Match_Patch_Proxy.diffString(lastOutput, aText);
		}
	}
	
	public static void setLimit(int i){
		limit = i;
	}
	
	protected String boundInfiniteLoop(String s) {
		String[] strings = s.split("\r\n",limit+1);
		if (strings.length == 201) {
			overflow = true;
			String first200Lines = "";
			for(int i = 0; i < limit; i++)
				first200Lines = first200Lines + "\r\n" + strings[i];
			return first200Lines + "\r\n\tExceeding " + limit + " lines, infinite loop suspected, output ignored.";
		}
		return s;
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
		attrMap.put("type", "ConsoleOutput");
		//attrMap.put("text", mExceptionText);
//		attrMap.put("output", outputText);
		attrMap.put("overflow", overflow+"");
		return attrMap;
	}

	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = super.getDataMap();
		if (diff != null) {
			dataMap.put("diff", diff);
		} else {
			dataMap.put("diff", "null");
		}
		
		return dataMap;			
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
		return "ConsoleOutput";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ConsoleOutput";
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
