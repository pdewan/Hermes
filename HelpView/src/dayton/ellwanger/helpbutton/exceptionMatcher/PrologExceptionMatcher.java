package dayton.ellwanger.helpbutton.exceptionMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrologExceptionMatcher extends ExceptionMatcher{
	private final static String ERROR = "(ERROR:[^\\n]++\\n)+(\\^\\s+Call:.*\\n?)?";
	private final static String PRECEDING_LINE = "(?<=\\n)";
	private final static String COMMAND = "((:-)|(\\|))[^\\s_][^\\n\\r]+\\r?\\n";
	private static PrologExceptionMatcher instance;
	private static String LANGUAGE = "prolog";

	private PrologExceptionMatcher() {
		super(ERROR, LANGUAGE);
	}
	
	public static PrologExceptionMatcher getInstance() {
		if (instance == null) {
			instance = new PrologExceptionMatcher();
		}
		return instance;
	}
	
	public List<String> match(String input){
		Matcher matcher = Pattern.compile(COMMAND).matcher(input);
		String lastCommand = "";
		while (matcher.find()) {
			lastCommand = matcher.group();
		}
		if (lastCommand.equals("")) {
			lastCommand = input;
			matcher = Pattern.compile(ERROR).matcher(lastCommand);
		} else {
			lastCommand = input.substring(input.lastIndexOf(lastCommand));
			matcher = Pattern.compile(PRECEDING_LINE+ERROR).matcher(lastCommand);
		}
		List<String> ret = new ArrayList<>();
		while (matcher.find()) {
			ret.add(matcher.group());
		}
		return ret;
	}
	
	public boolean isException(String input){
		Matcher matcher = Pattern.compile(COMMAND).matcher(input);
		String lastCommand = "";
		while (matcher.find()) {
			lastCommand = matcher.group();
		}
		if (lastCommand.equals("")) {
			lastCommand = input;
			matcher = Pattern.compile(ERROR).matcher(lastCommand);
		} else {
			lastCommand = input.substring(input.lastIndexOf(lastCommand));
			matcher = Pattern.compile(PRECEDING_LINE+ERROR).matcher(lastCommand);
		}
		return matcher.find();
	}
	
	public String getErrorMsg(String ex) {
		return ex.substring(ex.indexOf("\n") + 1).replace("\r\n", "");
	}
	
	public String getException(String exception) {
		if (exception.contains("\n")) return exception.substring(exception.indexOf(":")+1, exception.indexOf("\n"));
		else return exception.substring(exception.indexOf(":")+1);
	}
}
