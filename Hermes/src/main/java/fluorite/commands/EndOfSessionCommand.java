package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import fluorite.model.EHEventRecorder;
import fluorite.model.StatusConsts;

public class EndOfSessionCommand extends AbstractCommand{
	
	public static final String SESSION_NUMBER = "sessionNumber";
	
	public EndOfSessionCommand(int aSessionNumber)
	{
		mSessionNumber = aSessionNumber;
	}
	
	public EndOfSessionCommand() {
		// TODO Auto-generated constructor stub
	}

	int mSessionNumber;

	public int getSessionNumber() {
		return mSessionNumber;
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
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr attr = null;
		if ((attr = commandElement.getAttributeNode(SESSION_NUMBER)) != null) {
			mSessionNumber = Integer.parseInt(attr.getValue());
			
		}
	}

	@Override
	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put(SESSION_NUMBER, Integer.toString(mSessionNumber));
		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandType() {
		// TODO Auto-generated method stub
		return "AggregatedStatusCommand";
	}

	@Override
	public String getName() {
		return Integer.toString(mSessionNumber);
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
	public boolean combine(EHICommand anotherCommand) {
		// TODO Auto-generated method stub
		return false;
	}

}
