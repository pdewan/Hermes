package fluorite.recorders;

import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableListener;
import org.eclipse.ui.IEditorPart;

public class EHVariableValueRecorder extends EHBaseRecorder implements
IValueVariableListener {
	
	

	private static EHVariableValueRecorder instance = null;

	public static EHVariableValueRecorder getInstance() {
		if (instance == null) {
			instance = new EHVariableValueRecorder();
		}

		return instance;
	}

	private EHVariableValueRecorder() {
		super();
	}

	@Override
	public void addListeners(IEditorPart editor) {
		// Do nothing.
	}

	@Override
	public void removeListeners(IEditorPart editor) {
		// Do nothing.
	}

	@Override
	public void variablesAdded(IValueVariable[] variables) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void variablesRemoved(IValueVariable[] variables) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void variablesChanged(IValueVariable[] variables) {
		// TODO Auto-generated method stub
		
	}

	

}
