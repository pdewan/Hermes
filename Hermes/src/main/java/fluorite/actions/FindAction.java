package fluorite.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

import fluorite.commands.EHFindCommand;

public class FindAction extends Action {
	public FindAction() {
	}

	@Override
	public void run() {
		EHFindCommand fc = new EHFindCommand();
		fc.configureNew(Display.getDefault().getActiveShell());
	}

}
