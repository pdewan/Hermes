package fluorite.commands;

public class RunCommand extends ProgramExecutionEvent {
	public RunCommand() {
		
	}
	public RunCommand(boolean debug, boolean terminate, String projectName, String className, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn) {
		super(debug, terminate, projectName, className, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn);
	}
	
	public String getCommandType() {
		return "RunCommand";
	}
}
