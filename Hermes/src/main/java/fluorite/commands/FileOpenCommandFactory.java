package fluorite.commands;

import org.eclipse.ui.IEditorPart;

public interface FileOpenCommandFactory {
	FileOpenCommand createFileOpemCommand (IEditorPart editor);
}
