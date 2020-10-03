package dayton.ellwanger.helpbutton.exceptionMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ExceptionMatcher {
	private String regex;
	
	public ExceptionMatcher(String regex) {
		this.regex = regex;
	}
	
	public List<String> match(String input) {
		Matcher matcher = Pattern.compile(regex).matcher(input);
		List<String> ret = new ArrayList<>();
		while (matcher.find()) {
			ret.add(matcher.group());
		}
		return ret;
	}
}
