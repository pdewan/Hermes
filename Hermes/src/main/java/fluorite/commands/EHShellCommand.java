package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.commands.ICommand;
import fluorite.model.EHEventRecorder;

public class EHShellCommand extends EHAbstractCommand{

	public EHShellCommand() {
		
	}
	
	public EHShellCommand(boolean activated, boolean closed, boolean deactivated, boolean deiconified, boolean iconified)
	{
		mActivated = activated;
		mClosed = closed;
		mDeactivated = deactivated;
		mDeiconified = deiconified;
		mIconified = iconified;
	}
	private boolean mActivated;
	private boolean mClosed;
	private boolean mDeactivated;
	private boolean mDeiconified;
	private boolean mIconified;

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
		
		String name = "";
		
		if(mActivated == true)
			name = "ECLIPSE_GAINED_FOCUS";
		if(mClosed == true)
			name = "ECLIPSE_CLOSED";
		if(mDeactivated == true)
			name = "ECLIPSE_LOST_FOCUS";
		if(mDeiconified == true)
			name = "ECLIPSE_MAXIMIZED";
		if(mIconified == true)
			name = "ECLIPSE_MINIMIZED";
		
		attrMap.put("type",name);

		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandType() {

		return "ShellCommand";
	}

	@Override
	public String getName() {
		String name = "";
		
		if(mActivated == true)
			name = "ECLIPSE_GAINED_FOCUS";
		if(mClosed == true)
			name = "ECLIPSE_CLOSED";
		if(mDeactivated == true)
			name = "ECLIPSE_LOST_FOCUS";
		if(mDeiconified == true)
			name = "ECLIPSE_MAXIMIZED";
		if(mIconified == true)
			name = "ECLIPSE_MINIMIZED";
		
		return name;
	}
	
	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr attr = null;
		
		if ((attr = commandElement.getAttributeNode("type")) != null) {
			if (attr.getValue().equals("ECLIPSE_GAINED_FOCUS"))
			{
				mActivated = true;
				mClosed = false;
				mDeactivated = false;
				mDeiconified = false;
				mIconified = false;
			}
			
			if (attr.getValue().equals("ECLIPSE_CLOSED"))
			{
				mActivated = false;
				mClosed = true;
				mDeactivated = false;
				mDeiconified = false;
				mIconified = false;
			}
			
			if (attr.getValue().equals("ECLIPSE_LOST_FOCUS"))
			{
				mActivated = false;
				mClosed = false;
				mDeactivated = true;
				mDeiconified = false;
				mIconified = false;
			}
			
			if (attr.getValue().equals("ECLIPSE_MAXIMIZED"))
			{
				mActivated = false;
				mClosed = false;
				mDeactivated = false;
				mDeiconified = true;
				mIconified = false;
			}
			
			if (attr.getValue().equals("ECLIPSE_MINIMIZED"))
			{
				mActivated = false;
				mClosed = false;
				mDeactivated = false;
				mDeiconified = false;
				mIconified = true;
			}
		}
	}

	@Override
	public String getDescription() {
		
		return null;
	}

	@Override
	public String getCategory() {
		return EHEventRecorder.MacroCommandCategory;
	}

	@Override
	public String getCategoryID() {
		return EHEventRecorder.MacroCommandCategoryID;
	}

	@Override
	public boolean combine(ICommand anotherCommand) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String toString() {
		return super.toString() + ":" + getName();
	}

}
