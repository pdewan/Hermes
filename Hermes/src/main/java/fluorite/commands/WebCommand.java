package fluorite.commands;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import fluorite.model.EHEventRecorder;

public class WebCommand extends AbstractCommand implements EHICommand{
	private static final String XML_KEYWORD = "keyword";
	private static final String XML_URL = "URL";
//	private static final String XML_TITLE = "title";
	private static final String XML_TYPE = "type";
	private String keyword, url, type;	
	public static final String[] PROVIDED_URL = {"http://www.cs.mun.ca/~rod/W12/cs2710/notes/graphics-intro/graphics-intro.html", 
			"http://www.java2s.com/Tutorial/Java/0261__2D-Graphics/DrawRectangle.htm",
			"http://zetcode.com/gfx/java2d/shapesandfills/", 
			"http://www.tutorialspoint.com/javaexamples/gui_solid.htm", 
			"http://csis.pace.edu/~bergin/mvc/mvcgui.html\r\n", 
			"http://www.java2s.com/Tutorial/Java/0261__2D-Graphics/Fill3DRectangle.htm", 
			"http://www.java-tips.org/java-se-tips/java.awt.event/how-to-use-mouse-events-in-swing.html", 
			"http://php.scripts.psu.edu/djh300/cmpsc221/notes-graphics-intro.php", 
			"http://stackoverflow.com/questions/9333876/how-to-simply-implement-a-keylistener", 
			"http://stackoverflow.com/questions/15103553/difference-between-paint-and-paintcomponent"}; 

	public WebCommand() {}
	
	public WebCommand(String keyword, String url) {
		this.keyword = keyword;
		this.url = url;
		//this.title = title;
		type = getType();
	}
	
	private String getType() {
		for (String providedURL : PROVIDED_URL) {
			if (url.equals(providedURL)) {
				return "Instructor Provided Link";
			}
		}
		if (keyword.contains("Google Search")) {
			return "Search";
		}
		if (keyword.contains("Stack Overflow")) {
			return "Stack Overflow";
		}
//		if (keyword.equals(url) && keyword.contains("google.com/url?")) {
//			return "Search Result";
//		}
		return "Search Result";
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
		attrMap.put(XML_TYPE, type);
		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put(XML_KEYWORD, keyword);
		dataMap.put(XML_URL, url);
		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "WebCommand";
	}

	@Override
	public String getName() {
		return "Web";
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
		return EHEventRecorder.WebCategoryID;
	}
	
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		NodeList nodeList = null;

		if ((nodeList = commandElement.getElementsByTagName(XML_KEYWORD)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			keyword = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_TYPE)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			type = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_URL)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			url = textNode.getTextContent();
		}
		type = getType();
	}

}
