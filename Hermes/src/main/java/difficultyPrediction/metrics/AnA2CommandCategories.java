package difficultyPrediction.metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bus.uigen.ObjectEditor;

public class AnA2CommandCategories extends AnA1CommandCategories{
	private CommandName[] additionsToDebugCategory = { 
			CommandName.CompileError 
	};

	
	
	
	
//	CommandName[] navigationCommands = {
//			CommandName.FileOpenCommand,			
//			CommandName.FindCommand,
//			CommandName.view,			
//	};
//	CommandName[] focusCommands = {
//			CommandName.ShellCommand,			
//					
//	};
	public AnA2CommandCategories(boolean aMapCategories) {
		
		super(false); // do not call map commands from super as our variables are not initialized at that point
		if (aMapCategories) {
			mapCategories();
		}
			
	}
	public AnA2CommandCategories() {
		this(true);
	}
	

	protected CommandName[] debugCategory() {
		List<CommandName> resultList = new ArrayList();
		resultList.addAll(Arrays.asList(super.debugCategory()));
		resultList.addAll(Arrays.asList(additionsToDebugCategory));
		return resultList.toArray(new CommandName[] {});
	}
	
	public static void main (String[] args) {
		CommandCategoryMapping commandsToFeatures = new AnA2CommandCategories();
		ObjectEditor.edit(commandsToFeatures);
	}
}
