package difficultyPrediction.metrics;

public interface FeatureToCommands {

	public abstract String getFeature();

	public abstract String getCommands();

	public abstract void setCommands(String commands);

}