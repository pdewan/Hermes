package difficultyPrediction;

import fluorite.commands.EHICommand;

public interface PluginEventListener {
	void newCommand(final EHICommand newCommand);
	void commandProcessingStarted();
	void commandProcessingStopped();

}
