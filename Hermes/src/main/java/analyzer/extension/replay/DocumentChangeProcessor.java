package analyzer.extension.replay;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;

import fluorite.commands.BaseDocumentChangeEvent;
import fluorite.util.EHUtilities;
import programmatically.AnEclipseProgrammaticController;


public class DocumentChangeProcessor {

	private static final String INSERT_CHANGE = "Insert";
	private static final String DELETE_CHANGE = "Delete";
	private static final String REPLACE_CHANGE = "Replace";
	private static final String FILE_OPEN_COMMAND = "FileOpenCommand";

	private static final String TEXT_ELEMENT = "text";
	private static final String INSERTED_TEXT_ELEMENT = "insertedText";
	private static final String DELETED_TEXT_ELEMENT = "deletedText";

	private static final String FILE_PATH_ELEMENT = "filePath";
	private static final String SNAPSHOT_ELEMENT = "snapshot";
	private static final String DATA_ELEMENT = "data";
	
	private static final String START_ATTR = "start";
	private static final String END_ATTR = "end";
	private static final String OFFSET_ATTR = "offset";
	private static final String LENGTH_ATTR = "length";
	private static final String DOC_LENGTH_ATTR = "docLength";
	
	private final AnEclipseProgrammaticController aProgrammaticController;
	
	private Set<Path> files;
	
	public DocumentChangeProcessor(AnEclipseProgrammaticController aProgrammaticController) {
		super();
		this.aProgrammaticController = aProgrammaticController;
		files = new HashSet<>();
	}

	public void processDocumentChange(BaseDocumentChangeEvent change) {
		int repeat = change.getRepeatCount();
//		if (repeat == null) {
//			repeat = 1;
//		}
		repeat = 1;
		for(int i = 0; i < repeat; i ++) {
			if (INSERT_CHANGE.equals(change.getCommandType())) {
				processInsertChange(change);
			} else if (DELETE_CHANGE.equals(change.getCommandType())) {
				processDeleteChange(change);
			} else if (REPLACE_CHANGE.equals(change.getCommandType())) {
				processReplaceChange(change);
			} else if (FILE_OPEN_COMMAND.equals(change.getCommandType())){
				processFileOpenCommand(change);
			} else {
				System.out.println("Unprocessed change type: " + change.getCommandType());
			}
		}
	}
	
	public void reprocessInsert(BaseDocumentChangeEvent change) {
		processInsertChange(change);
	}
	
	private void processInsertChange(BaseDocumentChangeEvent change) {
		Map<String, String> data = change.getDataMap();
		Map<String, String> attrs = change.getAttributesMap();
		String offsetStr = attrs.get(OFFSET_ATTR);
		String lengthStr = attrs.get(LENGTH_ATTR);
		
		String inserted = data.get(TEXT_ELEMENT);
		System.out.println("\tInserting: '" + inserted + "'");
		inserted = inserted.replaceAll("\r?\n", "\r\n");
//		System.out.println("\tDeleting len " + lengthStr + " from " + offsetStr + ": '" + data.get(TEXT_ELEMENT) + "'");
		int offset = 0;
		int len = 0;
		try {
			offset = Integer.parseInt(offsetStr);
			len = Integer.parseInt(lengthStr);
			aProgrammaticController.insertAtLocation(inserted, offset);
//			aProgrammaticController.replaceTextInCurrentEditor(offset, 0, inserted);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
//		aProgrammaticController.insertStringAtCaret(inserted);
//		ProjectTracer.setInsert(true);
//		System.out.println("\tInsert of length " + change.getLength() + " at " + ProjectTracer.getCurrentFile().getCursor());
//		
//		ContentHelper contents = ContentHelper.mapContents(change.getContent());
//		Optional<String> addedText = contents.get(TEXT_ELEMENT);
//		
//		if (addedText.isPresent()) {
//			String added = addedText.get();
//			if (change.getLength() > added.length()) {
//				added = added.replaceAll("\r?\n", "\r\n");
//			}
//			if (ProjectTracer.getPrevious() instanceof BaseDocumentChangeEvent) {
//				if (REPLACE_CHANGE.equals(((BaseDocumentChangeEvent)ProjectTracer.getPrevious()).getCommandType())) {
//					ProjectTracer.setInserted(ProjectTracer.getInserted() + added);
//				} else {
//					ProjectTracer.setInserted(added);
//				}
//			} else {
//				ProjectTracer.setInserted(added);
//			}
//			System.out.println("\t\tInserting: " + added);
//			int start = change.getOffset();
//			int cursor = ProjectTracer.getCurrentFile().getCursor();
//			ProjectTracer.getCurrentFile().addText(start, added);
//			
//			ProjectTracer.getCurrentFile().setCursor(start + change.getLength());
//			
////			if (cursor > start) {
////				ProjectTracer.getCurrentFile().cursorRight(added.length());
////			}
//			
//			System.out.println("*** begin new file ***");
//			System.out.println(ProjectTracer.getCurrentFile().getContents());
//			System.out.println("*** end new file ***");
//		} else {
//			System.out.println("\t\tNo inserted text given");
//		}
	}
	
	private void processDeleteChange(BaseDocumentChangeEvent change) {
		Map<String, String> data = change.getDataMap();
		Map<String, String> attrs = change.getAttributesMap();
		String offsetStr = attrs.get(OFFSET_ATTR);
		String lengthStr = attrs.get(LENGTH_ATTR);
		System.out.println("\tDeleting len " + lengthStr + " from " + offsetStr + ": '" + data.get(TEXT_ELEMENT) + "'");
		try {
			int offset = Integer.parseInt(offsetStr);
			int len = Integer.parseInt(lengthStr);
			aProgrammaticController.replaceTextInCurrentEditor(offset, len, "");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
////		ProjectTracer.setInsert(false);
//		ContentHelper contents = ContentHelper.mapContents(change.getContent());
//		Optional<String> deletedText = contents.get(TEXT_ELEMENT);
//		int length = change.getLength();
//		System.out.println("\tDeletion of length " + length + " at " + change.getOffset());
//		if (deletedText.isPresent()) {
//			System.out.println("\t\tNo deleted text given");
//		}
//		int start = change.getOffset();
//		int end = start + length;
//		int cursor = ProjectTracer.getCurrentFile().getCursor();
//		if (cursor > start && cursor <= end) {
//			ProjectTracer.getCurrentFile().setCursor(start);
//		} else if (cursor > end) {
//			ProjectTracer.getCurrentFile().cursorLeft(length);
//		}
//		System.out.println("\t\tDelete " + start + " to " + end);
//		String removing = ProjectTracer.getCurrentFile().getContents().substring(start, end);
//		System.out.println("\t\tRemoving: " + removing);
//		System.out.println("\t\tBytes: " + Arrays.toString(removing.getBytes()));
//		ProjectTracer.getCurrentFile().removeText(start, change.getLength());
//		System.out.println("*** begin new file ***");
//		System.out.println(ProjectTracer.getCurrentFile().getContents());
//		System.out.println("*** end new file ***");
	}

	private void processReplaceChange(BaseDocumentChangeEvent change) {
		Map<String, String> data = change.getDataMap();
		Map<String, String> attrs = change.getAttributesMap();
		String offsetStr = attrs.get(OFFSET_ATTR);
		String lengthStr = attrs.get(LENGTH_ATTR);
		String oldStr = data.get(DELETED_TEXT_ELEMENT);
		String newStr = data.get(INSERTED_TEXT_ELEMENT);
		System.out.println("\tReplacing");
		System.out.println("\t\tOld: '" + oldStr + "'");
		System.out.println("\t\tNew: '" + newStr + "'");
		try {
			int offset = Integer.parseInt(offsetStr);
			int len = Integer.parseInt(lengthStr);
			if (len > newStr.length()) {
				newStr = newStr.replace("\r?\n", "\r\n");
			}
			aProgrammaticController.replaceTextInCurrentEditor(offset, len, newStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
////		ProjectTracer.setInsert(false);
//		ContentHelper contents = ContentHelper.mapContents(change.getContent());
//		Optional<String> insertedText = contents.get(INSERTED_TEXT_ELEMENT);
//		Optional<String> removedText = contents.get(DELETED_TEXT_ELEMENT);
//		if (insertedText.isPresent()) {
//			ProjectTracer.setInsert(true);
//			int start = change.getOffset();
//			int end = start + change.getLength();
//			int length = change.getLength();
//			int cursor = ProjectTracer.getCurrentFile().getCursor();
//			
//			System.out.println("\tDeletion of length " + change.getLength() + " at " + change.getOffset());
//			System.out.println("\t\tDeleting: " + removedText.get());
//			ProjectTracer.getCurrentFile().removeText(start, length);
//			if (cursor > start && cursor <= end) {
//				ProjectTracer.getCurrentFile().setCursor(start);
//			} else if (cursor > end) {
//				ProjectTracer.getCurrentFile().cursorLeft(length);z
//			}
//			
//			System.out.println("\tInsert of length " + change.getInsertionLength() + " at " + change.getOffset());
//
//			String added = insertedText.get();
//			ProjectTracer.setInserted(added);
//			System.out.println("\t\tInserting: " + added);
//			if (change.getInsertionLength() > added.length()) {
//				added = added.replaceAll("\r?\n", "\r\n");
//			}
//			ProjectTracer.getCurrentFile().addText(start, added);
//			if (cursor > start) {
//				ProjectTracer.getCurrentFile().cursorRight(change.getInsertionLength());
//			}
//			
//			System.out.println("*** begin new file ***");
//			System.out.println(ProjectTracer.getCurrentFile().getContents());
//			System.out.println("*** end new file ***");
//		} else {
//			System.out.println("\t\tNo inserted text given");
//		}
	}
	
	private void processFileOpenCommand(BaseDocumentChangeEvent change) {
		Map<String, String> data = change.getDataMap();
		String rawPathStr = data.get(FILE_PATH_ELEMENT);
		Path rawPath = Paths.get(rawPathStr);
		System.out.println("\tRaw path: " + rawPath);
		int srcDepth;
		for(srcDepth = rawPath.getNameCount() - 1; srcDepth > 0 && !rawPath.getName(srcDepth).getFileName().toString().equals("src"); srcDepth --);
		System.out.println("\t\tUsing subpath range [" + srcDepth + ", " + rawPath.getNameCount() + ")");
		Path relativePath = rawPath.subpath(srcDepth, rawPath.getNameCount());
		String relativePathStr = relativePath.toString();
		Path realPath = Paths.get(aProgrammaticController.getProjectLocation(), relativePathStr);
		System.out.println("\tOpening file: " + data.get(FILE_PATH_ELEMENT) + " (" + relativePathStr + ", " + realPath + ")");

		boolean newFile = false;
		IEditorPart editor = EHUtilities.getActiveEditor();
		if (!files.contains(realPath)) {
			newFile = true;
			try {
				Files.deleteIfExists(realPath);
				files.add(realPath);
				Files.createDirectories(realPath.getParent());
				Files.createFile(realPath);
				if (data.containsKey(SNAPSHOT_ELEMENT)) {
					String snapshot = data.get(SNAPSHOT_ELEMENT);
					System.out.println("=== BEGIN CONTENTS ===");
					System.out.println(snapshot);
					System.out.println("=== END CONTENTS ===");
					Map<String, String> attrs = change.getAttributesMap();
					String docLengthStr = attrs.get(DOC_LENGTH_ATTR);
					try {
						int docLength = Integer.parseInt(docLengthStr);
						snapshot = fixNewlines(snapshot, docLength);
						Files.write(realPath, snapshot.getBytes(Charset.forName("UTF-8")));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		StringBuilder sb = new StringBuilder();
		try {
			for(String line : Files.readAllLines(realPath)) {
				sb.append(line);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (newFile) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		aProgrammaticController.openEditor(relativePathStr);
//		if (newFile) {
//			try {
//				Thread.sleep(newFile ? 5000 : 1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		try {
//			System.in.read();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		aProgrammaticController.setTextEditorDataStructures();
	}
	
	private static String fixNewlines(String str, int len) {
		if (len > str.length()) {
			return str.replaceAll("\r?\n", "\r\n");
		} else {
			return str;
		}
	}
}
