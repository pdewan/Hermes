package fluorite.commands;

public class StepOverEvent extends ProgramExecutionEvent {
	public StepOverEvent() {
		super();
		
	}
	public StepOverEvent(boolean debug, boolean terminate, String projectName, String className, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn) {
		super(debug, terminate, projectName, className, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn);
	}

}
