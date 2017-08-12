package fluorite.commands;

public class EHStepOverEvent extends EHProgramExecutionEvent {
	public EHStepOverEvent() {
		super();
		
	}
	public EHStepOverEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn);
	}

}
