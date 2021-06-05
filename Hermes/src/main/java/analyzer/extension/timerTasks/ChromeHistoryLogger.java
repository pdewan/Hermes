package analyzer.extension.timerTasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;

import config.HelperConfigurationManagerFactory;
import dayton.ellwanger.hermes.preferences.Preferences;
import difficultyPrediction.web.WebFeatures;
import difficultyPrediction.web.chrome.AChromeHistoryAccessor;
import difficultyPrediction.web.chrome.AWebFeatures;
import difficultyPrediction.web.chrome.PageVisit;
import fluorite.util.EHUtilities;

public class ChromeHistoryLogger extends TimerTask {
	private static ChromeHistoryLogger instance;
	private boolean scheduled = false;
	private static final long FIVE_MIN = 300000L;
	private SimpleDateFormat fileNameDF = new SimpleDateFormat("M.d.yyyy HH.mm.ss a");
	private SimpleDateFormat recordDF = new SimpleDateFormat("M/d/yyyy HH:mm:ss a");
	private long lastTimeStamp = 0;
	private long startTimestamp = 0;
	private boolean sending = false;
	
	public static ChromeHistoryLogger getInstance() {
		if (instance == null) {
			return new ChromeHistoryLogger();
		}
		return instance;
	}
	
	public void run() {
		try {
			if (EditorsUI.getPreferenceStore().getBoolean(Preferences.CONNECT_TO_SERVER) &&
				!PlatformUI.getWorkbench().getDisplay().isDisposed()) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						String path = getCurrentProjectPath();
						new Thread(()->{
							log(path);
						}).start();
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void log(String path) {
		if (path.isEmpty()) {
			return;
		}
		try {
			sending = true;
			AChromeHistoryAccessor.setTerms(
					HelperConfigurationManagerFactory.getSingleton().getTechnicalTerms(),
					HelperConfigurationManagerFactory.getSingleton().getNonTechnicalTerms());
			long aTime = lastTimeStamp;
			long aTime2 = System.currentTimeMillis();
			WebFeatures aWebFetaures = new AWebFeatures();	
			aWebFetaures.setUnixStartTime(aTime);
			aWebFetaures.setElapsedTime(aTime2-aTime);

			AChromeHistoryAccessor.processURLs(aWebFetaures);
			if (aWebFetaures.getNumPagesVisited() > 0) {
				try {
					StringBuilder sb = new StringBuilder();
					for (PageVisit aPageVisit:aWebFetaures.getPageVisits()) {
						sb.append(getPageVisitString(aPageVisit));
					}
					File logFile = getChromeLogFile(path);
					BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
					bw.append(sb.toString());
					bw.close();
					lastTimeStamp = aTime2;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sending = false;
		}
	}
	
	private String getPageVisitString(PageVisit aPageVisit) {
		return recordDF.format(new Date(aPageVisit.unixTime)) + "\t" 
			 + aPageVisit.title + "\t" + aPageVisit.url + "\n";
	}
	
	private File getChromeLogFile(String path) {
		File logFile = new File(path, "Logs"+File.separator+"Browser");
		System.out.println(logFile.getPath());
		if (!logFile.exists()) {
			logFile.mkdirs();
		}
		logFile = new File(logFile, getWebLogName());
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return logFile;
	}
	
	private String getWebLogName() {
		return "histexp_" + fileNameDF.format(new Date(startTimestamp)) + ".txt";
	}
	
	public void schedule(Timer timer, long startTimestamp) {
		if (!scheduled) {
			lastTimeStamp = startTimestamp;
			this.startTimestamp = startTimestamp;
			timer.schedule(this, FIVE_MIN, FIVE_MIN);
//			timer.schedule(this, 15000,15000);
			scheduled = true;
		}
	}
	
	private String getCurrentProjectPath(){
		IProject currentProject = EHUtilities.getCurrentProject();
		if (currentProject == null) {
			return "";
		}
		return currentProject.getLocation().toOSString();
	}
	
	public void stop() {
		while (sending);
	}
}
