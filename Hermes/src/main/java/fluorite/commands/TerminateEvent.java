package fluorite.commands;

public class TerminateEvent extends ProgramExecutionEvent {
	public TerminateEvent() {
		
	}
	public TerminateEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn);
	}

}
