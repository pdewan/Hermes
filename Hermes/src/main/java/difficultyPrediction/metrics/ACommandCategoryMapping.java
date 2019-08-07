package difficultyPrediction.metrics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import util.models.HashcodeSet;
//import bus.uigen.hermes.HermesObjectEditorProxy;
/**
 * Super class of various mapping classes such as A1, A2, wtc
 * Why is this not an abstract class?
 * @author dewan
 *
 */

public abstract class ACommandCategoryMapping implements CommandCategoryMapping {

	
	String searchCommands = "";
	String debugCommands = "";
	String editCommands = "";
//	String editOrInsertCommands = "";
	String insertCommands = "";

//	String editOrInsertCommands = "";

	String focusCommands = "";
	String removeCommands = "";
	String navigationCommands = "";
	String unclassifiedCommands = "";
	/**
	 * why was this not a hashmap. For display purposes?
	 */
	CategorizedCommand[] commandsToCategories =
			new CategorizedCommand[CommandName.values().length] ;
	String[] categoriesToNames =
			new String[CommandCategory.values().length] ;
	public ACommandCategoryMapping() {
		initializeCommands();
		initializeCommandCategoryNames();
		createCategoryToCommandStrings();
	}
	
//	protected abstract void initializeCommands();
	/**
	 * This should really be an abstract method as all commands are OTHER
	 * Sbclases must be calling the setter in each command
	 */
	protected void initializeCommands() {
		CommandName[] aCommandNames = CommandName.values();
		for (CommandName aCommandName: aCommandNames) {
			commandsToCategories[aCommandName.ordinal()] =
					new ACategorizedCommand(aCommandName);
		}
	}
	@Override
	public String[] getCommandCategoryNames() {
		return categoriesToNames;
	}
	@Override

	public CommandCategory[] getOrderedRelevantCommandCategories() {
		return CommandCategory.values();
	}
	String[] orderedRelevantCommandCategoryNames;
	@Override
	public String[] getOrderedRelevantFeatureNames() {
		if (orderedRelevantCommandCategoryNames == null) {
			CommandCategory[] aRelevantCommandCategories = getOrderedRelevantCommandCategories();
			orderedRelevantCommandCategoryNames = new String[aRelevantCommandCategories.length];
			for (int i = 0; i < aRelevantCommandCategories.length; i++ ) {
				orderedRelevantCommandCategoryNames[i] = getFeatureName(aRelevantCommandCategories[i]);
			}
		}
		return orderedRelevantCommandCategoryNames;
	}
	
	protected void initializeCommandCategoryNames() {
		CommandCategory[] aCommandCategories = CommandCategory.values();
		for (CommandCategory aCommandCategory: aCommandCategories) {
			initializeCommandCategoryName(aCommandCategory);
//			categoriesToNames[aCommandCategory.ordinal()] =
//					aCommandCategory.toString();		
		}
	}
//	protected void mapCommandCategoryToFeatureName() {
//		commandCategoryToFetaureName.put(CommandCategory.EDIT, "Edit");
//		commandCategoryToFetaureName.put(CommandCategory.INSERT, "Insert");
//
//
////		commandCategoryToFetaureName.put(CommandCategory.EDIT_OR_INSERT, "Edit");
//		commandCategoryToFetaureName.put(CommandCategory.DEBUG, "Debug");
//		commandCategoryToFetaureName.put(CommandCategory.NAVIGATION, "Navigation");
//		commandCategoryToFetaureName.put(CommandCategory.FOCUS, "Focus");
//		commandCategoryToFetaureName.put(CommandCategory.REMOVE, "RemoveClass");
//		commandCategoryToFetaureName.put(CommandCategory.OTHER, "Unclassified");
////		commandCategoryToFetaureName.put(CommandCategory.SEARCH, "Navigation");
//
//
//
//	}
	protected void initializeCommandCategoryName(CommandCategory aCommandCategory) {
		categoriesToNames[aCommandCategory.ordinal()] =
				aCommandCategory.toString();
	}
	@Override
	public String getFeatureName(CommandCategory aCommandCategory) {
		return categoriesToNames[aCommandCategory.ordinal()];
	}
	
	@Override
	public String getSearchCommands() {
		return searchCommands;
	}
//	public void setSearchCommands(String searchCommands) {
//		this.searchCommands = searchCommands;
//	}
	@Override
	public String getDebugCommands() {
		return debugCommands;
	}
//	public void setDebugCommands(String debugCommands) {
//		this.debugCommands = debugCommands;
//	}
//	@Override
//	public String getEditOrInsertCommands() {
//		return editOrInsertCommands;
//	}
	@Override
	public String getEditCommands() {
		return editCommands;
	}
	@Override
	public String getInsertCommands() {
		return insertCommands;
	}
//	public void setEditOrInsertCommands(String editOrInsertCommands) {
//		this.editOrInsertCommands = editOrInsertCommands;
//	}
	@Override
	public String getFocusCommands() {
		return focusCommands;
	}
//	public void setFocusFeature(String focusFeature) {
//		this.focusCommands = focusCommands;
//	}
	@Override
	public String getRemoveCommands() {
		return removeCommands;
	}
	@Override
	public String getNavigationCommands() {
		return navigationCommands;
	}
	@Override
	public String getUnclassifiedCommands() {
		return unclassifiedCommands;
	}
//	public void setRemoveCommands(String removeFeature) {
//		this.removeFeatures = removeFeatures;
//	}
	@Override
	public CategorizedCommand[] getCommandsToCategories() {
		return commandsToCategories;
	}
	/**
	 * Process the commandsToCategoriesArray to create the strings
	 * displayed for each category. Seems only for display purposes.
	 * .
	 */
	public void createCategoryToCommandStrings () {
		searchCommands = "";
		debugCommands = "";
//		editOrInsertCommands = "";
		editCommands = "";
		insertCommands = "";
		focusCommands = "";
		removeCommands = "";
		navigationCommands = "";
		unclassifiedCommands = "";
		for (CategorizedCommand aCommand:commandsToCategories) {
			addCommandToCategoryString(aCommand);
		}
		
	}
	/**
	 * Process the commandsToCategoriesArray to create the counts for
	 * each category. This is probably used for prediction.
	 * .
	 */
	public void addCommandToCategoryString (CategorizedCommand aCommand) {
		Set<CommandCategory> aCommandCategories = aCommand.getCategories();
		for (CommandCategory aCommandCategory:aCommandCategories) {
		switch (aCommandCategory) {
//		case SEARCH: 
//			searchCommands += " " + aCommand.getCommand();
//			break;
		case DEBUG:
			debugCommands += " " + aCommand.getCommand();
			break;
//		case EDIT_OR_INSERT:
//			editOrInsertCommands += " " + aCommand.getCommand();
//			break;	
		case EDIT:
			editCommands += " " + aCommand.getCommand(); 
			break;
		case INSERT:
			insertCommands += " " + aCommand.getCommand(); 
			break;
		case FOCUS:
			focusCommands += " " + aCommand.getCommand();
			break;
		case REMOVE:
			removeCommands += " " + aCommand.getCommand();
			break;	
			
		case NAVIGATION:
			navigationCommands += " " + aCommand.getCommand();
			break;
		case OTHER:
			unclassifiedCommands += " " + aCommand.getCommand();
			break;
		}
		}

	
}
//	public void addCommandToCategoryString (CategorizedCommand aCommand) {
//		
//			switch (aCommand.getCategory()) {
////			case SEARCH: 
////				searchCommands += " " + aCommand.getCommand();
////				break;
//			case DEBUG:
//				debugCommands += " " + aCommand.getCommand();
//				break;
////			case EDIT_OR_INSERT:
////				editOrInsertCommands += " " + aCommand.getCommand();
////				break;	
//			case EDIT:
//				editCommands += " " + aCommand.getCommand(); 
//				break;
//			case INSERT:
//				insertCommands += " " + aCommand.getCommand(); 
//				break;
//			case FOCUS:
//				focusCommands += " " + aCommand.getCommand();
//				break;
//			case REMOVE:
//				removeCommands += " " + aCommand.getCommand();
//				break;	
//				
//			case NAVIGATION:
//				navigationCommands += " " + aCommand.getCommand();
//			case OTHER:
//				unclassifiedCommands += " " + aCommand.getCommand();
//				break;
//			}
//	
//		
//	}
	/**
	 * Need to change the name, Nils may have subclassed this
	 */
	@Override
	public void setCommandsToFeatureDesciptor(
			CategorizedCommand[] commandsToFeatureDesciptor) {
		this.commandsToCategories = commandsToFeatureDesciptor;
		
	}
	
	@Override
	/**
	 * Convert a command name to a command category
	 */
	public Set<CommandCategory> getCommandCategories(CommandName aCommandName) {
		int anIndex = aCommandName.ordinal();
		return commandsToCategories[anIndex].getCategories();
	}
	/**
	 * Convert a command ID to a command name with lower case
	 */
	@Override
	public Set<CommandName> searchCommandName(String anID) {
		Set<CommandName> retVal = new HashSet();
		String aLowerCaseID = anID.toLowerCase();
		CommandName[] aCommandNames = CommandName.values();
		for (CommandName aCommandName:aCommandNames) {
			String aCommandNameString = aCommandName.toString();
			// A few commands start with lowecase, the atomic ones I guess
			// ignore if command starts with lower case
			if (Character.isUpperCase(aCommandNameString.charAt(0)))
				continue;
			if (aLowerCaseID.contains(aCommandNameString))
				retVal.add(aCommandName);
//				return aCommandName; 
			// editor contains edit but us debug command
	//		autogen:::org.eclipse.jdt.debug.compilationuniteditor.breakpointruleractions/org.eclipse.jdt.debug.ui.actions.managebreakpointruleraction
		}
		return retVal;
	}
	protected Set<CommandCategory> emptyCommandCategory = new HashSet();
	/**
	 * Given an ID return the commandName and then the commandCategory
	 */
	@Override
	public Set<CommandCategory> searchCommandCategories(String anID) {
		Set<CommandName> aCommandNames = searchCommandName(anID); // there can be multiple commandNames too
		if (aCommandNames.isEmpty()) {
			return emptyCommandCategory;
		}
		Set<CommandCategory> retVal = new HashSet();
		for (CommandName aCommandName: aCommandNames) {
			retVal.addAll(getCommandCategories(aCommandName));
		}
		return retVal;
	}
	/**
	 * This is the method called to change the command to a different category
	 */
	protected void mapButDoNotCompute (CommandName aCommand, CommandCategory aFeatureName) {		
//		commandsToCategories[aCommand.ordinal()].setCategory(aFeatureName);
		commandsToCategories[aCommand.ordinal()].getCategories().add(aFeatureName);

	}
	@Override
	public void map (CommandName aCommand, CommandCategory aFeatureName) {		
//		commandsToCategories[aCommand.ordinal()].setCategory(aFeatureName);
		mapButDoNotCompute(aCommand, aFeatureName); // reuse the method above
		createCategoryToCommandStrings();
	}
	/**
	 * Subclasses call this method
	 */
	@Override
	public void map (CommandName[] aCommandNames, CommandCategory aFeatureName) {		
		for (CommandName aCommandName:aCommandNames) {
			mapButDoNotCompute (aCommandName, aFeatureName);
		}
		createCategoryToCommandStrings();
	}
	/**
	 * The whole class is geared to being displayed by OE
	 * @param aMapping
	 * @return
	 */
	public static OEFrame createUI(CommandCategoryMapping aMapping) {
		OEFrame aFrame = ObjectEditor.edit(aMapping);
		aFrame.setSize(500, 800);
		return aFrame;
		
	}
//	public static Object createUI(CommandCategoryMapping aMapping) {
//		Object aFrame = HermesObjectEditorProxy.edit(aMapping, 500, 800);
////		aFrame.setSize(500, 800);
//		return aFrame;
//		
//	}
//	public static void main (String[] args) {
//		CommandCategoryMapping commandsToFeatures = new ACommandCategoryMapping();
////		commandsToFeatures.map(CommandName.BreakPointCommand, CommandCategory.DEBUG);
////		ObjectEditor.edit(commandsToFeatures);
//		createUI(new ACommandCategoryMapping());
//	}
	

}
