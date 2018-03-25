package fluorite.commands;

public class RunTerminateEvent extends TerminateEvent {
	public RunTerminateEvent() {
		
	}
	public RunTerminateEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn);
	}

}
