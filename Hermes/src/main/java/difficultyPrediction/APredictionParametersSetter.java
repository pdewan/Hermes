package difficultyPrediction;

public class APredictionParametersSetter implements PredictionParametersSetter {
	private static int SEGMENT_LENGTH = 25;
	private static int START_UP_LAG = 50;
	private static int STATUSES_AGGREGATED = 5;
	public void setPredictionParameters() {
		PredictionParameters predictionParameters = APredictionParameters.getInstance();
		predictionParameters.setStartupLag(START_UP_LAG);
		predictionParameters.setSegmentLength(SEGMENT_LENGTH);
		predictionParameters.setStatusAggregated(STATUSES_AGGREGATED);
		
	}
	@Override
	public int getSegmentLength() {
		return SEGMENT_LENGTH;
	}
	@Override
	public int getStartupLag() {
		return START_UP_LAG;
		
	}
	
	

}
