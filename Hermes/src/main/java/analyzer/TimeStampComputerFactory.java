package analyzer;

public class TimeStampComputerFactory {
	static TimeStampComputer singleton =  new ATimeStampComputer();
	public static TimeStampComputer getSingleton() {
		if (singleton == null) {
			singleton = new ATimeStampComputer();
		}
		return singleton;
	}
	public static void setSingleton(TimeStampComputer singleton) {
		TimeStampComputerFactory.singleton = singleton;
	}

}
