package difficultyPrediction.metrics;

import java.util.ArrayList;
import java.util.List;

import util.annotations.Position;


public class ACategorizedCommand implements CategorizedCommand {
	CommandName command = null;
	List<CommandCategory> features = new ArrayList();
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
	public List<CommandCategory> getCategories() {
		return features;
	}
	@Override
	public void setCategories(List<CommandCategory> newVal) {
		this.features = newVal;
	}
	
	
	
}
