package fluorite.commands;

public class StepReturnEvent extends ProgramExecutionEvent {
	public StepReturnEvent() {
		super();
		
	}
	public StepReturnEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn);
	}

}
