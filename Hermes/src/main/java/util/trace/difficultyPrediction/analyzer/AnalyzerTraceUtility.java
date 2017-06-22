package util.trace.difficultyPrediction.analyzer;



import util.trace.ImplicitKeywordKind;
import util.trace.TraceableInfo;
import util.trace.Tracer;



public class AnalyzerTraceUtility {

	public static void setTracing() {
		Tracer.showInfo(true);
		Tracer.setDisplayThreadName(false); 
		TraceableInfo.setPrintTraceable(true);
		TraceableInfo.setPrintSource(true);
		Tracer.setImplicitPrintKeywordKind(ImplicitKeywordKind.OBJECT_CLASS_NAME);
	
		Tracer.setKeywordPrintStatus(AnalyzerPredictionStopNotification.class, true);
		Tracer.setKeywordPrintStatus(AnalyzerPredictionStopNotification.class, true);
	}

}
