package workspacelistener;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import util.trace.hermes.workspacelistener.WorkspaceListenerTraceUtility;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "WorkspaceListener"; //$NON-NLS-1$

	private static Activator plugin;

	public Activator() {}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		WorkspaceListenerTraceUtility.setTracing();
//		MessageBusClientsTraceUtility.setTracing();
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	public static Activator getDefault() {
		return plugin;
	}

}
