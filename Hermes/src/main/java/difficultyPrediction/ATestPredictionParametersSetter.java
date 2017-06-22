package difficultyPrediction;

public class ATestPredictionParametersSetter implements PredictionParametersSetter {
//	public static int SEGMENT_LENGTH = 25;
	private static int SEGMENT_LENGTH = 6;
//	public static int START_UP_LAG = 50;
	private static int START_UP_LAG = 5;
	private static int STATUSES_AGGREGATED = 3;
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
