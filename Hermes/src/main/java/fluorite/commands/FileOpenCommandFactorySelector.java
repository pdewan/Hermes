package fluorite.commands;

import org.eclipse.ui.IEditorPart;

import config.HelperConfigurationManagerFactory;

public class FileOpenCommandFactorySelector {
//	static FileOpenCommandFactory fileOpenCommandFactory = new AFileOpenCommandFactory();
//	static FileOpenCommandFactory fileOpenCommandFactory = new ADiffBasedFileOpenCommandFactory();

	static FileOpenCommandFactory fileOpenCommandFactory = null;

	public static FileOpenCommandFactory getFileOpenCommandFactory() {
		if (fileOpenCommandFactory == null) {
			Boolean diffLoggedFiles = HelperConfigurationManagerFactory.getSingleton().isDiffLoggedFiles();
			fileOpenCommandFactory = diffLoggedFiles?
					new ADiffBasedFileOpenCommandFactory():
				    new AFileOpenCommandFactory();
		}
		return fileOpenCommandFactory;
	}

	public static void setFileOpenCommandFactory(FileOpenCommandFactory fileOpenCommandFactory) {
		FileOpenCommandFactorySelector.fileOpenCommandFactory = fileOpenCommandFactory;
	}

	public static FileOpenCommand createFileOpemCommand (IEditorPart editor) {
		return getFileOpenCommandFactory().createFileOpemCommand(editor);
	}
}
