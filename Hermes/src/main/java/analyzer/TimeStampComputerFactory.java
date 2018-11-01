package analyzer;

public class TimeStampComputerFactory {
//	static TimeStampComputer singleton =  new AResettingTimeStampComputer();
	static TimeStampComputer singleton =  new ADirectTimeStampComputer();

	public static TimeStampComputer getSingleton() {
		if (singleton == null) {
			singleton = new AResettingTimeStampComputer();
		}
		return singleton;
	}
	public static void setSingleton(TimeStampComputer singleton) {
		TimeStampComputerFactory.singleton = singleton;
	}

}
