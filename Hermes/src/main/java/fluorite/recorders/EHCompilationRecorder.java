package fluorite.recorders;

import org.eclipse.ui.IEditorPart;

import edu.cmu.scs.fluorite.commands.ICommand;
import fluorite.commands.EHCompilationCommand;
import fluorite.commands.EHICommand;

public class EHCompilationRecorder extends EHBaseRecorder  {

private static EHCompilationRecorder instance = null;
	
	public static EHCompilationRecorder getInstance() {
		if (instance == null) {
			instance = new EHCompilationRecorder();
		}

		return instance;
	}
	public void record(EHICommand command) 

//	public void record(EHCompilationCommand command)
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


