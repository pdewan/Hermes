package dayton.ellwanger.helpbutton.exceptionMatcher;

import java.util.ArrayList;
import java.util.List;

public class SMLExceptionMatcher extends ExceptionMatcher{
	private final static String ERROR = ".*Error:[^\\n]+\\n((\\s+)[^\\n]*\\n?)*";
	private static SMLExceptionMatcher instance;
	private static String LANGUAGE = "SML";

	private SMLExceptionMatcher() {
		super(ERROR, LANGUAGE);
	}
	
	public static SMLExceptionMatcher getInstance() {
		if (instance == null) {
			instance = new SMLExceptionMatcher();
		}
		return instance;
	}
	
	public String getErrorMsg(String ex) {
		return ex.substring(ex.indexOf("\n")+1).replace("\r\n", "");
	}
	
	public String getException(String exception) {
		if (exception.contains("\n")) return exception.substring(exception.indexOf("Error:")+7, exception.indexOf("\n"));
		else return exception.substring(exception.indexOf("Error:")+7);
	}
	
	public List<String> match(String input) {
		if (input.contains("Error:")) {
			return super.match(input);
		}
		return new ArrayList<>();
	}
}
