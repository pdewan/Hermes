package util.trace.hermes.helpbutton;



import util.trace.ImplicitKeywordKind;
import util.trace.TraceableInfo;
import util.trace.Tracer;



public class HelpPluginTraceUtility {

	public static void setTracing() {
		Tracer.showInfo(true);
		Tracer.setDisplayThreadName(false); 
		TraceableInfo.setPrintTraceable(true);
		TraceableInfo.setPrintSource(true);
		Tracer.setImplicitPrintKeywordKind(ImplicitKeywordKind.OBJECT_CLASS_NAME);
		
	
		Tracer.setKeywordPrintStatus(DifficultyUpdateForwardedToConnectionManager.class, true);
		Tracer.setKeywordPrintStatus(HelpInformationForwardedToConnectionManager.class, true);
	}

}
