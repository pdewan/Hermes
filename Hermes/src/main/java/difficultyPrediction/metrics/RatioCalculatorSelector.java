package difficultyPrediction.metrics;


public class RatioCalculatorSelector {
	static RatioCalculatorFactory factory = new ARatioCalculatorFactory();
	static RatioCalculator ratioCalculator;
//	static RatioFeaturesFactory factory;

	public static RatioCalculatorFactory getFactory() {
		return factory;
	}
	public static void setFactory(RatioCalculatorFactory newVal) {
		factory = newVal;
	}
	public static  RatioCalculator getRatioCalculator() {
		if (ratioCalculator == null) {
			ratioCalculator = factory.createRatioCalculator();
		}
		return ratioCalculator;
	}

}
