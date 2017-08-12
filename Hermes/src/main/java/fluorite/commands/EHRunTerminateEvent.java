package fluorite.commands;

public class EHRunTerminateEvent extends EHTerminateEvent {
	public EHRunTerminateEvent() {
		
	}
	public EHRunTerminateEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn);
	}

}
