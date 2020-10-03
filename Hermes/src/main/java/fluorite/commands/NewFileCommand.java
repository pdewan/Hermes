package fluorite.commands;

public class NewFileCommand extends EclipseCommand{
	public NewFileCommand(String commandId) {
		super(commandId);
	}
	public NewFileCommand(String commandId, int repeatCount) {
		super(commandId, repeatCount);
	}
	
	public String getCommandType(){
		return "NewFileCommand";
	}
}
