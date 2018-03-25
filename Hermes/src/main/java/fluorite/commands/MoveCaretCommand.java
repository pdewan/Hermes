package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import fluorite.model.EHEventRecorder;
import fluorite.util.EHUtilities;

public class MoveCaretCommand 
extends AbstractCommand
//	extends MoveCaretCommand 
	implements EHICommand{
	
	public MoveCaretCommand() {
		super();
	}
//
	public MoveCaretCommand(int caretOffset, int docOffset) {
//		super(caretOffset, docOffset);
		mCaretOffset = caretOffset;
		mDocOffset = docOffset;
	}

	private int mCaretOffset;
	private int mDocOffset;

	public boolean execute(IEditorPart target) {
		StyledText styledText = EHUtilities.getStyledText(target);
		if (styledText == null) {
			return false;
		}

		styledText.setCaretOffset(this.mCaretOffset);
		return true;
	}

	public void dump() {
		// TODO Auto-generated method stub

	}

	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("caretOffset", Integer.toString(mCaretOffset));
		attrMap.put("docOffset", Integer.toString(mDocOffset));
		return attrMap;
	}

	public Map<String, String> getDataMap() {
		return null;
	}

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr attr = null;
		
		if ((attr = commandElement.getAttributeNode("caretOffset")) != null) {
			mCaretOffset = Integer.parseInt(attr.getValue());
		}
		
		if ((attr = commandElement.getAttributeNode("docOffset")) != null) {
			mDocOffset = Integer.parseInt(attr.getValue());
		}
	}

	public String getCommandType() {
		return "MoveCaretCommand";
	}

	public String getName() {
		return "Move Caret (caret offset: " + mCaretOffset
				+ ", document offset: " + mDocOffset + ")";
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
