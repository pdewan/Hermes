package fluorite.commands;

import org.eclipse.ui.IEditorPart;

public class ADiffBasedFileOpenCommandFactory implements FileOpenCommandFactory{

	@Override
	public FileOpenCommand createFileOpemCommand(IEditorPart editor) {
		return new DiffBasedFileOpenCommand(editor);
	}

}
