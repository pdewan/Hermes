package util.trace.view.help;

import org.eclipse.swt.widgets.Composite;

import util.trace.Tracer;
import util.trace.view.ViewCreated;


public class HelpViewCreated extends ViewCreated {
	Composite composite;
	public HelpViewCreated(String aMessage, Composite aComposite, Object aFinder) {
		super(aMessage, aComposite, aFinder);
	}
	public static HelpViewCreated newCase(String aMessage, Composite aComposite, Object aFinder) {
		String anInfo = Tracer.infoPrintBody(HelpViewCreated.class) + ") " + aMessage;
//		if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(HelpViewCreated.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println(anInfo);
		if (shouldInstantiate(HelpViewCreated.class)) {
		HelpViewCreated retVal = new HelpViewCreated(aMessage, aComposite, aFinder);
		retVal.announce();
		return retVal;
		}
		Tracer.info(aFinder, aMessage);
		Tracer.info(HelpViewCreated.class, aMessage);

		return null;
	}

	public static HelpViewCreated newCase(Composite aComposite, Object aFinder) {
		String aMessage = aComposite.toString();
		return newCase(aMessage, aComposite, aFinder);
	}

	
}
