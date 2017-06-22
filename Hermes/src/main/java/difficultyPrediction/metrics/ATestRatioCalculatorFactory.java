package difficultyPrediction.metrics;

public class ATestRatioCalculatorFactory implements RatioCalculatorFactory{

	@Override
	public RatioCalculator createRatioCalculator() {
		return new ATestRatioCalculator();
	}

}
