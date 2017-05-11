package workspacelistener;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.DocumentEvent;

public class Delta extends FileUpdate {

	private DocumentEvent changes;
	
	public Delta(IPath filePath, DocumentEvent changes) {
		super(filePath);
		this.changes = changes;
	}
	
	public DocumentEvent getChanges() {
		return changes;
	}
	
}
