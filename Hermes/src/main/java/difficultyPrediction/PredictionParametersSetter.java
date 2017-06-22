package difficultyPrediction;

public interface PredictionParametersSetter {
	public void setPredictionParameters();
	
	public int getSegmentLength();
	
	public int getStartupLag();
}
