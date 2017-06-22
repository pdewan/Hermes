package fluorite.viewpart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import fluorite.commands.EHAbstractCommand;
import fluorite.commands.EHICommandIndexListener;

public class IDViewPart extends ViewPart implements EHICommandIndexListener {

	private Label label;

	@Override
	public void createPartControl(Composite parent) {
		this.label = new Label(parent, SWT.CENTER);
		this.label.setText(Integer.toString(EHAbstractCommand
				.getCurrentCommandID()));

		EHAbstractCommand.addCommandIndexListener(this);
	}

	@Override
	public void dispose() {
		super.dispose();

		EHAbstractCommand.removeCommandIndexListener(this);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void setID(int id) {
		this.label.setText(Integer.toString(id));
	}

	public void commandIndexIncreased(final int currentIndex) {
		// This should be always run in the UI thread.
		// If not, SWTException is thrown. (e.g., when the RunCommand is
		// executed)
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				setID(currentIndex);
			}
		});
	}

}
