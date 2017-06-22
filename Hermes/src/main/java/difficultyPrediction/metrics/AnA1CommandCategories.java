package difficultyPrediction.metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bus.uigen.ObjectEditor;

public class AnA1CommandCategories extends AnA0CommandCategories{
//	CommandName[] editOrInsertCommands = {
//			CommandName.CopyCommand,
//			CommandName.CutCommand,
//			CommandName.Delete,
//			CommandName.Insert,
//			CommandName.InsertStringCommand,
//			CommandName.PasteCommand,
//			CommandName.RedoCommand,
//			CommandName.Replace,
//			CommandName.SelectTextCommand,
//			CommandName.UndoCommand,
//			CommandName.MoveCaretCommand,
//			CommandName.edit,			
//	};
//	CommandName[] exceptionCommands = {
//			CommandName.Exception
//	};
	
	private CommandName[] insertCategory = {
			CommandName.CopyCommand,			
			CommandName.Insert,
			CommandName.InsertStringCommand,
			CommandName.PasteCommand,
			CommandName.Replace,
			CommandName.MoveCaretCommand,
			CommandName.SelectTextCommand,
			CommandName.edit,
	};
	
	private CommandName[] removeCategory = {
			CommandName.CutCommand,			
			CommandName.Delete,
			CommandName.delete,
	};
	
//	private CommandName[] additionsToDebugCategory = {
//			CommandName.CompileError
//	};
	
	
//	CommandName[] debugCommands = {
//			CommandName.BreakPointCommand,			
//			CommandName.ExceptionCommand,
//			CommandName.RunCommand,			
//	};
//	
//	CommandName[] navigationCommands = {
//			CommandName.FileOpenCommand,			
//			CommandName.FindCommand,
//			CommandName.view,			
//	};
//	CommandName[] focusCommands = {
//			CommandName.ShellCommand,			
//					
//	};
	
	
	public AnA1CommandCategories(boolean aMapCategories) {
	
		super(false); // do not call map commands from super as our variables are not initialized at that point
		if (aMapCategories) {
			mapCategories();
		}
			
	}
	public AnA1CommandCategories() {
		this(true);
	}
	@Override
	protected CommandName[] editOrInsertCategory() {
		return insertCategory;
	}
	@Override
	protected CommandName[] removeCategory() {
		return removeCategory;
	}
//	protected void mapCategories() {
//		map (insertCategory(), CommandCategory.EDIT_OR_INSERT);
//		map (removeCategory(), CommandCategory.REMOVE);
//		map (debugCategory(), CommandCategory.DEBUG);
//		map (focusCategory(), CommandCategory.FOCUS);
//		map (navigationCategory(), CommandCategory.NAVIGATION);
//	}
//	protected CommandName[] debugCategory() {
//		List<CommandName> resultList = new ArrayList();
//		resultList.addAll(Arrays.asList(super.debugCategory()));
////		resultList.addAll(Arrays.asList(additionsToDebugCategory));
//		return resultList.toArray(new CommandName[]{});
//		
//	}
//	private void mapCategories() {
//		map (insertCommands, CommandCategory.EDIT_OR_INSERT);
//		map (removeCommands, CommandCategory.REMOVE);
//		map (debugCommands, CommandCategory.DEBUG);
//		map (focusCommands, CommandCategory.FOCUS);
//		map (navigationCommands, CommandCategory.NAVIGATION);
//	}
	public static void main (String[] args) {
		CommandCategoryMapping commandsToFeatures = new AnA1CommandCategories();
		ObjectEditor.edit(commandsToFeatures);
	}
}
