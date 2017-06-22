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
		Tracer.setKeywordPrintStatus(TimeWorkedForwardedToConnectionManager.class, true);

	}

}
