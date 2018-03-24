package bus.uigen.pluginproxy;

import java.awt.Container;
import java.beans.PropertyChangeSupport;

import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import util.models.AListenableVector;
import util.models.ListenableVector;

public class HermesObjectEditorProxy {
	public static Object edit(Object anObject) {
		return ObjectEditor.edit(anObject);
	}
	//move this to ObjectEditor at some point
	public static Object edit (Object anObject, int aFrameWidth, int aFrameHeight) {
		OEFrame anOEFrame = ObjectEditor.edit(anObject);
		anOEFrame.setSize(aFrameWidth, aFrameHeight);		
		return anOEFrame.getFrame().getPhysicalComponent();
	}
	public static void loadClasses() {
		ListenableVector.class.isAssignableFrom(HermesObjectEditorProxy.class);
		AListenableVector.class.isAssignableFrom(HermesObjectEditorProxy.class);
	}
	public static Object edit(Object anObject, 
			int aFrameWidth,
			int aFrameHeight,
			int aCloseOperation,
			boolean anAutoExitEnabled) {
		OEFrame oeFrame = ObjectEditor.edit(anObject);
		oeFrame.setSize(aFrameWidth, aFrameHeight);
		oeFrame.getFrame().setDefaultCloseOperation(aCloseOperation);
		oeFrame.setAutoExitEnabled(anAutoExitEnabled);
		return oeFrame.getFrame().getPhysicalComponent();
	}
	public static Object editInMainContainer (Object anObject, Container aContainer) {
		 return ObjectEditor.editInMainContainer(anObject, aContainer).getFrame().getPhysicalComponent();

	}
	public static void suppressNotifications(PropertyChangeSupport aPropertyChangeSupport) {
		aPropertyChangeSupport.firePropertyChange(OEFrame.SUPPRESS_NOTIFICATION_PROCESSING, false, true);
	}
	public static void unsuppressNotifications(PropertyChangeSupport aPropertyChangeSupport) {
		aPropertyChangeSupport.firePropertyChange(OEFrame.SUPPRESS_NOTIFICATION_PROCESSING, true, false);
	}
//	static {
//		loadClasses();
//	}

}
