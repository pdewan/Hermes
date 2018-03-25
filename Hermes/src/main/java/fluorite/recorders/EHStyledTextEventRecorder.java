package fluorite.recorders;

import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

import fluorite.commands.EHICommand;
import fluorite.commands.MouseWheelCommand;
import fluorite.commands.MoveCaretCommand;
import fluorite.commands.SelectTextCommand;
import fluorite.plugin.EHActivator;
import fluorite.preferences.Initializer;
import fluorite.util.EHUtilities;

public class EHStyledTextEventRecorder extends EHBaseRecorder implements Listener {

	private static EHStyledTextEventRecorder instance;

	public static EHStyledTextEventRecorder getInstance() {
		if (instance == null) {
			instance = new EHStyledTextEventRecorder();
		}

		return instance;
	}

	private EHStyledTextEventRecorder() {
		super();
	}

	@Override
	public void addListeners(IEditorPart editor) {
		final StyledText styledText = EHUtilities.getStyledText(editor);
		if (styledText == null)
			return;

		styledText.getDisplay().asyncExec(new Runnable() {
			public void run() {
				EHStyledTextEventRecorder styledTextEventRecorder = getInstance();
				styledText.addListener(SWT.KeyDown, styledTextEventRecorder);
				styledText.addListener(SWT.KeyUp, styledTextEventRecorder);
				styledText.addListener(SWT.MouseDown, styledTextEventRecorder);
				styledText.addListener(SWT.MouseUp, styledTextEventRecorder);
				styledText.addListener(SWT.MouseVerticalWheel,
						styledTextEventRecorder);
			}
		});
	}

	@Override
	public void removeListeners(IEditorPart editor) {
		try {
			StyledText styledText = EHUtilities.getStyledText(editor);

			if (styledText != null) {
				styledText.removeListener(SWT.KeyDown, this);
				styledText.removeListener(SWT.KeyUp, this);
				styledText.removeListener(SWT.MouseDown, this);
				styledText.removeListener(SWT.MouseUp, this);
				styledText.removeListener(SWT.MouseVerticalWheel, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isBound(Event event) {
		IBindingService bindingService = (IBindingService) PlatformUI
				.getWorkbench().getAdapter(IBindingService.class);
		KeyStroke k = KeyStroke.getInstance(event.stateMask, event.keyCode);
		if (k == null)
			return false;
		Binding b = bindingService.getPerfectMatch(KeySequence.getInstance(k));
		return (b != null);
	}

	public void handleEvent(Event event) {
		getRecorder().updateIncrementalFindMode();
		switch (event.type) {
		case SWT.KeyDown: {
			if (isBound(event))
				return;

			if (getRecorder().isAssistSession()) {
				return;
			}

			EHICommand command = EHUtilities.getCommandForKeyEvent(event);

			if (!getRecorder().isIncrementalFindMode()) {
				if (command != null) {
					getRecorder().recordCommand(command);
				}
			}
			break;
		}

		// case SWT.MouseDown:
		case SWT.MouseUp: {
			IEditorPart editor = EHUtilities.getActiveEditor();
			StyledText styledText = EHUtilities.getStyledText(editor);
			ISourceViewer viewer = EHUtilities.getSourceViewer(editor);
			if (styledText == null || viewer == null)
				break;

			if ((styledText.getSelection().x != styledText.getSelection().y)
					&& (styledText.getSelection().x != getRecorder()
							.getLastSelectionStart() || styledText
							.getSelection().y != getRecorder()
							.getLastSelectionEnd())) {
				EHICommand command = new SelectTextCommand(
						styledText.getSelection().x,
						styledText.getSelection().y,
						styledText.getCaretOffset());
				getRecorder().recordCommand(command);
			} else if (getRecorder().getLastCaretOffset() != styledText
					.getCaretOffset()) {
				EHICommand command = new MoveCaretCommand(
						styledText.getCaretOffset(),
						viewer.getSelectedRange().x);
				getRecorder().recordCommand(command);
			}

			break;
		}

		case SWT.MouseVerticalWheel: {
			if (EHActivator.getDefault().getPreferenceStore()
					.getBoolean(Initializer.Pref_LogMouseWheel) == false) {
				break;
			}

			getRecorder().recordCommand(new MouseWheelCommand(event.count));
		}
		}
	}
}
