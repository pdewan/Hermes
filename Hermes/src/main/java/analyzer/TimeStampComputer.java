package analyzer;

import fluorite.commands.EHICommand;

public interface TimeStampComputer {
	public long computeTimestamp(EHICommand aCommand);

	void reset();


}