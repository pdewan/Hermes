package dayton.ellwanger.helpbutton;

import org.eclipse.core.resources.IProject;

public interface HelpListener {
	public void requestHelp(String email, String course, String assign, String errorType, String errorMessage, String problem, String term, int difficulty, String helpText, String requestID, String output, String language, IProject project);
//	public void difficultyUpdate(int difficulty);
	public void getHelp(String email, String course, String assign, String errorType, String errorMessage, String problem, String term, String requestID, String output, String language);
	public void exceptionEvent(String output);
	public void consoleOutput(String output);
	public void setExceptionMatcher(String language);
}
