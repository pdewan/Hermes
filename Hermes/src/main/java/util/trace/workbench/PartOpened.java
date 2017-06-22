package util.trace.workbench;


import org.eclipse.ui.IWorkbenchPart;

import util.trace.TraceableInfo;
import util.trace.Tracer;


public class PartOpened extends TraceableInfo {
	IWorkbenchPart workbenchPart;
	public PartOpened(String aMessage, IWorkbenchPart aIWorkbenchPart, Object aFinder) {
		super(aMessage, aFinder);
	}
	
	
	public IWorkbenchPart getIWorkbenchPart() {
		return workbenchPart;
	}


	public static PartOpened newCase(String aMessage, IWorkbenchPart aIWorkbenchPart, Object aFinder) {
		String anInfo = Tracer.infoPrintBody(PartOpened.class) + ") " + aMessage;
//		if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(PartOpened.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println(anInfo);
		if (shouldInstantiate(PartOpened.class)) {
		PartOpened retVal = new PartOpened(aMessage, aIWorkbenchPart, aFinder);
		retVal.announce();
		return retVal;
		}
		Tracer.info(aFinder, aMessage);
		Tracer.info(PartOpened.class, aMessage);

		return null;
	}
	public static String toString(IWorkbenchPart anIWorkbenchPart) {
		return anIWorkbenchPart.toString();
	}

	public static PartOpened newCase(IWorkbenchPart aIWorkbenchPart, Object aFinder) {
		String aMessage = toString(aIWorkbenchPart);
		return newCase(aMessage, aIWorkbenchPart, aFinder);
//		MacroRecordingStarted retVal = new MacroRecordingStarted("", aFinder);
//		retVal.announce();
//		return retVal;
	}

	
}
