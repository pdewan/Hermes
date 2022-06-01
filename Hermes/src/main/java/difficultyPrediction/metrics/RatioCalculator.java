package difficultyPrediction.metrics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import difficultyPrediction.APredictionParameters;
import difficultyPrediction.featureExtraction.RatioFeatures;
import fluorite.commands.CompilationCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.EclipseCommand;

public interface RatioCalculator {

	public abstract boolean isDebugEvent(EHICommand event);

//	public abstract boolean isInsertOrEditEvent(EHICommand event);
	


	public abstract boolean isNavigationEvent(EHICommand event);

	public abstract boolean isFocusEvent(EHICommand event);

	public abstract boolean isAddRemoveEvent(EHICommand event);

	public abstract ArrayList<Double> computeMetrics(List<EHICommand> userActions);

	public abstract ArrayList<Integer> getPercentageData(
			List<EHICommand> userActions);

//	public abstract List<String> getFeatureNames(EHICommand myEvent);

	RatioFeatures computeRatioFeatures(List<EHICommand> userActions);

	RatioFeatures computeFeatures(List<EHICommand> userActions);

	boolean isInsertEvent(EHICommand event);

	boolean isEditEvent(EHICommand event);

	CommandCategory[] getComputedFeatures();

	List<String> getComputedFeatureNames();
//	public static List<CommandCategory> toCommandCategories(EHICommand aCommand) {
//		CommandCategoryMapping aCommandCategories = APredictionParameters.getInstance().
//						getCommandClassificationScheme().
//							getCommandCategoryMapping();
//		String aCommandString = aCommand.getCommandType();
//		if (aCommand instanceof CompilationCommand) {
//			CompilationCommand command = (CompilationCommand)aCommand;
//			if (!command.getIsWarning()) {
//				return aCommandCategories.getCommandCategories(CommandName.CompileError);
//			}
//		}
//		if (aCommand instanceof EclipseCommand) {
//			aCommandString = ((EclipseCommand) aCommand).getCommandID();
//			return aCommandCategories.searchCommandCategories(aCommandString);
//			
//		} else {
//			try {
//			CommandName aCommandName = CommandName.valueOf(aCommandString);
//			return aCommandCategories.getCommandCategories(aCommandName);
//			} catch (Exception e) {
////				return CommandCategory.OTHER;
//				return emptyCommandCategories;
//			}
////			return aCommandCategories.getCommandCategory(aCommandName)
//		}
//		
//		
//	}
	
	public static Set<CommandCategory> toCommandCategories(EHICommand aCommand) {
		
//		List<CommandCategory> retVal;
		CommandCategoryMapping aCommandCategories = APredictionParameters.getInstance().
						getCommandClassificationScheme().
							getCommandCategoryMapping();
		
		String aCommandString = aCommand.getCommandType();
		CommandName aCommandName = null;
		try {
			 aCommandName = CommandName.valueOf(aCommandString);
		}  catch (Exception e) {
			// ugh this is not really an exception
		}
		if (aCommandName != null) {
			return aCommandCategories.getCommandCategories(aCommandName);
		}
		if (aCommand instanceof CompilationCommand) {
			CompilationCommand command = (CompilationCommand)aCommand;
			if (!command.getIsWarning()) {
				return aCommandCategories.getCommandCategories(CommandName.CompileError);
			}
		}
		if (aCommand instanceof EclipseCommand) {
			String aCommandId = ((EclipseCommand) aCommand).getCommandID();
			if (!aCommandId.isEmpty()) {
				return aCommandCategories.searchCommandCategories(aCommandId);
			} else {
				return emptyCommandCategories;
			}
			
//			return aCommandCategories.searchCommandCategories(aCommandId);
			
		} 
		return aCommandCategories.searchCommandCategories(aCommandString);
		
		
	}
    static final Set<CommandCategory> emptyCommandCategories= new HashSet();

	public static List<String> getFeatureNames(EHICommand myEvent) {
		Set<CommandCategory> aCommandCategories = toCommandCategories(myEvent);
		List<String> aFeatureNames = new ArrayList();
		CommandCategoryMapping aCommandCategoryMapping = APredictionParameters.getInstance().
				getCommandClassificationScheme().
				getCommandCategoryMapping();
		for (CommandCategory aCommandCategory:aCommandCategories) {
			aFeatureNames.add(aCommandCategoryMapping.getFeatureName(aCommandCategory));
		}
		return aFeatureNames;
//		return commandCategories.getFeatureName(aCommandCategory);
		
//		if (isInsertOrEditEvent(myEvent)) {
//			return "Edit";
//
//		} else if (isDebugEvent(myEvent)) {
//			return "Debug";
//
//		} else if (isNavigationEvent(myEvent)) {
//			return "Navigation";
//
//		} else if (isFocusEvent(myEvent)) {
//			return "Focus";
//
//		} else if (isAddRemoveEvent(myEvent)) {
//			return "RemoveClass";
//		} else {
//			return "Unclassified";
//		}
	}

//	boolean[] getComputedFeatures();

}