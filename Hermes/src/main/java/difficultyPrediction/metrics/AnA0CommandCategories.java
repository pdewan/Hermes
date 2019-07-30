package difficultyPrediction.metrics;

import bus.uigen.ObjectEditor;
/**
 * 
 * @author dewan
 *
 */
public class AnA0CommandCategories extends ACommandCategoryMapping{
	

	/**
	 * edit and delete commands are mixed
	 */
	private CommandName[] editCatgory = {
			CommandName.CopyCommand,
			CommandName.CutCommand,
			CommandName.Delete,
			CommandName.Insert,
			CommandName.InsertStringCommand,
			CommandName.PasteCommand,
			CommandName.RedoCommand,
			CommandName.Replace,
			CommandName.SelectTextCommand,
			CommandName.UndoCommand,
			CommandName.MoveCaretCommand,
			CommandName.edit,			
	};
	// This is for A1 but AGenericRatioCalculator will use it to determine total events
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
	// This is for A1 but AGenericRatioCalculator will use it to determine total events

	private CommandName[] removeCategory = {
			CommandName.CutCommand,			
			CommandName.Delete,
			CommandName.delete,
	};
	private CommandName[] removeClassCategory = {			
	};
	
	
//	private CommandName[] exceptionCategory = {
//			CommandName.Exception
//	};
	
//	CommandName[] insertCommands = {
//			CommandName.CopyCommand,			
//			CommandName.Insert,
//			CommandName.InsertStringCommand,
//			CommandName.PasteCommand,
//			CommandName.Replace,
//			CommandName.MoveCaretCommand,
//			CommandName.SelectTextCommand,
//	};
	
//	CommandName[] removeCommands = {
////			CommandName.CutCommand,			
////			CommandName.Delete,
////			CommandName.delete,
//	};
//	private CommandName [] removeCommands = {
//			
//	};
//	
	private CommandName[] debugCategory = {
			CommandName.BreakPointCommand,			
			CommandName.ExceptionCommand,
			CommandName.RunCommand,			
	};
	
	private CommandName[] navigationCategory = {
			CommandName.FileOpenCommand,			
			CommandName.FindCommand,
			CommandName.view,			
	};
	private CommandName[] focusCategory = {
			CommandName.ShellCommand,			
					
	};
	
	public AnA0CommandCategories() {
		
		this (true);
		
		
	}
     public AnA0CommandCategories(boolean aMapCategories) {
		if (aMapCategories)
		mapCategories();
		
		
	}
     /* 2010 study arff file format
 	@attribute searchPercentage numeric
 	@attribute debugPercentage numeric
 	@attribute focusPercentage numeric
 	@attribute editPercentage numeric
 	@attribute removePercentage numeric
 	*/
     /**
      * This is a subset of the categories to which commands are mapped
      */
 	protected CommandCategory[] relevantCategoresA0 = {
 			CommandCategory.NAVIGATION, 
 			CommandCategory.DEBUG,
 			CommandCategory.FOCUS,
 			CommandCategory.EDIT,
 			CommandCategory.REMOVE_CLASS// the remove class attribute not used
 			
 			};
    @Override
    /**
     * These are the names in the 2010 study arff file header above
     */
 	protected void initializeCommandCategoryNames() {
		super.initializeCommandCategoryNames();
		categoriesToNames[CommandCategory.EDIT.ordinal()] = "editPercentage";
		categoriesToNames[CommandCategory.NAVIGATION.ordinal()] = "searchPercentage";
		categoriesToNames[CommandCategory.DEBUG.ordinal()] = "debugPercentage";
		categoriesToNames[CommandCategory.FOCUS.ordinal()] = "focusPercentage";		
		categoriesToNames[CommandCategory.REMOVE_CLASS.ordinal()] = "removePercentage";
		categoriesToNames[CommandCategory.REMOVE.ordinal()] = "removePercentage";	
		categoriesToNames[CommandCategory.INSERT.ordinal()] = "insertPercentage";
//		categoriesToNames[CommandCategory.REMOVE.ordinal()] = "removeTextPercentage";

	}
  
    @Override
    public CommandCategory[] getRelevantCommandCategories() {
		return relevantCategoresA0;
	}
 	
	protected void mapCategories() {
//		map (editOrInsertCategory(), CommandCategory.EDIT_OR_INSERT);
		map (editCategory(), CommandCategory.EDIT);
//		map (removeCategory(), CommandCategory.REMOVE);
		map (debugCategory(), CommandCategory.DEBUG);
		map (focusCategory(), CommandCategory.FOCUS);
		map (navigationCategory(), CommandCategory.NAVIGATION);
		map (removeClassCategory(), CommandCategory.REMOVE_CLASS );
		map (removeCategory(), CommandCategory.REMOVE);

		map (insertCategory(), CommandCategory.INSERT);
		
	}
	protected void initializeCommandCategoryName(CommandCategory aCommandCategory) {
		// make these null
		// should probably do this for remove also		
//		if (aCommandCategory.ordinal() > CommandCategory.DEBUG_RATE.ordinal() ) {
//			return;
//		}
		super.initializeCommandCategoryName(aCommandCategory);
	}
	/*
	 * No commands in remove category
	 */
	protected CommandName[] removeClassCategory() {
		return new CommandName[]{};
	}

//	protected CommandName[] editOrInsertCategory() {
//		return editCatgory;
//	}
	protected CommandName[] editCategory() {
		return editCatgory;
	}
	protected CommandName[] debugCategory() {
		return debugCategory;
	}
	protected CommandName[] navigationCategory() {
		return navigationCategory;
	}
	protected CommandName[] focusCategory() {
		return focusCategory;
	}
	protected CommandName[] remveClassCategory() {
		return removeClassCategory;
	}
	protected CommandName[] insertCategory() {
		return insertCategory;
	}
	protected CommandName[] removeCategory() {
		return removeCategory;
	}
	public static void main (String[] args) {
		CommandCategoryMapping commandsToFeatures = new AnA0CommandCategories();
		ObjectEditor.edit(commandsToFeatures);
	}
}
