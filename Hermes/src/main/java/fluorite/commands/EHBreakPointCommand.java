package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.commands.ICommand;
import fluorite.model.EHEventRecorder;

public class EHBreakPointCommand extends EHAbstractCommand{

	public static final String XML_BreakPointLineNumber_Tag = "breakPointLineNumberString";
	
	public EHBreakPointCommand()
	{
		
	}
	
	public EHBreakPointCommand(String lineNumber, boolean breakPointAdded)
	{
		mLineNumber = lineNumber;
		mBreakPointAdded = breakPointAdded;
	}
	
	private boolean mBreakPointAdded;
	private String mLineNumber;
	
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
		attrMap.put("type", mBreakPointAdded ? "BreakPointAdded" : "BreakPointRemoved");
		attrMap.put("lineNumber", mLineNumber);

		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		return null;
	}

	@Override
	public String getCommandType() {
		// TODO Auto-generated method stub
		return "BreakPointCommand";
	}
	
	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr attr = null;
		
		if ((attr = commandElement.getAttributeNode("type")) != null) {
			mBreakPointAdded = Boolean.parseBoolean(attr.getValue());
		}
		
		if ((attr = commandElement.getAttributeNode("lineNumber")) != null) {
			mLineNumber = attr.getValue();
		}
	}

	@Override
	public String getName() {
		String name;
		if(mBreakPointAdded)
			name = "BreakPointAdded";
		else
			name = "BreakPointRemoved";
		return name;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return EHEventRecorder.MacroCommandCategory;
	}

	@Override
	public String getCategoryID() {
		// TODO Auto-generated method stub
		return EHEventRecorder.MacroCommandCategoryID;
	}

	@Override
	public boolean combine(ICommand anotherCommand) {
		// TODO Auto-generated method stub
		return false;
	}

}
