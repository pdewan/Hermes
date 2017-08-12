package fluorite.commands;

public class EHStepReturnEvent extends EHProgramExecutionEvent {
	public EHStepReturnEvent() {
		super();
		
	}
	public EHStepReturnEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn);
	}

}
