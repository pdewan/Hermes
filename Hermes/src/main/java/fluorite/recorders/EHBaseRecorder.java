package fluorite.recorders;

import org.eclipse.ui.IEditorPart;

import fluorite.model.EHEventRecorder;
/*
 * Cannot extend BaseRecorder as it refers to EHEventRecorder which cannot be made a subclass of
 * EventRecorder with a private constructir
 */
public abstract class EHBaseRecorder 
//extends BaseRecorder
{

	protected EHBaseRecorder() {
		mRecorder = EHEventRecorder.getInstance();
	}

	public abstract void addListeners(IEditorPart editor);

	public abstract void removeListeners(IEditorPart editor);

	private EHEventRecorder mRecorder;

	protected EHEventRecorder getRecorder() {
		return (EHEventRecorder) mRecorder;
	}

}
