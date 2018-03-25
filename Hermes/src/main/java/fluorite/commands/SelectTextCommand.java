package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import fluorite.model.EHEventRecorder;
import fluorite.util.EHUtilities;

public class SelectTextCommand 
    extends AbstractCommand
//	extends edu.cmu.scs.fluorite.commands.SelectTextCommand 
	implements EHICommand{

	public SelectTextCommand() {
		super();
	}
//	
	public SelectTextCommand(int start, int end, int caretOffset, int docStart, int docEnd, int docOffset) {
//	super(start, end, caretOffset, docStart, docEnd, docOffset);
		mStart = start;
		mEnd = end;
		mCaretOffset = caretOffset;
	}
	public SelectTextCommand(int start, int end, int caretOffset) {
//		super(start, end, caretOffset, 0, 0, 0);
			mStart = start;
			mEnd = end;
			mCaretOffset = caretOffset;
		}

	private int mStart;
	private int mEnd;
	private int mCaretOffset;

	public boolean execute(IEditorPart target) {
		StyledText styledText = EHUtilities.getStyledText(target);
		if (styledText == null) {
			return false;
		}

		if (mStart == mCaretOffset) {
			styledText.setSelection(mEnd, mStart);
		} else {
			styledText.setSelection(mStart, mEnd);
		}

		return true;
	}

	public void dump() {
		// TODO Auto-generated method stub

	}

	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("start", Integer.toString(mStart));
		attrMap.put("end", Integer.toString(mEnd));
		attrMap.put("caretOffset", Integer.toString(mCaretOffset));
		return attrMap;
	}

	public Map<String, String> getDataMap() {
		return null;
	}

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr attr = null;
		
		if ((attr = commandElement.getAttributeNode("start")) != null) {
			mStart = Integer.parseInt(attr.getValue());
		}
		if ((attr = commandElement.getAttributeNode("end")) != null) {
			mEnd = Integer.parseInt(attr.getValue());
		}
		if ((attr = commandElement.getAttributeNode("caretOffset")) != null) {
			mCaretOffset = Integer.parseInt(attr.getValue());
		}
	}

	public String getCommandType() {
		return "SelectTextCommand";
	}

	public String getName() {
		return "Select Text (" + mStart + ", " + mEnd + ", " + mCaretOffset
				+ ")";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCategory() {
		return EHEventRecorder.MacroCommandCategory;
	}

	public String getCategoryID() {
		return EHEventRecorder.MacroCommandCategoryID;
	}

	public boolean combine(EHICommand anotherCommand) {
		return false;
	}
}
