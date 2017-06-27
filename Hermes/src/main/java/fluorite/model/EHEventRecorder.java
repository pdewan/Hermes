package fluorite.model;

import java.io.File;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dayton.ellwanger.hermes.HermesActivator;
import util.trace.plugin.PluginStopped;
import util.trace.recorder.ReceivedCommand;
import util.trace.recorder.AddedCommandToBuffers;
import util.trace.recorder.CombinedCommand;
import util.trace.recorder.DocumentChangeCommandExecuted;
import util.trace.recorder.DocumentChangeCommandNotified;
import util.trace.recorder.DocumentChangeFinalizedEventNotified;
import util.trace.recorder.RemovedCommandFromBuffers;
import util.trace.recorder.ForwardedCommandToPredictor;
import util.trace.recorder.IgnoredCommandAsRecordingSuspended;
import util.trace.recorder.LogFileCreated;
import util.trace.recorder.LogHandlerBound;
import util.trace.recorder.PendingCommandsLogBegin;
import util.trace.recorder.PendingCommandsLogEnd;
import util.trace.recorder.CommandLoggingInitiated;
import util.trace.recorder.MacroRecordingStarted;
import util.trace.recorder.NewMacroCommand;
import util.trace.recorder.NonDocumentChangeCommandExecuted;
import util.trace.recorder.NonDocumentChangeCommandNotified;
import util.trace.recorder.RecordedCommandsCleared;
import util.trace.workbench.PartListenerAdded;
import difficultyPrediction.ADifficultyPredictionPluginEventProcessor;
import edu.cmu.scs.fluorite.model.EventRecorder;
import fluorite.actions.FindAction;
import fluorite.commands.EHBaseDocumentChangeEvent;
import fluorite.commands.DifficulyStatusCommand;
import fluorite.commands.EHFileOpenCommand;
import fluorite.commands.EHFindCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.EHMoveCaretCommand;
import fluorite.commands.PredictionCommand;
import fluorite.plugin.EHActivator;
import fluorite.commands.EHSelectTextCommand;
import fluorite.preferences.Initializer;
import fluorite.recorders.EHBreakPointRecorder;
import fluorite.recorders.EHCompletionRecorder;
import fluorite.recorders.EHConsoleRecorder;
import fluorite.recorders.EHDebugEventSetRecorder;
import fluorite.recorders.EHDocumentRecorder;
import fluorite.recorders.EHExecutionRecorder;
import fluorite.recorders.EHPartRecorder;
import fluorite.recorders.EHShellRecorder;
import fluorite.recorders.EHStyledTextEventRecorder;
import fluorite.recorders.EHVariableValueRecorder;
import fluorite.util.EHUtilities;
import edu.cmu.scs.fluorite.model.CommandExecutionListener;

/*
 * Cannot extend EventRecorder as it has a private constructor
 */
public class EHEventRecorder {

	public static final String MacroCommandCategory = "EventLogger utility command";
	public static final String MacroCommandCategoryID = "eventlogger.category.utility.command";
	public static final String UserMacroCategoryID = "eventlogger.category.usermacros";
	public static final String UserMacroCategoryName = "User defined editor macros";
	public static final String AnnotationCategory = "Annotation";
	public static final String AnnotationCategoryID = "eventlogger.category.annotation";
	public static final String DocumentChangeCategory = "Every document changes";
	public static final String DocumentChangeCategoryID = "eventlogger.category.documentChange";
	public static final String DifficultyCategory = "Difficulty";
	public static final String DifficultyCategoryID = "eventerlogger.category.Difficulty";

	public static final String XML_Macro_Tag = "Events";
	public static final String XML_ID_Tag = "__id";
	public static final String XML_Description_Tag = "description";
	public static final String XML_Command_Tag = "Command";
	public static final String XML_DifficultyStatus_Tag = "DifficultyStatus";
	public static final String XML_DocumentChange_Tag = "DocumentChange";
	public static final String XML_Annotation_Tag = "Annotation";
	public static final String XML_CommandType_ATTR = "_type";
	public static final String PREF_USER_MACRO_DEFINITIONS = "Preference_UserMacroDefinitions";

	private IEditorPart mEditor;
	private LinkedList<EHICommand> allDocAndNonDocCommands;
	private LinkedList<EHICommand> mNormalCommands;
	private LinkedList<EHICommand> mDocumentChangeCommands;
	private boolean mCurrentlyExecutingCommand;
	private boolean mRecordCommands = false;
	private IAction mSavedFindAction;

	private int mLastCaretOffset;
	private int mLastSelectionStart;
	private int mLastSelectionEnd;

	private long mStartTimestamp;

	private boolean mStarted;
	private boolean mAssistSession;

	private boolean mCombineCommands;
	private boolean mNormalCommandCombinable;
	private boolean mDocChangeCombinable;
	private int mCombineTimeThreshold;

	private EHBaseDocumentChangeEvent mLastFiredDocumentChange;

	private Timer mTimer;
	private TimerTask mNormalTimerTask;
	private TimerTask mDocChangeTimerTask;

	private ListenerList mDocumentChangeListeners;
	private ListenerList<EHCommandExecutionListener> mCommandExecutionListeners;
	protected ListenerList<RecorderListener> recorderListener;
	protected ListenerList<EclipseEventListener> eventListener;



	private List<Runnable> mScheduledTasks;

	private static EHEventRecorder instance = null;
	// private static DifficultyRobot statusPredictor = null;

	private static TrayItem trayItem;
	static boolean asyncFireEvent = true;
	// private static ToolTip balloonTip;

	// protected Thread difficultyPredictionThread;
	// protected DifficultyPredictionRunnable difficultyPredictionRunnable;
	// protected BlockingQueue<ICommand> pendingPredictionCommands;
	//
	// enum PredictorThreadOption {
	// USE_CURRENT_THREAD,
	// NO_PROCESSING,
	// THREAD_PER_ACTION,
	// SINGLE_THREAD
	// } ;
	//// PredictorThreadOption predictorThreadOption =
	// PredictorThreadOption.THREAD_PER_ACTION;
	//// PredictorThreadOption predictorThreadOption =
	// PredictorThreadOption.USE_CURRENT_THREAD;
	//// PredictorThreadOption predictorThreadOption =
	// PredictorThreadOption.NO_PROCESSING;
	// PredictorThreadOption predictorThreadOption =
	// PredictorThreadOption.SINGLE_THREAD;

	private final static Logger LOGGER = Logger.getLogger(EHEventRecorder.class.getName());

	public static EHEventRecorder getInstance() {
		if (instance == null) {
			instance = new EHEventRecorder();
		}

		return instance;
	}

	private EHEventRecorder() {
		mEditor = null;

		mStarted = false;
		mAssistSession = false;

		mDocumentChangeListeners = new ListenerList();
		mCommandExecutionListeners = new ListenerList<>();
		recorderListener = new ListenerList<>();
		eventListener = new ListenerList<>();

		mTimer = new Timer();

		mScheduledTasks = new ArrayList<Runnable>();

		// statusPredictor = new DifficultyRobot("");
	}

	public void setCurrentlyExecutingCommand(boolean executingCommand) {
		mCurrentlyExecutingCommand = executingCommand;
	}

	public boolean isCurrentlyExecutingCommand() {
		return mCurrentlyExecutingCommand;
	}

	public void setIncrementalFindForward(boolean incrementalFindForward) {
		mIncrementalFindForward = incrementalFindForward;
	}

	public boolean isIncrementalFindForward() {
		return mIncrementalFindForward;
	}

	public void setIncrementalFindMode(boolean incrementalFindMode) {
		mIncrementalFindMode = incrementalFindMode;
	}

	public boolean isIncrementalFindMode() {
		return mIncrementalFindMode;
	}

	public void setIncrementalListener(Listener incrementalListener) {
		mIncrementalListener = incrementalListener;
	}

	public int getLastCaretOffset() {
		return mLastCaretOffset;
	}

	public int getLastSelectionStart() {
		return mLastSelectionStart;
	}

	public int getLastSelectionEnd() {
		return mLastSelectionEnd;
	}

	public void setAssistSession(boolean assistSession) {
		mAssistSession = assistSession;
	}

	public boolean isAssistSession() {
		return mAssistSession;
	}

	public void addDocumentChangeListener(EHDocumentChangeListener docChangeListener) {
		mDocumentChangeListeners.add(docChangeListener);
	}

	public void removeDocumentChangeListener(EHDocumentChangeListener docChangeListener) {
		mDocumentChangeListeners.remove(docChangeListener);
	}
	
	public void addRecorderListener(RecorderListener aListener) {
		recorderListener.add(aListener);
		if (mStartTimestamp > 0) {
			aListener.eventRecordingStarted(mStartTimestamp);
		}
	}

	public void removeRecorderListeer(RecorderListener aListener) {
		recorderListener.remove(aListener);
	}
	public void addEclipseEventListener(EclipseEventListener aListener) {
		eventListener.add(aListener);
		addRecorderListener(aListener);
//		recorderListener.add(aListener);
	}

	public void removeEclipseEventListener(RecorderListener aListener) {
		eventListener.remove(aListener);
		recorderListener.remove(aListener);
	}
	
	public void addCommandExecutionListener(EHCommandExecutionListener aListener) {
		mCommandExecutionListeners.add(aListener);
	}

	public void removeCommandExecutionListener(EHCommandExecutionListener aListener) {
		mDocumentChangeListeners.remove(aListener);
	}

	public void setCombineCommands(boolean enabled) {
		mCombineCommands = enabled;
	}

	public boolean getCombineCommands() {
		return mCombineCommands;
	}

	public void setCombineTimeThreshold(int newThreshold) {
		mCombineTimeThreshold = newThreshold;
	}

	public int getCombineTimeThreshold() {
		return mCombineTimeThreshold;
	}

	private Timer getTimer() {
		return mTimer;
	}

	public void fireActiveFileChangedEvent(EHFileOpenCommand foc) {
		for (Object listenerObj : mDocumentChangeListeners.getListeners()) {
			((EHDocumentChangeListener) listenerObj).activeFileChanged(foc);
		}
	}

	public void fireDocumentChangedEvent(EHBaseDocumentChangeEvent docChange) {
		for (Object listenerObj : mDocumentChangeListeners.getListeners()) {
			((EHDocumentChangeListener) listenerObj).documentChanged(docChange);
		}
		for (Object listenerObj : eventListener.getListeners()) {
			((EclipseEventListener)listenerObj).documentChanged(docChange.getTimestamp());
		}
	}
	public void fireCommandExecutedEvent(EHICommand command) {
		for (Object listenerObj : mCommandExecutionListeners.getListeners()) {
			((EHCommandExecutionListener)listenerObj).commandExecuted(command);
		}
		for (Object listenerObj : eventListener.getListeners()) {
			((EclipseEventListener)listenerObj).commandExecuted(command.getTimestamp());
		}
	}
	
	public void notifyRecordingStarted(long aStartTimestamp) {
		for (Object listenerObj : recorderListener.getListeners()) {
			((RecorderListener)listenerObj).eventRecordingStarted(aStartTimestamp);
		}
	}
	public void notifyRecordingEnded() {
		for (Object listenerObj : recorderListener.getListeners()) {
			((RecorderListener)listenerObj).eventRecordingEnded();
		}
		for (Object listenerObj : eventListener.getListeners()) {
			((EclipseEventListener)listenerObj).eventRecordingEnded();
		}
	}
	
	

	public synchronized void fireDocumentChangeFinalizedEvent(EHBaseDocumentChangeEvent docChange) {
		if (docChange instanceof EHFileOpenCommand) {
			return;
		}
		

		if (docChange == mLastFiredDocumentChange) {
			return;
		}

		for (Object listenerObj : mDocumentChangeListeners.getListeners()) {
			// System.out.println ("ASYNC EXEC ProCESSED");

			((EHDocumentChangeListener) listenerObj).documentChangeFinalized(docChange);
		}
		
		for (Object listenerObj : eventListener.getListeners()) {
			((EclipseEventListener)listenerObj).documentChangeFinalized(docChange.getTimestamp());
		}

		mLastFiredDocumentChange = docChange;
	}

	public void addListeners() {
		addListeners(EHUtilities.getActiveEditor());
	}

	public void addListeners(IEditorPart editor) {
		mEditor = editor;
		final StyledText styledText = EHUtilities.getStyledText(mEditor);
		final ISourceViewer viewer = EHUtilities.getSourceViewer(mEditor);

		if (styledText == null || viewer == null)
			return;

		EHStyledTextEventRecorder.getInstance().addListeners(editor);

		EHDocumentRecorder.getInstance().addListeners(editor);

		EHExecutionRecorder.getInstance().addListeners(editor);

		EHCompletionRecorder.getInstance().addListeners(editor);

		registerFindAction();

		styledText.getDisplay().asyncExec(new Runnable() {
			public void run() {
				mLastCaretOffset = styledText.getCaretOffset();

				mLastSelectionStart = styledText.getSelection().x;
				mLastSelectionEnd = styledText.getSelection().y;
				if (mLastSelectionStart != mLastSelectionEnd) {
					recordCommand(new EHSelectTextCommand(mLastSelectionStart, mLastSelectionEnd, mLastCaretOffset));
				} else {
					recordCommand(new EHMoveCaretCommand(mLastCaretOffset, viewer.getSelectedRange().x));
				}
			}
		});
	}

	public void removeListeners() {
		if (mEditor == null) {
			return;
		}

		try {
			EHStyledTextEventRecorder.getInstance().removeListeners(mEditor);

			EHDocumentRecorder.getInstance().removeListeners(mEditor);

			EHExecutionRecorder.getInstance().removeListeners(mEditor);

			EHCompletionRecorder.getInstance().removeListeners(mEditor);
			// TODO add any listeners I have not created here

			unregisterFindAction();
		} catch (Exception e) {
			// catch all exceptions since we don't want anything bad that
			// happens to prevent other cleanup
			e.printStackTrace();
		}

		mEditor = null;
	}

	private void registerFindAction() {
		AbstractTextEditor ate = findTextEditor(getEditor());
		if (ate != null) {
			mSavedFindAction = ate.getAction(ITextEditorActionConstants.FIND);
			IAction findAction = new FindAction();
			findAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_FIND_AND_REPLACE);
			ate.setAction(ITextEditorActionConstants.FIND, findAction);
		}

	}

	private void unregisterFindAction() {
		AbstractTextEditor ate = findTextEditor(getEditor());
		if (ate != null) {
			ate.setAction(ITextEditorActionConstants.FIND, mSavedFindAction);
		}
	}

	public static AbstractTextEditor findTextEditor(IEditorPart editor) {
		if (editor instanceof AbstractTextEditor)
			return (AbstractTextEditor) editor;

		if (editor instanceof MultiPageEditorPart) {
			MultiPageEditorPart mpe = (MultiPageEditorPart) editor;
			IEditorPart[] parts = mpe.findEditors(editor.getEditorInput());
			for (IEditorPart editorPart : parts) {
				if (editorPart instanceof AbstractTextEditor) {
					return (AbstractTextEditor) editorPart;
				}
			}
		}

		return null;
	}

	public void scheduleTask(Runnable runnable) {
		if (mStarted) {
			runnable.run();
		} else {
			mScheduledTasks.add(runnable);
		}
	}

	// protected void maybeCreateDifficultyPredictionThread() {
	// if (predictorThreadOption == PredictorThreadOption.SINGLE_THREAD &&
	// pendingPredictionCommands == null) {
	// // create the difficulty prediction thread
	// difficultyPredictionRunnable = new ADifficultyPredictionRunnable();
	// pendingPredictionCommands =
	// difficultyPredictionRunnable.getPendingCommands();
	// difficultyPredictionThread = new Thread(difficultyPredictionRunnable);
	// difficultyPredictionThread.setName(DifficultyPredictionRunnable.DIFFICULTY_PREDICTION_THREAD_NAME);
	// difficultyPredictionThread.setPriority(Math.min(
	// Thread.currentThread().getPriority(),
	// DifficultyPredictionRunnable.DIFFICULTY_PREDICTION_THREAD_PRIORITY));
	// difficultyPredictionThread.start();
	// PluginThreadCreated.newCase(difficultyPredictionThread.getName(), this);
	// }
	// }

	public void initCommands() {
		setPlugInMode(false);
		MacroRecordingStarted.newCase(this);
		allDocAndNonDocCommands = new LinkedList<EHICommand>();
		mNormalCommands = new LinkedList<EHICommand>();
		mDocumentChangeCommands = new LinkedList<EHICommand>();
		mCurrentlyExecutingCommand = false;
		System.out.println(" Recording started");
		mRecordCommands = true;
		mStartTimestamp = Calendar.getInstance().getTime().getTime();
		ADifficultyPredictionPluginEventProcessor.getInstance().commandProcessingStarted();

		// later commands
		initializeLogger();

		// Set the combine time threshold.
		// IPreferenceStore prefStore = edu.cmu.scs.fluorite.plugin.Activator
		// .getDefault().getPreferenceStore();
		//
		// setCombineCommands(prefStore
		// .getBoolean(Initializer.Pref_CombineCommands));
		// setCombineTimeThreshold(prefStore
		// .getInt(Initializer.Pref_CombineTimeThreshold));

		mStarted = true;
		notifyRecordingStarted(mStartTimestamp);

	}

	protected boolean plugInMode = false;

	public boolean isPlugInMode() {
		return plugInMode;
	}

	public void setPlugInMode(boolean plugInMode) {
		this.plugInMode = plugInMode;
	}

	public void start() {
		initCommands();
		setPlugInMode(true);
		// FactoriesSelector.configureFactories();
		// MacroRecordingStarted.newCase(this);
		// EventLoggerConsole.getConsole().writeln("***Started macro recording",
		// EventLoggerConsole.Type_RecordingCommand);
		// mCommands = new LinkedList<ICommand>();
		// mNormalCommands = new LinkedList<ICommand>();
		// mDocumentChangeCommands = new LinkedList<ICommand>();
		// mCurrentlyExecutingCommand = false;
		// System.out.println (" Recording started");
		// mRecordCommands = true;
		// mStartTimestamp = Calendar.getInstance().getTime().getTime();
		ADifficultyPredictionPluginEventProcessor.getInstance().commandProcessingStarted();
		// maybeCreateDifficultyPredictionThread();

		// have to create the tray icon on the UI thread
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				createTrayIcon();
			}
		});

		for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
			IPartService service = window.getPartService();
			if (service != null) {
				service.addPartListener(EHPartRecorder.getInstance());
				PartListenerAdded.newCase(service, EHPartRecorder.getInstance(), this);

				if (service.getActivePart() instanceof IEditorPart) {
					EHPartRecorder.getInstance().partActivated(service.getActivePart());
				}
			}
		}

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().addShellListener(EHShellRecorder.getInstance());

		DebugPlugin.getDefault().addDebugEventListener(EHDebugEventSetRecorder.getInstance());

		// listen for exceptions
		ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(EHConsoleRecorder.getInstance());

		// listen for adding breakpoints and removing breakpoints
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(EHBreakPointRecorder.getInstance());

		// try {
		// VariablesPlugin plugIn = VariablesPlugin.getDefault();
		// IStringVariableManager manager = plugIn.getStringVariableManager();
		// manager.addValueVariableListener(VariableValueRecorder.getInstance());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// initializeLogger();
		//
		// // Set the combine time threshold.
		IPreferenceStore prefStore = EHActivator.getDefault().getPreferenceStore();

		setCombineCommands(prefStore.getBoolean(Initializer.Pref_CombineCommands));
		setCombineTimeThreshold(prefStore.getInt(Initializer.Pref_CombineTimeThreshold));

		// mStarted = true;

		// Execute all the scheduled tasks.
		for (Runnable runnable : mScheduledTasks) {
			runnable.run();
		}
		

	}

	private final static String ICON_PATH = "icons/spy.png";

	@SuppressWarnings("deprecation")
	public static void createTrayIcon() {
		Tray tray = PlatformUI.getWorkbench().getDisplay().getSystemTray();

		// check is for systems that don't support Tray
		if (tray != null) {
			try {
//				URL url = new URL(fluorite.plugin.Activator.getDefault().getDescriptor().getInstallURL(),
//						ICON_PATH);
				URL url = new URL(HermesActivator.getInstallURL(),
						ICON_PATH);
				ImageDescriptor imageDescriptior = ImageDescriptor.createFromURL(url);
				Image image = imageDescriptior.createImage();
				trayItem = new TrayItem(tray, SWT.NONE);
				trayItem.setImage(image);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	public static TrayItem getTrayItem() {
		return trayItem;
	}

	public void stop() {
		PluginStopped.newCase(this);
		if (mStarted == false) {
			return;
		}

		updateIncrementalFindMode();

		// Flush the commands that are not yet logged into the file.
		PendingCommandsLogBegin.newCase(allDocAndNonDocCommands, this);
		for (EHICommand command : allDocAndNonDocCommands) {
			LOGGER.log(Level.FINE, null, command);
		}
		PendingCommandsLogEnd.newCase(allDocAndNonDocCommands, this);

		try {
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				if (window != null) {
					IPartService partService = window.getPartService();
					if (partService != null) {
						partService.removePartListener(EHPartRecorder.getInstance());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// try {
		// DebugPlugin.getDefault().removeDebugEventListener(
		// DebugEventSetRecorder.getInstance());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		try {
			VariablesPlugin.getDefault().getStringVariableManager()
					.removeValueVariableListener(EHVariableValueRecorder.getInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// purge timer events.
		getTimer().cancel();
		getTimer().purge();
		ADifficultyPredictionPluginEventProcessor.getInstance().commandProcessingStopped();
		// pendingPredictionCommands.add(new AnEndOfQueueCommand());
		notifyRecordingEnded();
	}

	private void initializeLogger() {
		LOGGER.setLevel(Level.FINE);

		File outputFile = null;
		try {
			File logLocation = getLogLocation();
			outputFile = new File(logLocation, EHEventRecorder.getUniqueMacroNameByTimestamp(getStartTimestamp(), false));
			LogFileCreated.newCase(outputFile.getName(), this);

			FileHandler handler = new FileHandler(outputFile.getPath());
			handler.setEncoding("UTF-8");
			handler.setFormatter(new EHXMLFormatter(getStartTimestamp()));

			LOGGER.addHandler(handler);
			LogHandlerBound.newCase(handler, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EHEvents getRecordedEventsSoFar() {
		return getRecordedEvents(allDocAndNonDocCommands);
	}

	public EHEvents getRecordedEvents(List<EHICommand> commands) {
		return new EHEvents(commands, "", Long.toString(getStartTimestamp()), "", getStartTimestamp());
	}

	private File getLogLocation() throws Exception {
		try {
			File logLocation = HermesActivator.getDefault().getStateLocation().append("Logs")
					.toFile();
			if (!logLocation.exists()) {
				if (!logLocation.mkdirs()) {
					throw new Exception("Could not make log directory!");
				}
			}
			return logLocation;
		} catch (Exception e) {
			return new File("Logs");
		}
	}

	private boolean mIncrementalFindMode = false;
	private boolean mIncrementalFindForward = true;
	private Listener mIncrementalListener = null;

	public IEditorPart getEditor() {
		return mEditor;
	}

	public void updateIncrementalFindMode() {
		if (!mIncrementalFindMode)
			return;

		StyledText st = EHUtilities.getStyledText(EHUtilities.getActiveEditor());
		Listener[] currentListeners = st.getListeners(SWT.MouseUp);
		boolean stillInList = false;
		for (Listener listener : currentListeners) {
			if (listener == mIncrementalListener) {
				stillInList = true;
				break;
			}
		}

		if (!stillInList) {
			mIncrementalFindMode = false;

			// add find command representing whatever is currently selected
			String selectionText = st.getSelectionText();
			EHFindCommand findCommand = new EHFindCommand(selectionText);
			findCommand.setSearchForward(mIncrementalFindForward);
			recordCommand(findCommand);
			// System.out.println("Incremental find string: " + selectionText);
		}
	}

	// @Override
	// public void modifyText(ExtendedModifyEvent event)
	// {
	// // if (!mCurrentlyExecutingCommand)
	// // {
	// // System.out.println(event);
	// // }
	// // //the text modify event is used to handle character insert/delete
	// events
	// // if (event.replacedText.length()>0)
	// // {
	// // mCommands.add(new StyledTextCommand(ST.DELETE_NEXT));
	// // }
	// //
	// // if ()
	// }

	public void endIncrementalFindMode() {

	}

	public void pauseRecording() {
		mRecordCommands = false;
	}

	public void resumeRecording() {
		mRecordCommands = true;
	}

	boolean isPredictionRelatedCommand(final EHICommand newCommand) {
		return newCommand instanceof PredictionCommand || newCommand instanceof DifficulyStatusCommand;
	}
	
	protected void notifyCommandAndDocChangeListeners(EHICommand newCommand, 
			EHICommand lastCommand) {
		if (newCommand instanceof EHBaseDocumentChangeEvent) {
			if (!(newCommand instanceof EHFileOpenCommand)) {
				fireDocumentChangedEvent((EHBaseDocumentChangeEvent)newCommand);
				DocumentChangeCommandNotified.newCase((EHBaseDocumentChangeEvent)newCommand, mStartTimestamp, this);
			}
			
			if (lastCommand instanceof EHBaseDocumentChangeEvent && lastCommand != mLastFiredDocumentChange) {
				fireDocumentChangeFinalizedEvent((EHBaseDocumentChangeEvent)lastCommand);
				DocumentChangeFinalizedEventNotified.newCase(
						(EHBaseDocumentChangeEvent)lastCommand, mStartTimestamp, this);
			}
		}
		else {
			fireCommandExecutedEvent(newCommand);
			NonDocumentChangeCommandNotified.newCase(newCommand, mStartTimestamp, this);
		}
	}
	

	public void recordCommand(final EHICommand newCommand) {
//		System.out.println("Recording command:" + newCommand);
		ReceivedCommand.newCase(newCommand, mStartTimestamp, this);
		

		if (!mRecordCommands) {
//			System.out.println("Ignoring command:" + newCommand);
			IgnoredCommandAsRecordingSuspended.newCase(newCommand, mStartTimestamp, this);

			return;
		}
		// long values cannot be null, but they are initialized by default to 0L
//		if (newCommand.getTimestamp() == 0L) {
//			long timestamp = Calendar.getInstance().getTime().getTime();
//			timestamp -= mStartTimestamp;
//			NewMacroCommand.newCase(newCommand.getName(), timestamp, this);
//			newCommand.setTimestamp(timestamp);
//			newCommand.setTimestamp2(timestamp);
//		} else {
//				NewMacroCommand.newCase(newCommand.getName(), newCommand.getTimestamp(), this);
//				newCommand.setTimestamp(newCommand.getTimestamp() - mStartTimestamp);
//				newCommand.setTimestamp2(newCommand.getTimestamp() - mStartTimestamp);
//		}
		long timestamp = Calendar.getInstance().getTime().getTime();
		timestamp -= mStartTimestamp;
		NewMacroCommand.newCase(newCommand.getName(), mStartTimestamp, this);
		newCommand.setTimestamp(timestamp);
		newCommand.setTimestamp2(timestamp);
		// NewMacroCommand.newCase(newCommand.getName(),
		// newCommand.getTimestamp(), this);
		// newCommand.setTimestamp(newCommand.getTimestamp() - mStartTimestamp);
		// newCommand.setTimestamp2(newCommand.getTimestamp() -
		// mStartTimestamp);
		// EventLoggerConsole.getConsole().writeln(
		// "*Command added to macro: " + newCommand.getName()
		// + "\ttimestamp: " + timestamp,
		// EventLoggerConsole.Type_RecordingCommand);

		final boolean isDocChange = (newCommand instanceof EHBaseDocumentChangeEvent);		
		final LinkedList<EHICommand> docOrNormalCommands = isDocChange ? 
				mDocumentChangeCommands : 
					mNormalCommands;
		if (isDocChange) {
			DocumentChangeCommandExecuted.newCase((EHBaseDocumentChangeEvent) newCommand, mStartTimestamp, this);
		} else {
			NonDocumentChangeCommandExecuted.newCase(newCommand, mStartTimestamp, this);
		}
//		System.out.println(" isDocChange" + isDocChange + " commandslist:" + docOrNormalCommands);

		boolean combined = false;
		final EHICommand lastCommand = docOrNormalCommands.size() > 0 ?
				docOrNormalCommands.get(docOrNormalCommands.size() - 1) : 
				null;

		// See if combining with previous command is possible .
		if (!isPredictionRelatedCommand(newCommand) && 
				lastCommand != null
				&& isCombineEnabled(newCommand, lastCommand, isDocChange)) {
			combined = lastCommand.combineWith(newCommand);
		}
//		System.out
//				.println("Combining command:" + combined + " newCommand" + newCommand + " lastCommand " + lastCommand);
		if (combined) {
			CombinedCommand.newCase(newCommand.toString(), lastCommand.toString(), this);
		}
		// If combining has failed, just add it.
		if (!combined) {
//			System.out.println("Adding command:" + newCommand + " to both commands and mCommands");
			// we do not add combined command to buffers as repeat count of lastcommand takes care of this info
			// A timer determines if the combine is actually done or not, as it changes instance variables
			docOrNormalCommands.add(newCommand);
			allDocAndNonDocCommands.add(newCommand);
			AddedCommandToBuffers.newCase(newCommand, docOrNormalCommands.toString(), allDocAndNonDocCommands.toString(),  this);
			notifyCommandAndDocChangeListeners(newCommand, lastCommand);
			/*
			 * The code below is old fluorite
			 */
//			if (newCommand instanceof EHBaseDocumentChangeEvent && !(newCommand instanceof EHFileOpenCommand)) {
//				fireDocumentChangedEvent((EHBaseDocumentChangeEvent) newCommand);
//
//				if (lastCommand instanceof EHBaseDocumentChangeEvent && lastCommand != mLastFiredDocumentChange) {
//					fireDocumentChangeFinalizedEvent((EHBaseDocumentChangeEvent) lastCommand);
//				}
//			}

		}

//		if (mCommands.getFirst() != commands.getFirst()) {
//			System.err.println("Commands and mcommands have diverged because we chnaged command type");
//		}

		// moving to where the command i logged so that one can get the combined
		// event
		// ADifficultyPredictionPluginEventProcessor.getInstance().newCommand(newCommand);
		PendingCommandsLogBegin.newCase(docOrNormalCommands, this);
		// Log to the file.
		/*
		 * Do not empty the current command list so that the last command can be combined with future commands
		 * Do not empty list if current command type is the same  asprevious command type except the first time we run it
		 * Rationale is that document change commands (e.g. insert) should trigger normal command (e.g move caret) logging
		 * Vice versa also, combine a bunch of inserts until a move caret is detected

		 * It seems first commands are doc change
		 */
		while (docOrNormalCommands.size() > 1 && 
				docOrNormalCommands.getFirst() == allDocAndNonDocCommands.getFirst()) {
			final EHICommand firstCmd = docOrNormalCommands.getFirst();
			CommandLoggingInitiated.newCase(firstCmd,mStartTimestamp, this);
			LOGGER.log(Level.FINE, null, firstCmd);
			// System.out.println ("LOGGING COMMAND:" + firstCmd + " THIS is
			// what should be sent to prediction, not individual commands");

			// Remove the first item from the list
			docOrNormalCommands.removeFirst();
			allDocAndNonDocCommands.removeFirst();
			RemovedCommandFromBuffers.newCase(firstCmd.toString(), docOrNormalCommands.toString(), allDocAndNonDocCommands.toString(), this);
//			System.out.println("Giving command to pluginevent processor" + firstCmd);
			ForwardedCommandToPredictor.newCase(firstCmd, mStartTimestamp, this);

			ADifficultyPredictionPluginEventProcessor.getInstance().newCommand(firstCmd);

		}
		PendingCommandsLogEnd.newCase(docOrNormalCommands, this);
		RecordedCommandsCleared.newCase(docOrNormalCommands, this);

		if (!isAsyncFireEvent())
			return;
		// perhaps this is screwing performance

		// WHY do we need all of the stuff below
		if (isPlugInMode()) {
			StyledText styledText = EHUtilities.getStyledText(EHUtilities.getActiveEditor());
			if (styledText != null) {
				this.mLastCaretOffset = styledText.getCaretOffset();
				this.mLastSelectionStart = styledText.getSelection().x;
				this.mLastSelectionEnd = styledText.getSelection().y;
			}
		}

		// Deal with timer.
		// TODO Refactor!! maybe use State pattern or something, using inner
		// classes.
		if (isDocChange) {
			if (mDocChangeTimerTask != null) {
				mDocChangeTimerTask.cancel();
			}

			mDocChangeTimerTask = new TimerTask() {
				public void run() {
					// System.out.println("NEW THREAD! THIS MAY BE THE ISSUE
					// WITH PERFOMANCE");
					mDocChangeCombinable = false;
					// System.out.println("COMBINABLE: FALSE");

					try {

						final EHICommand lastCommand = (mDocumentChangeCommands.size() > 0)
								? mDocumentChangeCommands.get(mDocumentChangeCommands.size() - 1) : null;
						if (lastCommand != null && lastCommand != mLastFiredDocumentChange) {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									fireDocumentChangeFinalizedEvent((EHBaseDocumentChangeEvent) lastCommand);
								}
							});
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			getTimer().schedule(mDocChangeTimerTask, (long) getCombineTimeThreshold());
			mDocChangeCombinable = true;
			// System.out.println(" EVENT Recorder Combinable ");
		} else {
			if (mNormalTimerTask != null) {
				mNormalTimerTask.cancel();
			}

			mNormalTimerTask = new TimerTask() {
				public void run() {
					mNormalCommandCombinable = false;
				}
			};
			getTimer().schedule(mNormalTimerTask, (long) getCombineTimeThreshold());
			mNormalCommandCombinable = true;
		}
	}

	// public void changeStatusInHelpView(PredictionCommand predictionCommand) {
	// String status = "";
	// switch (predictionCommand.getPredictionType()) {
	// case MakingProgress:
	// status = StatusConsts.MAKING_PROGRESS_STATUS;
	// break;
	// case HavingDifficulty:
	// status = StatusConsts.SLOW_PROGRESS_STATUS;
	// break;
	// case Indeterminate:
	// status = StatusConsts.INDETERMINATE;
	// break;
	// }
	//
	// showStatusInBallonTip(status);
	// HelpViewPart.displayStatusInformation(status);
	// }

	// private void showStatusInBallonTip(String status) {
	// if (balloonTip == null) {
	// balloonTip = new ToolTip(PlatformUI.getWorkbench()
	// .getActiveWorkbenchWindow().getShell(), SWT.BALLOON
	// | SWT.ICON_INFORMATION);
	//
	// }
	//
	// if (!balloonTip.isDisposed()) {
	// balloonTip.setMessage("Status: " + status);
	// balloonTip.setText("Status Change Notification");
	// trayItem.setToolTip(balloonTip);
	// balloonTip.setVisible(true);
	// }
	//
	// }

	private boolean isCombineEnabled(EHICommand newCommand, EHICommand lastCommand, boolean isDocChange) {
		return getCombineCommands() && (isDocChange ? mDocChangeCombinable : mNormalCommandCombinable);
	}

	public long getStartTimestamp() {
		return mStartTimestamp;
	}

	// in replay mode
	public void setStartTimeStamp(long newVal) {
		mStartTimestamp = newVal;
	}

	public static String getUniqueMacroNameByTimestamp(long timestamp, boolean autosave) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
		return "Log" + format.format(new Date(timestamp)) + (autosave ? "-Autosave" : "") + ".xml";
	}

	public static Document createDocument(EHEvents events) {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			// create the root element and add it to the document
			Element root = doc.createElement(XML_Macro_Tag);
			doc.appendChild(root);
			events.persist(doc, root);

			return doc;

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		return null;
	}

	public static String outputXML(Document doc) {
		try {
			// set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			// create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			String xmlString = sw.toString();
			return xmlString;
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		return null;
	}

	public static String persistMacro(EHEvents macro) {
		Document doc = createDocument(macro);
		return outputXML(doc);
	}

	public static boolean isAsyncFireEvent() {
		return asyncFireEvent;
	}

	public static void setAsyncFireEvent(boolean asyncFireEvent) {
		EHEventRecorder.asyncFireEvent = asyncFireEvent;
	}
}
