package analyzer.extension.timerTasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.json.JSONException;
import org.json.JSONObject;
import dayton.ellwanger.hermes.preferences.Preferences;
import fluorite.commands.EHICommand;
import fluorite.model.EHEventRecorder;
import fluorite.util.EHUtilities;

public class LogSender extends TimerTask {
//	private static final String reportURL = "https://us-south.functions.appdomain.cloud/api/v1/web/ORG-UNC-dist-seed-james_dev/cyverse/add-cyverse-log";
	private static final String reportURL=
			"https://us-east-1.aws.data.mongodb-api.com/app/rest-api-vsfoo/endpoint/add_log?db=studies&collection=dewan-eclipse";
	private static final String password = "sYCUBa*shZKU4F-yxHrTk8D7FHo4xbBBV.-BK!-L";
	private static final int timeout = 5000;
	private static final String XML_START1 = "<Events startTimestamp=\"";
	private static final String XML_START2 = "\" logVersion=\"";
	private static final String XML_VERSION = "1.0.0.202008151525";
	private static final String XML_START3 = "\">\r\n";
	private static final String XML_FILE_ENDING = "\r\n</Events>"; 
	private long startTimeStamp = 0;
	private static LogSender instance;
//	private String logFilePath = "";
//	private byte[] machineID; 
//	private String loggedName;
	private int sequence = 0;
	private static final String MACHINE_ID = "machine_id";
	private static final String LOG_ID = "log_id";
//	private static final String USER_ID = "uer_id";
	private static final String SESSION_ID = "session_id";
	private static final String LOG_TYPE = "log_type";
	public static final String COURSE_ID = "course_id";
	public static final String PASSWORD = "password";

	private static final String LOG = "log";
	private static final String JSON = "json";
	private static final String NO_NETWORK = "No Network Interface Found";
	private static final String ECLIPSE = "eclipse";
	private static final String COURSE = "course";
	private static final long FIVE_MIN = 300000;
	private int index = 0;
	private boolean sending = false;
	private boolean scheduled = false;
	private List<EHICommand> commands;
	private String lastPath = "";
	private File lastLogDirectory = null;
	private String lastLogFile = "";
	private long totalLogSizeSent = 0;
	private long totalTimeTaken = 0;
	private long totalSends = 0;
	private static final String TIME_STATISTICS_FILE_NAME = "timeStatistics.csv";
	
	public static LogSender getInstance() {
		if (instance == null) {
			instance = new LogSender();
		}
		return instance;
	}
	
//	void initMachineID() {
//		Enumeration<NetworkInterface> e;
//		machineID = null;
//		try {
//			e = NetworkInterface.getNetworkInterfaces();
//			while(machineID == null && e.hasMoreElements()) {
//				machineID = e.nextElement().getHardwareAddress();
//			}
//		} catch (SocketException e1) {
//			e1.printStackTrace();
//		}
//	}
//	
//	void initLoggedName() {
//		loggedName = LogNameManager.getLoggedName();
//	}
	
	private LogSender() {
//		initMachineID();
//		initLoggedName();

		
//		Enumeration<NetworkInterface> e;
//		machineID = null;
//		try {
//			e = NetworkInterface.getNetworkInterfaces();
//			while(machineID == null && e.hasMoreElements()) {
//				machineID = e.nextElement().getHardwareAddress();
//			}
//		} catch (SocketException e1) {
//			e1.printStackTrace();
//		}
		
		
//		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//			public void run() {
//				lastPath = EHEventRecorder.loggerToFileName.get(Logger.getLogger(EHUtilities.getCurrentProject().getName())).getPath();
//				System.out.println(lastPath);
//			}
//		});
	}
	
	private void appendStatistics(final String aStats)  {
		PrintWriter out = null;
		try {
		if (lastLogDirectory == null) {
			return;
		}
		File aStatsFile = new File(lastLogDirectory, TIME_STATISTICS_FILE_NAME);
		
		
		    out = new PrintWriter(new BufferedWriter(new FileWriter(aStatsFile, true)));
		    out.println(aStats);
		} catch (IOException e) {
		    System.err.println(e);
		} finally {
		    if (out != null) {
		        out.close();
		    }
		}
	}
	protected String getLogIdIncremental(String aLoggedName) {
		return  aLoggedName + ":" + index + ":" + System.currentTimeMillis() + ":" + lastLogFile; // need log to be unique key
//		return  aLoggedName + ":" + lastLogFile; // need log to be unique key

	}
	
	protected String getLogIdSession(String aLoggedName) {
		return  aLoggedName + 
				":" + lastLogFile; // need log to be unique key

	}
	
	protected String getLog(int aFrom,int anEnd) {
		StringBuilder sb = new StringBuilder();
//		for (int anIndex = 0; anIndex < anEnd; anIndex++) {

		for (int anIndex = aFrom; anIndex < anEnd; anIndex++) {
			EHICommand command = commands.get(anIndex);
			sb.append(command.persist());
		}
		index = anEnd;
		return  format(sb.toString());
	}
	
	private synchronized void sendLog (int partNum, String path) {
		long aStartTime = System.currentTimeMillis();
		if (lastPath != path) {
		lastPath = path;
		File aPathFile = new File(lastPath);
		lastLogFile = aPathFile.getName();
		lastLogDirectory = new File(aPathFile.getParent());
		
		}
//		int aFileNameIndex = lastPath.indexOf("Log");
//		if (aFileNameIndex != -1) {
//			lastLogFile = lastPath.substring(aFileNameIndex);
//			lastLogDirectory = lastPath.substring(0, aFileNameIndex);
//		}
		sending = true;
		int start = index;
		try {
			int end = commands.size();
			if (end <= start) {
				return;
			}
			startTimeStamp = commands.get(index).getStartTimestamp();
			JSONObject log = new JSONObject();
			JSONObject report = new JSONObject();
//			report.put(LOG_ID, path); // need log to be unique key
			report.put(SESSION_ID, partNum);
//			if (machineID == null) {
//				report.put(MACHINE_ID, NO_NETWORK);
//			} else {
//				report.put(MACHINE_ID, machineID);
//			}
			
			String aLoggedName = LogNameManager.getLoggedName();
			if (aLoggedName == null) { // this should never happen
				aLoggedName = LogNameManager.getMachineId();
			}
			report.put(MACHINE_ID, aLoggedName);
//			String aLoggedPath = lastLogDirectory.isEmpty()?
//			report.put(LOG_ID, aLoggedName + ":" + index + ":" + System.currentTimeMillis() + ":" + path); // need log to be unique key

			

			
//			report.put(LOG_ID, aLoggedName + ":" + index + ":" + System.currentTimeMillis() + ":" + lastLogFile); // need log to be unique key

			String aLogID = getLogIdIncremental(aLoggedName);
			report.put(LOG_ID, aLogID); // need log to be unique key

			
			
//			if (loggedName == null) {
//				report.put(MACHINE_ID, NO_NETWORK);
//			} else {
//				report.put(MACHINE_ID, loggedName);
//			}
			
			report.put(LOG_TYPE, ECLIPSE);
			report.put(COURSE_ID, EditorsUI.getPreferenceStore().getString(COURSE));
			report.put(PASSWORD, password);
//			report.put(LOG, log);
			report.put(LOG, log); 
			
			String aLog = getLog(start, end); // get from last end position

//			StringBuilder sb = new StringBuilder();
//			for (; index < end; index++) {
//				EHICommand command = commands.get(index);
//				sb.append(command.persist());
//			}
//			log.put(JSON, format(sb.toString()));
			log.put(JSON, aLog);
			JSONObject response = post(report, reportURL);
			if(response == null) {
				index = start;
			}
			long anEndTime = System.currentTimeMillis();
			long aTimeTaken = anEndTime - aStartTime;
			int  aLogLength = aLog.length();
			totalSends++;
			totalLogSizeSent += aLogLength;
			totalTimeTaken += aTimeTaken;
//			System.out.println(aLogID + " " + aLogLength + " " + aTimeTaken);
			
		} catch (Exception e) {
			e.printStackTrace();
			index = start;
		} finally {
			sending = false;
		}
	
	}
	
	private String format(String s) {
		s = XML_START1 + startTimeStamp + XML_START2 + XML_VERSION + XML_START3 + s;
		s += XML_FILE_ENDING;
		return s;
	}
	
	public static JSONObject post(JSONObject request, String urlString) {
		BufferedReader reader;
		String line;
		StringBuffer sb = new StringBuffer();
		int status = 500;
		JSONObject body = new JSONObject();
		try {
			body.put("body", request);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Length", (body.toString().length()+2)+"");
			OutputStream os = conn.getOutputStream();
			byte[] input = body.toString().getBytes();
//			System.out.println(body.toString(4));
			os.write(input, 0, input.length);
			os.write("\r\n".getBytes());
			conn.setConnectTimeout(timeout);
			conn.setReadTimeout(timeout);
			
			status = conn.getResponseCode();
			
			if (status > 299) {
				reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				reader.close();
			} else {
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				reader.close();
			}
			conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		try {
			return new JSONObject(sb.toString().substring(sb.toString().indexOf("{")));
		} catch (JSONException e) {
		}
		return null;
	}
	
	public synchronized  void stop() {
//		while (sending);
		String aStatsString = totalSends + "," + totalLogSizeSent + "," + totalTimeTaken;
		appendStatistics(aStatsString);
		sendLog(-1, lastPath);
	}

	public void run() {
		try {
			if (EditorsUI.getPreferenceStore().getBoolean(Preferences.CONNECT_TO_SERVER) &&
				!PlatformUI.getWorkbench().getDisplay().isDisposed()) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						String path;
						try {
							 path = EHEventRecorder.loggerToFileName.get(Logger.getLogger(EHUtilities.getCurrentProject().getName())).getPath();
						} catch (Exception e) {
							return;
						}
//						new Thread(()->{
//							sendLog(sequence, path);
//							sequence++;
//						}).start();
						
						Thread aThread = new Thread(()->{
							sendLog(sequence, path);
							sequence++;
						});
//						int aPriority = Thread.currentThread().getPriority();
//						if (aPriority > 1)
						aThread.setPriority(1); // lowest priority
						aThread.start();
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void schedule(Timer timer, List<EHICommand> commands) {
		if (!scheduled) {
			this.commands = commands;
//			logFilePath = path;
			timer.schedule(this, FIVE_MIN, FIVE_MIN);
			scheduled = true;
		}
	}
}
