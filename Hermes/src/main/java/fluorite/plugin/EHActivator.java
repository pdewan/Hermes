package fluorite.plugin;

import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;

import fluorite.model.EHEventRecorder;
import fluorite.preferences.Initializer;
import util.trace.plugin.PluginStarted;

/**
 * The activator class controls the plug-in life cycle
 */
public class EHActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "EHEventLogger";

	// The shared instance
	private static EHActivator plugin;

	/**
	 * The constructor
	 */
	public EHActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
//		System.setProperty("user.timezone", "America/New_York");
//		System.setProperty("user.timezone", "America/Los_Angeles");

		if (EHActivator.getDefault().getPreferenceStore().getBoolean(Initializer.Pref_EnableEventLogger)) {
			System.out.println ("Read preference store, initializing fluorite");

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
			System.out.println ("Set system etc in start");

			uiJob.setSystem(true);
			uiJob.setUser(false);
			uiJob.schedule();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		EHEventRecorder.getInstance().stop();

		plugin = null;

		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static EHActivator getDefault() {
		if (plugin == null) {
			plugin = new EHActivator();
		}
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public Image getImage(String key) {
		ImageRegistry imageRegistry = getImageRegistry();
		Image image = imageRegistry.get(key);
		if (image == null) {
			ImageDescriptor descriptor = getImageDescriptor(key);
			if (descriptor != null) {
				imageRegistry.put(key, descriptor);
				image = imageRegistry.get(key);
			}

		}
		return image;
	}
	public static URL getInstallURL() {
		EHActivator anActivator = EHActivator.getDefault();

//		URL anInstallURL = null;
//		IPluginDescriptor aDescriptor = anActivator.getDescriptor();
//		if (aDescriptor != null) {
//			anInstallURL = aDescriptor.getInstallURL();
//		} else {
//			anInstallURL = anActivator.getBundle().getEntry("/");
//		}
	URL anInstallURL = anActivator.getBundle().getEntry("/");
	return anInstallURL;
	}
}
