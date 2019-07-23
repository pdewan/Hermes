package util.trace.hermes.helpbutton;



import config.HelperConfigurationManagerFactory;
import util.trace.ImplicitKeywordKind;
import util.trace.TraceableInfo;
import util.trace.Tracer;



public class HelpPluginTraceUtility {

	public static void setTracing() {
		Boolean aShowInfo = HelperConfigurationManagerFactory.getSingleton().isTraceInfo();
		Tracer.showInfo(aShowInfo);
		Tracer.setDisplayThreadName(false); 
		TraceableInfo.setPrintTraceable(true);
		TraceableInfo.setPrintSource(true);
		Tracer.setImplicitPrintKeywordKind(ImplicitKeywordKind.OBJECT_CLASS_NAME);
		
	
		Tracer.setKeywordPrintStatus(DifficultyUpdateForwardedToConnectionManager.class, true);
		Tracer.setKeywordPrintStatus(HelpInformationForwardedToConnectionManager.class, true);
	}

}
