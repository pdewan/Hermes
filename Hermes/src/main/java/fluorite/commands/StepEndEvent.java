package fluorite.commands;

public class StepEndEvent extends ProgramExecutionEvent {
	public StepEndEvent() {
		super();
		
	}
	public StepEndEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn);
	}

}
