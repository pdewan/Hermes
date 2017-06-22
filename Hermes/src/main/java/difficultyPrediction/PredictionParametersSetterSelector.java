package difficultyPrediction;

public class PredictionParametersSetterSelector {
	static PredictionParametersSetter Parameter_Setter = new APredictionParametersSetter();
	public static PredictionParametersSetter getSingleton() {
		return Parameter_Setter;
	}

	public static void setSingleton(PredictionParametersSetter newVal) {
		Parameter_Setter = newVal;
	}
	
}
