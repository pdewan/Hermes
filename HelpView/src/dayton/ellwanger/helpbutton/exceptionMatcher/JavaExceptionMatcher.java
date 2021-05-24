package dayton.ellwanger.helpbutton.exceptionMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaExceptionMatcher extends ExceptionMatcher{
	private final static String ERROR = ".+Exception[^\\n]?(((?!Exception)[\\s\\S])*)(\\s+at .++)+(\\nCaused by:.*\\n(\\s+at .++)+)?";
	private static JavaExceptionMatcher instance;
	private static String LANGUAGE = "java";

	private JavaExceptionMatcher() {
		super(ERROR, LANGUAGE);
	}
	
	public static JavaExceptionMatcher getInstance() {
		if (instance == null) {
			instance = new JavaExceptionMatcher();
		}
		return instance;
	}
	
	public String getErrorMsg(String ex) {
		return ex.substring(ex.indexOf("\n\t") + 2).replace("\r\n", "");
	}
	
	public String getException(String exception) {
		Matcher m = Pattern.compile("\\S(\\.\\S*)?\\S*Exception").matcher(exception);
		m.find();
		String ex = m.group();
		return ex;
	}
	
	public List<String> match(String input) {
		if (input.contains("Exception")) {
			return super.match(input);
		}
		return new ArrayList<>();
	}
}
