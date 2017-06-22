package difficultyPrediction.metrics;

import java.util.HashMap;
import java.util.Map;

import util.annotations.Position;
import fluorite.commands.EHICommand;


public class ACategorizedCommand implements CategorizedCommand {
	CommandName command = null;
	CommandCategory feature = CommandCategory.OTHER;
	public ACategorizedCommand(CommandName command, CommandCategory feature) {
		this.command = command;
		this.feature = feature;
	}
	public ACategorizedCommand(CommandName command) {
		this.command = command;
	}
	
	@Override
	@Position(0)
	public CommandName getCommand() {
		return command;
	}
	
	@Override
	@Position(1)
	public CommandCategory getCategory() {
		return feature;
	}
	@Override
	public void setCategory(CommandCategory feature) {
		this.feature = feature;
	}
	
	
	
}
