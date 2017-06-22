package difficultyPrediction.metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bus.uigen.ObjectEditor;

public class AnA3CommandCategories extends AnA2CommandCategories{
	private CommandName[] additionsToInsertCategory = { 
			CommandName.RedoCommand,
			CommandName.CutCommand
	};
	private CommandName[] deletionsToInsertCategory = { 
			CommandName.MoveCaretCommand 
	};
	private CommandName[] additionsToRemoveCategory = { 
			CommandName.UndoCommand,
	};
	private CommandName[] deletionsToRemoveCategory = { 
			CommandName.delete ,
			CommandName.CutCommand
	};
	
	
	public AnA3CommandCategories(boolean aMapCategories) {
		
		super(false); // do not call map commands from super as our variables are not initialized at that point
		if (aMapCategories) {
			mapCategories();
		}
			
	}
	public AnA3CommandCategories() {
		this(true);
	}
	
	@Override
	protected CommandName[] editOrInsertCategory() {
		List<CommandName> resultList = new ArrayList();
		resultList.addAll(Arrays.asList(super.editOrInsertCategory()));
		resultList.addAll(Arrays.asList(additionsToInsertCategory));
		resultList.removeAll(Arrays.asList(deletionsToInsertCategory));
		return resultList.toArray(new CommandName[] {});
	}
	@Override
	protected CommandName[] removeCategory() {
		List<CommandName> resultList = new ArrayList();
		resultList.addAll(Arrays.asList(super.removeCategory()));
		resultList.addAll(Arrays.asList(additionsToRemoveCategory));
		resultList.removeAll(Arrays.asList(deletionsToRemoveCategory));
		return resultList.toArray(new CommandName[] {});
	}
	
	public static void main (String[] args) {
		CommandCategoryMapping commandsToFeatures = new AnA3CommandCategories();
		ObjectEditor.edit(commandsToFeatures);
	}
}
