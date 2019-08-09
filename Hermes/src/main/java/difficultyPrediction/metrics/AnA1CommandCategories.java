package difficultyPrediction.metrics;

import bus.uigen.ObjectEditor;
/**
 * Same as 
 * @author dewan
 *
 */
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
	
	/*
	@Attribute editOrInsertPercentage numeric
	@Attribute debugPercentage numeric
	@Attribute navigationPercentage numeric
	@Attribute focusPercentage numeric
	@Attribute removePercentage numeric
	@Attribute stuck {YES,NO}
	*/
	protected CommandCategory[] relevantCategoresA1 = {
 			CommandCategory.INSERT, 
 			CommandCategory.DEBUG,
 			CommandCategory.NAVIGATION,
 			CommandCategory.FOCUS,
 			CommandCategory.REMOVE
 			
 	};
	 @Override
	    public CommandCategory[] getOrderedRelevantCommandCategories() {
			return relevantCategoresA1;
		}
	 /**
	     * These are the names in the  arff file header above
	     */
	 	protected void initializeCommandCategoryNames() {
			super.initializeCommandCategoryNames();				
			categoriesToNames[CommandCategory.INSERT.ordinal()] = "editOrInsertPercentage";
			categoriesToNames[CommandCategory.NAVIGATION.ordinal()] = "navigationPercentage";

		}
	
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
	protected void mapCategories() {
//		map (editOrInsertCategory(), CommandCategory.EDIT_OR_INSERT);
		map (insertCategory(), CommandCategory.INSERT);
		map (removeCategory(), CommandCategory.REMOVE);
		map (debugCategory(), CommandCategory.DEBUG);
		map (focusCategory(), CommandCategory.FOCUS);
		map (navigationCategory(), CommandCategory.NAVIGATION);
	}
	public AnA1CommandCategories() {
		this(true);
	}
	protected CommandName[] insertCategory() {
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
