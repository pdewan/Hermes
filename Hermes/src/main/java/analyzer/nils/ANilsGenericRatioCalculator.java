//package analyzer.nils;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import difficultyPrediction.APredictionParameters;
//import difficultyPrediction.featureExtraction.RatioFeatures;
//import difficultyPrediction.featureExtraction.RatioFeaturesFactorySelector;
//import difficultyPrediction.metrics.CommandCategory;
//import difficultyPrediction.metrics.CommandCategoryMapping;
//import difficultyPrediction.metrics.CommandName;
//import difficultyPrediction.metrics.RatioCalculator;
//import fluorite.commands.CompilationCommand;
//import fluorite.commands.EclipseCommand;
//import fluorite.commands.EHICommand;
//
//public class ANilsGenericRatioCalculator implements RatioCalculator {
//	
//	public final static int DEBUG_EVENT_INDEX = 0;
//	public final static int SEARCH_EVENT_INDEX = 1;
//	public final static int EDIT_EVENT_INDEX = 2;
//	public final static int FOCUS_EVENT_INDEX = 3;
//	public final static int REMOVE_EVENT_INDEX = 4;
//	
//	public final static int COPY_EVENT_INDEX = 5;
//	public final static int PASTE_EVENT_INDEX = 6;
//	
//	static RatioCalculator instance = new ANilsGenericRatioCalculator();
//	protected Map<NilsCommandCategory, String> commandCategoryToFetaureName = new HashMap();
//	
//	public ANilsGenericRatioCalculator() {
//		mapCommandCategoryToFeatureName();
//	}
//
//	public static NilsCommandCategory toCommandCategory(EHICommand aCommand) {
//		CommandCategoryMapping aCommandCategories = APredictionParameters.getInstance().
//						getCommandClassificationScheme().
//							getCommandCategoryMapping();
//		String aCommandString = aCommand.getCommandType();
//		if (aCommand instanceof CompilationCommand) {
//			CompilationCommand command = (CompilationCommand)aCommand;
//			if (!command.getIsWarning()) {
//				return aCommandCategories.getCommandCategory(CommandName.CompileError);
//			}
//		}
//		if (aCommand instanceof EclipseCommand) {
//			aCommandString = ((EclipseCommand) aCommand).getCommandID();
//			return aCommandCategories.searchCommandCategory(aCommandString);
//			
//		} else {
//			try {
//			CommandName aCommandName = CommandName.valueOf(aCommandString);
//			return aCommandCategories.getCommandCategory(aCommandName);
//			} catch (Exception e) {
//				return NilsCommandCategory.OTHER;
//			}
////			return aCommandCategories.getCommandCategory(aCommandName)
//		}
//		
//		
//	}
//	
//	@Override
//	public boolean isDebugEvent(EHICommand event) {
//		return toCommandCategory(event) == NilsCommandCategory.DEBUG;
//	}
//
//	@Override
//	public boolean isInsertOrEditEvent(EHICommand event) {
//		return toCommandCategory(event) == NilsCommandCategory.EDIT_OR_INSERT;
//
//	}
//
//	@Override
//	public boolean isNavigationEvent(EHICommand event) {
//		return toCommandCategory(event) == NilsCommandCategory.NAVIGATION;
//
//	}
//
//	@Override
//	public boolean isFocusEvent(EHICommand event) {
//		return toCommandCategory(event) == NilsCommandCategory.FOCUS;
//
//	}
//
//	@Override
//	public boolean isAddRemoveEvent(EHICommand event) {
//		return toCommandCategory(event) == NilsCommandCategory.REMOVE;
//	}
//	
//	public boolean isCopyEvent(EHICommand event) {
//		return toCommandCategory(event) == NilsCommandCategory.COPY;
//	}
//	
//	public boolean isPasteEvent(EHICommand event) {
//		return toCommandCategory(event) == NilsCommandCategory.PASTE;
//	}
//	
//	public static double computeDebugPercentage(ArrayList<Integer> eventData) {
//		double numberOfDebugEvents = eventData.get(DEBUG_EVENT_INDEX);
//		double numberOfSearchEvents = eventData.get(SEARCH_EVENT_INDEX);
//		double numberOfEditEvents = eventData.get(EDIT_EVENT_INDEX);
//		double numberOfFocusEvents = eventData.get(FOCUS_EVENT_INDEX);
//		double numberOfRemoveEvents = eventData.get(REMOVE_EVENT_INDEX);
//		double numberOfCopyEvents = eventData.get(COPY_EVENT_INDEX);
//		double numberOfPasteEvents = eventData.get(PASTE_EVENT_INDEX);
//
//		double debugPercentage = 0;
//
//		if (numberOfDebugEvents > 0)
//			debugPercentage = (numberOfDebugEvents / (numberOfDebugEvents
//					+ numberOfSearchEvents + numberOfEditEvents
//					+ numberOfFocusEvents + numberOfRemoveEvents
//					+ numberOfCopyEvents + numberOfPasteEvents)) * 100;
//
//		return debugPercentage;
//	}
//
//	public static double computeNavigationPercentage(
//			ArrayList<Integer> eventData) {
//		double numberOfDebugEvents = eventData.get(DEBUG_EVENT_INDEX);
//		double numberOfSearchEvents = eventData.get(SEARCH_EVENT_INDEX);
//		double numberOfEditEvents = eventData.get(EDIT_EVENT_INDEX);
//		double numberOfFocusEvents = eventData.get(FOCUS_EVENT_INDEX);
//		double numberOfRemoveEvents = eventData.get(REMOVE_EVENT_INDEX);
//		double numberOfCopyEvents = eventData.get(COPY_EVENT_INDEX);
//		double numberOfPasteEvents = eventData.get(PASTE_EVENT_INDEX);
//
//		double searchPercentage = 0;
//
//		if (numberOfSearchEvents > 0)
//			searchPercentage = (numberOfSearchEvents / (numberOfDebugEvents
//					+ numberOfSearchEvents + numberOfEditEvents
//					+ numberOfFocusEvents + numberOfRemoveEvents
//					+ numberOfCopyEvents + numberOfPasteEvents)) * 100;
//
//		return searchPercentage;
//	}
//
//	public static double computeEditPercentage(ArrayList<Integer> eventData) {
//		double numberOfDebugEvents = eventData.get(DEBUG_EVENT_INDEX);
//		double numberOfSearchEvents = eventData.get(SEARCH_EVENT_INDEX);
//		double numberOfEditEvents = eventData.get(EDIT_EVENT_INDEX);
//		double numberOfFocusEvents = eventData.get(FOCUS_EVENT_INDEX);
//		double numberOfRemoveEvents = eventData.get(REMOVE_EVENT_INDEX);
//		double numberOfCopyEvents = eventData.get(COPY_EVENT_INDEX);
//		double numberOfPasteEvents = eventData.get(PASTE_EVENT_INDEX);
//
//		double editPercentage = 0;
//
//		if (numberOfEditEvents > 0)
//			editPercentage = (numberOfEditEvents / (numberOfDebugEvents
//					+ numberOfSearchEvents + numberOfEditEvents
//					+ numberOfFocusEvents + numberOfRemoveEvents
//					+ numberOfCopyEvents + numberOfPasteEvents)) * 100;
//
//		return editPercentage;
//	}
//
//	public static double computeFocusPercentage(ArrayList<Integer> eventData) {
//		double numberOfDebugEvents = eventData.get(DEBUG_EVENT_INDEX);
//		double numberOfSearchEvents = eventData.get(SEARCH_EVENT_INDEX);
//		double numberOfEditEvents = eventData.get(EDIT_EVENT_INDEX);
//		double numberOfFocusEvents = eventData.get(FOCUS_EVENT_INDEX);
//		double numberOfRemoveEvents = eventData.get(REMOVE_EVENT_INDEX);
//		double numberOfCopyEvents = eventData.get(COPY_EVENT_INDEX);
//		double numberOfPasteEvents = eventData.get(PASTE_EVENT_INDEX);
//
//		double focusPercentage = 0;
//
//		if (numberOfFocusEvents > 0)
//			focusPercentage = (numberOfFocusEvents / (numberOfDebugEvents
//					+ numberOfSearchEvents + numberOfEditEvents
//					+ numberOfFocusEvents + numberOfRemoveEvents
//					+ numberOfCopyEvents + numberOfPasteEvents)) * 100;
//
//		return focusPercentage;
//	}
//
//	public static double computeRemoveEventsPercentage(
//			ArrayList<Integer> eventData) {
//		double numberOfDebugEvents = eventData.get(DEBUG_EVENT_INDEX);
//		double numberOfSearchEvents = eventData.get(SEARCH_EVENT_INDEX);
//		double numberOfEditEvents = eventData.get(EDIT_EVENT_INDEX);
//		double numberOfFocusEvents = eventData.get(FOCUS_EVENT_INDEX);
//		double numberOfRemoveEvents = eventData.get(REMOVE_EVENT_INDEX);
//		double numberOfCopyEvents = eventData.get(COPY_EVENT_INDEX);
//		double numberOfPasteEvents = eventData.get(PASTE_EVENT_INDEX);
//
//		double removePercentage = 0;
//
//		if (numberOfRemoveEvents > 0)
//			removePercentage = (numberOfRemoveEvents / (numberOfDebugEvents
//					+ numberOfSearchEvents + numberOfEditEvents
//					+ numberOfFocusEvents + numberOfRemoveEvents
//					+ numberOfCopyEvents + numberOfPasteEvents)) * 100;
//
//		return removePercentage;
//	}
//	
//	public static double computeCopyEventsPercentage(
//			ArrayList<Integer> eventData) {
//		double numberOfDebugEvents = eventData.get(DEBUG_EVENT_INDEX);
//		double numberOfSearchEvents = eventData.get(SEARCH_EVENT_INDEX);
//		double numberOfEditEvents = eventData.get(EDIT_EVENT_INDEX);
//		double numberOfFocusEvents = eventData.get(FOCUS_EVENT_INDEX);
//		double numberOfRemoveEvents = eventData.get(REMOVE_EVENT_INDEX);
//		double numberOfCopyEvents = eventData.get(COPY_EVENT_INDEX);
//		double numberOfPasteEvents = eventData.get(PASTE_EVENT_INDEX);
//
//		double copyPercentage = 0;
//
//		if (numberOfCopyEvents > 0)
//			copyPercentage = (numberOfCopyEvents / (numberOfDebugEvents
//					+ numberOfSearchEvents + numberOfEditEvents
//					+ numberOfFocusEvents + numberOfRemoveEvents
//					+ numberOfCopyEvents + numberOfPasteEvents)) * 100;
//
//		return copyPercentage;
//	}
//	
//	public static double computePasteEventsPercentage(
//			ArrayList<Integer> eventData) {
//		double numberOfDebugEvents = eventData.get(DEBUG_EVENT_INDEX);
//		double numberOfSearchEvents = eventData.get(SEARCH_EVENT_INDEX);
//		double numberOfEditEvents = eventData.get(EDIT_EVENT_INDEX);
//		double numberOfFocusEvents = eventData.get(FOCUS_EVENT_INDEX);
//		double numberOfRemoveEvents = eventData.get(REMOVE_EVENT_INDEX);
//		double numberOfCopyEvents = eventData.get(COPY_EVENT_INDEX);
//		double numberOfPasteEvents = eventData.get(PASTE_EVENT_INDEX);
//
//		double pastePercentage = 0;
//
//		if (numberOfPasteEvents > 0)
//			pastePercentage = (numberOfPasteEvents / (numberOfDebugEvents
//					+ numberOfSearchEvents + numberOfEditEvents
//					+ numberOfFocusEvents + numberOfRemoveEvents
//					+ numberOfCopyEvents + numberOfPasteEvents)) * 100;
//
//		return pastePercentage;
//	}
//	
//
//	@Override
//	public ArrayList<Double> computeMetrics(List<EHICommand> userActions) {
//		ArrayList<Integer> metrics = getPercentageData(userActions);
//		double debugPercentage = computeDebugPercentage(metrics);
//		double editPercentage = computeEditPercentage(metrics);
//		double navigationPercentage = computeNavigationPercentage(metrics);
//		double removePercentage = computeRemoveEventsPercentage(metrics);
//		double focusPercentage = computeFocusPercentage(metrics);
//		// int searchPercentage, int debugPercentage, int focusPercentage , int
//		// editPercentage, int removePercentage
//		ArrayList<Double> percentages = new ArrayList<Double>();
//		percentages.add(navigationPercentage);
//		percentages.add(debugPercentage);
//		percentages.add(focusPercentage);
//		percentages.add(editPercentage);
//		percentages.add(removePercentage);
//
//		return percentages;
//	}
//	@Override
//	public RatioFeatures computeFeatures(List<EHICommand> userActions) {
//		return computeRatioFeatures(userActions);
//	}
//	@Override
//	public  ArrayList<Integer> getPercentageData(List<EHICommand> userActions) {
//		int numberOfDebugEvents = 0;
//		int numberOfSearchEvents = 0;
//		int numberOfEditOrInsertEvents = 0;
//		int numberOfFocusEvents = 0;
//		int numberOfRemoveEvents = 0;
//		int numberOfCopyEvents = 0;
//		int numberOfPasteEvents = 0;
//
//		for (int i = 0; i < userActions.size(); i++) {
//			EHICommand myEvent = userActions.get(i);
//			
//			NilsCommandCategory aCommandCategory = toCommandCategory(myEvent);
//			if (aCommandCategory == null) {
//				System.err.println ("Unclassified command:" + myEvent);
//				continue;
//			}
//			System.out.println(myEvent + " -->" + aCommandCategory);
//			switch (aCommandCategory) {
//			case EDIT_OR_INSERT:
//				numberOfEditOrInsertEvents++;
//				break;
//			case DEBUG:
//				numberOfDebugEvents++;
//				break;
//
//			case NAVIGATION:
//				numberOfSearchEvents++;
//				break;
//			case FOCUS:
//				numberOfFocusEvents++;
//				break;
//			case REMOVE:
//				numberOfRemoveEvents++;
//				break;
//			case COPY:
//				numberOfCopyEvents++;
//				break;
//			case PASTE:
//				numberOfPasteEvents++;
//				break;
//			case OTHER:
//					
//			}
//		}
//
//		ArrayList<Integer> eventData = new ArrayList<Integer>();
//		eventData.add(numberOfDebugEvents);
//		eventData.add(numberOfSearchEvents);
//		eventData.add(numberOfEditOrInsertEvents);
//		eventData.add(numberOfFocusEvents);
//		eventData.add(numberOfRemoveEvents);
//
//		return eventData;
//	}
//	@Override
//	public  RatioFeatures computeRatioFeatures(List<EHICommand> userActions) {
//		double numberOfDebugEvents = 0;
//		double numberOfSearchEvents = 0;
//		double numberOfEditOrInsertEvents = 0;
//		double numberOfFocusEvents = 0;
//		double numberOfRemoveEvents = 0;
//		double numberOfCopyEvents = 0;
//		double numberOfPasteEvents = 0;
//
//		for (int i = 0; i < userActions.size(); i++) {
//			EHICommand myEvent = userActions.get(i);
//			
//			NilsCommandCategory aCommandCategory = toCommandCategory(myEvent);
//			if (aCommandCategory == null) {
//				System.err.println ("Unclassified command:" + myEvent);
//				continue;
//			}
//			System.out.println(myEvent + " -->" + aCommandCategory);
//			switch (aCommandCategory) {
//			case EDIT_OR_INSERT:
//				numberOfEditOrInsertEvents++;
//				break;
//			case DEBUG:
//				numberOfDebugEvents++;
//				break;
//
//			case NAVIGATION:
//				numberOfSearchEvents++;
//				break;
//			case FOCUS:
//				numberOfFocusEvents++;
//				break;
//			case REMOVE:
//				numberOfRemoveEvents++;
//				break;
//			case COPY:
//				numberOfCopyEvents++;
//				break;
//			case PASTE:
//				numberOfPasteEvents++;
//				break;
//			case OTHER:
//					
//			}	
//		}
//		double totalEvents = 
//				numberOfDebugEvents + 
//				numberOfSearchEvents + 
//				numberOfEditOrInsertEvents +
//				numberOfFocusEvents +
//				numberOfRemoveEvents + 
//				numberOfCopyEvents + 
//				numberOfPasteEvents;
//		RatioFeatures aRatioFeatures = RatioFeaturesFactorySelector.createRatioFeatures();
//
//		if (totalEvents > 0) { // avoid deivide by zero
//			aRatioFeatures.setDebugRatio(numberOfDebugEvents/totalEvents * 100);
//			aRatioFeatures.setEditRatio(numberOfEditOrInsertEvents/totalEvents * 100);
//			aRatioFeatures.setInsertionRatio(aRatioFeatures.getEditRatio());
//			aRatioFeatures.setNavigationRatio(numberOfSearchEvents/totalEvents * 100);
//			aRatioFeatures.setFocusRatio(numberOfFocusEvents/totalEvents * 100);
//			aRatioFeatures.setRemoveRatio(numberOfRemoveEvents/totalEvents * 100);
//			//TODO
//		}
//		return aRatioFeatures;
//	}
//
//	@Override
//	public  String getFeatureName(EHICommand myEvent) {
//		NilsCommandCategory aCommandCategory = toCommandCategory(myEvent);
//		return commandCategoryToFetaureName.get(aCommandCategory);
//	}
//	
//	protected void mapCommandCategoryToFeatureName() {
//		commandCategoryToFetaureName.put(NilsCommandCategory.EDIT_OR_INSERT, "Edit");
//		commandCategoryToFetaureName.put(NilsCommandCategory.DEBUG, "Debug");
//		commandCategoryToFetaureName.put(NilsCommandCategory.NAVIGATION, "Navigation");
//		commandCategoryToFetaureName.put(NilsCommandCategory.FOCUS, "Focus");
//		commandCategoryToFetaureName.put(NilsCommandCategory.REMOVE, "RemoveClass");
//		commandCategoryToFetaureName.put(NilsCommandCategory.OTHER, "Unclassified");
//		// TODO
////		commandCategoryToFetaureName.put(CommandCategory.SEARCH, "Navigation");
//
//
//
//	}
//	
//	public static RatioCalculator getInstance() {
//		return instance;
//	}
//
//}
