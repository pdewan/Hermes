package workspacelistener;

import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorListener implements IPartListener, WorkspaceFileListener {

	private WorkspaceFileListener listener;
	private HashSet<IDocument> documents;
	
	public EditorListener(WorkspaceFileListener listener, IWorkbenchPart activePart) {
		this.listener = listener;
		documents = new HashSet<IDocument>();
		partActivated(activePart);
	}
	
	@Override
	public void partActivated(IWorkbenchPart part) {
		try {
			if(part instanceof IEditorPart) {
				IEditorPart editorPart = (IEditorPart) part;
				ITextEditor textEditor = (ITextEditor) editorPart.getAdapter(ITextEditor.class);
				IDocumentProvider docProvider = textEditor.getDocumentProvider();
				IDocument doc = docProvider.getDocument(textEditor.getEditorInput());
				//Make sure we don't add two listeners to the same doc
				if(!documents.contains(doc)) {
					IFile file = textEditor.getEditorInput().getAdapter(IFile.class);
					IPath filePath = file.getFullPath().makeAbsolute();
					doc.addDocumentListener(new DocumentListener(filePath, this));
					documents.add(doc);
				}
			}
		} catch (Exception ex) {ex.printStackTrace();}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {}

	@Override
	public void partClosed(IWorkbenchPart part) {}

	@Override
	public void partDeactivated(IWorkbenchPart part) {}

	@Override
	public void partOpened(IWorkbenchPart part) {}

	@Override
	public void newFileContents(NewFileContents newFileContents) {}

	@Override
	public void fileDelta(Delta fileDelta) {
		listener.fileDelta(fileDelta);
	}
	
}