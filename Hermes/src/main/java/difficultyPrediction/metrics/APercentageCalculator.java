package difficultyPrediction.metrics;

import java.util.ArrayList;
import java.util.List;

import difficultyPrediction.featureExtraction.RatioFeatures;
import fluorite.commands.EHCompilationCommand;
import fluorite.commands.EHEclipseCommand;
import fluorite.commands.EHICommand;


public class APercentageCalculator implements RatioCalculator {
	public final static int DEBUG_EVENT_INDEX = 0;
	public final static int SEARCH_EVENT_INDEX = 1;
	public final static int EDIT_EVENT_INDEX = 2;
	public final static int FOCUS_EVENT_INDEX = 3;
	public final static int REMOVE_EVENT_INDEX = 4;
	static RatioCalculator instance;

	/* (non-Javadoc)
	 * @see difficultyPrediction.metrics.FeatureCalculator#isDebugEvent(edu.cmu.scs.fluorite.commands.ICommand)
	 */
	@Override
	public boolean isDebugEvent(EHICommand event) {
		boolean isDebugEvent = false;
		if ((event.getCommandType().equals("BreakPointCommand"))
				|| (event.getCommandType().equals("ExceptionCommand"))
				|| (event.getCommandType().equals("RunCommand"))) {
			isDebugEvent = true;
		}
		
		if(event.getCommandType().equals("CompilationCommand"))
		{
			EHCompilationCommand command = (EHCompilationCommand)event;
			//if the compilation is an error
			if(! command.getIsWarning())
			{
				isDebugEvent = true;
			}
		}

		// switch(event.getEventKind()) {
		// case DEBUG:
		// isDebugEvent = true;
		// break;
		// /*case BUILD:
		// isDebugEvent = true;
		// break;*/
		// }
		return isDebugEvent;
	}

	/* (non-Javadoc)
	 * @see difficultyPrediction.metrics.FeatureCalculator#isEditEvent(edu.cmu.scs.fluorite.commands.ICommand)
	 */
	@Override
	public boolean isInsertOrEditEvent(EHICommand event) {
		boolean isEditEvent = false;
		if ((event.getCommandType().equals("CopyCommand"))
				|| (event.getCommandType().equals("CutCommand"))
				|| (event.getCommandType().equals("Delete"))
				|| (event.getCommandType().equals("Insert"))
				|| (event.getCommandType().equals("InsertStringCommand"))
				|| (event.getCommandType().equals("PasteCommand"))
				|| (event.getCommandType().equals("RedoCommand"))
				|| (event.getCommandType().equals("Replace"))
				|| (event.getCommandType().equals("SelectTextCommand"))
				|| (event.getCommandType().equals("UndoCommand"))

		) {
			isEditEvent = true;
		}

		if (event.getCommandType().equals("EclipseCommand")) {
			EHEclipseCommand eclipseCommand = (EHEclipseCommand) event;
			if (eclipseCommand.getCommandID().toLowerCase().contains("edit")) {
				isEditEvent = true;
			}
		}
		// switch(event.getEventKind()) {
		// case EDIT:
		// isEditEvent=true;
		// break;
		// case COMMAND:
		// if (event.getCommandId() != null) {
		// if(event.getCommandId().toLowerCase().contains("edit")) {
		// isEditEvent = true;
		// }
		// }
		// break;
		// }
		return isEditEvent;
	}

	/* (non-Javadoc)
	 * @see difficultyPrediction.metrics.FeatureCalculator#isNavigationEvent(edu.cmu.scs.fluorite.commands.ICommand)
	 */
	@Override
	public boolean isNavigationEvent(EHICommand event) {
		boolean isNavigationEvent = false;
		if (event.getCommandType().equals("EclipseCommand")) {
			EHEclipseCommand eclipseCommand = (EHEclipseCommand) event;
			if (eclipseCommand.getCommandID().toLowerCase().contains("view")) {
				isNavigationEvent = true;
			}
		}

		if (event.getCommandType().equals("FileOpenCommand")
				|| (event.getCommandType().equals("FindCommand"))) {
			isNavigationEvent = true;
		}

		// switch(event.getEventKind())
		// {
		// case COMMAND:
		// if (event.getCommandId() != null)
		// {
		// if (event.getCommandId().toLowerCase().contains("view"))
		// {
		// isNavigationEvent = true;
		// }
		// }
		// break;
		// case WINDOW:
		// if (event.getWindowEventKind() == WindowEventKind.ACTIVATED)
		// {
		// if (event.getWindowTitle() != null)
		// {
		// if (event.getWindowTitle().contains(".cs") ||
		// event.getWindowTitle().contains(".cpp") ||
		// event.getWindowTitle().contains(".java"))
		// {
		// isNavigationEvent = true;
		// }
		//
		// }
		// }
		// break;
		// }
		return isNavigationEvent;
	}

	/* (non-Javadoc)
	 * @see difficultyPrediction.metrics.FeatureCalculator#isFocusEvent(edu.cmu.scs.fluorite.commands.ICommand)
	 */
	@Override
	public boolean isFocusEvent(EHICommand event) {
		boolean isFocusEvent = false;

		if (event.getCommandType().equals("ShellCommand")) {
			isFocusEvent = true;
		}
		// switch(event.getEventKind())
		// {
		// case WINDOW:
		// if (event.getWindowEventKind() == WindowEventKind.ECLIPSE_LOST_FOCUS)
		// isFocusEvent = true;
		// break;
		// }

		return isFocusEvent;
	}

	/* (non-Javadoc)
	 * @see difficultyPrediction.metrics.FeatureCalculator#isAddRemoveEvent(edu.cmu.scs.fluorite.commands.ICommand)
	 */
	@Override
	public boolean isAddRemoveEvent(EHICommand event) {
		boolean isAddRemoveEvent = false;
		// only had code for visual studio events here
		return isAddRemoveEvent;
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

	/* (non-Javadoc)
	 * @see difficultyPrediction.metrics.FeatureCalculator#computeMetrics(java.util.List)
	 */
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

	/* (non-Javadoc)
	 * @see difficultyPrediction.metrics.FeatureCalculator#getPercentageData(java.util.List)
	 */
	@Override
	public  ArrayList<Integer> getPercentageData(List<EHICommand> userActions) {
		int numberOfDebugEvents = 0;
		int numberOfSearchEvents = 0;
		int numberOfEditEvents = 0;
		int numberOfFocusEvents = 0;
		int numberOfRemoveEvents = 0;

		for (int i = 0; i < userActions.size(); i++) {
			EHICommand myEvent = userActions.get(i);

			if (isInsertOrEditEvent(myEvent)) {
				numberOfEditEvents++;
				System.out.println ("Edit command:" + myEvent);
			} else if (isDebugEvent(myEvent)) {
				numberOfDebugEvents++;
				System.out.println ("Debug command:" + myEvent);

			} else if (isNavigationEvent(myEvent)) {
				numberOfSearchEvents++;
				System.out.println ("navigation command:" + myEvent);

			} else if (isFocusEvent(myEvent)) {
				numberOfFocusEvents++;
				System.out.println ("Focus command:" + myEvent);

			} else if (isAddRemoveEvent(myEvent)) {
				numberOfRemoveEvents++;
				System.out.println ("remove command:" + myEvent);
			} else {
				System.out.println("Unclassified command: " + myEvent);
			}

		}

		ArrayList<Integer> eventData = new ArrayList<Integer>();
		eventData.add(numberOfDebugEvents);
		eventData.add(numberOfSearchEvents);
		eventData.add(numberOfEditEvents);
		eventData.add(numberOfFocusEvents);
		eventData.add(numberOfRemoveEvents);

		return eventData;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.metrics.FeatureCalculator#getFeatureName(edu.cmu.scs.fluorite.commands.ICommand)
	 */
	@Override
	public  String getFeatureName(EHICommand myEvent) {
		
			if (isInsertOrEditEvent(myEvent)) {
				return "Edit";
				
			} else if (isDebugEvent(myEvent)) {
				return "Debug";

			} else if (isNavigationEvent(myEvent)) {
				return "Navigation";

			} else if (isFocusEvent(myEvent)) {
				return "Focus";

			} else if (isAddRemoveEvent(myEvent)) {
				return "RemoveClass";
			} else {
				return "Unclassified";
			}

		

	}
//	public static RatioCalculator getInstance() {
//		if (instance == null)
//			instance = new APercentageCalculator();
//		return instance;
//	}

	@Override
	public RatioFeatures computeRatioFeatures(List<EHICommand> userActions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RatioFeatures computeFeatures(List<EHICommand> userActions) {
		// TODO Auto-generated method stub
		return null;
	}

}
