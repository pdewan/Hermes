package analyzer.extension.replay;

import java.util.Map;

import fluorite.commands.EHICommand;
import fluorite.commands.EclipseCommand;
import programmatically.AnEclipseProgrammaticController;

public class CommandProcessor {
	private static final String MOVE_CARET_COMMAND = "MoveCaretCommand";
	private static final String FILE_OPEN_COMMAND = "FileOpenCommand";
	private static final String ECLISPE_COMMAND = "EclipseCommand";
	private static final String INSERT_STRING_COMMAND = "InsertStringCommand";
	private static final String SELECT_TEXT_COMMAND = "SelectTextCommand";
	private static final String COPY_COMMAND = "CopyCommand";
	private static final String PASTE_COMMAND = "PasteCommand";
	private static final String CUT_COMMAND = "CutCommand";
	private static final String SHELL_COMMAND = "ShellCommand";
	private static final String DIFFICULTY_COMMAND = "DifficultyCommand";
	private static final String UNDO_COMMAND = "UndoCommand";
	private static final String REDO_COMMAND = "RedoCommand";
	private static final String RUN_COMMAND = "RunCommand";
	private static final String ASSIST_COMMAND = "AssistCommand";
	private static final String COMPILATION_COMMAND = "CompilationCommand";
	private static final String PREDICTION_COMMAND = "PredictionCommand";
	private static final String EXCEPTION_COMMAND = "ExceptionCommand";
	private static final String FIND_COMMAND = "FindCommand";

	private static final String FILE_PATH_ELEMENT = "filePath";
	private static final String SNAPSHOT_ELEMENT = "snapshot";
	private static final String DATA_ELEMENT = "data";
	
	private static final String START_ATTR = "start";
	private static final String END_ATTR = "end";
	private static final String CARET_OFFSET_ATTR = "caretOffset";
	
	private final AnEclipseProgrammaticController aProgrammaticController;
	
	public CommandProcessor(AnEclipseProgrammaticController aProgrammaticController) {
		super();
		this.aProgrammaticController = aProgrammaticController;
	}

	public void processCommand(EHICommand command) {
		int repeat = command.getRepeatCount();
//		if (repeat == null) {
//			repeat = 1;
//		}
//		if (!INSERT_STRING_COMMAND.equals(command.getCommandType())) {
//			ProjectTracer.setInsert(false);
//		}
		
		if (MOVE_CARET_COMMAND.equals(command.getCommandType())) {
			processMoveCaretCommand(command);
		} else if (FILE_OPEN_COMMAND.equals(command.getCommandType())) {
			processFileOpenCommand(command);
		} else if (ECLISPE_COMMAND.equals(command.getCommandType())) {
			processEclipseCommand(command);
		} else if (INSERT_STRING_COMMAND.equals(command.getCommandType())) {
			processInsertStringCommand(command, 1); // comment
		} else if (COPY_COMMAND.equals(command.getCommandType())) {
			processCopyCommand(command);
		} else if (PASTE_COMMAND.equals(command.getCommandType())) {
			processPasteCommand(command, repeat);
		} else if (CUT_COMMAND.equals(command.getCommandType())) {
			processCutCommand(command); // comment
		} else if (SELECT_TEXT_COMMAND.equals(command.getCommandType())) {
			processSelectTextCommand(command);
		} else if (SHELL_COMMAND.equals(command.getCommandType())) {
		} else if (DIFFICULTY_COMMAND.equals(command.getCommandType())) {
		} else if (UNDO_COMMAND.equals(command.getCommandType())) {
		} else if (RUN_COMMAND.equals(command.getCommandType())) {
		} else if (ASSIST_COMMAND.equals(command.getCommandType())) {
		} else if (COMPILATION_COMMAND.equals(command.getCommandType())) {
		} else if (PREDICTION_COMMAND.equals(command.getCommandType())) {
		} else if (EXCEPTION_COMMAND.equals(command.getCommandType())) {
		} else if (FIND_COMMAND.equals(command.getCommandType())) {
		} else {
			System.out.println("Unprocessed command type: " + command.getCommandType());
		}
	}
	
	private void processSelectTextCommand(EHICommand command) {
		Map<String, String> attrs = command.getAttributesMap();
		System.out.println("\tSelecting range [" + attrs.get(START_ATTR) + ", " + attrs.get(END_ATTR) + "], caret: " + attrs.get(CARET_OFFSET_ATTR));
//		int start = command.getStart();
//		int end = command.getEnd();
//		int caret = command.getCaretOffset();
//		System.out.println("\tSelecting string from " + start + " to " + end + " at " + caret);
//		Document current = ProjectTracer.getCurrentFile();
//		current.setSelect(start, end);
//		current.setCursor(caret);
//		if (caret == start) {
//			ProjectTracer.markSelectingLeft();
//		} else {
//			ProjectTracer.markSelectingRight();
//		}
//		ProjectTracer.markSelected();
	}
	
	private void processCopyCommand(EHICommand command) {
		System.out.println("\tCopying");
//		String copied = ProjectTracer.getCurrentFile().copySelection();
//		System.out.println("\tCopying string of length " + copied.length() + " at " + ProjectTracer.getCaretPos());
//		ProjectTracer.setCopied(copied);
	}
	
	private void processPasteCommand(EHICommand command, int repeat) {
		System.out.println("\tPasting");
//		ProjectTracer.setInsert(false);
//		Serializable previous = ProjectTracer.getPrevious();
//		if (previous instanceof DocumentChangeType) {
//			System.out.println("\tPasting");
//			DocumentChangeProcessor.reprocessInsert((DocumentChangeType)previous);
//		}
//		String pasting = ProjectTracer.getCopied();
//		for (int i = 0; i < repeat; i ++) {
//			System.out.println("\tPasting string of length " + pasting.length() + " at " + ProjectTracer.getCaretPos());
//			ProjectTracer.getCurrentFile().insert(pasting);
//		}
	}
	
	private void processCutCommand(EHICommand command) {
		System.out.println("\tCutting");
//		String copied = ProjectTracer.getCurrentFile().cutSelection();
//		System.out.println("\tCutting string of length " + copied.length() + " at " + ProjectTracer.getCaretPos());
//		ProjectTracer.setCopied(copied);
	}
	
	private void processInsertStringCommand(EHICommand command, int repeat) {
		Map<String, String> data = command.getDataMap();
		String inserted = data.get(DATA_ELEMENT);
		System.out.println("\tInserting string: '" + inserted + "'");
//		aProgrammaticController.addTextAndRefreshPredefinedFile(inserted);
//		ContentHelper contents = ContentHelper.mapContents(command.getContent());
//		Optional<String> iData = contents.get(DATA_ELEMENT);
//		if (iData.isPresent()) {
//			String data = iData.get();
//	
////			if (command.getLength() > data.length()) {
//				data = data.replaceAll("\r?\n", "\r\n");
////			}
//			if (ProjectTracer.wasInsert()) {
//				String inserted = ProjectTracer.getInserted();
//				inserted = inserted.replaceFirst("^\t+", "");
//				String dataCopy = data.replaceFirst("^\t+", "");
//				System.out.println("Ins:  " + Arrays.toString(inserted.getBytes()));
//				System.out.println("Comp: " + Arrays.toString(dataCopy.getBytes()));
//				if (inserted.startsWith(dataCopy)) {
//					if (inserted.length() == dataCopy.length()) {
//						ProjectTracer.setInsert(false);
//					} else {
//						ProjectTracer.setInserted(inserted.substring(dataCopy.length()));
//					}
//					return;
//				} else {
//					ProjectTracer.setInsert(false);
//				}
//			}
//			for (int i = 0; i < repeat; i ++) {
//				System.out.println("\tInserting string of length " + data.length() + " at " + ProjectTracer.getCurrentFile().getCursor());
//				ProjectTracer.getCurrentFile().insert(data);
//			}
//	
//			System.out.println("*** begin new file ***");
//			System.out.println(ProjectTracer.getCurrentFile().getContents());
//			System.out.println("*** end new file ***");
//		} else {
//			System.out.println("\tNo inserted string given");
//		}
	}
	
	private void processMoveCaretCommand(EHICommand command) {
		Map<String, String> attrs = command.getAttributesMap();
		String caretOffsetStr = attrs.get(CARET_OFFSET_ATTR);
		System.out.println("\tMoving cursor to " + caretOffsetStr);
		try {
			int offset = Integer.parseInt(caretOffsetStr);
			aProgrammaticController.positionCursorInCurrentEditor(offset);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
//		System.out.println("\tMoving caret from " + ProjectTracer.getCaretPos() + " to " + command.getCaretOffset());
//		ProjectTracer.setCaretPos(command.getCaretOffset());
	}
	
	private void processEclipseCommand(EHICommand command) {
		System.out.println("\tEclipse command: " + ((EclipseCommand)command).getCommandID() + " " + command.getClass() + " " + command.getClass().getSuperclass());
//		EclipseCommandProcessor.processEclipseCommand(command);
	}
	
	private void processFileOpenCommand(EHICommand command) {
		System.out.println("\tOpening file");
//		ContentHelper contents = ContentHelper.mapContents(command.getContent());
//		Optional<String> fName = contents.get(FILE_PATH_ELEMENT);
//		Optional<String> fContent = contents.get(SNAPSHOT_ELEMENT);
//		if (!fName.isPresent()) {
//			System.out.println("No file name found");
//		} else {
//			String name = fName.get().toLowerCase();
//			System.out.println("\tOpening file: " + name);
//			if (fContent.isPresent()) {
//				String content = fContent.get();
//				if (content.length() != command.getDocLength()) {
//					content = content.replaceAll("\r?\n", "\r\n");
//					fContent = Optional.of(content);
//				}
//			}
//			if (ProjectTracer.hasFile(name)) {
//				Document doc = ProjectTracer.getFile(name);
//				if (fContent.isPresent()) {
//					String content = fContent.get();
//					if (!content.equals(doc.getContents())) {
//						System.out.println("\t\tFile contents mismatch, updating with new. (Old len: " + doc.getLength() + ", New len: " + command.getDocLength() + ")");
//						doc.setContents(content);
//					}
//				}
//			} else {
//				System.out.println("\t\tFile length: " + command.getDocLength());
//				Document doc;
//				if (!fContent.isPresent()) {
//					doc = new EclipseDocument();
//				} else {
//					doc = new EclipseDocument(fContent.get());
//				}
//				ProjectTracer.addFile(name, doc);
//			}
//			ProjectTracer.setCurrentFileName(name);
//			System.out.println("*** begin opened file ***");
//			System.out.println(ProjectTracer.getCurrentFile().getContents());
//			System.out.println("*** end opened file ***");
//		}
	}
}
