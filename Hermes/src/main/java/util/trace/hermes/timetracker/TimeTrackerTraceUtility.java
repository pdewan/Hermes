package util.trace.hermes.timetracker;



import config.HelperConfigurationManagerFactory;
import util.trace.ImplicitKeywordKind;
import util.trace.Traceable;
import util.trace.TraceableInfo;
import util.trace.Tracer;
//import util.trace.xmpp.XMPPPacketReceived;
//import util.trace.xmpp.XMPPPacketSent;


public class TimeTrackerTraceUtility {

	public static void setTracing() {
		Boolean aShowInfo = HelperConfigurationManagerFactory.getSingleton().isTraceInfo();
		Tracer.showInfo(aShowInfo);
		Traceable.setDefaultInstantiate(HelperConfigurationManagerFactory.getSingleton().isInstantiateTracerClass());

//		Tracer.showInfo(true);
		Tracer.setDisplayThreadName(true); 
		TraceableInfo.setPrintTraceable(true);
		TraceableInfo.setPrintSource(true);
		Tracer.setImplicitPrintKeywordKind(ImplicitKeywordKind.OBJECT_CLASS_NAME);	
		Tracer.setKeywordPrintStatus(ActivityDetected.class, true);
		Tracer.setKeywordPrintStatus(ActivitySessionStarted.class, true);
		Tracer.setKeywordPrintStatus(ActivitySessionEnded.class, true);
		Tracer.setKeywordPrintStatus(EclipeSessionStarted.class, true);
		Tracer.setKeywordPrintStatus(EclipeSessionEnded.class, true);
		Tracer.setKeywordPrintStatus(IdleCycleDetected.class, true);
		Tracer.setKeywordPrintStatus(TimeWorkedForwardedToConnectionManager.class, true);

	}

}
