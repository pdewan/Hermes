package dayton.ellwanger.helpbutton;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;

import dayton.ellwanger.helpbutton.exceptionMatcher.ExceptionMatcher;

public interface HelpListener {

	public JSONObject requestHelp(String email, String course, String assign, String errorType, String errorMessage, String problem, String term, int difficulty, String helpText, String requestID, String output, String language) throws IOException;
//	public void difficultyUpdate(int difficulty);
	public JSONObject getHelp(String email, String course, String assign, String errorType, String errorMessage, String problem, String term, String requestID, String output, String language) throws IOException;
	public void exceptionEvent(String output);
	public void consoleOutput(String output);
	public void setExceptionMatcher(String language);
}
