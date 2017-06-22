package difficultyPrediction.metrics;

import fluorite.commands.EHEclipseCommand;
import fluorite.commands.EHICommand;

public class ACommandDescriptor {
	String kind;	
	String regex;
	public ACommandDescriptor(String kind, String regex) {
		super();
		this.kind = kind;
		this.regex = regex;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
//	public boolean matches(ICommand aCommand) {
//		boolean result = aCommand.getCommandType().equals(kind);
//		if (regex == null) {
//			return result;
//		}
//		if (! (aCommand.getCommandType().equals("EclipseCommand") &&
//				aCommand instanceof EclipseCommand)) {
//			
//		}
//				
//						
//	}
}
