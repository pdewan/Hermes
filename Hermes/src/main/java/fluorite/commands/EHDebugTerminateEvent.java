package fluorite.commands;

public class EHDebugTerminateEvent extends EHProgramExecutionEvent {
	public EHDebugTerminateEvent() {
		
	}
	public EHDebugTerminateEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn, int aNumEvents) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn, aNumEvents);
	}

}
