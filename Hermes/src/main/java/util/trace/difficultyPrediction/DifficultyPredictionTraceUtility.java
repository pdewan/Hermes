package util.trace.difficultyPrediction;

import util.trace.ImplicitKeywordKind;
import util.trace.MessagePrefixKind;
import util.trace.Traceable;
import util.trace.TraceableInfo;
import util.trace.Tracer;
import util.trace.workbench.PartActivated;
import util.trace.workbench.PartOpened;


public class DifficultyPredictionTraceUtility {
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

//		Tracer.setKeywordPrintStatus(PluginEarlyStarted.class, true);
//		Tracer.setKeywordPrintStatus(PluginStarted.class, true);
//		Tracer.setKeywordPrintStatus(PluginStopped.class, true);
//		Tracer.setKeywordPrintStatus(MacroRecordingStarted.class, true);
//		Tracer.setKeywordPrintStatus(ExcludedCommand.class, true);
//		Tracer.setKeywordPrintStatus(NewMacroCommand.class, true);
//		Tracer.setKeywordPrintStatus(RecordedCommandsCleared.class, true);
//		Tracer.setKeywordPrintStatus(HelpViewCreated.class, true);

//		Tracer.setKeywordPrintStatus(StatusAggregationStarted.class, true);
//		Tracer.setKeywordPrintStatus(LogFileCreated.class, true);
//		Tracer.setKeywordPrintStatus(MacroCommandsLogBegin.class, true);
//		Tracer.setKeywordPrintStatus(MacroCommandsLogEnd.class, true);
//		Tracer.setKeywordPrintStatus(HelpViewCreated.class, true);
//		Tracer.setKeywordPrintStatus(PluginThreadCreated.class, true);
//		Tracer.setKeywordPrintStatus(NewFileSnapshot.class, true);
		Tracer.setKeywordPrintStatus(PartActivated.class, true);
		Tracer.setKeywordPrintStatus(PartOpened.class, true);
		Tracer.setKeywordPrintStatus(PredictionChanged.class, true);
		Tracer.setKeywordPrintStatus(AggregatePredictionChanged.class, true);
		Tracer.setKeywordPrintStatus(CommandIgnoredBecauseQueueFull.class, true);
		
		Tracer.setKeywordPrintStatus(AddedCommandToPredictionQueue.class, true);
		Tracer.setKeywordPrintStatus(AggregatePredictionChanged.class, true);
		Tracer.setKeywordPrintStatus(CommandIgnoredBecauseQueueFull.class, true);
		Tracer.setKeywordPrintStatus(NewCommand.class, true);
		Tracer.setKeywordPrintStatus(NewEventSegmentAggregation.class, true);
		Tracer.setKeywordPrintStatus(NewExtractedFeatures.class, true);
		Tracer.setKeywordPrintStatus(NewExtractedStatusInformation.class, true);
		Tracer.setKeywordPrintStatus(NewPrediction.class, true);
		Tracer.setKeywordPrintStatus(PredictionChanged.class, true);
		Tracer.setKeywordPrintStatus(PredictionValueToStatus.class, true);
		Tracer.setKeywordPrintStatus(RemovedCommandFromPredictionQueue.class, true);
		Tracer.setKeywordPrintStatus(StatusAggregationStarted.class, true);












		





	}

}
