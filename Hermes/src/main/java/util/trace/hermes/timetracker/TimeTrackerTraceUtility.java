package util.trace.hermes.timetracker;



import util.trace.ImplicitKeywordKind;
import util.trace.TraceableInfo;
import util.trace.Tracer;
//import util.trace.xmpp.XMPPPacketReceived;
//import util.trace.xmpp.XMPPPacketSent;


public class TimeTrackerTraceUtility {

	public static void setTracing() {
		Tracer.showInfo(true);
		Tracer.setDisplayThreadName(false); 
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
