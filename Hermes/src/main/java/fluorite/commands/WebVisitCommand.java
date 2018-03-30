package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fluorite.model.EHEventRecorder;

public class WebVisitCommand extends AbstractCommand {
	public static final String XML_SearchString_Tag = "searchString";
	public static final String XML_URL_Tag = "url";

	protected String searchString = "";
	protected String url = "";
	public WebVisitCommand()
	{
		
	}
	
	public WebVisitCommand(String aSearchString, String aURL)
	{
		searchString = aSearchString;
		url = aURL;		
	}
	
	
	
	
	
	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		Attr attr = null;
		NodeList nodeList = null;
		
		if ((attr = commandElement.getAttributeNode(XML_SearchString_Tag)) != null) {
			searchString = attr.getValue();			
		}
		if ((attr = commandElement.getAttributeNode(XML_URL_Tag)) != null) {
			url = attr.getValue();			
		}
		

//		if ((nodeList = commandElement
//				.getElementsByTagName(XML_SearchString_Tag)).getLength() > 0) {
//			Node textNode = nodeList.item(0);
//			searchString = textNode.getTextContent();
//		}
//
//		if ((nodeList = commandElement
//				.getElementsByTagName(XML_URL_Tag)).getLength() > 0) {
//			Node textNode = nodeList.item(0);
//			url = textNode.getTextContent();
//		}

		
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
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put(XML_SearchString_Tag, searchString);
		attrMap.put(XML_URL_Tag, url);
		return attrMap;
	}

	


	@Override
	public String getCommandType() {
		return "WebVisitCommand";
	}

	@Override
	public String getName() {
		return "WebVisit";
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getCategory() {
		return EHEventRecorder.WebCategory;
	}

	@Override
	public String getCategoryID() {
		return EHEventRecorder.DifficultyCategoryID;
	}

	@Override
	public boolean combine(EHICommand anotherCommand) {
		return false;
	}

	@Override
	public Map<String, String> getDataMap() {
		return null;
//		Map<String, String> dataMap = new HashMap<String, String>();
//		return dataMap;
		
	}
	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	

}
