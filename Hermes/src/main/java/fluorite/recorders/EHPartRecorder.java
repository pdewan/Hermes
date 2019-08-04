package fluorite.recorders;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import fluorite.commands.FileOpenCommand;
import fluorite.model.FileSnapshotManager;
import fluorite.util.EHUtilities;
import util.trace.workbench.PartActivated;
import util.trace.workbench.PartOpened;

public class EHPartRecorder extends EHBaseRecorder implements IPartListener {

	private static EHPartRecorder instance = null;
	List<IWorkbenchPart> opendedParts = new ArrayList();
	List<IWorkbenchPart> activeParts = new ArrayList();
	List<IPartListener> partListeners = new ArrayList();
	protected boolean updateSnaphot = false;

	public static EHPartRecorder getInstance() {
		if (instance == null) {
			instance = new EHPartRecorder();
		}

		return instance;
	}

	private EHPartRecorder() {
		super();
	}
	
	public void addPartListener(IPartListener newVal) {
		if (!partListeners.contains(newVal))
			partListeners.add(newVal);
		
	}

	@Override
	public void addListeners(IEditorPart editor) {
		// Do nothing.
	}

	@Override
	public void removeListeners(IEditorPart editor) {
		// Do nothing.
	}

	public void partActivated(IWorkbenchPart part) {
		
		PartActivated.newCase(part, this);
		try {
			EHUtilities.setCommandService(
			
			PlatformUI
            .getWorkbench().getActiveWorkbenchWindow()
            .getService(ICommandService.class));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!activeParts.contains(part))
			activeParts.add(part);
		for (IPartListener aListener:partListeners) {
			aListener.partActivated(part);
		}
		if (part instanceof IEditorPart) {
			if (getRecorder().getEditor() == part) {
				return;
			}

			if (getRecorder().getEditor() != null) {
//				EHUtilities.getSourceViewerExtension4(getRecorder().getEditor()).
//				getContentAssistantFacade().addCompletionListener(ContentAssistListener.getInstance());
//				EHUtilities.getSourceViewerExtension3(getRecorder().getEditor()).
//				getQuickAssistAssistant().addCompletionListener(ContentAssistListener.getInstance());
				String filePath = EHUtilities.getFilePathFromEditor(getRecorder()
						.getEditor());
				IDocument currentDoc = EHUtilities.getDocument(getRecorder()
						.getEditor());

				if (filePath != null && currentDoc != null &&updateSnaphot) {
					
					FileSnapshotManager.getInstance().updateSnapshot(filePath,
							currentDoc.get());
				}

				getRecorder().removeListeners();
			}

			IEditorPart editor = (IEditorPart) part;
			EHUtilities.setCurrentEditorPartAndFile(editor);
			
			try {
			EHUtilities.getSourceViewerExtension4(editor).
			getContentAssistantFacade().addCompletionListener(ContentAssistListener.getInstance());
			getRecorder().addListeners(editor);
			} catch (Exception e) {
//				e.printStackTrace();
			}
			
			FileOpenCommand newFoc = new FileOpenCommand(editor);
			getRecorder().recordCommand(newFoc);
			getRecorder().fireActiveFileChangedEvent(newFoc);
		}
	}

	public void partBroughtToTop(IWorkbenchPart part) {
		for (IPartListener aListener:partListeners) {
			aListener.partBroughtToTop(part);
		}
		// TODO Auto-generated method stub

	}

	public void partClosed(IWorkbenchPart part) {
		if (opendedParts.contains(part))
			opendedParts.remove(part);
		for (IPartListener aListener:partListeners) {
			aListener.partClosed(part);
		}
		if (part instanceof IEditorPart) {
			getRecorder().removeListeners();
		}
	}

	public void partDeactivated(IWorkbenchPart part) {
		if (activeParts.contains(part))
			activeParts.remove(part);
		for (IPartListener aListener:partListeners) {
			aListener.partDeactivated(part);
		}
		// if (part instanceof IEditorPart) {
		// removeListeners();
		// }
	}

	public void partOpened(IWorkbenchPart part) {
		if (!opendedParts.contains(part))
			opendedParts.add(part);
		for (IPartListener aListener:partListeners) {
			aListener.partOpened(part);
		}
//		if (part instanceof SarosView  ) {
//			SarosAccessorFactory.getSingleton().setSarosView((SarosView) part); 
//		}
		PartOpened.newCase(part, this);

	}

}
