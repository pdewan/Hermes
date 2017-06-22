package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cmu.scs.fluorite.commands.AssistCommand;
import fluorite.model.EHEventRecorder;

public class EHAssistCommand extends AssistCommand implements EHICommand{

//	public enum AssistType {
//		CONTENT_ASSIST, QUICK_ASSIST,
//	}
//
//	public enum StartEndType {
//		START, END,
//	}
//
//	private AssistType mAssistType;
//	private StartEndType mStartEndType;
//	private boolean mAutoActivated;
//	private String mContext;
//	
	public EHAssistCommand() {
		super();
	}
//
//	@Override
//	public void createFrom(Element commandElement) {
//		super.createFrom(commandElement);
//		
//		Attr attr = null;
//		NodeList nodeList = null;
//		
//		if ((attr = commandElement.getAttributeNode("assist_type")) != null) {
//			if(attr.getValue().equals("CONTENT_ASSIST"))
//			{
//				mAssistType = AssistType.CONTENT_ASSIST;
//			}
//			if(attr.getValue().equals("QUICK_ASSIST"))
//			{
//				mAssistType = AssistType.QUICK_ASSIST;
//			}
//		}
//		
//		if ((attr = commandElement.getAttributeNode("start_end")) != null) {
//			if(attr.getValue().equals("START"))
//			{
//				mStartEndType =StartEndType.START;
//			}
//			if(attr.getValue().equals("END"))
//			{
//				mStartEndType = StartEndType.END;
//			}
//		}
//
//		if ((attr = commandElement.getAttributeNode("auto_activated")) != null) {
//			mAutoActivated = Boolean.parseBoolean((attr.getValue()));
//		}
//		
//		if ((nodeList = commandElement.getElementsByTagName("mContext")).getLength() > 0) {
//			Node textNode = nodeList.item(0);
//			mContext = textNode.getTextContent();
//		}
//	}
//
	public EHAssistCommand(AssistType assistType, StartEndType startEndType,
			boolean autoActivated, String context) {
		super(assistType, startEndType, autoActivated, context);
//		mAssistType = assistType;
//		mStartEndType = startEndType;
//		mAutoActivated = (mStartEndType == StartEndType.END) ? false
//				: autoActivated;
//		mContext = context;
	}
//
//	public boolean execute(IEditorPart target) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public void dump() {
//		// TODO Auto-generated method stub
//
//	}
//
//	public Map<String, String> getAttributesMap() {
//		Map<String, String> attrMap = new HashMap<String, String>();
//		attrMap.put("assist_type", mAssistType.toString());
//		attrMap.put("start_end", mStartEndType.toString());
//		attrMap.put("auto_activated", Boolean.toString(mAutoActivated));
//		return attrMap;
//	}
//
//	public Map<String, String> getDataMap() {
//		Map<String, String> dataMap = new HashMap<String, String>();
//		if (mContext != null) {
//			dataMap.put("context", mContext);
//		}
//		return dataMap;
//	}
//
//	public String getCommandType() {
//		return "AssistCommand";
//	}
//
//	public String getName() {
//		return mAssistType.toString() + " " + mStartEndType.toString()
//				+ ", AutoActivated: " + Boolean.toString(mAutoActivated)
//				+ ", Context: " + mContext;
//	}
//
//	public String getDescription() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public String getCategory() {
//		return EventRecorder.MacroCommandCategory;
//	}
//
//	public String getCategoryID() {
//		return EventRecorder.MacroCommandCategoryID;
//	}
//
//	@Override
//	public boolean combine(EHICommand anotherCommand) {
//		// TODO Auto-generated method stub
//		return false;
//	}

}
