package difficultyPrediction.metrics;

import java.util.Set;

public interface CommandCategoryMapping {

	public abstract String getSearchCommands();


	public abstract String getDebugCommands();


//	public abstract String getEditOrInsertCommands();

	
	public abstract String getFocusCommands();

	
	public abstract String getRemoveCommands();

	
	public abstract CategorizedCommand[] getCommandsToCategories();

	public abstract void setCommandsToFeatureDesciptor(
			CategorizedCommand[] commandsToFeatureDesciptor);

	void map(CommandName aCommand, CommandCategory aFeatureName);

	String getUnclassifiedCommands();

	void map(CommandName[] aCommandNames, CommandCategory aFeatureName);

	String getNavigationCommands();

	Set<CommandCategory> getCommandCategories(CommandName aCommandName);

	Set<CommandName> searchCommandName(String anID);

//	CommandCategory searchCommandCategory(String anID);
	Set<CommandCategory> searchCommandCategories(String anID);


	String getFeatureName(CommandCategory aCommandCategory);

	String getEditCommands();

	String getInsertCommands();

	/**
	 * We will use these names when doing prediction from features
	 * By default a category is its name. 
	 * Subclasses will change it based on the weka file used
	 */
	String[] getCommandCategoryNames();

	/**
	 * This will be the order of the command categories in the arff file
	 */
	CommandCategory[] getOrderedRelevantCommandCategories();


	String[] getOrderedRelevantFeatureNames();

}