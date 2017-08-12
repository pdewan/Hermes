package fluorite.commands;

public class EHTerminateEvent extends EHProgramExecutionEvent {
	public EHTerminateEvent() {
		
	}
	public EHTerminateEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn);
	}

}
