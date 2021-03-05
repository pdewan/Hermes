package dayton.ellwanger.helpbutton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWTException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.TextConsole;

import fluorite.model.EHEventRecorder;
import fluorite.recorders.EHConsoleRecorder;

public class ConsoleListener extends EHConsoleRecorder {
	private static ConsoleListener instance;
	List<HelpListener> listeners = new ArrayList<>();

	public ConsoleListener() {
		EHEventRecorder.getInstance().addEclipseEventListener(this);
		ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(this);
	}
	
	public void addListener(HelpListener hl) {
		listeners.add(hl);
	}
	
	public void removeListeners() {
		listeners.clear();
	}
	
	public static ConsoleListener getInstance() {
		if (instance == null) 
			instance = new ConsoleListener();
		return instance;
	}
	
	public void consolesAdded(IConsole[] consoles) {
		for (int i = 0; i < consoles.length; i++)
		{
			if (consoles[i] instanceof TextConsole && !consoles[i].getName().equals("debugRequestHelp"))
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
						/*
						 * Both input and output received here. Can distinguish between them by seeing if we get a new line in
						 * text. Input is received on each charcter, so we will get a series of inputs followed by  new line
						 * Should we leave this to console listener? Need to use offsets to determine the exact text. Dayton's console
						 * listener does not seem to take into account back slashes. Maybe it should listen to this object, which
						 * should notify anyone interested.
						 */
						
						String inputOrOutputUnit = event.getText();
						boolean hasNewLine = inputOrOutputUnit.contains("\n");
						boolean isNewLine = inputOrOutputUnit.equals("\r\n");
						//  end of file condition? cannot duplicate it
						if (!inMiddleOfInputLine && inputOrOutputUnit.isEmpty() ) {
							return;
						}
						/*
						 * If already in middle of input line, then stay in middle of it,
						 * and if does not have new line, then start in middle
						 * One gets an empty string at end of file?
						 */
						if (!inMiddleOfInputLine && 
								!hasNewLine 
//								inputOrOutputUnit.length() == 1) //a println will produce at least a new line and return
								&& !inputOrOutputUnit.isEmpty()) {
							// starting input
							lastInputStartOffset = event.getOffset(); // not sure will use this
							inMiddleOfInputLine = true;
//							lastInputLine.setLength(0);
						}
						if (inMiddleOfInputLine && !isNewLine && inputOrOutputUnit.length() > 1) {
							inMiddleOfInputLine = false; // ignore this spurious input?
							lastInputLine.append(inputOrOutputUnit); // this can continue later after output
							return;
						}
						
						if (inMiddleOfInputLine) {
							if (inputOrOutputUnit.isEmpty() && lastInputLine.length() > 0) {
								lastInputLine.setLength(lastInputLine.length() - 1);
								return;
							} else  {
								lastInputLine.append(inputOrOutputUnit);
								if (hasNewLine) { // or is new line?
									inMiddleOfInputLine = false;
									currentConsoleContents.add(">" + lastInputLine.toString());	
									consoleString.append(">" + lastInputLine.toString());
									lastInputLine.setLength(0);
								}
							}
							return;
						}
						
						
						//distinguish sys.out and sys.err
//						if (exceptionPattern.matcher(event.getText()).find())
//						{
//							currentConsoleContents.add(inputOrOutputUnit);
//							consoleString.append(inputOrOutputUnit);
//							for (HelpListener listener : listeners) {
//								listener.exceptionEvent(inputOrOutputUnit);
//							}
//							return;
//						}
						// this is regular output
						lastConsoleOutput = inputOrOutputUnit;
						currentConsoleContents.add(inputOrOutputUnit);
						consoleString.append(inputOrOutputUnit);
						for (int i = 0; i < listeners.size(); i++) {
							try {
								listeners.get(i).consoleOutput(consoleString.toString());
							} catch (SWTException e) {
								listeners.remove(i);
								i--;
							}
						}
					}
				};
				textConsole.getDocument().addDocumentListener(
						consoleDocumentListener);
			}
		}
	}
}
