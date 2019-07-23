package util.trace.hermes.workspacelistener;



import config.HelperConfigurationManagerFactory;
import util.trace.ImplicitKeywordKind;
import util.trace.TraceableInfo;
import util.trace.Tracer;



public class WorkspaceListenerTraceUtility {

	public static void setTracing() {
//		Tracer.showInfo(true);
		Boolean aShowInfo = HelperConfigurationManagerFactory.getSingleton().isTraceInfo();
		Tracer.showInfo(aShowInfo);
		Tracer.setDisplayThreadName(false); 
		TraceableInfo.setPrintTraceable(true);
		TraceableInfo.setPrintSource(true);
		Tracer.setImplicitPrintKeywordKind(ImplicitKeywordKind.OBJECT_CLASS_NAME);
		
	
		Tracer.setKeywordPrintStatus(FileForwardedToConnectionManager.class, true);

	}

}
