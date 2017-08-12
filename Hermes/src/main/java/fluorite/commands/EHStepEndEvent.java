package fluorite.commands;

public class EHStepEndEvent extends EHProgramExecutionEvent {
	public EHStepEndEvent() {
		super();
		
	}
	public EHStepEndEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn);
	}

}
