package fluorite.commands;

import org.eclipse.ui.IEditorPart;

public class FileOpenCommandFactorySelector {
	static FileOpenCommandFactory fileOpenCommandFactory = new AFileOpenCommandFactory();

	public static FileOpenCommandFactory getFileOpenCommandFactory() {
		return fileOpenCommandFactory;
	}

	public static void setFileOpenCommandFactory(FileOpenCommandFactory fileOpenCommandFactory) {
		FileOpenCommandFactorySelector.fileOpenCommandFactory = fileOpenCommandFactory;
	}

	public static FileOpenCommand createFileOpemCommand (IEditorPart editor) {
		return fileOpenCommandFactory.createFileOpemCommand(editor);
	}
}
