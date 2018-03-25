package fluorite.commands;

import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;

import fluorite.model.EHEventRecorder;

public class RedoCommand
extends AbstractCommand
//	extends RedoCommand 
	implements EHICommand {
	
	public RedoCommand() {
	}

	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}

	public void dump() {
		// TODO Auto-generated method stub

	}

	public Map<String, String> getAttributesMap() {
		return null;
	}

	public Map<String, String> getDataMap() {
		return null;
	}

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
	}

	public String getCommandType() {
		return "RedoCommand";
	}

	public String getName() {
		return "Redo";
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

	@Override
	public boolean combine(EHICommand anotherCommand) {
		if (!(anotherCommand instanceof RedoCommand)) {
			return false;
		}

		return true;
	}

}
