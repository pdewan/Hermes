package fluorite.recorders;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import fluorite.commands.EHContentAssistProposalsCommand;
import fluorite.commands.EHCopyCommand;
import fluorite.commands.EHCutCommand;
import fluorite.commands.EHDeleteResourceCommand;
import fluorite.commands.EHEclipseCommand;
import fluorite.commands.EHFileRefreshCommand;
import fluorite.commands.EHFileSaveCommand;
import fluorite.commands.EHFileSearchCommand;
import fluorite.commands.EHFindDefinitionCommand;
import fluorite.commands.EHFindReferencesCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.EHImportProjectCommand;
import fluorite.commands.EHMoveElementCommand;
import fluorite.commands.EHPasteCommand;
import fluorite.commands.EHPropertyDialogClosedCommand;
import fluorite.commands.EHRedoCommand;
import fluorite.commands.EHRenameCommand;
import fluorite.commands.EHSelectLineCommand;
import fluorite.commands.EHStepIntoCommand;
import fluorite.commands.EHStepReturnCommand;
import fluorite.commands.EHTeamPullCommand;
import fluorite.commands.EHToggleBreakpointCommand;
import fluorite.commands.EHToggleCommentCommand;
import fluorite.commands.EHTypeHierarchyCommand;
import fluorite.commands.EHUndoCommand;
import fluorite.util.EventLoggerConsole;
import fluorite.util.EHUtilities;
import util.trace.recorder.ExcludedCommand;

public class EHExecutionRecorder extends EHBaseRecorder implements
		IExecutionListener {

	private static EHExecutionRecorder instance = null;

	public static EHExecutionRecorder getInstance() {
		if (instance == null) {
			instance = new EHExecutionRecorder();
		}

		return instance;
	}

	private EHExecutionRecorder() {
		super();

		// TODO: get from extension point?
		mNonRecordableCommandIds = new HashSet<String>();
		mNonRecordableCommandIds
				.add(IWorkbenchCommandConstants.EDIT_FIND_AND_REPLACE);
		mNonRecordableCommandIds.add("eventlogger.actions.viewLastLog");
		mNonRecordableCommandIds.add("eventlogger.actions.annotateBackward");
		mNonRecordableCommandIds.add("eventlogger.actions.annotateForward");
	}

	@Override
	public void addListeners(IEditorPart editor) {
		ICommandService cs = (ICommandService) PlatformUI.getWorkbench()
				.getAdapter(ICommandService.class);
		if (cs != null) {
			cs.addExecutionListener(this);
		}
	}

	@Override
	public void removeListeners(IEditorPart editor) {
		try {
			ICommandService cs = (ICommandService) PlatformUI.getWorkbench()
					.getAdapter(ICommandService.class);
			if (cs != null) {
				cs.removeExecutionListener(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Listener[] mPreexecuteListeners = null;
	private Set<String> mNonRecordableCommandIds;

	public void notHandled(String commandId, NotHandledException exception) {
		getRecorder().setCurrentlyExecutingCommand(false);
		getRecorder().endIncrementalFindMode();
		System.out.println("not handled: " + commandId);
	}

	public void postExecuteFailure(String commandId,
			ExecutionException exception) {
		getRecorder().setCurrentlyExecutingCommand(false);
		getRecorder().endIncrementalFindMode();
		System.out.println("command failed: " + commandId);

	}

	public void postExecuteSuccess(String commandId, Object returnValue) {
		getRecorder().setCurrentlyExecutingCommand(false);
		getRecorder().endIncrementalFindMode();
		if (mNonRecordableCommandIds.contains(commandId)) {
			// we *always" see the record command first, so don't log it
			if (!commandId.equals("eventlogger.actions.recordMacro")
					&& !commandId
							.equals(IWorkbenchCommandConstants.EDIT_FIND_AND_REPLACE)) {
				ExcludedCommand.newCase(commandId, this);
				EventLoggerConsole.getConsole().writeln(
						"Not recording command (it's in the exclude list): "
								+ commandId, EventLoggerConsole.Type_Standard);
			}
			return;
		}

		if (commandId.equals(ITextEditorActionDefinitionIds.FIND_INCREMENTAL)
				|| commandId
						.equals(ITextEditorActionDefinitionIds.FIND_INCREMENTAL_REVERSE)) {
			if (commandId
					.equals(ITextEditorActionDefinitionIds.FIND_INCREMENTAL))
				getRecorder().setIncrementalFindForward(true);
			else
				getRecorder().setIncrementalFindForward(false);
			Listener[] currentListeners = EHUtilities.getStyledText(
					EHUtilities.getActiveEditor()).getListeners(SWT.MouseUp);
			getRecorder().setIncrementalListener(null);
			for (Listener listener : currentListeners) {
				boolean inCurrentList = false;
				for (Listener listenerBeforeFind : mPreexecuteListeners) {
					if (listenerBeforeFind == listener)
						inCurrentList = true;
					break;
				}
				if (!inCurrentList) {
					getRecorder().setIncrementalListener(listener);
				}
			}
		}

		getRecorder().updateIncrementalFindMode();
		if (!getRecorder().isIncrementalFindMode()) {
			// System.out.println("Command executed: " + commandId);

			getRecorder().recordCommand(
					createCommandByEclipseCommandId(commandId));
		}
	}

	public void preExecute(String commandId, ExecutionEvent event) {
		getRecorder().setCurrentlyExecutingCommand(true);
		if (commandId.equals(ITextEditorActionDefinitionIds.FIND_INCREMENTAL)
				|| commandId
						.equals(ITextEditorActionDefinitionIds.FIND_INCREMENTAL_REVERSE)) {
			getRecorder().setIncrementalFindMode(true);
			StyledText widget = EHUtilities.getStyledText(EHUtilities
					.getActiveEditor());
			if (widget != null) {
				mPreexecuteListeners = widget.getListeners(SWT.MouseUp);
			}
		}
		// System.out.println("preexecute: " + commandId);
	}

	private EHICommand createCommandByEclipseCommandId(String commandId) {
		if (commandId.equals("org.eclipse.ui.edit.undo")) {
			return new EHUndoCommand();
		} else if (commandId.equals("org.eclipse.ui.edit.redo")) {
			return new EHRedoCommand();
		} else if (commandId.equals("org.eclipse.ui.edit.copy")) {
			return new EHCopyCommand();
		} else if (commandId.equals("org.eclipse.ui.edit.cut")) {
			return new EHCutCommand();
		} else if (commandId.equals("org.eclipse.ui.edit.paste")) {
			return new EHPasteCommand();
		} else if (commandId.equals("org.eclipse.jdt.ui.edit.text.java.rename.element")){
			return new EHRenameCommand(commandId);
//			org.eclipse.jdt.ui.edit.text.java.move.element	
		} else if (commandId.equals("org.eclipse.jdt.ui.edit.text.java.move.element")) {
			return new EHMoveElementCommand(commandId);
//			org.eclipse.ui.edit.text.contentAssist.proposals
		} else if (commandId.equals("org.eclipse.ui.edit.text.contentAssist.proposals")) {
			return new EHContentAssistProposalsCommand(commandId);			
//			AUTOGEN:::org.eclipse.jdt.debug.CompilationUnitEditor.BreakpointRulerActions/org.eclipse.jdt.debug.ui.actions.ManageBreakpointRulerAction
		} else if (commandId.equals("AUTOGEN:::org.eclipse.jdt.debug.CompilationUnitEditor.BreakpointRulerActions/org.eclipse.jdt.debug.ui.actions.ManageBreakpointRulerAction")) {
			return new EHToggleBreakpointCommand(commandId);
//			AUTOGEN:::org.eclipse.jdt.internal.ui.CompilationUnitEditor.ruler.actions/org.eclipse.jdt.internal.ui.javaeditor.JavaSelectRulerAction
		} else if (commandId.equals("AUTOGEN:::org.eclipse.jdt.internal.ui.CompilationUnitEditor.ruler.actions/org.eclipse.jdt.internal.ui.javaeditor.JavaSelectRulerAction")) {
			return new EHSelectLineCommand(commandId);//			AUTOGEN:::org.eclipse.jdt.internal.ui.CompilationUnitEditor.ruler.actions/org.eclipse.jdt.internal.ui.javaeditor.JavaSelectRulerAction
//			org.eclipse.debug.ui.commands.StepReturn
		} else if (commandId.equals("org.eclipse.debug.ui.commands.StepReturn")) {
			return new EHStepReturnCommand(commandId);//			org.eclipse.debug.ui.commands.StepOver
//			org.eclipse.debug.ui.commands.StepInto
		} else if (commandId.equals("org.eclipse.debug.ui.commands.StepInto")) {
			return new EHStepIntoCommand(commandId);//			org.eclipse.debug.ui.commands.Resume
//			org.eclipse.ui.file.refresh
		} else if (commandId.equals("org.eclipse.ui.file.refresh")) {
			return new EHFileRefreshCommand(commandId);//			org.eclipse.ui.file.properties
//			org.eclipse.search.ui.openFileSearchPage
		} else if (commandId.equals("org.eclipse.search.ui.openFileSearchPage")) {
			return new EHFileSearchCommand(commandId);
//			org.eclipse.jdt.ui.edit.text.java.open.editor (F3)
		} else if (commandId.equals("org.eclipse.jdt.ui.edit.text.java.open.editor")) {
			return new EHFindDefinitionCommand(commandId);			
//			org.eclipse.jdt.ui.edit.text.java.search.references.in.workspace (CTRL SHIFT G)
		} else if (commandId.equals("org.eclipse.jdt.ui.edit.text.java.search.references.in.workspace")) {
			return new EHFindReferencesCommand(commandId);//			org.eclipse.jdt.ui.edit.text.java.open.type.hierarchy
//			org.eclipse.jdt.ui.edit.text.java.open.type.hierarchy
		} else if (commandId.equals("org.eclipse.jdt.ui.edit.text.java.open.type.hierarchy")) {
			return new EHTypeHierarchyCommand(commandId);
//			org.eclipse.ui.file.import
		} else if (commandId.equals("org.eclipse.ui.file.import")) {
			return new EHImportProjectCommand(commandId);
//			org.eclipse.egit.ui.team.Pull
		} else if (commandId.equals("org.eclipse.egit.ui.team.Pull")) {
			return new EHTeamPullCommand(commandId);
//			org.eclipse.ltk.ui.refactoring.commands.deleteResources
		} else if (commandId.equals("org.eclipse.ltk.ui.refactoring.commands.deleteResources")) {
			return new EHDeleteResourceCommand(commandId);
//			org.eclipse.ui.file.save
		} else if (commandId.equals("org.eclipse.ui.file.save")) {
//			return new EHDeleteResourceCommand(commandId);
			return new EHFileSaveCommand(commandId);
//			org.eclipse.jdt.ui.edit.text.java.toggle.comment
		} else if (commandId.equals("org.eclipse.jdt.ui.edit.text.java.toggle.comment")) {
			return new EHToggleCommentCommand(commandId);
		} else if (commandId.equals("org.eclipse.ui.project.properties")) {
			return new EHPropertyDialogClosedCommand(commandId);
//org.eclipse.ui.project.properties		
		} else {
			return new EHEclipseCommand(commandId);
		}
	}
}
