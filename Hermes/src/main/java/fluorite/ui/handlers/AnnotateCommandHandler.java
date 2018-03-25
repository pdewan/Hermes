package fluorite.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;

import fluorite.commands.AnnotateCommand;
import fluorite.dialogs.AddAnnotationDialog;
import fluorite.model.EHEventRecorder;

public class AnnotateCommandHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		AddAnnotationDialog dialog = new AddAnnotationDialog(Display
				.getDefault().getActiveShell());
		dialog.open();

		EHEventRecorder.getInstance()
				.recordCommand(
						new AnnotateCommand(dialog.getReturnCode(), dialog
								.getComment()));
		return null;
	}

}
