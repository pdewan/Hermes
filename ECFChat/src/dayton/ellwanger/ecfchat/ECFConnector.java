package dayton.ellwanger.ecfchat;

import java.util.Collection;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.ecf.presence.im.IChatMessageEvent;
import org.eclipse.ecf.presence.im.IChatMessageSender;
import org.eclipse.ecf.presence.im.ITypingMessageEvent;
import org.eclipse.ecf.presence.im.ITypingMessageSender;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.presence.ui.MessagesView;
import org.eclipse.ecf.provider.xmpp.XMPPContainer;
import org.eclipse.ecf.ui.actions.AsynchContainerConnectAction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.jivesoftware.smack.XMPPException;

import dayton.ellwanger.hermes.xmpp.ConnectionManager;


public class ECFConnector {

	private ID targetID;
	private IConnectContext connectContext;
	protected MyXMPPContainer container;
	private IChatMessageSender icms;
	private ITypingMessageSender itms;
	private IRosterManager rosterManager;

	
	class ConnectionSetter implements Runnable {
		
		public void run() {
			System.out.println("ECF Connected");
			String instructorID = ConnectionManager.getInstance().getInstructorID();
			try {
				container.getRoster().createEntry(instructorID, instructorID, null);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}

	}
	
	public void connect(String connectID, String password, String hostname) {

		try {
			//this.container = ContainerFactory.getDefault().createContainer("ecf.xmpp.smack");
			String[] hostnameArgs = {hostname};
			this.container = (MyXMPPContainer) ContainerFactory.getDefault().createContainer("dayton.ellwanger.hermes", hostnameArgs);
		} catch (Exception ex) {
			System.out.println("Could not obtain container");
			ex.printStackTrace();
		}

		connectContext = ConnectContextFactory.createPasswordConnectContext(password);
		try {
			targetID = IDFactory.getDefault().createID(container.getConnectNamespace(), connectID);
		} catch (Exception e) {
			System.out.println("Error creating targetID");
			e.printStackTrace();
		}

		final IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) container
				.getAdapter(IPresenceContainerAdapter.class);
		final IChatManager icm = adapter.getChatManager();
		icms = icm.getChatMessageSender();
		itms = icm.getTypingMessageSender();

		rosterManager = adapter.getRosterManager();

		icm.addMessageListener(new IIMMessageListener() {
			public void handleMessageEvent(IIMMessageEvent e) {
				if (e instanceof IChatMessageEvent) {
					displayMessage((IChatMessageEvent) e);
				} else if (e instanceof ITypingMessageEvent) {
					displayTypingNotification((ITypingMessageEvent) e);
				}
			}
		});

		// Connect
		new AsynchContainerConnectAction(container, targetID, connectContext, null,
				new ConnectionSetter()).run();

	}
	
	public void disconnect() {
		container.disconnect();
	}

	void displayMessage(IChatMessageEvent e) {
		final IChatMessage message = e.getChatMessage();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessagesView view = (MessagesView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.findView(MessagesView.VIEW_ID);
				if (view != null) {
					final IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) view.getSite()
							.getAdapter(IWorkbenchSiteProgressService.class);
					view.openTab(icms, itms, targetID, message.getFromID(), message.getFromID().getName());
					view.showMessage(message);
					service.warnOfContentChange();
				} else {
					try {
						final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						view = (MessagesView) page.showView(MessagesView.VIEW_ID, null, IWorkbenchPage.VIEW_CREATE);
						if (!page.isPartVisible(view)) {
							final IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) view.getSite()
									.getAdapter(IWorkbenchSiteProgressService.class);
							service.warnOfContentChange();
						}
						view.openTab(icms, itms, targetID, message.getFromID(), message.getFromID().getName());
						view.showMessage(message);
					} catch (final PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void displayTypingNotification(final ITypingMessageEvent e) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				final MessagesView view = (MessagesView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.findView(MessagesView.VIEW_ID);
				if (view != null)
					view.displayTypingNotification(e);
			}
		});
	}

	public IRosterEntry getEntryForUsername(String username) {
		Collection<IRosterItem> rosterItems = (Collection<IRosterItem>) rosterManager.getRoster().getItems();
		for(IRosterItem r : rosterItems) {
			if(r instanceof IRosterEntry) {
				String name = ((IRosterEntry) r).getName();
				if(name.equals(username)) {
					return ((IRosterEntry) r);
				}
			}
		}
		return null;
	}

}
