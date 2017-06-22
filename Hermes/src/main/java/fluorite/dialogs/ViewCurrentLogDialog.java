package fluorite.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import fluorite.model.EHEventRecorder;
import fluorite.model.EHEvents;

public class ViewCurrentLogDialog extends Dialog {
	private EHEvents mEvents;
	private StyledText mExportText;

	public ViewCurrentLogDialog(Shell shell, EHEvents events) {
		super(shell);
		mEvents = events;
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("View Current Log");
		Composite comp = new Composite(parent, SWT.None);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));

		mExportText = new StyledText(comp, SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 600;
		gd.widthHint = 800;
		mExportText.setLayoutData(gd);

		String eventsXML = EHEventRecorder.persistMacro(mEvents);
		mExportText.setText(eventsXML);

		return comp;
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}
}
