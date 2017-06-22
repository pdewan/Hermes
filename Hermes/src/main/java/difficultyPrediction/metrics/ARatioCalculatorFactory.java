package difficultyPrediction.metrics;

public class ARatioCalculatorFactory implements RatioCalculatorFactory{

	@Override
	public RatioCalculator createRatioCalculator() {
		return new ATestRatioCalculator();
//		return new APercentageCalculator();
	}

}
