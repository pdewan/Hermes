package dayton.ellwanger.helpbutton.exceptionMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ExceptionMatcher {
	private String regex;
	private String language;
	
	public ExceptionMatcher(String regex, String language) {
		this.regex = regex;
		this.language = language;
	}
	
	public List<String> match(String input) {
		Matcher matcher = Pattern.compile(regex).matcher(input);
		List<String> ret = new ArrayList<>();
		while (matcher.find()) {
			ret.add(matcher.group());
		}
		return ret;
	}
	
	public boolean isException(String input) {
		Matcher matcher = Pattern.compile(regex).matcher(input);
		return matcher.find();
	}
	
	public String getException(String exception) {
		return exception;
	}
	
	public String getErrorMsg(String ex) {
		return ex;
	}
	
	public String getLanguage() {
		return language;
	}
}
