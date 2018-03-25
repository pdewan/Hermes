package fluorite.recorders;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IEditorPart;

import fluorite.commands.Delete;
import fluorite.commands.EHICommand;
import fluorite.commands.Insert;
import fluorite.commands.Replace;
import fluorite.plugin.EHActivator;
//import fluorite.plugin.Activator;
import fluorite.preferences.Initializer;
import fluorite.util.EHUtilities;

public class EHDocumentRecorder extends EHBaseRecorder implements IDocumentListener {

	private static EHDocumentRecorder instance = null;

	public static EHDocumentRecorder getInstance() {
		if (instance == null) {
			instance = new EHDocumentRecorder();
		}

		return instance;
	}

	private EHDocumentRecorder() {
		super();
	}

	@Override
	public void addListeners(IEditorPart editor) {
		IDocument document = EHUtilities.getIDocumentForEditor(editor);
		if (document != null) {
			document.addDocumentListener(this);
		}
	}

	@Override
	public void removeListeners(IEditorPart editor) {
		try {
			IDocument document = EHUtilities.getIDocumentForEditor(editor);
			if (document != null) {
				document.removeDocumentListener(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void documentAboutToBeChanged(DocumentEvent event) {
		// DeleteCommand or ReplaceCommand
		if (event.getLength() > 0) {
			IDocument doc = event.getDocument();

			try {
				int startLine = doc.getLineOfOffset(event.getOffset());
				int endLine = doc.getLineOfOffset(event.getOffset()
						+ event.getLength());

				String deletedText = null;
				if (EHActivator.getDefault().getPreferenceStore()
						.getBoolean(Initializer.Pref_LogDeletedText)) {
					deletedText = doc.get(event.getOffset(), event.getLength());
				}

				String insertedText = null;
				if (EHActivator.getDefault().getPreferenceStore()
						.getBoolean(Initializer.Pref_LogInsertedText)) {
					insertedText = event.getText();
				}

				EHICommand command = null;
				if (event.getText().length() > 0) {
					command = new Replace(event.getOffset(), event.getLength(),
							startLine, endLine, event.getText().length(),
							deletedText, insertedText, doc);
				} else {
					command = new Delete(event.getOffset(), event.getLength(),
							startLine, endLine, deletedText, doc);

				}

				if (command != null) {
					getRecorder().recordCommand(command);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void documentChanged(DocumentEvent event) {
		if (!getRecorder().isCurrentlyExecutingCommand()) {
			getRecorder().endIncrementalFindMode();
		}

		// InsertCommand
		if (event.getText().length() > 0 && event.getLength() <= 0) {
			try {
				IDocument doc = event.getDocument();

				String text = null;
				if (EHActivator.getDefault().getPreferenceStore()
						.getBoolean(Initializer.Pref_LogInsertedText)) {
					text = event.getText();
				}

				Insert command = new Insert(event.getOffset(), text, doc);

				getRecorder().recordCommand(command);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
