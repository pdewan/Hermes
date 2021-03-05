package dayton.ellwanger.helpbutton.exceptionMatcher;

public class SMLExceptionMatcher extends ExceptionMatcher{
	private final static String ERROR = ".*Error:[^\\n]+\\n((\\s+)[^\\n]*\\n?)*";
	private static SMLExceptionMatcher instance;

	private SMLExceptionMatcher() {
		super(ERROR);
	}
	
	public static SMLExceptionMatcher getInstance() {
		if (instance == null) {
			instance = new SMLExceptionMatcher();
		}
		return instance;
	}
}
