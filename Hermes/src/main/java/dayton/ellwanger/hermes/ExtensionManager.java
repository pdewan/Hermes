package dayton.ellwanger.hermes;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IStartup;

import dayton.ellwanger.hermes.xmpp.ConnectStateListener;
import dayton.ellwanger.hermes.xmpp.ConnectionManager;
import dayton.ellwanger.hermes.xmpp.MessageListener;


public class ExtensionManager {

	private static final String CONNECTION_LISTENER_ID = "dayton.ellwanger.hermes.connectionListener";
	private static final String MESSAGE_LISTENER_ID = "dayton.ellwanger.hermes.messagelistener";
	private static final String SUBVIEWS_ID = "dayton.ellwanger.subview";
	private static final String EARLY_START_ID = "dayton.ellwanger.hermes.startup";


	private static List<Object> instantiateExtensions(String extensionID) {
		List<Object> objectList = new LinkedList<Object>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] config = registry.getConfigurationElementsFor(extensionID);
		try {
			for (IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");
				objectList.add(o);
			}
		} catch (CoreException ex) {
			System.out.println(ex.getMessage());
		}
		return objectList;
	}

	public static void earlyStartExtensions() {
		List<Object> extensions = instantiateExtensions(EARLY_START_ID);
		for(Object o : extensions) {
			if (o instanceof IStartup) {
				((IStartup) o).earlyStartup();
			}
		}
	}

	public static void loadSubviews(Composite parent) {
		List<Object> subviews = instantiateExtensions(SUBVIEWS_ID);
		for(Object o : subviews) {
			if (o instanceof SubView) {
				Composite separatorComposite = new Composite(parent, SWT.NONE);
				separatorComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				separatorComposite.setLayout(new GridLayout(1, false));
				Label separator = new Label(separatorComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
				separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				Composite childComposite = new Composite(parent, SWT.NONE);
				childComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				((SubView) o).createPartControl(childComposite);
			}
		}
	}

	public static void initiateConnectionListeners() {
		List<Object> listeners = instantiateExtensions(CONNECTION_LISTENER_ID);
		for(Object o : listeners) {
			if (o instanceof ConnectStateListener) {
				addConnectionListener((ConnectStateListener) o);
			}
		}
	}

	private static void addConnectionListener(final ConnectStateListener l) {
		ISafeRunnable runnable = new ISafeRunnable() {
			@Override
			public void handleException(Throwable e) {}
			@Override
			public void run() throws Exception {
				ConnectionManager.getInstance().addStateListener(l);
			}
		};
		SafeRunner.run(runnable);
	}

	public static void initiateMessageListeners() {
		List<Object> listeners = instantiateExtensions(MESSAGE_LISTENER_ID);
		for(Object o : listeners) {
			if (o instanceof MessageListener) {
				addMessageListener((MessageListener) o);
			}
		}
	}

	private static void addMessageListener(final MessageListener l) {
		ISafeRunnable runnable = new ISafeRunnable() {
			@Override
			public void handleException(Throwable e) {}
			@Override
			public void run() throws Exception {
				ConnectionManager.getInstance().addMessageListener(l);
			}
		};
		SafeRunner.run(runnable);
	}

}
