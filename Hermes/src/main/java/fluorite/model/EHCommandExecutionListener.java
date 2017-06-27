package fluorite.model;

import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.model.CommandExecutionListener;
import edu.cmu.scs.fluorite.model.DocumentChangeListener;
import fluorite.commands.EHBaseDocumentChangeEvent;
import fluorite.commands.EHFileOpenCommand;

public interface EHCommandExecutionListener
extends CommandExecutionListener
//extends DocumentChangeListener
{
//	public void commandExecuted(ICommand aCommand);

	

}
