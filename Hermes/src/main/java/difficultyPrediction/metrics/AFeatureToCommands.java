package difficultyPrediction.metrics;

import util.annotations.Visible;

public class AFeatureToCommands implements FeatureToCommands {
	String feature;
	String commands = "";
	
	public AFeatureToCommands(String feature) {
		super();
		this.feature = feature;
	}
	@Override
	@Visible(false)
	public String getFeature() {
		return feature;
	}
	
	@Override
	public String getCommands() {
		return commands;
	}
	@Override
	public void setCommands(String commands) {
		this.commands = commands;
	}
	

}
