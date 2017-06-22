package difficultyPrediction.metrics;

public class AGenericRatioCalculatorFactory implements RatioCalculatorFactory{

	@Override
	public RatioCalculator createRatioCalculator() {
		return new AGenericRatioCalculator();
	}

}
