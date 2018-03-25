package fluorite.recorders;

import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewerExtension3;
import org.eclipse.jface.text.source.ISourceViewerExtension4;
import org.eclipse.ui.IEditorPart;

import fluorite.commands.AssistCommand;
import fluorite.util.EHUtilities;

public class EHCompletionRecorder extends EHBaseRecorder implements
		ICompletionListener {

	private static EHCompletionRecorder instance = null;

	public static EHCompletionRecorder getInstance() {
		if (instance == null) {
			instance = new EHCompletionRecorder();
		}

		return instance;
	}

	private EHCompletionRecorder() {
		super();
	}

	@Override
	public void addListeners(IEditorPart editor) {
		ISourceViewerExtension3 sourceViewerExtension3 = EHUtilities
				.getSourceViewerExtension3(editor);
		if (sourceViewerExtension3 != null) {
			if (sourceViewerExtension3.getQuickAssistAssistant() != null) {
				sourceViewerExtension3.getQuickAssistAssistant()
						.addCompletionListener(this);
			}
		}

		ISourceViewerExtension4 sourceViewerExtension4 = EHUtilities
				.getSourceViewerExtension4(editor);
		if (sourceViewerExtension4 != null) {
			if (sourceViewerExtension4.getContentAssistantFacade() != null) {
				sourceViewerExtension4.getContentAssistantFacade()
						.addCompletionListener(this);
			}
		}
	}

	@Override
	public void removeListeners(IEditorPart editor) {
		try {
			ISourceViewerExtension3 sourceViewerExtension3 = EHUtilities
					.getSourceViewerExtension3(getRecorder().getEditor());
			if (sourceViewerExtension3 != null) {
				if (sourceViewerExtension3.getQuickAssistAssistant() != null) {
					sourceViewerExtension3.getQuickAssistAssistant()
							.removeCompletionListener(this);
				}
			}

			ISourceViewerExtension4 sourceViewerExtension4 = EHUtilities
					.getSourceViewerExtension4(getRecorder().getEditor());
			if (sourceViewerExtension4 != null) {
				if (sourceViewerExtension4.getContentAssistantFacade() != null) {
					sourceViewerExtension4.getContentAssistantFacade()
							.removeCompletionListener(this);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void assistSessionStarted(ContentAssistEvent event) {
		getRecorder().setAssistSession(true);

		if (event.assistant == null) {
			return;
		}

		AssistCommand.AssistType assistType = (event.assistant.getClass()
				.getCanonicalName().indexOf("QuickAssist") != -1) ? AssistCommand.AssistType.QUICK_ASSIST
				: AssistCommand.AssistType.CONTENT_ASSIST;

		getRecorder().recordCommand(
				new AssistCommand(assistType, AssistCommand.StartEndType.START,
						event.isAutoActivated, null));
	}

	public void assistSessionEnded(ContentAssistEvent event) {
		getRecorder().setAssistSession(false);

		if (event.assistant == null) {
			return;
		}

		AssistCommand.AssistType assistType = (event.assistant.getClass()
				.getCanonicalName().indexOf("QuickAssist") != -1) ? AssistCommand.AssistType.QUICK_ASSIST
				: AssistCommand.AssistType.CONTENT_ASSIST;

		getRecorder().recordCommand(
				new AssistCommand(assistType, AssistCommand.StartEndType.END,
						false, null));
	}

	public void selectionChanged(ICompletionProposal proposal,
			boolean smartToggle) {
		// TODO Auto-generated method stub

	}

}
