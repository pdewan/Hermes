package util.trace.workbench;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;

import util.trace.TraceableInfo;
import util.trace.Tracer;

public class PartListenerAdded extends TraceableInfo{
	IPartListener partListener;
	IPartService partService;
	
	public PartListenerAdded(String aMessage, IPartService aPartService, IPartListener aPartListener,  Object aFinder) {
		 super(aMessage, aFinder);
		 partListener = aPartListener;
		 partService = aPartService;
	}
	public IPartListener getPartListener() {
		return partListener;
	}
	
	public IPartService getPartService() {
		return partService;
	}
	
	
    public static String toString(IPartService aPartService, IPartListener aPartListener) {
    	return("(" + 
    				aPartService.toString()
    				+ ", " + aPartListener.toString()
    				+ ")");
    }
    public static PartListenerAdded newCase (String aMessage, IPartService aPartService, IPartListener aPartListener,  Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(PartListenerAdded.class))
//      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(PartListenerAdded.class) + ") " +aMessage);
    	if (shouldInstantiate(PartListenerAdded.class)) {
    	PartListenerAdded retVal = new PartListenerAdded(aMessage, aPartService, aPartListener, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(PartListenerAdded.class, aMessage);


    	return null;
    }
    public static PartListenerAdded newCase (IPartService aPartService, IPartListener aPartListener,  Object aFinder) {
    	String aMessage = toString(aPartService, aPartListener);
    	return newCase(aMessage, aPartService, aPartListener, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aPartListener, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
