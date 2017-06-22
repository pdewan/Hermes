package difficultyPrediction.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bus.uigen.visitors.IsEditedAdapterVisitor;
import difficultyPrediction.APredictionParameters;
import difficultyPrediction.featureExtraction.ARatioFeatures;
import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.featureExtraction.RatioFeaturesFactorySelector;
import fluorite.commands.EHCompilationCommand;
import fluorite.commands.EHEclipseCommand;
import fluorite.commands.EHICommand;

public class AGenericRatioCalculator implements RatioCalculator {
	
	public final static int DEBUG_EVENT_INDEX = 0;
	public final static int SEARCH_EVENT_INDEX = 1;
	public final static int EDIT_EVENT_INDEX = 2;
	public final static int FOCUS_EVENT_INDEX = 3;
	public final static int REMOVE_EVENT_INDEX = 4;
	static RatioCalculator instance = new AGenericRatioCalculator();
	protected Map<CommandCategory, String> commandCategoryToFetaureName = new HashMap();
	
	public AGenericRatioCalculator() {
		mapCommandCategoryToFeatureName();
	}
	

	public static CommandCategory toCommandCategory(EHICommand aCommand) {
		CommandCategoryMapping aCommandCategories = APredictionParameters.getInstance().
						getCommandClassificationScheme().
							getCommandCategoryMapping();
		String aCommandString = aCommand.getCommandType();
		if (aCommand instanceof EHCompilationCommand) {
			EHCompilationCommand command = (EHCompilationCommand)aCommand;
			if (!command.getIsWarning()) {
				return aCommandCategories.getCommandCategory(CommandName.CompileError);
			}
		}
		if (aCommand instanceof EHEclipseCommand) {
			aCommandString = ((EHEclipseCommand) aCommand).getCommandID();
			return aCommandCategories.searchCommandCategory(aCommandString);
			
		} else {
			try {
			CommandName aCommandName = CommandName.valueOf(aCommandString);
			return aCommandCategories.getCommandCategory(aCommandName);
			} catch (Exception e) {
				return CommandCategory.OTHER;
			}
//			return aCommandCategories.getCommandCategory(aCommandName)
		}
		
		
	}
	
	@Override
	public boolean isDebugEvent(EHICommand event) {
		return toCommandCategory(event) == CommandCategory.DEBUG;
	}

	@Override
	public boolean isInsertOrEditEvent(EHICommand event) {
		return toCommandCategory(event) == CommandCategory.EDIT_OR_INSERT;

	}

	@Override
	public boolean isNavigationEvent(EHICommand event) {
		return toCommandCategory(event) == CommandCategory.NAVIGATION;

	}

	@Override
	public boolean isFocusEvent(EHICommand event) {
		return toCommandCategory(event) == CommandCategory.FOCUS;

	}

	@Override
	public boolean isAddRemoveEvent(EHICommand event) {
		return toCommandCategory(event) == CommandCategory.REMOVE;
	}
	public static double computeDebugPercentage(ArrayList<Integer> eventData) {
		double numberOfDebugEvents = eventData.get(DEBUG_EVENT_INDEX);
		double numberOfSearchEvents = eventData.get(SEARCH_EVENT_INDEX);
		double numberOfEditEvents = eventData.get(EDIT_EVENT_INDEX);
		double numberOfFocusEvents = eventData.get(FOCUS_EVENT_INDEX);
		double numberOfRemoveEvents = eventData.get(REMOVE_EVENT_INDEX);

		double debugPercentage = 0;

		if (numberOfDebugEvents > 0)
			debugPercentage = (numberOfDebugEvents / (numberOfDebugEvents
					+ numberOfSearchEvents + numberOfEditEvents
					+ numberOfFocusEvents + numberOfRemoveEvents)) * 100;

		return debugPercentage;
	}

	public static double computeNavigationPercentage(
			ArrayList<Integer> eventData) {
		double numberOfDebugEvents = eventData.get(DEBUG_EVENT_INDEX);
		double numberOfSearchEvents = eventData.get(SEARCH_EVENT_INDEX);
		double numberOfEditEvents = eventData.get(EDIT_EVENT_INDEX);
		double numberOfFocusEvents = eventData.get(FOCUS_EVENT_INDEX);
		double numberOfRemoveEvents = eventData.get(REMOVE_EVENT_INDEX);

		double searchPercentage = 0;

		if (numberOfSearchEvents > 0)
			searchPercentage = (numberOfSearchEvents / (numberOfDebugEvents
					+ numberOfSearchEvents + numberOfEditEvents
					+ numberOfFocusEvents + numberOfRemoveEvents)) * 100;

		return searchPercentage;
	}

	public static double computeEditPercentage(ArrayList<Integer> eventData) {
		double numberOfDebugEvents = eventData.get(DEBUG_EVENT_INDEX);
		double numberOfSearchEvents = eventData.get(SEARCH_EVENT_INDEX);
		double numberOfEditEvents = eventData.get(EDIT_EVENT_INDEX);
		double numberOfFocusEvents = eventData.get(FOCUS_EVENT_INDEX);
		double numberOfRemoveEvents = eventData.get(REMOVE_EVENT_INDEX);

		double editPercentage = 0;

		if (numberOfEditEvents > 0)
			editPercentage = (numberOfEditEvents / (numberOfDebugEvents
					+ numberOfSearchEvents + numberOfEditEvents
					+ numberOfFocusEvents + numberOfRemoveEvents)) * 100;

		return editPercentage;
	}

	public static double computeFocusPercentage(ArrayList<Integer> eventData) {
		double numberOfDebugEvents = eventData.get(DEBUG_EVENT_INDEX);
		double numberOfSearchEvents = eventData.get(SEARCH_EVENT_INDEX);
		double numberOfEditEvents = eventData.get(EDIT_EVENT_INDEX);
		double numberOfFocusEvents = eventData.get(FOCUS_EVENT_INDEX);
		double numberOfRemoveEvents = eventData.get(REMOVE_EVENT_INDEX);

		double focusPercentage = 0;

		if (numberOfFocusEvents > 0)
			focusPercentage = (numberOfFocusEvents / (numberOfDebugEvents
					+ numberOfSearchEvents + numberOfEditEvents
					+ numberOfFocusEvents + numberOfRemoveEvents)) * 100;

		return focusPercentage;
	}

	public static double computeRemoveEventsPercentage(
			ArrayList<Integer> eventData) {
		double numberOfDebugEvents = eventData.get(DEBUG_EVENT_INDEX);
		double numberOfSearchEvents = eventData.get(SEARCH_EVENT_INDEX);
		double numberOfEditEvents = eventData.get(EDIT_EVENT_INDEX);
		double numberOfFocusEvents = eventData.get(FOCUS_EVENT_INDEX);
		double numberOfRemoveEvents = eventData.get(REMOVE_EVENT_INDEX);

		double removePercentage = 0;

		if (numberOfRemoveEvents > 0)
			removePercentage = (numberOfRemoveEvents / (numberOfDebugEvents
					+ numberOfSearchEvents + numberOfEditEvents
					+ numberOfFocusEvents + numberOfRemoveEvents)) * 100;

		return removePercentage;
	}
	

	@Override
	public ArrayList<Double> computeMetrics(List<EHICommand> userActions) {
		ArrayList<Integer> metrics = getPercentageData(userActions);
		double debugPercentage = computeDebugPercentage(metrics);
		double editPercentage = computeEditPercentage(metrics);
		double navigationPercentage = computeNavigationPercentage(metrics);
		double removePercentage = computeRemoveEventsPercentage(metrics);
		double focusPercentage = computeFocusPercentage(metrics);
		// int searchPercentage, int debugPercentage, int focusPercentage , int
		// editPercentage, int removePercentage
		ArrayList<Double> percentages = new ArrayList<Double>();
		percentages.add(navigationPercentage);
		percentages.add(debugPercentage);
		percentages.add(focusPercentage);
		percentages.add(editPercentage);
		percentages.add(removePercentage);

		return percentages;
	}
	@Override
	public RatioFeatures computeFeatures(List<EHICommand> userActions) {
		return computeRatioFeatures(userActions);
	}
	@Override
	public  ArrayList<Integer> getPercentageData(List<EHICommand> userActions) {
		int numberOfDebugEvents = 0;
		int numberOfSearchEvents = 0;
		int numberOfEditOrInsertEvents = 0;
		int numberOfFocusEvents = 0;
		int numberOfRemoveEvents = 0;

		for (int i = 0; i < userActions.size(); i++) {
			EHICommand myEvent = userActions.get(i);
			
			CommandCategory aCommandCategory = toCommandCategory(myEvent);
			if (aCommandCategory == null) {
				System.err.println ("Unclassified command:" + myEvent);
				continue;
			}
			System.out.println(myEvent + " -->" + aCommandCategory);
			switch (aCommandCategory) {
			case EDIT_OR_INSERT:
				numberOfEditOrInsertEvents++;
				break;
			case DEBUG:
				numberOfDebugEvents++;
				break;

			case NAVIGATION:
				numberOfSearchEvents++;
				break;
			case FOCUS:
				numberOfFocusEvents++;
				break;
			case REMOVE:
				numberOfRemoveEvents++;
				break;
			case OTHER:
					
			}
//				if (isInsertOrEditEvent(myEvent)) {
//					numberOfEditOrInsertEvents++;
//					System.out.println ("Edit command:" + myEvent);
//				} else if (isDebugEvent(myEvent)) {
//					numberOfDebugEvents++;
//					//System.out.println ("Debug command:" + myEvent);
//
//				} else if (isNavigationEvent(myEvent)) {
//					numberOfSearchEvents++;
//					//System.out.println ("navigation command:" + myEvent);
//
//				} else if (isFocusEvent(myEvent)) {
//					numberOfFocusEvents++;
//					//System.out.println ("Focus command:" + myEvent);
//
//				} else  if(isAddRemoveEvent(myEvent)){
//					numberOfRemoveEvents++;
//					System.out.println("Removecommand: " + myEvent);
//				} else {
//					System.out.println("Unclassifiedcommand: " + myEvent);
//
//				}

				

		}

		ArrayList<Integer> eventData = new ArrayList<Integer>();
		eventData.add(numberOfDebugEvents);
		eventData.add(numberOfSearchEvents);
		eventData.add(numberOfEditOrInsertEvents);
		eventData.add(numberOfFocusEvents);
		eventData.add(numberOfRemoveEvents);

		return eventData;
	}
	@Override
	public  RatioFeatures computeRatioFeatures(List<EHICommand> userActions) {
		double numberOfDebugEvents = 0;
		double numberOfSearchEvents = 0;
		double numberOfEditOrInsertEvents = 0;
		double numberOfFocusEvents = 0;
		double numberOfRemoveEvents = 0;

		for (int i = 0; i < userActions.size(); i++) {
			EHICommand myEvent = userActions.get(i);
			
			CommandCategory aCommandCategory = toCommandCategory(myEvent);
			if (aCommandCategory == null) {
				System.err.println ("Unclassified command:" + myEvent);
				continue;
			}
			System.out.println(myEvent + " -->" + aCommandCategory);
			switch (aCommandCategory) {
			case EDIT_OR_INSERT:
				numberOfEditOrInsertEvents++;
				break;
			case DEBUG:
				numberOfDebugEvents++;
				break;

			case NAVIGATION:
				numberOfSearchEvents++;
				break;
			case FOCUS:
				numberOfFocusEvents++;
				break;
			case REMOVE:
				numberOfRemoveEvents++;
				break;
			case OTHER:
					
			}
//				if (isInsertOrEditEvent(myEvent)) {
//					numberOfEditOrInsertEvents++;
//					System.out.println ("Edit command:" + myEvent);
//				} else if (isDebugEvent(myEvent)) {
//					numberOfDebugEvents++;
//					//System.out.println ("Debug command:" + myEvent);
//
//				} else if (isNavigationEvent(myEvent)) {
//					numberOfSearchEvents++;
//					//System.out.println ("navigation command:" + myEvent);
//
//				} else if (isFocusEvent(myEvent)) {
//					numberOfFocusEvents++;
//					//System.out.println ("Focus command:" + myEvent);
//
//				} else  if(isAddRemoveEvent(myEvent)){
//					numberOfRemoveEvents++;
//					System.out.println("Removecommand: " + myEvent);
//				} else {
//					System.out.println("Unclassifiedcommand: " + myEvent);
//
//				}

				

		}
		double totalEvents = 
				numberOfDebugEvents + 
				numberOfSearchEvents + 
				numberOfEditOrInsertEvents +
				numberOfFocusEvents +
				numberOfRemoveEvents;
		RatioFeatures aRatioFeatures = RatioFeaturesFactorySelector.createRatioFeatures();

		if (totalEvents > 0) { // avoid deivide by zero
			aRatioFeatures.setDebugRatio(numberOfDebugEvents/totalEvents * 100);
			aRatioFeatures.setEditRatio(numberOfEditOrInsertEvents/totalEvents * 100);
			aRatioFeatures.setInsertionRatio(aRatioFeatures.getEditRatio());
			aRatioFeatures.setNavigationRatio(numberOfSearchEvents/totalEvents * 100);
			aRatioFeatures.setFocusRatio(numberOfFocusEvents/totalEvents * 100);
			aRatioFeatures.setRemoveRatio(numberOfRemoveEvents/totalEvents * 100);

		}
		return aRatioFeatures;
	}

	@Override
	public  String getFeatureName(EHICommand myEvent) {
		CommandCategory aCommandCategory = toCommandCategory(myEvent);
		return commandCategoryToFetaureName.get(aCommandCategory);
		
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
	
	protected void mapCommandCategoryToFeatureName() {
		commandCategoryToFetaureName.put(CommandCategory.EDIT_OR_INSERT, "Edit");
		commandCategoryToFetaureName.put(CommandCategory.DEBUG, "Debug");
		commandCategoryToFetaureName.put(CommandCategory.NAVIGATION, "Navigation");
		commandCategoryToFetaureName.put(CommandCategory.FOCUS, "Focus");
		commandCategoryToFetaureName.put(CommandCategory.REMOVE, "RemoveClass");
		commandCategoryToFetaureName.put(CommandCategory.OTHER, "Unclassified");
//		commandCategoryToFetaureName.put(CommandCategory.SEARCH, "Navigation");



	}
	
	public static RatioCalculator getInstance() {
		return instance;
	}

}
