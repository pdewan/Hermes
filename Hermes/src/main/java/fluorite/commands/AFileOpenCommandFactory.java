package fluorite.commands;

import org.eclipse.ui.IEditorPart;

public class AFileOpenCommandFactory implements FileOpenCommandFactory{

	@Override
	public FileOpenCommand createFileOpemCommand(IEditorPart editor) {
		return new FileOpenCommand(editor);
	}

}
