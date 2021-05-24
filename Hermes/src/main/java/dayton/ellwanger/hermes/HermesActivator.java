package dayton.ellwanger.hermes;

import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;

import analyzer.extension.timerTasks.LogSender;
import dayton.ellwanger.hermes.preferences.Preferences;
import fluorite.model.EHEventRecorder;
import fluorite.plugin.EHActivator;
import fluorite.preferences.Initializer;
import util.trace.Tracer;
import util.trace.plugin.PluginStarted;

/**
 * The activator class controls the plug-in life cycle
 */
public class HermesActivator extends AbstractUIPlugin {

	// The plug-in ID
//	public static final String PLUGIN_ID = "Hermes"; //$NON-NLS-1$
	public static final String PLUGIN_ID = "dayton.ellwanger.Hermes"; //$NON-NLS-1$

	// The shared instance
	private static HermesActivator plugin;
	
	/**
	 * The constructor
	 */
	public HermesActivator() {
	}
	
	public void ehStart(BundleContext context) throws Exception {
		if (EHActivator.getDefault().getPreferenceStore().getBoolean(Initializer.Pref_EnableEventLogger)) {
			Tracer.info (this, "Read preference store, initializing fluorite");

			// NOTE: This event recording must start after the workbench is
			// fully loaded.
			// So, run this in UIJob so it runs after the workbench loads.
			UIJob uiJob = new UIJob("Fluorite Initialization") {

				@Override
				public IStatus runInUIThread(IProgressMonitor arg0) {
					PluginStarted.newCase(this);
					EHEventRecorder.getInstance().start();
					return Status.OK_STATUS;
				}

			};
			Tracer.info (this, "Set system etc in start");

			uiJob.setSystem(true);
			uiJob.setUser(false);
			uiJob.schedule();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		ehStart(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		//Ken's code to send commands to server before closing Eclipse 
		if (EditorsUI.getPreferenceStore().getBoolean(Preferences.CONNECT_TO_SERVER)) {
			LogSender.getInstance().stop();
		}
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static HermesActivator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	public static URL getInstallURL() {
	   HermesActivator anActivator = HermesActivator.getDefault();
	   URL anInstallURL = anActivator.getBundle().getEntry("/");
	   return anInstallURL;
	}
}
