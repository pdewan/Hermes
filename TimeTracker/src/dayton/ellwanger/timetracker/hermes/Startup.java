package dayton.ellwanger.timetracker.hermes;

import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		//System.out.println("Time Tracker Startup");
		new FluoriteListener();
	}

}
