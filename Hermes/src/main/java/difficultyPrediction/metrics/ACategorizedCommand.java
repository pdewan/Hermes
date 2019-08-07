package difficultyPrediction.metrics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import util.annotations.Position;


public class ACategorizedCommand implements CategorizedCommand {
	CommandName command = null;
	Set<CommandCategory> features = new HashSet<>();
//	public ACategorizedCommand(CommandName command) {
//		this.command = command;
////		this.feature = feature;
//	}
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
	public Set<CommandCategory> getCategories() {
		return features;
	}
	@Override
	public void setCategories(Set<CommandCategory> newVal) {
		this.features = newVal;
	}
	
	
	
}
