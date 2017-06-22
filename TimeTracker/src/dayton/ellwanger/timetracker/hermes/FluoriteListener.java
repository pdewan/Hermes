package dayton.ellwanger.timetracker.hermes;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import dayton.ellwanger.hermes.xmpp.ConnectionManager;
import edu.cmu.scs.fluorite.commands.FileOpenCommand;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.commands.document.DocChange;
import edu.cmu.scs.fluorite.model.CommandExecutionListener;
import edu.cmu.scs.fluorite.model.DocumentChangeListener;
import edu.cmu.scs.fluorite.model.EventRecorder;
import util.trace.hermes.timetracker.TimeWorkedForwardedToConnectionManager;

public class FluoriteListener implements DocumentChangeListener, CommandExecutionListener {

	IdleTimer idleTimer;
	
	public FluoriteListener() {
		idleTimer = new IdleTimer();
		EventRecorder eventRecorder = EventRecorder.getInstance();
		eventRecorder.addCommandExecutionListener(this);
		eventRecorder.addDocumentChangeListener(this);
	}

	@Override
	public void commandExecuted(ICommand arg0) {
		idleTimer.recordActivity();
	}

	@Override
	public void activeFileChanged(FileOpenCommand arg0) {}

	@Override
	public void documentChangeAmended(DocChange arg0, DocChange arg1) {}

	@Override
	public void documentChangeFinalized(DocChange arg0) {}

	@Override
	public void documentChangeUpdated(DocChange arg0) {}

	@Override
	public void documentChanged(DocChange arg0) {
		idleTimer.recordActivity();
	}
	
	private void sessionEnded(Date startDate, Date endDate) {
		System.out.println("Session ended");
		double sessionLength = ((endDate.getTime() - startDate.getTime()) / (60.0 * 1000));
		File sessionFile = new File(System.getProperty("user.home") + "/sessions.txt");
		try {
			sessionFile.createNewFile();
		} catch (Exception ex) {ex.printStackTrace();}
		try {
			PrintWriter output = new PrintWriter(new FileWriter(sessionFile, true));
			output.append("Session start: " + startDate);
			output.append("\nSession end: " + endDate);
			output.append("\nSession duration: " + sessionLength + "\n\n");
			output.close();
		} catch (Exception ex) {ex.printStackTrace();}
		sendSession(startDate, endDate);
	}
	
	public void sendSession(Date startDate, Date endDate) {
		if(ConnectionManager.getInstance() != null) {
			JSONObject messageData = new JSONObject();
			try {
				messageData.put("startDate", startDate.toString());
				messageData.put("endDate", endDate.toString());
				JSONArray tags = new JSONArray();
				tags.put("TIME_TRACKER");
				messageData.put("tags", tags);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			TimeWorkedForwardedToConnectionManager.newCase(this, messageData.toString());
//			JSONObjectForwardedToConnectionManager.newCase(this, messageData.toString());
			ConnectionManager.getInstance().sendMessage(messageData);
		}
	}
	
	class IdleTimer implements Runnable {
		
		private boolean inSession;
		private boolean idleLastMinute;
		
		public IdleTimer() {
			inSession = false;
		}
		
		public void run() {
			System.out.println("Session started");
			Date startDate = new Date();
			Date endDate = new Date();
			int idleMinutes = 0;
			while(idleMinutes < 10) {
				idleLastMinute = true;
				try {
					Thread.sleep(/*60 * */ 1000);
				} catch (Exception ex) {}
				if(idleLastMinute) {
					idleMinutes++;
				} else {
					endDate = new Date();
					idleMinutes = 0;
				}
			}
			inSession = false;
			sessionEnded(startDate, endDate);
		}
		
		public void recordActivity() {
			if(inSession) {
				idleLastMinute = false;
			} else {
				inSession = true;
				(new Thread(this)).start();
			}
		}
		
	}
	
}
