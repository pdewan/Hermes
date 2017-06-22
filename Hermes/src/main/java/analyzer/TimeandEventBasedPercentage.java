package analyzer;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import difficultyPrediction.metrics.APercentageCalculator;
import difficultyPrediction.metrics.RatioCalculator;
import fluorite.commands.EHEclipseCommand;
import fluorite.commands.EHICommand;

public class TimeandEventBasedPercentage {

	public final static int INSERTION_EVENT_INDEX = 5;
	public final static int DELETION_EVENT_INDEX = 6;
	public final static int EXCEPTION_EVENT_INDEX = 7;
	// this class computes percentages
	private RatioCalculator percentage;
	private long savedTimeStamp;
	public TimeandEventBasedPercentage() {
		percentage = new APercentageCalculator();
		savedTimeStamp = 0;
	}


	private long currentTimeStamp;
	public long getCurrentTimeStamp()
	{
		return currentTimeStamp;
	}
	
	public long getSavedTimeStamp()
	{
		return savedTimeStamp;
	}
	public static double computeDebugPercentage(ArrayList<Integer> eventData) {
		double numberOfDebugEvents = eventData
				.get(APercentageCalculator.DEBUG_EVENT_INDEX);
		double numberOfSearchEvents = eventData
				.get(APercentageCalculator.SEARCH_EVENT_INDEX);
		double numberOfEditEvents = eventData.get(APercentageCalculator.EDIT_EVENT_INDEX);
		double numberOfFocusEvents = eventData
				.get(APercentageCalculator.FOCUS_EVENT_INDEX);
		double numberOfRemoveEvents = eventData
				.get(APercentageCalculator.REMOVE_EVENT_INDEX);
		double numberOfInsertionEvents = eventData.get(INSERTION_EVENT_INDEX);
		double numberOfDeletionEvents = eventData.get(DELETION_EVENT_INDEX);
		double debugPercentage = 0;

		if (numberOfDebugEvents > 0)
			debugPercentage = (numberOfDebugEvents / (numberOfDebugEvents
					+ numberOfSearchEvents + numberOfInsertionEvents
					+ numberOfDeletionEvents + numberOfFocusEvents + numberOfRemoveEvents)) * 100;

		return debugPercentage;
	}

	public static double computeNavigationPercentage(
			ArrayList<Integer> eventData) {
		double numberOfDebugEvents = eventData
				.get(APercentageCalculator.DEBUG_EVENT_INDEX);
		double numberOfSearchEvents = eventData
				.get(APercentageCalculator.SEARCH_EVENT_INDEX);
		double numberOfEditEvents = eventData.get(APercentageCalculator.EDIT_EVENT_INDEX);
		double numberOfFocusEvents = eventData
				.get(APercentageCalculator.FOCUS_EVENT_INDEX);
		double numberOfRemoveEvents = eventData
				.get(APercentageCalculator.REMOVE_EVENT_INDEX);
		double numberOfInsertionEvents = eventData.get(INSERTION_EVENT_INDEX);
		double numberOfDeletionEvents = eventData.get(DELETION_EVENT_INDEX);

		double navigationPercentage = 0;

		if (numberOfSearchEvents > 0)
			navigationPercentage = (numberOfSearchEvents / (numberOfDebugEvents
					+ numberOfSearchEvents + numberOfInsertionEvents
					+ numberOfDeletionEvents + numberOfFocusEvents + numberOfRemoveEvents)) * 100;

		return navigationPercentage;
	}

	public static double computeInsertionPercentage(ArrayList<Integer> eventData) {
		double numberOfDebugEvents = eventData
				.get(APercentageCalculator.DEBUG_EVENT_INDEX);
		double numberOfSearchEvents = eventData
				.get(APercentageCalculator.SEARCH_EVENT_INDEX);
		double numberOfEditEvents = eventData.get(APercentageCalculator.EDIT_EVENT_INDEX);
		double numberOfFocusEvents = eventData
				.get(APercentageCalculator.FOCUS_EVENT_INDEX);
		double numberOfRemoveEvents = eventData
				.get(APercentageCalculator.REMOVE_EVENT_INDEX);
		double numberOfInsertionEvents = eventData.get(INSERTION_EVENT_INDEX);
		double numberOfDeletionEvents = eventData.get(DELETION_EVENT_INDEX);

		double insertionPercentage = 0;

		if (numberOfInsertionEvents > 0)
			insertionPercentage = (numberOfInsertionEvents / (numberOfDebugEvents
					+ numberOfSearchEvents
					+ numberOfInsertionEvents
					+ numberOfDeletionEvents + numberOfFocusEvents + numberOfRemoveEvents)) * 100;

		return insertionPercentage;
	}

	public static double computeDeletionPercentage(ArrayList<Integer> eventData) {
		double numberOfDebugEvents = eventData
				.get(APercentageCalculator.DEBUG_EVENT_INDEX);
		double numberOfSearchEvents = eventData
				.get(APercentageCalculator.SEARCH_EVENT_INDEX);
		double numberOfEditEvents = eventData.get(APercentageCalculator.EDIT_EVENT_INDEX);
		double numberOfFocusEvents = eventData
				.get(APercentageCalculator.FOCUS_EVENT_INDEX);
		double numberOfRemoveEvents = eventData
				.get(APercentageCalculator.REMOVE_EVENT_INDEX);
		double numberOfInsertionEvents = eventData.get(INSERTION_EVENT_INDEX);
		double numberOfDeletionEvents = eventData.get(DELETION_EVENT_INDEX);

		double deletionPercentage = 0;

		if (numberOfDeletionEvents > 0)
			deletionPercentage = (numberOfDeletionEvents / (numberOfDebugEvents
					+ numberOfSearchEvents + numberOfInsertionEvents
					+ numberOfDeletionEvents + numberOfFocusEvents + numberOfRemoveEvents)) * 100;

		return deletionPercentage;
	}

	public static double computeFocusPercentage(ArrayList<Integer> eventData) {
		double numberOfDebugEvents = eventData
				.get(APercentageCalculator.DEBUG_EVENT_INDEX);
		double numberOfSearchEvents = eventData
				.get(APercentageCalculator.SEARCH_EVENT_INDEX);
		double numberOfEditEvents = eventData.get(APercentageCalculator.EDIT_EVENT_INDEX);
		double numberOfFocusEvents = eventData
				.get(APercentageCalculator.FOCUS_EVENT_INDEX);
		double numberOfRemoveEvents = eventData
				.get(APercentageCalculator.REMOVE_EVENT_INDEX);
		double numberOfInsertionEvents = eventData.get(INSERTION_EVENT_INDEX);
		double numberOfDeletionEvents = eventData.get(DELETION_EVENT_INDEX);

		double focusPercentage = 0;

		if (numberOfFocusEvents > 0)
			focusPercentage = (numberOfFocusEvents / (numberOfDebugEvents
					+ numberOfSearchEvents + numberOfInsertionEvents
					+ numberOfDeletionEvents + numberOfFocusEvents + numberOfRemoveEvents)) * 100;

		return focusPercentage;
	}

	public static double computeRemoveEventsPercentage(
			ArrayList<Integer> eventData) {
		double numberOfDebugEvents = eventData
				.get(APercentageCalculator.DEBUG_EVENT_INDEX);
		double numberOfSearchEvents = eventData
				.get(APercentageCalculator.SEARCH_EVENT_INDEX);
		double numberOfEditEvents = eventData.get(APercentageCalculator.EDIT_EVENT_INDEX);
		double numberOfFocusEvents = eventData
				.get(APercentageCalculator.FOCUS_EVENT_INDEX);
		double numberOfRemoveEvents = eventData
				.get(APercentageCalculator.REMOVE_EVENT_INDEX);
		double numberOfInsertionEvents = eventData.get(INSERTION_EVENT_INDEX);
		double numberOfDeletionEvents = eventData.get(DELETION_EVENT_INDEX);

		double removePercentage = 0;

		if (numberOfRemoveEvents > 0)
			removePercentage = (numberOfRemoveEvents / (numberOfDebugEvents
					+ numberOfSearchEvents + numberOfInsertionEvents
					+ numberOfDeletionEvents + numberOfFocusEvents + numberOfRemoveEvents)) * 100;

		return removePercentage;
	}

	public static double computeExceptionPerRun(ArrayList<Integer> eventData)
	{
		double numberOfExceptionsPerRun = 0;
		double numberOfDebugEvents = eventData
				.get(APercentageCalculator.DEBUG_EVENT_INDEX);
		double numberOfExceptions = eventData.get(EXCEPTION_EVENT_INDEX);
		
		if(numberOfDebugEvents > 0)
		{
			numberOfExceptionsPerRun = numberOfExceptions/numberOfDebugEvents;
		}

		return numberOfExceptionsPerRun;
	}
	
	private long startTimeStamp;
	public ArrayList<Double> computeMetrics(List<EHICommand> userActions, long startTimeStamp) {
		
		
		this.startTimeStamp = startTimeStamp;
		ArrayList<Integer> metrics = getPercentageData(userActions);
		double debugPercentage = computeDebugPercentage(metrics);
		double editPercentage = 0;//computeEditPercentage(metrics);
		double navigationPercentage = computeNavigationPercentage(metrics);
		double removePercentage = APercentageCalculator
				.computeRemoveEventsPercentage(metrics);
		double focusPercentage = computeFocusPercentage(metrics);
		double insertionPercentage = computeInsertionPercentage(metrics);
		double deletionPercentage = computeDeletionPercentage(metrics);
		double numberOfExceptionsPerRun = computeExceptionPerRun(metrics);
		// int searchPercentage, int debugPercentage, int focusPercentage , int
		// editPercentage, int removePercentage
		ArrayList<Double> percentages = new ArrayList<Double>();
		percentages.add(navigationPercentage);
		percentages.add(debugPercentage);
		percentages.add(focusPercentage);
		percentages.add(editPercentage);
		percentages.add(removePercentage);
		percentages.add(insertionPercentage);
		percentages.add(deletionPercentage);
		percentages.add(numberOfExceptionsPerRun);

		return percentages;
	}

	public boolean isEditEvent(EHICommand event) {
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
				|| (event.getCommandType().equals("MoveCaretCommand"))

		) {
			isEditEvent = true;
		}

		if (event.getCommandType().equals("EclipseCommand")) {
			EHEclipseCommand eclipseCommand = (EHEclipseCommand) event;
			if (eclipseCommand.getName().toLowerCase().contains("edit")) {
				isEditEvent = true;
			}
		}

		return isEditEvent;
	}

	public boolean isExceptionEvent(EHICommand event) {
		boolean isExceptionEvent = false;

		if ((event.getCommandType().equals("Exception"))) {
			isExceptionEvent = true;
		}
		return isExceptionEvent;
	}

	public boolean isInsertionEvent(EHICommand event) {
		boolean isInsertionEvent = false;
		if ((event.getCommandType().equals("CopyCommand"))
				|| (event.getCommandType().equals("Insert"))
				|| (event.getCommandType().equals("InsertStringCommand"))
				|| (event.getCommandType().equals("PasteCommand"))
				|| (event.getCommandType().equals("Replace"))
				|| (event.getCommandType().equals("MoveCaretCommand"))
				|| (event.getCommandType().equals("SelectTextCommand"))) {
			isInsertionEvent = true;
		}

		if (event.getCommandType().equals("EclipseCommand")) {
			EHEclipseCommand eclipseCommand = (EHEclipseCommand) event;
			if (eclipseCommand.getCommandID().toLowerCase().contains("edit")) {
				isInsertionEvent = true;
			}
		}

		return isInsertionEvent;
	}

	public boolean isDeletionEvent(EHICommand event) {
		boolean isDeletionEvent = false;
		if ((event.getCommandType().equals("CutCommand"))
				|| (event.getCommandType().equals("Delete"))) {
			isDeletionEvent = true;
		}

		if (event.getCommandType().equals("EclipseCommand")) {
			EHEclipseCommand eclipseCommand = (EHEclipseCommand) event;
			if (eclipseCommand.getCommandID().toLowerCase().contains("delete")) {
				isDeletionEvent = true;
			}
		}

		return isDeletionEvent;
	}
	
	public boolean isDebugEvent(EHICommand event) {
		boolean isDebugEvent = false;
		if ((event.getCommandType().equals("BreakPointCommand"))
				|| (event.getCommandType().equals("ExceptionCommand"))
				|| (event.getCommandType().equals("RunCommand"))) {
			isDebugEvent = true;
		}
		
//		if(event.getCommandType().equals("CompilationCommand"))
//		{
//			CompliationCommand command = (CompliationCommand)event;
//			//if the compilation is an error
//			if(! command.getIsWarning())
//			{
//				isDebugEvent = true;
//			}
//		}

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

	//i want to compute the amount of time someone spends inserting text
	//if you see an insertion
	//start timestamp
	//insert 5:30, insert 5:32, insert 5:34, delete 5:35, insert 5:36, delete 5:34, delete 5:45, delete 5:50
	
//	subtract the insert timestamp
//	from the delete timestamp
//	and don't do any more subtraction until i see an insertion
	//current - previous
	public ArrayList<Integer> getPercentageData(List<EHICommand> userActions) {
		int numberOfDebugEvents = 0;
		int numberOfSearchEvents = 0;
		int numberOfEditEvents = 0;
		int numberOfFocusEvents = 0;
		int numberOfRemoveEvents = 0;
		int numberOfInsertionEvents = 0;
		int numberOfDeletionEvents = 0;
		int numberOfExceptions = 0;

		long currentTime = 0;
		long insertionTime = 0;
		for (int i = 0; i < userActions.size(); i++) {
			EHICommand myEvent = userActions.get(i);
			
			
		
			
			//algorithm to determine insertion time
			//if this is the first time and this event is insertion
			//set insertion time equal to the timestamp of the event, startimestamp + myEventTimestamp
			//
			
				
//			if(savedTimeStamp == 0)
//			{
//				savedTimeStamp = this.startTimeStamp;
//			}
//			else
//			{
//				 currentTime = savedTimeStamp + myEvent.getTimestamp();	
//			}
			
			 currentTime = this.startTimeStamp + myEvent.getTimestamp();

			// if (isEditEvent(myEvent))
			// numberOfEditEvents++;
			if (isInsertionEvent(myEvent))
			{
				numberOfInsertionEvents++;
			}
			else if (isDeletionEvent(myEvent))
				numberOfDeletionEvents++;
			else if (isDebugEvent(myEvent))
				numberOfDebugEvents++;
			else if (percentage.isNavigationEvent(myEvent))
				numberOfSearchEvents++;
			else if (percentage.isFocusEvent(myEvent))
				numberOfFocusEvents++;
			else if (percentage.isAddRemoveEvent(myEvent))
				numberOfRemoveEvents++;
			else if(isExceptionEvent(myEvent))
				numberOfExceptions++;
			
			System.out.println(currentTime);
			
		}
		currentTimeStamp = currentTime;
		//get the timestamp of the last event
		//savedTimeStamp = currentTime;
		DateTime timestamp = new DateTime(currentTimeStamp);
		System.out.println(timestamp.toString("MM-dd-yyyy H:mm:ss"));
		ArrayList<Integer> eventData = new ArrayList<Integer>();
		eventData.add(numberOfDebugEvents);
		eventData.add(numberOfSearchEvents);
		eventData.add(numberOfEditEvents);
		eventData.add(numberOfFocusEvents);
		eventData.add(numberOfRemoveEvents);
		eventData.add(numberOfInsertionEvents);
		eventData.add(numberOfDeletionEvents);
		eventData.add(numberOfExceptions);
		return eventData;
	}
}
