package difficultyPrediction.metrics;

import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
//import bus.uigen.hermes.HermesObjectEditorProxy;

public class ACommandCategoryMapping implements CommandCategoryMapping {
//	String searchFeature = new AString(FeatureName.SEARCH.toString());
//	String debugFeature = new AString(FeatureName.EDIT_OR_INSERT.toString());
//
//	String editOrInsertFeature = new AString(FeatureName.EDIT_OR_INSERT.toString());
//	String focusFeature = new AString(FeatureName.FOCUS.toString());
//	String removeFeature = new AString(FeatureName.REMOVE.toString());
	
	String searchCommands = "";
	String debugCommands = "";
	String editOrInsertCommands = "";
	String focusCommands = "";
	String removeCommands = "";
	String navigationCommands = "";
	String unclassifiedCommands = "";
	CategorizedCommand[] commandsToCategories =
			new CategorizedCommand[CommandName.values().length] ;
	public ACommandCategoryMapping() {
		initializeCommands();
		computeCommands();
	}
	protected void initializeCommands() {
		CommandName[] aCommandNames = CommandName.values();
		for (CommandName aCommandName: aCommandNames) {
			commandsToCategories[aCommandName.ordinal()] =
					new ACategorizedCommand(aCommandName);
		}
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
	@Override
	public String getEditOrInsertCommands() {
		return editOrInsertCommands;
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
	public void computeCommands () {
		searchCommands = "";
		debugCommands = "";
		editOrInsertCommands = "";
		focusCommands = "";
		removeCommands = "";
		navigationCommands = "";
		unclassifiedCommands = "";
		for (CategorizedCommand aCommand:commandsToCategories) {
			computeCommands(aCommand);
		}
		
	}
	public void computeCommands (CategorizedCommand aCommand) {
			switch (aCommand.getCategory()) {
//			case SEARCH: 
//				searchCommands += " " + aCommand.getCommand();
//				break;
			case DEBUG:
				debugCommands += " " + aCommand.getCommand();
				break;
			case EDIT_OR_INSERT:
				editOrInsertCommands += " " + aCommand.getCommand();
				break;	
			case FOCUS:
				focusCommands += " " + aCommand.getCommand();
				break;
			case REMOVE:
				removeCommands += " " + aCommand.getCommand();
				break;	
			case NAVIGATION:
				navigationCommands += " " + aCommand.getCommand();
			case OTHER:
				unclassifiedCommands += " " + aCommand.getCommand();
				break;
			}
	
		
	}
	@Override
	public void setCommandsToFeatureDesciptor(
			CategorizedCommand[] commandsToFeatureDesciptor) {
		this.commandsToCategories = commandsToFeatureDesciptor;
		
	}
	protected void mapButDoNotCompute (CommandName aCommand, CommandCategory aFeatureName) {		
		commandsToCategories[aCommand.ordinal()].setCategory(aFeatureName);
	}
	@Override
	public CommandCategory getCommandCategory(CommandName aCommandName) {
		int anIndex = aCommandName.ordinal();
		return commandsToCategories[anIndex].getCategory();
	}
	@Override
	public CommandName searchCommandName(String anID) {
		String aLowerCaseID = anID.toLowerCase();
		CommandName[] aCommandNames = CommandName.values();
		for (CommandName aCommandName:aCommandNames) {
			String aCommandNameString = aCommandName.toString();
			if (Character.isUpperCase(aCommandNameString.charAt(0)))
				continue;
			if (aLowerCaseID.contains(aCommandNameString))
				return aCommandName;
		}
		return null;
	}
	@Override
	public CommandCategory searchCommandCategory(String anID) {
		CommandName aCommandName = searchCommandName(anID);
		if (aCommandName == null) {
			return CommandCategory.OTHER;
		}
		return getCommandCategory(aCommandName);
	}
	@Override
	public void map (CommandName aCommand, CommandCategory aFeatureName) {		
		commandsToCategories[aCommand.ordinal()].setCategory(aFeatureName);
		computeCommands();
	}
	@Override
	public void map (CommandName[] aCommandNames, CommandCategory aFeatureName) {		
		for (CommandName aCommandName:aCommandNames) {
			mapButDoNotCompute (aCommandName, aFeatureName);
		}
		computeCommands();
	}
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
	public static void main (String[] args) {
		CommandCategoryMapping commandsToFeatures = new ACommandCategoryMapping();
//		commandsToFeatures.map(CommandName.BreakPointCommand, CommandCategory.DEBUG);
//		ObjectEditor.edit(commandsToFeatures);
		createUI(new ACommandCategoryMapping());
	}
	

}
