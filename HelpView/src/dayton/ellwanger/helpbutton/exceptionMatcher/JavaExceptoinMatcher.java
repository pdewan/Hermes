package dayton.ellwanger.helpbutton.exceptionMatcher;

public class JavaExceptoinMatcher extends ExceptionMatcher{
	private final static String ERROR = ".+Exception[^\\n]?(((?!Exception)[\\s\\S])*)(\\s+at .++)+(\\nCaused by:.*\\n(\\s+at .++)+)?";
	private static JavaExceptoinMatcher instance;

	private JavaExceptoinMatcher() {
		super(ERROR);
	}
	
	public static JavaExceptoinMatcher getInstance() {
		if (instance == null) {
			instance = new JavaExceptoinMatcher();
		}
		return instance;
	}
}
