package fluorite.commands;

public class DebugRunEvent extends ProgramExecutionEvent {
	public DebugRunEvent() {
		super();
		
	}
	public DebugRunEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn, int aNumEvents) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn, aNumEvents);
	}

}
