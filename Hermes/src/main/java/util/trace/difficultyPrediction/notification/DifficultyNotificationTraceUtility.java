package util.trace.difficultyPrediction.notification;

import util.trace.ImplicitKeywordKind;
import util.trace.MessagePrefixKind;
import util.trace.Traceable;
import util.trace.TraceableInfo;
import util.trace.Tracer;
import util.trace.workbench.PartActivated;
import util.trace.workbench.PartOpened;


public class DifficultyNotificationTraceUtility {
//	public static void trace() {
//
//		Tracer.showInfo(true);
//
//
//		setTraceParameters();
//		setPrintStatus();		
//	}
	
	public static void setTraceParameters() {
		TraceableInfo.setPrintSource(true);
		Traceable.setPrintTime(false);
		Traceable.setPrintThread(false);
		Tracer.setMessagePrefixKind(MessagePrefixKind.FULL_CLASS_NAME);
		Tracer.setImplicitPrintKeywordKind(ImplicitKeywordKind.OBJECT_CLASS_NAME);
		Traceable.setDefaultInstantiate(false);
	}
	
	public static void setTracing() {
		Tracer.showInfo(true);

		setTraceParameters();
		setPrintStatus();		
	}
	
	public static void setPrintStatus() {


		Tracer.setKeywordPrintStatus(NotifiedCommandToListener.class, true);
		Tracer.setKeywordPrintStatus(NotifiedManualStatusToListener.class, true);
		Tracer.setKeywordPrintStatus(NotifiedRatiosToListener.class, true);
		Tracer.setKeywordPrintStatus(NotifiedReplayedStatusToListener.class, true);
		Tracer.setKeywordPrintStatus(NotifiedStatusToListener.class, true);
		Tracer.setKeywordPrintStatus(RegisteredCommandListener.class, true);
		Tracer.setKeywordPrintStatus(RegisteredRatioListener.class, true);
		Tracer.setKeywordPrintStatus(RegisteredStatusListener.class, true);


		
		












		





	}

}
