package fluorite.recorders;

import org.eclipse.ui.IEditorPart;

import fluorite.commands.EHCompilationCommand;

public class EHCompilationRecorder extends EHBaseRecorder  {

private static EHCompilationRecorder instance = null;
	
	public static EHCompilationRecorder getInstance() {
		if (instance == null) {
			instance = new EHCompilationRecorder();
		}

		return instance;
	}
	
	public void record(EHCompilationCommand command)
	{
		getRecorder().recordCommand(command);
	}
	
	
	@Override
	public void addListeners(IEditorPart editor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListeners(IEditorPart editor) {
		// TODO Auto-generated method stub
		
	}


}


