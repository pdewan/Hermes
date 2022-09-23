package fluorite.commands;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ui.IEditorPart;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import fluorite.model.EHEventRecorder;

public class PiazzaPostCommand extends AbstractCommand implements EHICommand{
	protected JSONObject piazzaPost;
//	final static String FAIL_DECLINE = "fail_decline";
//	final static String FAIL_GROWTH = "fail_growth";
	final static String PIAZZA_POST = "piazza_post";

	
	public PiazzaPostCommand() {}
	
	public PiazzaPostCommand(JSONObject piazzaPost) {
		this.piazzaPost = piazzaPost;
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
		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put(PIAZZA_POST, piazzaPost.toString(4));
		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "PiazzaPostCommand";
	}

	@Override
	public String getName() {
		return "PiazzaPost";
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

		if ((nodeList = commandElement.getElementsByTagName(PIAZZA_POST)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			piazzaPost = new JSONObject(textNode.getTextContent());
		}
		
	}
}
