package difficultyPrediction;

public class MediatorFactory {
	static Mediator mediator = new DifficultyRobot("");

	public static Mediator getMediator() {
		return mediator;
	}

	public static void setMediator(Mediator mediator) {
		MediatorFactory.mediator = mediator;
	}

}
