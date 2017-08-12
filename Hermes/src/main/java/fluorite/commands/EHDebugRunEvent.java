package fluorite.commands;

public class EHDebugRunEvent extends EHProgramExecutionEvent {
	public EHDebugRunEvent() {
		super();
		
	}
	public EHDebugRunEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn, int aNumEvents) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn, aNumEvents);
	}

}
