package dayton.ellwanger.timetracker.hermes;

import org.eclipse.ui.IStartup;

import util.trace.hermes.timetracker.TimeTrackerTraceUtility;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		//System.out.println("Time Tracker Startup");
		TimeTrackerTraceUtility.setTracing();
		new FluoriteListener();
	}

}
