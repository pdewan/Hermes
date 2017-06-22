package difficultyPrediction.metrics;

public interface CommandCategoryMapping {

	public abstract String getSearchCommands();

	//	public void setSearchCommands(String searchCommands) {
	//		this.searchCommands = searchCommands;
	//	}
	public abstract String getDebugCommands();

	//	public void setDebugCommands(String debugCommands) {
	//		this.debugCommands = debugCommands;
	//	}
	public abstract String getEditOrInsertCommands();

	//	public void setEditOrInsertCommands(String editOrInsertCommands) {
	//		this.editOrInsertCommands = editOrInsertCommands;
	//	}
	public abstract String getFocusCommands();

	//	public void setFocusCommands(String focusCommands) {
	//		this.focusCommands = focusCommands;
	//	}
	public abstract String getRemoveCommands();

	//	public void setRemoveCommands(String removeCommands) {
	//		this.removeCommands = removeCommands;
	//	}
	public abstract CategorizedCommand[] getCommandsToCategories();

	public abstract void setCommandsToFeatureDesciptor(
			CategorizedCommand[] commandsToFeatureDesciptor);

	void map(CommandName aCommand, CommandCategory aFeatureName);

	String getUnclassifiedCommands();

	void map(CommandName[] aCommandNames, CommandCategory aFeatureName);

	String getNavigationCommands();

	CommandCategory getCommandCategory(CommandName aCommandName);

	CommandName searchCommandName(String anID);

	CommandCategory searchCommandCategory(String anID);

}