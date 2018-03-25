package fluorite.commands;

public class IncrementalCompilationEvent extends EclipseCommand {

	public IncrementalCompilationEvent(String commandId) {
		super(commandId);
	}
	public IncrementalCompilationEvent(String commandId, int repeatCount) {
		super(commandId, repeatCount);
	}
	

}
