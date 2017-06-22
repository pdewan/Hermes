package analyzer;

public class TimeStampComputerFactory {
	static TimeStampComputer singleton;
	public static TimeStampComputer getSingleton() {
		if (singleton == null) {
			singleton = new ATimeStampComputer();
		}
		return singleton;
	}

}
