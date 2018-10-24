package remoteEclipseHelper; 

import org.eclipse.ui.IStartup;


public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		System.out.println("************************** Remote Eclipse Helper Starting **************************");
		new RemoteEHRecordListener();
	}

}
