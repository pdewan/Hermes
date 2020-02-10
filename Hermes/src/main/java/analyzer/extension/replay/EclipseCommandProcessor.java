package analyzer.extension.replay;

import fluorite.commands.EclipseCommand;
import programmatically.AnEclipseProgrammaticController;

public class EclipseCommandProcessor {
	private static final String ECLIPSE_SAVE = "org.eclipse.ui.file.save";
	private static final String ECLIPSE_SAVE_AS = "org.eclipse.ui.file.saveAs";
	private static final String ECLIPSE_LINE_UP = "eventLogger.styledTextCommand.LINE_UP";
	private static final String ECLIPSE_LINE_DOWN = "eventLogger.styledTextCommand.LINE_DOWN";
	private static final String ECLIPSE_DELETE = "org.eclipse.ui.edit.delete";
	private static final String ECLIPSE_DELETE_PREVIOUS = "eventLogger.styledTextCommand.DELETE_PREVIOUS";
	private static final String ECLIPSE_COLUMN_NEXT = "eventLogger.styledTextCommand.COLUMN_NEXT";
	private static final String ECLIPSE_COLUMN_PREVIOUS = "eventLogger.styledTextCommand.COLUMN_PREVIOUS";
	private static final String ECLIPSE_GOTO_START = "org.eclipse.ui.edit.text.goto.lineStart";
	private static final String ECLIPSE_GOTO_END = "org.eclipse.ui.edit.text.goto.lineEnd";
	private static final String ECLIPSE_SELECT_Start = "org.eclipse.ui.edit.text.select.lineStart";
	private static final String ECLIPSE_SELECT_END = "org.eclipse.ui.edit.text.select.lineEnd";
	private static final String ECLIPSE_SELECT_UP = "eventLogger.styledTextCommand.SELECT_LINE_UP";
	private static final String ECLIPSE_SELECT_DOWN = "eventLogger.styledTextCommand.SELECT_LINE_DOWN";
	private static final String ECLIPSE_SELECT_COLUMN_NEXT = "eventLogger.styledTextCommand.SELECT_COLUMN_NEXT";
	private static final String ECLIPSE_SELECT_ECLIPSE_COLUMN_PREVIOUS = "eventLogger.styledTextCommand.SELECT_COLUMN_PREVIOUS";
	private static final String ECLIPSE_RENAME = "org.eclipse.jdt.ui.edit.text.java.rename.element";
	private static final String ECLIPSE_TOGGLE_COMMENT = "org.eclipse.jdt.ui.edit.text.java.toggle.comment";
	private static final String ECLISPE_SHOW_OUTLINE = "org.eclipse.jdt.ui.edit.text.java.show.outline";
	private static final String ECLIPSE_COMPILE_SOMETHING = "AUTOGEN:::org.eclipse.jdt.internal.ui.CompilationUnitEditor.ruler.actions/org.eclipse.jdt.internal.ui.javaeditor.JavaSelectRulerAction";
	private static final String ECLIPSE_SELECT_ALL = "org.eclipse.ui.edit.selectAll";
	private static final String ECLISPE_ACTIVATE_EDITOR = "org.eclipse.ui.window.activateEditor";
	private static final String ECLIPSE_SELECT_NEXT_WORD = "org.eclipse.ui.edit.text.select.wordNext";
	private static final String ECLIPSE_SELECT_PREVIOUS_WORD = "org.eclipse.ui.edit.text.select.wordPrevious";
	private static final String ECLIPSE_TOGGLE_OVERWRITE = "org.eclipse.ui.edit.text.toggleOverwrite";
	
	private static final String ECLIPSE_RUN_LAST = "org.eclipse.debug.ui.commands.RunLast";
	private static final String ECLIPSE_SHOW_RULER_CONTEXT_MENU = "org.eclipse.ui.edit.text.showRulerContextMenu";
	private static final String ECLIPSE_DELETE_PREVIOUS_WORD = "org.eclipse.ui.edit.text.deletePreviousWord";
	private static final String ECLIPSE_FORMAT = "org.eclipse.jdt.ui.edit.text.java.format";
	private static final String ECLIPSE_CONENT_ASSIST_PROPOSE = "org.eclipse.ui.edit.text.contentAssist.proposals";
	private static final String ECLIPSE_SHOW_VIEW = "org.eclipse.ui.views.showView";
	private static final String ECLIPSE_SAVE_ALL = "org.eclipse.ui.file.saveAll";

	private final AnEclipseProgrammaticController aProgrammaticController;
	
	public EclipseCommandProcessor(AnEclipseProgrammaticController aProgrammaticController) {
		super();
		this.aProgrammaticController = aProgrammaticController;
	}

	public void processEclipseCommand(EclipseCommand command) {
		System.out.println("\t" + command.getCommandID());
//		ProjectTracer.setInsert(false);
		int repeat = command.getRepeatCount();
//		if (repeat == null) {
//			repeat = 1;
//		}
//		repeat = 1;
		if (ECLIPSE_SAVE.equals(command.getCommandID())) {
			processSaveCommand(command);
		} else if (ECLIPSE_SAVE_AS.equals(command.getCommandID())) {
			processSaveAsCommand(command);
		} else if (ECLIPSE_RENAME.equals(command.getCommandID())) {
			processRenameCommand(command);
		} else if (ECLIPSE_LINE_UP.equals(command.getCommandID())) {
			processLineUpCommand(command, repeat);
		} else if (ECLIPSE_LINE_DOWN.equals(command.getCommandID())) {
			processLineDownCommand(command, repeat);
		} else if (ECLIPSE_DELETE.equals(command.getCommandID())) {
			processDeleteCommand(command, repeat); // comment
		} else if (ECLIPSE_DELETE_PREVIOUS.equals(command.getCommandID())) {
			processDeletePreviousCommand(command, repeat); // comment
		} else if (ECLIPSE_COLUMN_NEXT.equals(command.getCommandID())) {
			processColumnNextCommand(command, repeat);
		} else if (ECLIPSE_COLUMN_PREVIOUS.equals(command.getCommandID())) {
			processColumnPreviousCommand(command, repeat);
		} else if (ECLIPSE_GOTO_START.equals(command.getCommandID())) {
			processGoToStartCommand(command);
		} else if (ECLIPSE_GOTO_END.equals(command.getCommandID())) {
			processGoToEndCommand(command);
		} else if (ECLIPSE_SELECT_Start.equals(command.getCommandID())) {
			processSelectStartCommand(command);
		} else if (ECLIPSE_SELECT_END.equals(command.getCommandID())) {
			processSelectEndCommand(command);
		} else if (ECLIPSE_SELECT_UP.equals(command.getCommandID())) {
			processSelectUpCommand(command, repeat);
		} else if (ECLIPSE_SELECT_DOWN.equals(command.getCommandID())) {
			processSelectDownCommand(command, repeat);
		} else if (ECLIPSE_SELECT_COLUMN_NEXT.equals(command.getCommandID())) {
			processSelectColumnNextCommand(command, repeat);
		} else if (ECLIPSE_SELECT_ECLIPSE_COLUMN_PREVIOUS.equals(command.getCommandID())) {
			processSelectColumnPreviousCommand(command, repeat);
		} else if (ECLIPSE_TOGGLE_COMMENT.equals(command.getCommandID())) {
			processToggleComment(command); // comment
		} else if (ECLISPE_SHOW_OUTLINE.equals(command.getCommandID())) {
		} else if (ECLIPSE_COMPILE_SOMETHING.equals(command.getCommandID())) {
		} else if (ECLIPSE_SELECT_ALL.equals(command.getCommandID())) {
		} else if (ECLISPE_ACTIVATE_EDITOR.equals(command.getCommandID())) {
		} else if (ECLIPSE_TOGGLE_OVERWRITE.equals(command.getCommandID())) {
		} else if (ECLIPSE_SELECT_NEXT_WORD.equals(command.getCommandID())) {
		} else if (ECLIPSE_SELECT_PREVIOUS_WORD.equals(command.getCommandID())) {
		} else if (ECLIPSE_RUN_LAST.equals(command.getCommandID())) {
		} else if (ECLIPSE_SHOW_RULER_CONTEXT_MENU.equals(command.getCommandID())) {
		} else if (ECLIPSE_DELETE_PREVIOUS_WORD.equals(command.getCommandID())) {
		} else if (ECLIPSE_FORMAT.equals(command.getCommandID())) {
		} else if (ECLIPSE_CONENT_ASSIST_PROPOSE.equals(command.getCommandID())) {
		} else if (ECLIPSE_SHOW_VIEW.equals(command.getCommandID())) {
		} else if (ECLIPSE_SAVE_ALL.equals(command.getCommandID())) {
		} else {
			System.out.println("\tUnprocessed command ID: " + command.getCommandID());
		}
	}

	private void processToggleComment(EclipseCommand command) {
		System.out.println("\t\tToggling comment");
	}

	private void processSelectDownCommand(EclipseCommand command, int repeat) {
		System.out.println("\t\tSelecting " + repeat + " lines down from cursor");
//		Document current = ProjectTracer.getCurrentFile();
//		int start = current.getCursor();
//		int end = current.peekCursorDown(repeat);
//		if (ProjectTracer.wasSelecting()) {
//			if (ProjectTracer.wasSelectingLeft()) {
//				if (end < current.getSelectEnd()) {
//					start = current.getSelectEnd();
//					ProjectTracer.markSelectingRight();
//				}
//			} else {
//				start = current.getSelectStart();
//				ProjectTracer.markSelectingRight();
//			}
//		}
//		current.setSelect(start, end);
//		current.setCursor(end);
//		ProjectTracer.markSelected();
	}

	private void processSelectUpCommand(EclipseCommand command, int repeat) {
		System.out.println("\t\tSelecting " + repeat + " lines up from cursor");
//		Document current = ProjectTracer.getCurrentFile();
//		int start = current.peekCursorUp(repeat);
//		int end = current.getCursor();
//		if (ProjectTracer.wasSelecting()) {
//			if (ProjectTracer.wasSelectingRight()) {
//				if (start > current.getSelectStart()) {
//					end = current.getSelectEnd();
//					ProjectTracer.markSelectingLeft();
//				}
//			} else {
//				end = current.getSelectEnd();
//				ProjectTracer.markSelectingLeft();
//			}
//		}
//		current.setSelect(start, end);
//		current.setCursor(start);
//		ProjectTracer.markSelected();
	}

	private void processSelectColumnPreviousCommand(EclipseCommand command, int repeat) {
		System.out.println("\t\tSelecting " + repeat + " left from cursor");
//		Document current = ProjectTracer.getCurrentFile();
//		int start = current.peekCursorLeft(repeat);
//		int end = current.getCursor();
//		if (ProjectTracer.wasSelecting()) {
//			if (ProjectTracer.wasSelectingRight()) {
//				if (start > current.getSelectStart()) {
//					end = current.getSelectStart();
//					ProjectTracer.markSelectingLeft();
//				}
//			} else {
//				end = current.getSelectEnd();
//				ProjectTracer.markSelectingLeft();
//			}
//		}
//		current.setSelect(start, end);
//		current.setCursor(start);
//		ProjectTracer.markSelected();
	}

	private void processSelectColumnNextCommand(EclipseCommand command, int repeat) {
		System.out.println("\t\tSelecting " + repeat + " right from cursor");
//		Document current = ProjectTracer.getCurrentFile();
//		int start = current.getCursor();
//		int end = current.peekCursorRight(repeat);
//		if (ProjectTracer.wasSelecting()) {
//			if (ProjectTracer.wasSelectingLeft()) {
//				if (end < current.getSelectEnd()) {
//					start = current.getSelectEnd();
//					ProjectTracer.markSelectingRight();
//				}
//			} else {
//				start = current.getSelectStart();
//				ProjectTracer.markSelectingRight();
//			}
//		}
//		current.setSelect(start, end);
//		current.setCursor(start);
//		ProjectTracer.markSelected();
	}

	private void processSelectEndCommand(EclipseCommand command) {
		System.out.println("\t\tSelecting from cursor to end");
//		Document current = ProjectTracer.getCurrentFile();
//		int end = current.peekCursorEnd();
//		current.setSelect(current.getCursor(), end);
//		current.setCursor(end);
	}

	private void processSelectStartCommand(EclipseCommand command) {
		System.out.println("\t\tSelecting from cursor to start");
//		Document current = ProjectTracer.getCurrentFile();
//		int start = current.peekCursorStart();
//		current.setSelect(start, current.getCursor());
//		current.setCursor(start);
	}

	private void processGoToEndCommand(EclipseCommand command) {
		System.out.println("\t\tMoving cursor to line end");
//		Document current = ProjectTracer.getCurrentFile();
//		current.cursorEnd();
		aProgrammaticController.scrollEnd();
	}

	private void processGoToStartCommand(EclipseCommand command) {
		System.out.println("\t\tMoving cursor to line start");
//		Document current = ProjectTracer.getCurrentFile();
//		current.cursorStart();
		aProgrammaticController.scrollHome();
	}

	private void processColumnPreviousCommand(EclipseCommand command, int repeat) {
		System.out.println("\t\tMoving cursor left " + repeat);
//		Document current = ProjectTracer.getCurrentFile();
//		current.cursorLeft(repeat);
		aProgrammaticController.scrollLeft(command.getRepeatCount());
	}

	private void processColumnNextCommand(EclipseCommand command, int repeat) {
		System.out.println("\t\tMoving cursor right " + repeat);
//		Document current = ProjectTracer.getCurrentFile();
//		current.cursorRight(repeat);
		aProgrammaticController.scrollRight(command.getRepeatCount());
	}

	private void processDeletePreviousCommand(EclipseCommand command, int repeat) {
		System.out.println("\t\tDeleting " + repeat + " before cursor");
//		Document current = ProjectTracer.getCurrentFile();
//		current.removeBefore(repeat);
	}

	private void processDeleteCommand(EclipseCommand command, int repeat) {
		System.out.println("\t\tDeleting " + repeat + " after cursor");
//		Document current = ProjectTracer.getCurrentFile();
//		current.removeAfter(repeat);
	}

	private void processLineDownCommand(EclipseCommand command, int repeat) {
		System.out.println("\t\tMoving cursor down " + repeat);
//		Document current = ProjectTracer.getCurrentFile();
//		current.cursorDown(repeat);
		aProgrammaticController.scrollDown(command.getRepeatCount());
	}

	private void processLineUpCommand(EclipseCommand command, int repeat) {
		System.out.println("\t\tMoving cursor up " + repeat);
//		Document current = ProjectTracer.getCurrentFile();
//		current.cursorUp(repeat);
		aProgrammaticController.scrollUp(command.getRepeatCount());
	}

	private void processRenameCommand(EclipseCommand command) {
		System.out.println("\t\tRenaming unknown elment");
	}

	private void processSaveCommand(EclipseCommand command) {
		System.out.println("\t\tSaving file");// '" + ProjectTracer.getCurrentFileName() + "'");
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		aProgrammaticController.saveTextInCurrentEditor();
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private void processSaveAsCommand(EclipseCommand command) {
		System.out.println("\t\tSaving file as");
//		String currentName = ProjectTracer.getCurrentFileName();
//		if (currentName == null) {
//			System.out.println("\t\tSaving unsaved file with as unknown name");
//		} else {
//			System.out.println("\t\tSaving file '" + currentName + "' with unknown name");
//		}
	}
}
