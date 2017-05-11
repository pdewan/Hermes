package dayton.ellwanger.hermes.ui;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import dayton.ellwanger.hermes.ExtensionManager;

public class StatusView extends ViewPart {
	
	private List<ConnectButtonListener> connectListeners;
	private Button connectButton;

	public StatusView() {
		connectListeners = new LinkedList<ConnectButtonListener>();
	}
	
	public void addConnectListener(ConnectButtonListener l) {
		connectListeners.add(l);
	}
	
	public void removeConnectListener(ConnectButtonListener l) {
		connectListeners.remove(l);
	}
	
	public void setConnectButtonText(String text) {
		connectButton.setText(text);
		connectButton.pack();
		connectButton.getParent().requestLayout();
	}
	
	@Override
	public void createPartControl(Composite oParent) {
		oParent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		ScrolledComposite scrollParent = new ScrolledComposite(oParent, SWT.V_SCROLL | SWT.H_SCROLL);
		scrollParent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		Composite parent = new Composite(scrollParent, SWT.NONE);
		scrollParent.setContent(parent);
		RowLayout layout = new RowLayout();
		layout.type = SWT.VERTICAL;
		layout.pack = true;
		parent.setLayout(layout);
		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		RowLayout childLayout = new RowLayout();
		childLayout.type = SWT.VERTICAL;
		childLayout.pack = true;
		buttonComposite.setLayout(childLayout);

		connectButton = new Button(buttonComposite, SWT.NONE);
		connectButton.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		connectButton.addSelectionListener(new ConnectButtonHandler());
		
		new StatusViewController(this);
		ExtensionManager.loadSubviews(parent);
		parent.setSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	@Override
	public void setFocus() {}
	
	class ConnectButtonHandler extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			for(ConnectButtonListener l : connectListeners) {
				l.connectButtonPressed();
			}
		}
	}

}
