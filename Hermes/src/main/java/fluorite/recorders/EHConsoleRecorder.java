package fluorite.recorders;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.TextConsole;

import fluorite.commands.EHExceptionCommand;

public class EHConsoleRecorder extends EHBaseRecorder implements IConsoleListener {

	
	private static EHConsoleRecorder instance = null;

	public static EHConsoleRecorder getInstance() {
		if (instance == null) {
			instance = new EHConsoleRecorder();
		}

		return instance;
	}
	@Override
	public void consolesAdded(IConsole[] consoles) {
		for (int i = 0; i < consoles.length; i++)
		{
			if (consoles[i] instanceof TextConsole)
			{
				TextConsole textConsole = (TextConsole) consoles[i];
				IDocumentListener consoleDocumentListener = new IDocumentListener()
				{
					@Override
					public void documentAboutToBeChanged(
							DocumentEvent event)
					{
					}

					@Override
					public void documentChanged(DocumentEvent event)
					{

						if (event.getText().toLowerCase().contains("exception"))
						{
							getRecorder().recordCommand(new EHExceptionCommand(event.getText()));
						}
					}
				};
				textConsole.getDocument().addDocumentListener(
						consoleDocumentListener);
			}
		}
		
	}

	@Override
	public void consolesRemoved(IConsole[] consoles) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListeners(IEditorPart editor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListeners(IEditorPart editor) {
		// TODO Auto-generated method stub
		
	}

}
