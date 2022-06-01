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
	public static final String XML_NUM_VISITS_TAG = "numVisits";

	protected String searchString = "";
	protected String url = "";
	protected int numVisits = 0;


	//	public String title;
//	public int numVisits;
//	public String url;
//	public long unixTime;
	public WebVisitCommand()
	{
		
	}
	
	public WebVisitCommand(String aSearchString, String aURL, int aNumVisits)
	{
		searchString = aSearchString;
		url = aURL;	
		numVisits = aNumVisits;
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
		if ((attr = commandElement.getAttributeNode(XML_NUM_VISITS_TAG)) != null) {
			try {
			numVisits = Integer.parseInt(attr.getValue());	
			} catch (Exception e) {
				e.printStackTrace();
			}
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
		attrMap.put(XML_NUM_VISITS_TAG, Integer.toString(numVisits));
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
	public int getNumVisits() {
		return numVisits;
	}

	public void setNumVisits(int numVisits) {
		this.numVisits = numVisits;
	}

}
