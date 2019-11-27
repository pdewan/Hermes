package util.trace.recorder;



import config.HelperConfigurationManagerFactory;
import fluorite.model.EHEventRecorder;
import util.trace.ImplicitKeywordKind;
import util.trace.Traceable;
import util.trace.TraceableInfo;
import util.trace.Tracer;



public class FlouriteRecordingTraceUtility {

	public static void setTracing() {
		Boolean aShowInfo = HelperConfigurationManagerFactory.getSingleton().isTraceInfo();
		Tracer.showInfo(aShowInfo);
		Traceable.setDefaultInstantiate(HelperConfigurationManagerFactory.getSingleton().isInstantiateTracerClass());
		EHEventRecorder.setAsyncFireEvent(HelperConfigurationManagerFactory.getSingleton().isAsyncFireEvent());
//		Tracer.showInfo(true);
		Tracer.setDisplayThreadName(true); 
		TraceableInfo.setPrintTraceable(true);
		TraceableInfo.setPrintSource(true);
		Tracer.setImplicitPrintKeywordKind(ImplicitKeywordKind.OBJECT_CLASS_NAME);
		
		Tracer.setKeywordPrintStatus(AddedCommandToBuffers.class, true);
		Tracer.setKeywordPrintStatus(CombinedCommand.class, true);
		Tracer.setKeywordPrintStatus(DocumentChangeCommandExecuted.class, true);
		Tracer.setKeywordPrintStatus(NonDocumentChangeCommandExecuted.class, true);
		Tracer.setKeywordPrintStatus(ExcludedCommand.class, true);
		Tracer.setKeywordPrintStatus(ForwardedCommandToPredictor.class, true);
		Tracer.setKeywordPrintStatus(IgnoredCommandAsRecordingSuspended.class, true);
		Tracer.setKeywordPrintStatus(CommandLoggingInitiated.class, true);
		Tracer.setKeywordPrintStatus(LogHandlerBound.class, true);
		Tracer.setKeywordPrintStatus(LogFileCreated.class, true);
		Tracer.setKeywordPrintStatus(PendingCommandsLogBegin.class, true);
		Tracer.setKeywordPrintStatus(PendingCommandsLogEnd.class, true);

		Tracer.setKeywordPrintStatus(ParsedCommand.class, true);

		Tracer.setKeywordPrintStatus(MacroRecordingStarted.class, true);
		Tracer.setKeywordPrintStatus(NewFileSnapshot.class, true);
		Tracer.setKeywordPrintStatus(NewMacroCommand.class, true);
		Tracer.setKeywordPrintStatus(ReceivedCommand.class, true);

		Tracer.setKeywordPrintStatus(RecordedCommandsCleared.class, true);	
		Tracer.setKeywordPrintStatus(RemovedCommandFromBuffers.class, true);	
		
		Tracer.setKeywordPrintStatus(UnparsedCommand.class, true);



	}

}
