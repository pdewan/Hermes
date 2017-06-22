package util.trace.workbench;


import org.eclipse.ui.IWorkbenchPart;

import util.trace.TraceableInfo;
import util.trace.Tracer;


public class PartActivated extends TraceableInfo {
	IWorkbenchPart workbenchPart;
	public PartActivated(String aMessage, IWorkbenchPart aIWorkbenchPart, Object aFinder) {
		super(aMessage, aFinder);
	}
	
	
	public IWorkbenchPart getIWorkbenchPart() {
		return workbenchPart;
	}


	public static PartActivated newCase(String aMessage, IWorkbenchPart aIWorkbenchPart, Object aFinder) {
		String anInfo = Tracer.infoPrintBody(PartActivated.class) + ") " + aMessage;
//		if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(PartActivated.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println(anInfo);
		if (shouldInstantiate(PartActivated.class)) {
		PartActivated retVal = new PartActivated(aMessage, aIWorkbenchPart, aFinder);
		retVal.announce();
		return retVal;
		}
		Tracer.info(aFinder, aMessage);
		Tracer.info(PartActivated.class, aMessage);

		return null;
	}
	public static String toString(IWorkbenchPart anIWorkbenchPart) {
		return anIWorkbenchPart.toString();
	}

	public static PartActivated newCase(IWorkbenchPart aIWorkbenchPart, Object aFinder) {
		String aMessage = toString(aIWorkbenchPart);
		return newCase(aMessage, aIWorkbenchPart, aFinder);
//		MacroRecordingStarted retVal = new MacroRecordingStarted("", aFinder);
//		retVal.announce();
//		return retVal;
	}

	
}
