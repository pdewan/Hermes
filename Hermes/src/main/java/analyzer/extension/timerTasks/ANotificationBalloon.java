package analyzer.extension.timerTasks;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import dayton.ellwanger.hermes.HermesActivator;
import dayton.ellwanger.hermes.preferences.Preferences;
import fluorite.commands.Delete;
import fluorite.commands.EHICommand;
import fluorite.commands.FileOpenCommand;
import fluorite.commands.Insert;
import fluorite.commands.PauseCommand;
import fluorite.model.EHEventRecorder;
import fluorite.model.EHXMLFormatter;
import util.trace.recorder.LogHandlerBound;

public class ANotificationBalloon extends TimerTask {
	private static ANotificationBalloon instance;
	private int index = 0;
	private static final long FIVE_MIN = 300000L;
	private static final long HOUR = 3600000L;
	private HashSet<String> fileEdited;
	private HashSet<String> projectWorked;
	private boolean scheduled = false;
	private List<EHICommand> commands;
	private String currentFile = "";
	private String currentProject = "";
//	private TrayIcon trayIcon;
	private File logFile;
	private long startTimestamp = 0;
//	private final static String ICON_PATH = "icons/spy.png";

	
	private ToolTip toolTip;
	
	public static ANotificationBalloon getInstance() {
		if (instance == null) {
			instance = new ANotificationBalloon();
		}
		return instance;
	}
	
	public ANotificationBalloon() {
		fileEdited = new HashSet<>();
		projectWorked = new HashSet<>();
		
		try {
			logFile = new File(System.getProperty("user.home"), "helper-config" + File.separator + "NotificationLog.txt");
			if (!logFile.getParentFile().exists()) {
				logFile.getParentFile().mkdirs();
			}
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logFile = null;
		}
//		if (SystemTray.isSupported()) {
//			System.out.println("system tray not supported");
//			SystemTray tray = SystemTray.getSystemTray();
//			try {
//				URL url = new URL(HermesActivator.getInstallURL(), ICON_PATH);
//				Image image = Toolkit.getDefaultToolkit().createImage(url);
//				trayIcon = new TrayIcon(image);
//				System.out.println("tray icon created");
//				trayIcon.setImageAutoSize(true);
//				tray.add(trayIcon);
//			} catch (IOException e1) {
//				System.out.println("failed to read image");
//				e1.printStackTrace();
//			} catch (AWTException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	public void showNotification() {
		if (toolTip == null) {
			toolTip = new ToolTip(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), SWT.BALLOON
					| SWT.ICON_INFORMATION);
			toolTip.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					if (logFile != null && logFile.exists()) {
						try {
							Desktop.getDesktop().open(logFile.getParentFile());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
		}
		if (!toolTip.isDisposed()) {
			toolTip.setMessage(getNotification());
			EHEventRecorder.getTrayItem().setToolTip(toolTip);
			toolTip.setVisible(true);
		}
		
//		if (trayIcon != null) {
//			trayIcon.displayMessage("", getNotification(), MessageType.NONE);
//		}
	}
	
	public String getNotification() {
		StringBuilder sb = new StringBuilder();
		int insert = 0;
		int delete = 0;
		int start = index;
		int end = commands.size();
		long startTime = 0;
		long pauseTime = 0;
		long lastEdit = 0;
		
		for (; index < end; index++) {
			EHICommand command = commands.get(index);
			if (index == start && command instanceof PauseCommand) {
				continue;
			}
			if (startTime == 0) {
				startTime = command.getStartTimestamp() + command.getTimestamp();
			}
			if (command instanceof Insert) {
				long currentEdit = command.getStartTimestamp() + command.getTimestamp();
				if (lastEdit != 0 && currentEdit - lastEdit > FIVE_MIN) {
					pauseTime += currentEdit - lastEdit;
				}
				lastEdit = currentEdit;
				fileEdited.add(currentFile);
				projectWorked.add(currentProject);
				insert += command.getDataMap().get("text").length();
			} else if (command instanceof Delete) {
				long currentEdit = command.getStartTimestamp() + command.getTimestamp();
				if (lastEdit != 0 && currentEdit - lastEdit > FIVE_MIN) {
					pauseTime += currentEdit - lastEdit;
				}
				lastEdit = currentEdit;
				fileEdited.add(currentFile);
				projectWorked.add(currentProject);
				delete += command.getDataMap().get("text").length();
			} else if (command instanceof FileOpenCommand) {
				long currentEdit = command.getStartTimestamp() + command.getTimestamp();
				if (lastEdit != 0 && currentEdit - lastEdit > FIVE_MIN) {
					pauseTime += currentEdit - lastEdit;
				}
				lastEdit = currentEdit;
				currentFile = command.getDataMap().get("filePath");
				currentProject = getProjectName(currentFile);
			} 
		}
		long workTime = (lastEdit - startTime - pauseTime) / 60000;
		if (workTime < 0) {
			workTime = 0;
		} else if (workTime >= 55) {
			workTime = 60;
		}
		if (workTime == 0 && insert == 0 && delete == 0) {
			sb.append("Hope you enjoyed your last hour aways from Eclipse-based programming!");
		} else {
			sb.append("Worked " + workTime + " minutes in last hour\n");
			sb.append("Edited " + fileEdited.size() + " Files\n");
			sb.append("Made " + insert + " Inserts; " + delete + " Deletes");
		}
		logCommands(workTime, startTime, lastEdit, start, end, insert, delete);
		fileEdited.clear();
		projectWorked.clear();
		return sb.toString();
	}
	
	private void logCommands(long workTime, long startTime, long endTime, int start, int end, int insert, int delete) {
		if (logFile.exists()) {
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(logFile, true));
				StringBuilder sb2 = new StringBuilder();
				sb2.append("*******************************************\n");
				sb2.append("Worked " + workTime + " minutes from " + 
							new Date(startTime).toString() + " to " + 
							new Date(endTime).toString() + "\n");
				sb2.append("Edited " + projectWorked.size() + "Projects: \n");
				for (String project : projectWorked) {
					sb2.append("\t" + project + "\n");
				}
				sb2.append("Edited " + fileEdited.size() + " Files: \n");
				for (String file : fileEdited) {
					sb2.append("\t" + file + "\n");
				}
				sb2.append("Made " + insert + " Inserts; " + delete + " Deletes\n");
				File logFolder = new File(logFile.getParent(), "Logs");
				if (!logFolder.exists()) {
					logFolder.mkdirs();
				}
				File commandLog = new File(logFolder, System.currentTimeMillis()+".xml");
				sb2.append("Commands for this notification stored at Logs" + File.separator + commandLog.getName() + "\n");
				bw.write(sb2.toString());
				bw.close();
				Logger logger = Logger.getLogger(commandLog.getName());
				initializeLoggerFile(logger, commandLog);
				for (int i = start; i < end; i++) {
					logger.log(Level.FINE, null, commands.get(i));
				}
				for (Handler fh : logger.getHandlers()) {
					fh.flush();
					logger.removeHandler(fh);
					fh.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				if (bw != null) {
					try {
						bw.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
	
	private void initializeLoggerFile(Logger aLogger, File outputFile) {
		aLogger.setLevel(Level.FINE);
		try {
			if (startTimestamp == 0) {
				startTimestamp = EHEventRecorder.getInstance().getStartTimestamp();
			}
			FileHandler handler = new FileHandler(outputFile.getPath());
			handler.setEncoding("UTF-8");
			handler.setFormatter(new EHXMLFormatter());
			aLogger.addHandler(handler);
			LogHandlerBound.newCase(handler, this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private String getProjectName(String fileName) {
		File file = new File(fileName);
		while (file != null && !file.getName().equals("src")) {
			file = file.getParentFile();
		}
		if (file == null) {
			return "";
		}
		return file.getParentFile().getName();
	}
	
	public void schedule(Timer timer, List<EHICommand> commands) {
		if (!scheduled) {
			this.commands = commands;
			timer.schedule(this, HOUR, HOUR);
//			timer.schedule(this, 15000, 15000);
			scheduled = true;
		}
	}

	public void run() {
		if (EditorsUI.getPreferenceStore().getBoolean(Preferences.CONNECT_TO_SERVER)
		 && EditorsUI.getPreferenceStore().getBoolean(Preferences.SHOW_STATUS_NOTIFICATION)) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					showNotification();
				}
			});
		}
	}
	
	public void setCommands(List<EHICommand> commands) {
		this.commands = commands;
	}
}
