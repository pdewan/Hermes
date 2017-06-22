package difficultyPrediction;

import fluorite.commands.EHICommand;
import util.trace.difficultyPrediction.notification.NotifiedCommandToListener;

public class ANewCommandNotifier  implements Runnable{
	PluginEventListener listener;
	EHICommand command;
	public ANewCommandNotifier(PluginEventListener aListener, EHICommand aCommand) {		
			listener = aListener;
			command = aCommand;		
	}
	public void run() {
		NotifiedCommandToListener.newCase(command, listener, this);
		listener.newCommand(command);
	}
	
	

}
