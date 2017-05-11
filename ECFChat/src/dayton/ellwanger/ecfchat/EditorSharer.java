package dayton.ellwanger.ecfchat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.docshare.DocShare;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorSharer {

	IRosterEntry shareWith;
	
	public void setShareWith(IRosterEntry shareWith) {
		this.shareWith = shareWith;
	}
	
	public static IDocument getDocument() {
		final ITextEditor textEditor = getTextEditor();
		IDocumentProvider docProvider = textEditor.getDocumentProvider();
		return docProvider.getDocument(textEditor.getEditorInput());
	}
	
	public static IDocumentProvider getDocumentProvider() {
		final ITextEditor textEditor = getTextEditor();
		return textEditor.getDocumentProvider();
	}
	
	public static IPath getOpenFile() {
		final ITextEditor textEditor = getTextEditor();
		IFile file = textEditor.getEditorInput().getAdapter(IFile.class);
		return file.getFullPath().makeAbsolute();
	}
	
	//Must be called from UI thread
	public void shareEditor() {
		if (shareWith != null) {
			IRoster roster = shareWith.getRoster();
			final IContainer container = (IContainer) roster.getPresenceContainerAdapter().getAdapter(IContainer.class);
			if (container.getConnectedID() == null) {
				System.out.println("Bad container");
				return;
			}
				//showErrorMessage(Messages.DocShareRosterMenuHandler_ERROR_NOT_CONNECTED);
			ID id = container.getID();
			final DocShare sender = org.eclipse.ecf.internal.docshare.Activator.getDefault().getDocShare(container.getID());
			if (sender == null) {
				System.out.println("Bad sender");
				return;
			}
				//showErrorMessage(Messages.DocShareRosterMenuHandler_ERROR_NO_SENDER);
			if (sender.isSharing()) {
				System.out.println("Sender sharing");
				return;
			}
				//showErrorMessage(Messages.DocShareRosterMenuHandler_ERROR_EDITOR_ALREADY_SHARING);
			final ITextEditor textEditor = getTextEditor();
			if (textEditor == null) {
				System.out.println("Bad editor");
				return;
			}
				//showErrorMessage(Messages.DocShareRosterMenuHandler_EXCEPTION_EDITOR_NOT_TEXT);
			final String inputName = getInputName(textEditor);
			if (inputName == null) {
				System.out.println("Bad input file");
				return;
			}
				//showErrorMessage(Messages.DocShareRosterMenuHandler_NO_FILENAME_WITH_CONTENT);
			final IUser user = roster.getUser();
			
			sender.startShare(user.getID(), user.getName(), shareWith.getUser().getID(), inputName, textEditor);
		}
	}
	
	protected static ITextEditor getTextEditor() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return null;
		final IWorkbenchWindow ww = workbench.getActiveWorkbenchWindow();
		if (ww == null)
			return null;
		final IWorkbenchPage wp = ww.getActivePage();
		if (wp == null)
			return null;
		final IEditorPart ep = wp.getActiveEditor();
		if (ep instanceof ITextEditor)
			return (ITextEditor) ep;
		if (ep != null)
			return (ITextEditor) ep.getAdapter(ITextEditor.class);
		return null;
	}
	
	private static String getInputName(IEditorPart editorPart) {
		final IEditorInput input = editorPart.getEditorInput();
		if (input != null) {
			return input.getName();
		}
		return null;
	}
		
}
