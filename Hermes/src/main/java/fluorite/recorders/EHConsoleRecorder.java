package fluorite.recorders;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.TextConsole;

import fluorite.commands.ConsoleInput;
import fluorite.commands.ConsoleOutput;
import fluorite.commands.ExceptionCommand;
import fluorite.commands.ProgramExecutionEvent;
import fluorite.commands.RunCommand;
import fluorite.model.EHEventRecorder;
import fluorite.model.EclipseEventListener;

public class EHConsoleRecorder extends EHBaseRecorder implements IConsoleListener, EclipseEventListener {

	
	private static EHConsoleRecorder instance = null;
	protected StringBuffer lastInputLine = new StringBuffer();
	protected boolean inMiddleOfInputLine;
	protected int lastInputStartOffset; // not used currently
	protected List<String> currentConsoleContents = new ArrayList<>();
	protected List<String> previousConsoleContents = new ArrayList<>();
	protected StringBuffer consoleString = new StringBuffer();
	protected String lastConsoleOutput = null;
	private final Pattern javaExceptionPattern = Pattern.compile(".+Exception[^\\n]?(((?!Exception)[\\s\\S])*)(\\s+at .++)+(\\nCaused by:.*\\n(\\s+at .++)+)?");
	private final Pattern prologExceptionPattern = Pattern.compile("(ERROR:[^\\n]++\\n)+(\\^\\s+Call:.*\\n?)?");
	private final Pattern SMLExcpetionPattern = Pattern.compile(".*Error:[^\\n]+\\n((\\s+)[^\\n]*\\n?)*");
	private static final String[] UNCHECKED_OUTPUT = {"I***", "[WARN]", "[INFO]", 
													  "Buffer traced", "ObjectEditor",
													  "###", "Running junit test",
													  "(Client", "(Server", "(Registry",
													  "Steps traced"}; 
	private static final int LINE_LIM = 50;

	public EHConsoleRecorder() {
		EHEventRecorder.getInstance().addEclipseEventListener(this);

	}
	
	public void updateCosoleString() {
		consoleString.setLength(0);
		for (String aString:currentConsoleContents) {
			consoleString.append(aString);
		}
		
	}

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
									getRecorder().recordCommand(new ConsoleInput(lastInputLine.toString()));
									currentConsoleContents.add(">" + lastInputLine.toString());	
									consoleString.append(">" + lastInputLine.toString());
									lastInputLine.setLength(0);

								}
							}
							return;
							
						}
						
						//distinguish sys.out and sys.err
						//Ken's code detect exceptions
						inputOrOutputUnit = removeUncheckedOutput(inputOrOutputUnit);
						if (inputOrOutputUnit.isEmpty()) {
							return;
						}
						if (inputOrOutputUnit.contains("Exception") && javaExceptionPattern.matcher(inputOrOutputUnit).find())
						{
							getRecorder().recordCommand(new ExceptionCommand(inputOrOutputUnit, "java"));
							currentConsoleContents.add(inputOrOutputUnit);
							consoleString.append(inputOrOutputUnit);
							return;
						} else if (prologExceptionPattern.matcher(inputOrOutputUnit).find()) {
							getRecorder().recordCommand(new ExceptionCommand(inputOrOutputUnit, "prolog"));
							currentConsoleContents.add(inputOrOutputUnit);
							consoleString.append(inputOrOutputUnit);
							return;
						} else if (inputOrOutputUnit.contains("Error") && SMLExcpetionPattern.matcher(inputOrOutputUnit).find()) {
							getRecorder().recordCommand(new ExceptionCommand(inputOrOutputUnit, "SML"));
							currentConsoleContents.add(inputOrOutputUnit);
							consoleString.append(inputOrOutputUnit);
						}
						// this is regular output
						getRecorder().recordCommand(new ConsoleOutput(inputOrOutputUnit, lastConsoleOutput));
						lastConsoleOutput = inputOrOutputUnit;
						currentConsoleContents.add(inputOrOutputUnit);
						consoleString.append(inputOrOutputUnit);
					}
					
					private String removeUncheckedOutput(String input) {
						StringBuilder sb = new StringBuilder();
						String[] list = input.split("\\r?\\n");
						int numLines = 0;
						for (String s : list) {
							if (!isUnchecked(s)) {
								sb.append(s+"\r\n");
								numLines++;
								if (numLines >= 50) {
									return sb.toString();
								}
							}
						}
						return sb.toString();
					}
					
					private boolean isUnchecked(String s) {
						for (String s2 : UNCHECKED_OUTPUT) {
							if (s.startsWith(s2)) {
								return true;
							}
						}
						return false;
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

	@Override
	public void eventRecordingStarted(long aStartTimestamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eventRecordingEnded() {
		// TODO Auto-generated method stub
		
	}
	
	protected void newRunCommand() {
		previousConsoleContents.clear();
		previousConsoleContents.addAll(currentConsoleContents);
		currentConsoleContents.clear();
		lastInputLine.setLength(0);
		consoleString.setLength(0);
	}

	@Override
	public void commandExecuted(String aCommandName, long aTimestamp) {
//		if (aCommandName.equals(ProgramExecutionEvent.class.getSimpleName())) {
		if (aCommandName.equals(RunCommand.class.getSimpleName())) {
			newRunCommand();
		}
		
	}

	@Override
	public void documentChanged(String aCommandName, long aTimestamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void documentChangeFinalized(long aTimestamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timestampReset(long aStartTimestamp) {
		// TODO Auto-generated method stub
		
	}

}
