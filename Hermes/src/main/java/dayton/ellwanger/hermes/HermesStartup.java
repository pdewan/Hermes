package dayton.ellwanger.hermes;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.editors.text.EditorsUI;

import dayton.ellwanger.hermes.preferences.Preferences;
import dayton.ellwanger.hermes.xmpp.ConnectionManager;
import util.trace.difficultyPrediction.DifficultyPredictionTraceUtility;
import util.trace.difficultyPrediction.analyzer.AnalyzerTraceUtility;
import util.trace.difficultyPrediction.notification.DifficultyNotificationTraceUtility;
//import util.trace.hermes.helpbutton.HelpPluginTraceUtility;
//import util.trace.hermes.timetracker.TimeTrackerTraceUtility;
//import util.trace.hermes.workspacelistener.WorkspaceListenerTraceUtility;
//import util.trace.messagebus.clients.MessageBusClientsTraceUtility;
import util.trace.hermes.helpbutton.HelpPluginTraceUtility;
import util.trace.hermes.timetracker.TimeTrackerTraceUtility;
import util.trace.hermes.workspacelistener.WorkspaceListenerTraceUtility;
import util.trace.messagebus.clients.MessageBusClientsTraceUtility;
import util.trace.plugin.PluginEarlyStarted;
import util.trace.recorder.FlouriteRecordingTraceUtility;

public class HermesStartup implements IStartup {
	public void tracePlugins() {
		FlouriteRecordingTraceUtility.setTracing();
//		DifficultyPredictionTraceUtility.setTracing();		
//		DifficultyNotificationTraceUtility.setTracing();
//		HelpPluginTraceUtility.setTracing();
//		AnalyzerTraceUtility.setTracing();		
////		TimeTrackerTraceUtility.setTracing(); // its startup sets the tracing
//		WorkspaceListenerTraceUtility.setTracing();
//		MessageBusClientsTraceUtility.setTracing();
	}
	@Override
	public void earlyStartup() {
		ExtensionManager.earlyStartExtensions();
		if(EditorsUI.getPreferenceStore().getBoolean(Preferences.AUTOLOGIN)) {
			ConnectionManager.getInstance().doConnect();
		}
		tracePlugins();
		
		PluginEarlyStarted.newCase(this);

		
	}

}
