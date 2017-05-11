package workspacelistener;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

public class DocumentListener implements IDocumentListener {

	private WorkspaceFileListener listener;
	private IPath documentPath;
	
	public DocumentListener(IPath documentPath, WorkspaceFileListener listener) {
		this.documentPath = documentPath;
		this.listener = listener;
	}
	
	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {}

	@Override
	public void documentChanged(DocumentEvent event) {
		listener.fileDelta(new Delta(documentPath, event));
	}
	
}