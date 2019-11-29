package fluorite.commands;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//import edu.cmu.scs.fluorite.commands.AbstractCommand;
//import edu.cmu.scs.fluorite.commands.ICommand;
//import edu.cmu.scs.fluorite.util.Utilities;
import fluorite.model.EHEventRecorder;
import fluorite.plugin.EHActivator;
//import fluorite.plugin.Activator;
import fluorite.preferences.Initializer;
import fluorite.util.EHUtilities;

public abstract class AbstractCommand implements
		fluorite.commands.EHICommand, Comparable<EHICommand> {

	private static boolean incrementCommandID = true;
	private static int currentCommandID = -1;

	private static Set<ICommandIndexListener> commandIndexListeners = new HashSet<ICommandIndexListener>();

	public static void addCommandIndexListener(ICommandIndexListener listener) {
		if (!commandIndexListeners.contains(listener)) {
			commandIndexListeners.add(listener);
		}
	}

	public static void removeCommandIndexListener(ICommandIndexListener listener) {
		if (commandIndexListeners.contains(listener)) {
			commandIndexListeners.remove(listener);
		}
	}

	public static int getCurrentCommandID() {
		return currentCommandID;
	}
	
	public static void setIncrementCommandID(boolean state) {
		incrementCommandID = state;
	}
	
	public static boolean getIncrementCommandID() {
		return incrementCommandID;
	}

	public AbstractCommand() {
		if (getIncrementCommandID()) {
			mRepeatCount = 1;
			mCommandIndex = ++currentCommandID;
	
			fireCommandIndexChanged();
		}

		try {
			if (EHActivator.getDefault().getPreferenceStore()
					.getBoolean(Initializer.Pref_LogTopBottomLines)) {
				mTopBottomLinesRecorded = true;
	
				IEditorPart editor = EHUtilities.getActiveEditor();
				StyledText styledText = EHUtilities.getStyledText(editor);
	
				int clientAreaHeight = styledText.getClientArea().height;
				mTopLineNumber = styledText.getLineIndex(0) + 1;
				mBottomLineNumber = styledText.getLineIndex(clientAreaHeight) + 1;
			} else {
				mTopBottomLinesRecorded = false;
			}
		}
		catch (NullPointerException e) {
			// Maybe this is created in a JUnit test.
			// Don't bother to add these things.
		}
	}

	private static void fireCommandIndexChanged() {
		for (ICommandIndexListener listener : commandIndexListeners) {
			listener.commandIndexIncreased(currentCommandID);
		}
	}

	private long mTimestamp;
	private long mTimestamp2;
	private long startTimestamp;

	private int mRepeatCount;
	private int mCommandIndex;

	// Top Bottom Lines
	private boolean mTopBottomLinesRecorded;
	private int mTopLineNumber;
	private int mBottomLineNumber;
	protected IProject project;

	
	public String persist() {
		return EHUtilities.persistCommand(getAttributesMap(), getDataMap(), this);
	}

	public void persist(Document doc, Element commandElement) {
		EHUtilities.persistCommand(doc, commandElement, getCommandType(),
				getAttributesMap(), getDataMap(), this);
	}

	@Override
	public void createFrom(Element commandElement) {
		if (commandElement == null) {
			throw new IllegalArgumentException();
		}
		
		Attr attr = null;
		
		if ((attr = commandElement.getAttributeNode("__id")) != null) {
			mCommandIndex = Integer.parseInt(attr.getValue());
		}
		
		if ((attr = commandElement.getAttributeNode("timestamp")) != null) {
			mTimestamp = Long.parseLong(attr.getValue());
		}
		if ((attr = commandElement.getAttributeNode("starttimestamp")) != null) {
			startTimestamp = Long.parseLong(attr.getValue());
		}
		
		if ((attr = commandElement.getAttributeNode("repeat")) != null) {
			mRepeatCount = Integer.parseInt(attr.getValue());
		}
		else {
			mRepeatCount = 1;
		}
		
		if ((attr = commandElement.getAttributeNode("timestamp2")) != null) {
			mTimestamp2 = Long.parseLong(attr.getValue());
		}
		
		mTopBottomLinesRecorded = false;
		if ((attr = commandElement.getAttributeNode("topLine")) != null) {
			mTopLineNumber = Integer.parseInt(attr.getValue());
			mTopBottomLinesRecorded = true;
		}
		
		if ((attr = commandElement.getAttributeNode("bottomLine")) != null) {
			mBottomLineNumber = Integer.parseInt(attr.getValue());
			mTopBottomLinesRecorded = true;
		}
	}

	public void setStartTimestamp(long timestamp) {
		startTimestamp = timestamp;
	}

	public long getStartTimestamp() {
		return startTimestamp;
	}
	
	public void setTimestamp(long timestamp) {
		mTimestamp = timestamp;
	}

	public long getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp2(long timestamp) {
		mTimestamp2 = timestamp;
	}

	public long getTimestamp2() {
		return mTimestamp2;
	}

	public void increaseRepeatCount() {
		++mRepeatCount;
	}

	public int getRepeatCount() {
		return mRepeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		mRepeatCount = repeatCount;
	}

	public int getCommandIndex() {
		return mCommandIndex;
	}

	public boolean areTopBottomLinesRecorded() {
		return mTopBottomLinesRecorded;
	}

	public int getTopLineNumber() {
		return mTopLineNumber;
	}

	public int getBottomLineNumber() {
		return mBottomLineNumber;
	}

	/*
	 * Trap calls to (Icimmand rather than EHICommandto handle more cases
	 */
//	public boolean combineWith(ICommand anotherCommand) {
////		IPreferenceStore prefStore = fluorite.plugin.Activator
////				.getDefault().getPreferenceStore();
//		
//		IPreferenceStore prefStore = EHActivator
//				.getDefault().getPreferenceStore();
//
//		// preference option check.
//		if (!prefStore.getBoolean(Initializer.Pref_CombineCommands)) {
//			return false;
//		}
//
//		// Time threshold check.
//		if (anotherCommand.getTimestamp() - getTimestamp2() > prefStore
//				.getInt(Initializer.Pref_CombineTimeThreshold)) {
//			return false;
//		}
//
//		if (combine(anotherCommand)) {
//			setTimestamp2(anotherCommand.getTimestamp());
//			increaseRepeatCount();
//			return true;
//		}
//
//		return false;
//	}
	
//
//	public abstract boolean combine(EHICommand anotherCommand);
//
	public String getCommandTag() {
		String commandTag = EHEventRecorder.XML_Command_Tag;
		String categoryID = getCategoryID();
		if (categoryID == null) {
			// do nothing
		} else if (categoryID.equals(EHEventRecorder.DocumentChangeCategoryID)) {
			commandTag = EHEventRecorder.XML_DocumentChange_Tag;
		} else if (categoryID.equals(EHEventRecorder.AnnotationCategoryID)) {
			commandTag = EHEventRecorder.XML_Annotation_Tag;
		}

		return commandTag;
	}
	
	public static void resetCommandID() {
		currentCommandID = -1;
		fireCommandIndexChanged();
	}
	
	public String toString() {		
		return getCommandType() + " " + 
					getName() + " " + 
					(getDescription() != null? getDescription(): "") +  " " + 
					getCategory();
	}
	
/// Stuff thathas been added	
	public boolean combineWith(EHICommand anotherCommand) {
		return combineWith(this, anotherCommand);
	}

	
	public static boolean combineWith(AbstractCommand aCommand, EHICommand anotherCommand) {
//		IPreferenceStore prefStore = fluorite.plugin.Activator
//				.getDefault().getPreferenceStore();
		
		IPreferenceStore prefStore = EHActivator
				.getDefault().getPreferenceStore();

		// preference option check.
		if (!prefStore.getBoolean(Initializer.Pref_CombineCommands)) {
			return false;
		}

		// Time threshold check.
		if (anotherCommand.getTimestamp() - aCommand.getTimestamp2() > prefStore
				.getInt(Initializer.Pref_CombineTimeThreshold)) {
			return false;
		}

		if (aCommand.combine(anotherCommand)) {
			aCommand.setTimestamp2(anotherCommand.getTimestamp());
			aCommand.increaseRepeatCount();
			return true;
		}

		return false;
	}
	public boolean combine(EHICommand arg0) {
		return false;
	}
	public int compareTo(EHICommand o){
		return (int) (getTimestamp() - o.getTimestamp());
		
	}
	@Override
	public IProject getProject() {
		return project;
	}
	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

}
