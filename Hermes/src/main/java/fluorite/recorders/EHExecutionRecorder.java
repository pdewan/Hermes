package fluorite.recorders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.jdt.core.refactoring.descriptors.JavaRefactoringDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.RenameJavaElementDescriptor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameFieldProcessor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptorProxy;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.history.IRefactoringExecutionListener;
import org.eclipse.ltk.core.refactoring.history.IRefactoringHistoryService;
import org.eclipse.ltk.core.refactoring.history.RefactoringExecutionEvent;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import fluorite.commands.ContentAssistProposalsCommand;
import fluorite.commands.CopyCommand;
import fluorite.commands.CutCommand;
import fluorite.commands.DeleteResourceCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.EclipseCommand;
import fluorite.commands.FileRefreshCommand;
import fluorite.commands.FileSaveCommand;
import fluorite.commands.FileSearchCommand;
import fluorite.commands.FindDefinitionCommand;
import fluorite.commands.FindReferencesCommand;
import fluorite.commands.ImportProjectCommand;
import fluorite.commands.MoveElementCommand;
import fluorite.commands.PasteCommand;
import fluorite.commands.PropertyDialogClosedCommand;
import fluorite.commands.RedoCommand;
import fluorite.commands.RenameCommand;
import fluorite.commands.SelectLineCommand;
import fluorite.commands.StepIntoCommand;
import fluorite.commands.StepReturnCommand;
import fluorite.commands.TeamPullCommand;
import fluorite.commands.ToggleBreakpointCommand;
import fluorite.commands.ToggleCommentCommand;
import fluorite.commands.TypeHierarchyCommand;
import fluorite.commands.UndoCommand;
import fluorite.util.EHUtilities;
import fluorite.util.EventLoggerConsole;
import util.trace.recorder.ExcludedCommand;
public class EHExecutionRecorder extends EHBaseRecorder implements
		IExecutionListener, IRefactoringExecutionListener {

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
		IRefactoringHistoryService aRefactoringHistoryService = RefactoringCore.getHistoryService();
		aRefactoringHistoryService.connect();
		aRefactoringHistoryService.addExecutionListener(this);
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
			return new UndoCommand();
		} else if (commandId.equals("org.eclipse.ui.edit.redo")) {
			return new RedoCommand();
		} else if (commandId.equals("org.eclipse.ui.edit.copy")) {
			return new CopyCommand();
		} else if (commandId.equals("org.eclipse.ui.edit.cut")) {
			return new CutCommand();
		} else if (commandId.equals("org.eclipse.ui.edit.paste")) {
			return new PasteCommand();
		} else if (commandId.equals("org.eclipse.jdt.ui.edit.text.java.rename.element")){
			return new RenameCommand(commandId);
//			org.eclipse.jdt.ui.edit.text.java.move.element	
		} else if (commandId.equals("org.eclipse.jdt.ui.edit.text.java.move.element")) {
			return new MoveElementCommand(commandId);
//			org.eclipse.ui.edit.text.contentAssist.proposals
		} else if (commandId.equals("org.eclipse.ui.edit.text.contentAssist.proposals")) {
			return new ContentAssistProposalsCommand(commandId);			
//			AUTOGEN:::org.eclipse.jdt.debug.CompilationUnitEditor.BreakpointRulerActions/org.eclipse.jdt.debug.ui.actions.ManageBreakpointRulerAction
		} else if (commandId.equals("AUTOGEN:::org.eclipse.jdt.debug.CompilationUnitEditor.BreakpointRulerActions/org.eclipse.jdt.debug.ui.actions.ManageBreakpointRulerAction")) {
// does not give line number so better to use breakpoint command			
			return new ToggleBreakpointCommand(commandId);
//			AUTOGEN:::org.eclipse.jdt.internal.ui.CompilationUnitEditor.ruler.actions/org.eclipse.jdt.internal.ui.javaeditor.JavaSelectRulerAction
		} else if (commandId.equals("AUTOGEN:::org.eclipse.jdt.internal.ui.CompilationUnitEditor.ruler.actions/org.eclipse.jdt.internal.ui.javaeditor.JavaSelectRulerAction")) {
			return new SelectLineCommand(commandId);//			AUTOGEN:::org.eclipse.jdt.internal.ui.CompilationUnitEditor.ruler.actions/org.eclipse.jdt.internal.ui.javaeditor.JavaSelectRulerAction
//			org.eclipse.debug.ui.commands.StepReturn
		} else if (commandId.equals("org.eclipse.debug.ui.commands.StepReturn")) {
			return new StepReturnCommand(commandId);//			org.eclipse.debug.ui.commands.StepOver
//			org.eclipse.debug.ui.commands.StepInto
		} else if (commandId.equals("org.eclipse.debug.ui.commands.StepInto")) {
			return new StepIntoCommand(commandId);//			org.eclipse.debug.ui.commands.Resume
//			org.eclipse.ui.file.refresh
		} else if (commandId.equals("org.eclipse.ui.file.refresh")) {
			return new FileRefreshCommand(commandId);//			org.eclipse.ui.file.properties
//			org.eclipse.search.ui.openFileSearchPage
		} else if (commandId.equals("org.eclipse.search.ui.openFileSearchPage")) {
			return new FileSearchCommand(commandId);
//			org.eclipse.jdt.ui.edit.text.java.open.editor (F3)
		} else if (commandId.equals("org.eclipse.jdt.ui.edit.text.java.open.editor")) {
			return new FindDefinitionCommand(commandId);			
//			org.eclipse.jdt.ui.edit.text.java.search.references.in.workspace (CTRL SHIFT G)
		} else if (commandId.equals("org.eclipse.jdt.ui.edit.text.java.search.references.in.workspace")) {
			return new FindReferencesCommand(commandId);//			org.eclipse.jdt.ui.edit.text.java.open.type.hierarchy
//			org.eclipse.jdt.ui.edit.text.java.open.type.hierarchy
		} else if (commandId.equals("org.eclipse.jdt.ui.edit.text.java.open.type.hierarchy")) {
			return new TypeHierarchyCommand(commandId);
//			org.eclipse.ui.file.import
		} else if (commandId.equals("org.eclipse.ui.file.import")) {
			return new ImportProjectCommand(commandId);
//			org.eclipse.egit.ui.team.Pull
		} else if (commandId.equals("org.eclipse.egit.ui.team.Pull")) {
			return new TeamPullCommand(commandId);
//			org.eclipse.ltk.ui.refactoring.commands.deleteResources
		} else if (commandId.equals("org.eclipse.ltk.ui.refactoring.commands.deleteResources")) {
			return new DeleteResourceCommand(commandId);
//			org.eclipse.ui.file.save
		} else if (commandId.equals("org.eclipse.ui.file.save")) {
//			return new EHDeleteResourceCommand(commandId);
			return new FileSaveCommand(commandId);
//			org.eclipse.jdt.ui.edit.text.java.toggle.comment
		} else if (commandId.equals("org.eclipse.jdt.ui.edit.text.java.toggle.comment")) {
			return new ToggleCommentCommand(commandId);
		} else if (commandId.equals("org.eclipse.ui.project.properties")) {
			return new PropertyDialogClosedCommand(commandId);
//org.eclipse.ui.project.properties		
		} else {
			return new EclipseCommand(commandId);
		}
	}
	
	public static void fillTextEdits(Change aChange, List<TextEdit> aList) {
//		if (aChange instanceof TextChange) {
//			aList.add((TextChange) aChange);
//			return;			
//		}
		if (aChange instanceof CompositeChange) {
			CompositeChange aCompositeChange = (CompositeChange) aChange;
			for (Change aChild: aCompositeChange.getChildren() ) {
				fillTextEdits(aChild, aList);
			}
			return;
		}
		if (aChange instanceof CompilationUnitChange) {
			aList.add(((CompilationUnitChange) aChange).getEdit());
			return;
		}
		if (aChange instanceof TextChange) {
		    TextChange textChange = (TextChange)aChange;
		    aList.add(textChange.getEdit());		    
		}
		
	}

	@Override
	public void executionNotification(RefactoringExecutionEvent event) {
		System.out.println("Refactoring event:" + event + " description" + event.getDescriptor().getDescription());
		RefactoringDescriptorProxy descriptorProxy = event.getDescriptor();
		RefactoringDescriptor descriptor = descriptorProxy.requestDescriptor(new NullProgressMonitor());
		System.out.println("Desciptor:" + descriptor);
		if (descriptor instanceof JavaRefactoringDescriptor) {
			JavaRefactoringDescriptor aJavaRefactoringDescriptor = (JavaRefactoringDescriptor) descriptor;
			if (aJavaRefactoringDescriptor instanceof RenameJavaElementDescriptor ) {
				RenameJavaElementDescriptor aRenameJavaElementDescriptor = (RenameJavaElementDescriptor) aJavaRefactoringDescriptor;
				
			}
		}
		try {
//			Refactoring refactor =	descriptor.createRefactoringContext(new RefactoringStatus()).getRefactoring();
		Refactoring refactor = descriptor.createRefactoring(new RefactoringStatus());
		if (refactor == null) {
			return;
		}
		try {
		refactor.checkInitialConditions(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
		refactor.checkFinalConditions(null);
		} catch (Exception e) {
			e.printStackTrace();
			int i = 5;
		}
		if (refactor instanceof RenameRefactoring ) {
			try {
	    	RenameRefactoring aRenameRefactoring = (RenameRefactoring) refactor;
	    	RenameFieldProcessor aRenameFieldProcessor = (RenameFieldProcessor) aRenameRefactoring.getProcessor();
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}		
//		Change change = refactor.createChange(new NullProgressMonitor());
		Change aChange = refactor.createChange(null);
		List<TextEdit> aList = new ArrayList();
		fillTextEdits(aChange, aList);
		System.out.println("Changes:" + aList);
//		if (change instanceof CompositeChange) {
//			System.out.println("Change:" + (CompositeChange) change);
//			CompositeChange aCompositeChange = (CompositeChange) change;
//			Change[] aChanges = aCompositeChange.getChildren();
//			DynamicValidationRefactoringChange a;
//			for (Change aChange:aChanges) {
////				if (aChange instanceof DynamicValidationRefactoringChange) {
//				if (aChange instanceof CompositeChange) {
//
////					DynamicValidationRefactoringChange aDynamicValidationRefactoringChange = (DynamicValidationRefactoringChange) aChange;
//					for (Change aChange2: ((CompositeChange) aChange).getChildren()) {
//						System.out.println(aChange2);
//						if (aChange2 instanceof CompilationUnitChange) {
//							System.out.println(((CompilationUnitChange) aChange2).getEdit());
//						}
//
//					}
//				
//
//				}
//				System.out.println(aChange);
//			}
//
//		}
//		System.out.println("Change:" + change);
//		Object aChange = change.getModifiedElement();
//		if (change instanceof TextChange) {
//		    TextChange textChange = (TextChange)change;
//		    TextEdit edit = textChange.getEdit();
//		    System.out.println("Edit: " + edit);
//		}
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
	}
}
