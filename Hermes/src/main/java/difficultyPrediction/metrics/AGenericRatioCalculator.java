package difficultyPrediction.metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import difficultyPrediction.APredictionParameters;
import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.featureExtraction.RatioFeaturesFactorySelector;
import fluorite.commands.CompilationCommand;
import fluorite.commands.EclipseCommand;
import util.trace.Tracer;
import fluorite.commands.EHICommand;

public class AGenericRatioCalculator implements RatioCalculator {

	public final static int DEBUG_EVENT_INDEX = 0;
	public final static int SEARCH_EVENT_INDEX = 1;
	public final static int EDIT_EVENT_INDEX = 2;
	public final static int FOCUS_EVENT_INDEX = 3;
	public final static int REMOVE_EVENT_INDEX = 4;
	// static RatioCalculator instance = new AGenericRatioCalculator();
	// This belongs in CommandCategoryMapping
	// protected Map<CommandCategory, String> commandCategoryToFetaureName = new
	// HashMap();
	CommandCategoryMapping commandCategoryMapping;
	List<CommandCategory> relevantCommandCategoryList;

	public AGenericRatioCalculator() {
		// mapCommandCategoryToFeatureName();
		commandCategoryMapping = APredictionParameters.getInstance().getCommandClassificationScheme()
				.getCommandCategoryMapping();
		relevantCommandCategoryList = Arrays.asList(commandCategoryMapping.getRelevantCommandCategories());
	}

	// protected static List<CommandCategory> emptyCommandCategories= new
	// ArrayList();

	// public static List<CommandCategory> toCommandCategories(EHICommand
	// aCommand) {
	// CommandCategoryMapping aCommandCategories =
	// APredictionParameters.getInstance().
	// getCommandClassificationScheme().
	// getCommandCategoryMapping();
	// String aCommandString = aCommand.getCommandType();
	// if (aCommand instanceof CompilationCommand) {
	// CompilationCommand command = (CompilationCommand)aCommand;
	// if (!command.getIsWarning()) {
	// return aCommandCategories.getCommandCategories(CommandName.CompileError);
	// }
	// }
	// if (aCommand instanceof EclipseCommand) {
	// aCommandString = ((EclipseCommand) aCommand).getCommandID();
	// return aCommandCategories.searchCommandCategories(aCommandString);
	//
	// } else {
	// try {
	// CommandName aCommandName = CommandName.valueOf(aCommandString);
	// return aCommandCategories.getCommandCategories(aCommandName);
	// } catch (Exception e) {
	//// return CommandCategory.OTHER;
	// return emptyCommandCategories;
	// }
	//// return aCommandCategories.getCommandCategory(aCommandName)
	// }
	//
	//
	// }

	@Override
	public boolean isDebugEvent(EHICommand event) {
		// return RatioCalculator.toCommandCategory(event) ==
		// CommandCategory.DEBUG;
		return RatioCalculator.toCommandCategories(event).contains(CommandCategory.DEBUG);

	}

	// @Override
	// public boolean isInsertOrEditEvent(EHICommand event) {
	// return RatioCalculator.toCommandCategory(event) ==
	// CommandCategory.EDIT_OR_INSERT;
	//
	// }
	@Override
	public boolean isInsertEvent(EHICommand event) {
		return RatioCalculator.toCommandCategories(event).contains(CommandCategory.INSERT);

	}

	@Override
	public boolean isEditEvent(EHICommand event) {
		return RatioCalculator.toCommandCategories(event).contains(CommandCategory.INSERT);

	}

	@Override
	public boolean isNavigationEvent(EHICommand event) {
		return RatioCalculator.toCommandCategories(event).contains(CommandCategory.NAVIGATION);

	}

	@Override
	public boolean isFocusEvent(EHICommand event) {
		return RatioCalculator.toCommandCategories(event).contains(CommandCategory.FOCUS);

	}

	@Override
	public boolean isAddRemoveEvent(EHICommand event) {
		return RatioCalculator.toCommandCategories(event).contains(CommandCategory.REMOVE);
	}

	public static double computeDebugPercentage(ArrayList<Integer> eventData) {
		double numberOfDebugEvents = eventData.get(DEBUG_EVENT_INDEX);
		double numberOfSearchEvents = eventData.get(SEARCH_EVENT_INDEX);
		double numberOfEditEvents = eventData.get(EDIT_EVENT_INDEX);
		double numberOfFocusEvents = eventData.get(FOCUS_EVENT_INDEX);
		double numberOfRemoveEvents = eventData.get(REMOVE_EVENT_INDEX);

		double debugPercentage = 0;

		if (numberOfDebugEvents > 0)
			debugPercentage = (numberOfDebugEvents / (numberOfDebugEvents + numberOfSearchEvents + numberOfEditEvents
					+ numberOfFocusEvents + numberOfRemoveEvents)) * 100;

		return debugPercentage;
	}

	public static double computeNavigationPercentage(ArrayList<Integer> eventData) {
		double numberOfDebugEvents = eventData.get(DEBUG_EVENT_INDEX);
		double numberOfSearchEvents = eventData.get(SEARCH_EVENT_INDEX);
		double numberOfEditEvents = eventData.get(EDIT_EVENT_INDEX);
		double numberOfFocusEvents = eventData.get(FOCUS_EVENT_INDEX);
		double numberOfRemoveEvents = eventData.get(REMOVE_EVENT_INDEX);

		double searchPercentage = 0;

		if (numberOfSearchEvents > 0)
			searchPercentage = (numberOfSearchEvents / (numberOfDebugEvents + numberOfSearchEvents + numberOfEditEvents
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
			editPercentage = (numberOfEditEvents / (numberOfDebugEvents + numberOfSearchEvents + numberOfEditEvents
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
			focusPercentage = (numberOfFocusEvents / (numberOfDebugEvents + numberOfSearchEvents + numberOfEditEvents
					+ numberOfFocusEvents + numberOfRemoveEvents)) * 100;

		return focusPercentage;
	}

	public static double computeRemoveEventsPercentage(ArrayList<Integer> eventData) {
		double numberOfDebugEvents = eventData.get(DEBUG_EVENT_INDEX);
		double numberOfSearchEvents = eventData.get(SEARCH_EVENT_INDEX);
		double numberOfEditEvents = eventData.get(EDIT_EVENT_INDEX);
		double numberOfFocusEvents = eventData.get(FOCUS_EVENT_INDEX);
		double numberOfRemoveEvents = eventData.get(REMOVE_EVENT_INDEX);

		double removePercentage = 0;

		if (numberOfRemoveEvents > 0)
			removePercentage = (numberOfRemoveEvents / (numberOfDebugEvents + numberOfSearchEvents + numberOfEditEvents
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
		initEventCounts();
		return computeRatioFeatures(userActions);
	}

	@Override
	public ArrayList<Integer> getPercentageData(List<EHICommand> userActions) {
		int numberOfDebugEvents = 0;
		int numberOfSearchEvents = 0;
		// int numberOfEditOrInsertEvents = 0;
		int numberOfEditEvents = 0;
		int numberOfInsertEvents = 0;
		int numberOfFocusEvents = 0;
		int numberOfRemoveEvents = 0;

		for (int i = 0; i < userActions.size(); i++) {
			EHICommand myEvent = userActions.get(i);

			List<CommandCategory> aCommandCategories = RatioCalculator.toCommandCategories(myEvent);
			for (CommandCategory aCommandCategory : aCommandCategories) {
				// if (aCommandCategory == null) {
				// System.err.println ("Unclassified command:" + myEvent);
				// continue;
				// }
				Tracer.info(this, myEvent + " -->" + aCommandCategory);
				switch (aCommandCategory) {
				// case EDIT_OR_INSERT:
				// numberOfEditOrInsertEvents++;
				// break;
				case EDIT:
					numberOfEditEvents++;
					break;
				case INSERT:
					numberOfInsertEvents++;
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
			}
			// if (isInsertOrEditEvent(myEvent)) {
			// numberOfEditOrInsertEvents++;
			// System.out.println ("Edit command:" + myEvent);
			// } else if (isDebugEvent(myEvent)) {
			// numberOfDebugEvents++;
			// //System.out.println ("Debug command:" + myEvent);
			//
			// } else if (isNavigationEvent(myEvent)) {
			// numberOfSearchEvents++;
			// //System.out.println ("navigation command:" + myEvent);
			//
			// } else if (isFocusEvent(myEvent)) {
			// numberOfFocusEvents++;
			// //System.out.println ("Focus command:" + myEvent);
			//
			// } else if(isAddRemoveEvent(myEvent)){
			// numberOfRemoveEvents++;
			// System.out.println("Removecommand: " + myEvent);
			// } else {
			// System.out.println("Unclassifiedcommand: " + myEvent);
			//
			// }

		}

		ArrayList<Integer> eventData = new ArrayList<Integer>();
		eventData.add(numberOfDebugEvents);
		eventData.add(numberOfSearchEvents);
		// eventData.add(numberOfEditOrInsertEvents);
		eventData.add(numberOfEditEvents);

		eventData.add(numberOfFocusEvents);
		eventData.add(numberOfRemoveEvents);
		eventData.add(numberOfInsertEvents);

		return eventData;
	}

	double numberOfDebugEvents = 0;
	double numberOfSearchEvents = 0;
	// double numberOfEditOrInsertEvents = 0;
	double numberOfEditEvents = 0;
	double numberOfInsertEvents = 0;
	double numberOfFocusEvents = 0;
	double numberOfRemoveEvents = 0;
	double numberOfRemoveClassEvents = 0;
	double totalEvents = 0;
	long segmentStartTime = 0;
	long segmentEndTime = 0;
	int segmentSize = 0;
	StringBuffer commandString = new StringBuffer();

	protected void initEventCounts() {
		numberOfDebugEvents = 0;
		numberOfSearchEvents = 0;
		// double numberOfEditOrInsertEvents = 0;
		numberOfEditEvents = 0;
		numberOfInsertEvents = 0;
		numberOfFocusEvents = 0;
		numberOfRemoveEvents = 0;
		numberOfRemoveClassEvents = 0;
		totalEvents = 0;
		segmentStartTime = 0;
		segmentSize = 0;
		segmentEndTime = 0;
		commandString.setLength(0);
	}

	protected void processCategorizedCommand(EHICommand myEvent, CommandCategory aCommandCategory) {

		if (aCommandCategory == null) {
			// System.err.println ("Unclassified command:" + myEvent);
			return;
		}
		// totalEvents++;
		Tracer.info(myEvent + " -->" + aCommandCategory);
		switch (aCommandCategory) {
		case EDIT:
			numberOfEditEvents++;
			break;
		// case EDIT_OR_INSERT:
		// numberOfEditOrInsertEvents++;
		// break;
		case INSERT:
			numberOfInsertEvents++;
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
		case REMOVE_CLASS:
			numberOfRemoveClassEvents++;
			break;
		default:
			// totalEvents--;
		}

	}

	// protected void computeTotalEvents() {
	// totalEvents =
	// numberOfDebugEvents +
	// numberOfSearchEvents +
	//// numberOfEditOrInsertEvents +
	// // number of edit events has remove and insert we assume
	// numberOfInsertEvents +
	// numberOfFocusEvents +
	// numberOfRemoveEvents +
	// numberOfRemoveClassEvents;
	// }
	protected RatioFeatures computeRatioFeatures() {
		RatioFeatures aRatioFeatures = RatioFeaturesFactorySelector.createRatioFeatures();

		if (totalEvents > 0) { // avoid deivide by zero
			aRatioFeatures.setDebugRatio(numberOfDebugEvents / totalEvents * 100);
			aRatioFeatures.setEditRatio(numberOfEditEvents / totalEvents * 100);
			// aRatioFeatures.setEditRatio(numberOfEditOrInsertEvents/totalEvents
			// * 100);
			// aRatioFeatures.setInsertionRatio(aRatioFeatures.getEditRatio());
			aRatioFeatures.setInsertionRatio(numberOfInsertEvents / totalEvents);
			aRatioFeatures.setNavigationRatio(numberOfSearchEvents / totalEvents * 100);
			aRatioFeatures.setFocusRatio(numberOfFocusEvents / totalEvents * 100);
			aRatioFeatures.setRemoveRatio(numberOfRemoveEvents / totalEvents * 100);
			aRatioFeatures.setRemoveClassRatio(0);

			int anElapsedTime = (int) (segmentEndTime - segmentStartTime);
			double aSegmentTimePeriodSecs = anElapsedTime / 1000;
			aRatioFeatures.setElapsedTime(anElapsedTime);

			if (aSegmentTimePeriodSecs > 0) {
				aRatioFeatures.setCommandRate(totalEvents / aSegmentTimePeriodSecs);
				aRatioFeatures.setDebugRate(numberOfDebugEvents / aSegmentTimePeriodSecs);
				aRatioFeatures.setEditRate(numberOfEditEvents / aSegmentTimePeriodSecs);
				// aRatioFeatures.setEditRatio(numberOfEditOrInsertEvents/aSegmentTimePeriod);
				// aRatioFeatures.setInsertionRatio(aRatioFeatures.getEditRatio());
				aRatioFeatures.setInsertionRate(numberOfInsertEvents / aSegmentTimePeriodSecs);
				aRatioFeatures.setNavigationRate(numberOfSearchEvents / aSegmentTimePeriodSecs);
				aRatioFeatures.setFocusRate(numberOfFocusEvents / aSegmentTimePeriodSecs);
				aRatioFeatures.setRemoveRate(numberOfRemoveEvents / aSegmentTimePeriodSecs);

			}
		}

		return aRatioFeatures;
	}

	protected boolean isCountableEvent(List<CommandCategory> aCommandCategories) {
		for (CommandCategory aCommandCategory : aCommandCategories) {
			if (relevantCommandCategoryList.contains(aCommandCategory))
				return true;
		}
		return false;
	}

	protected CommandCategory[] computedFeatures = { CommandCategory.NAVIGATION, CommandCategory.DEBUG,
			// SEARCH,
			CommandCategory.FOCUS, CommandCategory.EDIT, CommandCategory.REMOVE, CommandCategory.INSERT,
			CommandCategory.COMMAND_RATE, CommandCategory.NAVIGATION_RATE, CommandCategory.DEBUG_RATE,
			// SEARCH,
			CommandCategory.FOCUS_RATE, CommandCategory.EDIT_RATE, CommandCategory.REMOVE_RATE,
			CommandCategory.INSERT_RATE,

	};

	@Override
	public CommandCategory[] getComputedFeatures() {
		return computedFeatures;
	}

	// @Override
	// public boolean[] getComputedFeatures() {
	// if (computedFeatures == null) {
	// computedFeatures = new boolean[CommandCategory.values().length];
	// computedFeatures[CommandCategory.DEBUG.ordinal()] = true;
	// computedFeatures[CommandCategory.EDIT.ordinal()] = true;
	// computedFeatures[CommandCategory.INSERT.ordinal()] = true;
	// computedFeatures[CommandCategory.NAVIGATION.ordinal()] = true;
	// computedFeatures[CommandCategory.FOCUS.ordinal()] = true;
	// computedFeatures[CommandCategory.REMOVE.ordinal()] = true;
	// }
	// return computedFeatures;
	// }
	protected List<String> computedFeatureNames;

	@Override
	public List<String> getComputedFeatureNames() {
		if (computedFeatureNames == null) {
			computedFeatureNames = new ArrayList();
			computedFeatureNames.addAll(Arrays.asList(commandCategoryMapping.getOrderedRelevantFeatureNames()));
			CommandCategory[] aComputedFeatures = getComputedFeatures();
			for (CommandCategory aCommandCategory : aComputedFeatures) {
				String aFeatureName = commandCategoryMapping.getFeatureName(aCommandCategory);
				if (!computedFeatureNames.contains(aFeatureName)) {
					computedFeatureNames.add(aFeatureName);
				}
			}

		}
		return computedFeatureNames;
	}

	public static final String COMMAND_CATEGORY_SEPARATOR = " ";
	public static final String CATEGORIES_COUNT_SEPARATOR = ":";

	@Override
	/**
	 * This code is an alternative to the above and duplicates the code subclass
	 * can override to modularize it
	 */
	public RatioFeatures computeRatioFeatures(List<EHICommand> userActions) {
		// double numberOfDebugEvents = 0;
		// double numberOfSearchEvents = 0;
		//// double numberOfEditOrInsertEvents = 0;
		// double numberOfEditEvents = 0;
		// double numberOfInsertEvents = 0;
		// double numberOfFocusEvents = 0;
		// double numberOfRemoveEvents = 0;
		// double numberOfRemoveClassEvents = 0;

		// need to extend this to add more events;
		// may need to make these instance vars into globals, which is ugh as
		// this is not persmanent
		// object state

		segmentSize = userActions.size();
		if (segmentSize > 0) {
			segmentStartTime = userActions.get(0).getTimestamp();
			segmentEndTime = userActions.get(userActions.size() - 1).getTimestamp();
		}

		List<CommandCategory> aPreviousCategories = emptyCommandCategories;
		int aPreviousCategoriesIndex = 0;
		int aPreviousCategoriesCount = 0;

		for (int i = 0; i < userActions.size(); i++) {
			EHICommand myEvent = userActions.get(i);

			List<CommandCategory> aCommandCategories = RatioCalculator.toCommandCategories(myEvent);
			if (aCommandCategories == null || aCommandCategories.isEmpty()) {
				System.err.println("Unclassified command:" + myEvent);
				continue;
			}
			if (isCountableEvent(aCommandCategories)) {
				totalEvents++;
			}
			// We will process non countable events also for metrics files
			if (!aPreviousCategories.equals(aCommandCategories)) {
				commandString.append("(");
			}
			boolean anIsFirst = true;

			for (CommandCategory aCommandCategory : aCommandCategories) {
				processCategorizedCommand(myEvent, aCommandCategory);
				if (aPreviousCategories.equals(aCommandCategories)) {
					aPreviousCategoriesCount++; 
				} else {
					aPreviousCategoriesCount = 0;
					if (anIsFirst) {
						anIsFirst = false;
					} else {
						commandString.append(COMMAND_CATEGORY_SEPARATOR);
					}
					String aCommandCategoryString = aCommandCategory.toString();
					char aCommandcatagoryFirstChar = aCommandCategoryString.charAt(0);
					

					commandString.append(aCommandcatagoryFirstChar);
				}
			}
			
			if (!aPreviousCategories.equals(aCommandCategories)) {
				commandString.append(")");
				aPreviousCategoriesIndex = commandString.length();
				
			} 
			commandString.append(aPreviousCategoriesCount);


		}

		return computeRatioFeatures();
	}
	// public RatioFeatures computeRatioFeatures(List<EHICommand> userActions) {
	// double numberOfDebugEvents = 0;
	// double numberOfSearchEvents = 0;
	//// double numberOfEditOrInsertEvents = 0;
	// double numberOfEditEvents = 0;
	// double numberOfInsertEvents = 0;
	// double numberOfFocusEvents = 0;
	// double numberOfRemoveEvents = 0;
	// double numberOfRemoveClassEvents = 0;
	//
	//
	// // need to extend this to add more events;
	// // may need to make these instance vars into globals, which is ugh as
	// this is not persmanent
	// // object state
	//
	// for (int i = 0; i < userActions.size(); i++) {
	// EHICommand myEvent = userActions.get(i);
	//
	// CommandCategory aCommandCategory = toCommandCategory(myEvent);
	// if (aCommandCategory == null) {
	// System.err.println ("Unclassified command:" + myEvent);
	// continue;
	// }
	// System.out.println(myEvent + " -->" + aCommandCategory);
	// switch (aCommandCategory) {
	// case EDIT:
	// numberOfEditEvents++;
	// break;
	//// case EDIT_OR_INSERT:
	//// numberOfEditOrInsertEvents++;
	//// break;
	// case INSERT:
	// numberOfInsertEvents++;
	// break;
	// case DEBUG:
	// numberOfDebugEvents++;
	// break;
	//
	// case NAVIGATION:
	// numberOfSearchEvents++;
	// break;
	// case FOCUS:
	// numberOfFocusEvents++;
	// break;
	// case REMOVE:
	// numberOfRemoveEvents++;
	// break;
	// case REMOVE_CLASS:
	// numberOfRemoveClassEvents++;
	// break;
	// case OTHER:
	//
	// }
	//
	//
	//
	//
	// }
	// double totalEvents =
	// numberOfDebugEvents +
	// numberOfSearchEvents +
	//// numberOfEditOrInsertEvents +
	// // number of edit events has remove and insert we assume
	// numberOfInsertEvents +
	// numberOfFocusEvents +
	// numberOfRemoveEvents +
	// numberOfRemoveClassEvents;
	// RatioFeatures aRatioFeatures =
	// RatioFeaturesFactorySelector.createRatioFeatures();
	//
	// if (totalEvents > 0) { // avoid deivide by zero
	// aRatioFeatures.setDebugRatio(numberOfDebugEvents/totalEvents * 100);
	// aRatioFeatures.setEditRatio(numberOfEditEvents/totalEvents * 100);
	//// aRatioFeatures.setEditRatio(numberOfEditOrInsertEvents/totalEvents *
	// 100);
	//// aRatioFeatures.setInsertionRatio(aRatioFeatures.getEditRatio());
	// aRatioFeatures.setInsertionRatio(numberOfInsertEvents/totalEvents);
	// aRatioFeatures.setNavigationRatio(numberOfSearchEvents/totalEvents *
	// 100);
	// aRatioFeatures.setFocusRatio(numberOfFocusEvents/totalEvents * 100);
	// aRatioFeatures.setRemoveRatio(numberOfRemoveEvents/totalEvents * 100);
	//
	//
	// }
	// return aRatioFeatures;
	// }

	// public static List<String> getFeatureNames(EHICommand myEvent) {
	// List<CommandCategory> aCommandCategories = toCommandCategories(myEvent);
	// List<String> aFeatureNames = new ArrayList();
	// CommandCategoryMapping aCommandCategoryMapping =
	// APredictionParameters.getInstance().
	// getCommandClassificationScheme().
	// getCommandCategoryMapping();
	// for (CommandCategory aCommandCategory:aCommandCategories) {
	// aFeatureNames.add(aCommandCategoryMapping.getFeatureName(aCommandCategory));
	// }
	// return aFeatureNames;
	//// return commandCategories.getFeatureName(aCommandCategory);
	//
	//// if (isInsertOrEditEvent(myEvent)) {
	//// return "Edit";
	////
	//// } else if (isDebugEvent(myEvent)) {
	//// return "Debug";
	////
	//// } else if (isNavigationEvent(myEvent)) {
	//// return "Navigation";
	////
	//// } else if (isFocusEvent(myEvent)) {
	//// return "Focus";
	////
	//// } else if (isAddRemoveEvent(myEvent)) {
	//// return "RemoveClass";
	//// } else {
	//// return "Unclassified";
	//// }
	// }

	// protected void mapCommandCategoryToFeatureName() {
	// commandCategoryToFetaureName.put(CommandCategory.EDIT, "Edit");
	// commandCategoryToFetaureName.put(CommandCategory.INSERT, "Insert");
	//
	//
	//// commandCategoryToFetaureName.put(CommandCategory.EDIT_OR_INSERT,
	// "Edit");
	// commandCategoryToFetaureName.put(CommandCategory.DEBUG, "Debug");
	// commandCategoryToFetaureName.put(CommandCategory.NAVIGATION,
	// "Navigation");
	// commandCategoryToFetaureName.put(CommandCategory.FOCUS, "Focus");
	// commandCategoryToFetaureName.put(CommandCategory.REMOVE, "RemoveClass");
	// commandCategoryToFetaureName.put(CommandCategory.OTHER, "Unclassified");
	//// commandCategoryToFetaureName.put(CommandCategory.SEARCH, "Navigation");
	//
	//
	//
	// }

	// public static RatioCalculator getInstance() {
	// return instance;
	// }

}
