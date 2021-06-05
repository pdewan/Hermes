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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import fluorite.commands.BalloonClickedCommand;
import fluorite.commands.BalloonCommand;
import fluorite.commands.Delete;
import fluorite.commands.DifficultyCommand;
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
	private static final long DAY = 24*HOUR;
	private HashSet<String> fileEdited;
	private HashSet<String> projectWorked;
	private boolean scheduled = false;
	private List<EHICommand> commands;
	private String currentFile = "";
	private String currentProject = "";
//	private TrayIcon trayIcon;
	private File logFile;
	private File logFolder;
//	private long startTimestamp = 0;
	private HashMap<String, Long> pauseMap;
	private HashMap<String, Long> nextPauseMap;
	private StringBuilder sb, sb2;
	private EHXMLFormatter formatter;
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
		sb = new StringBuilder();
		sb2 = new StringBuilder();
		formatter = new EHXMLFormatter(EHEventRecorder.getInstance().getStartTimestamp());
		initPauseMap();
		try {
			logFile = new File(System.getProperty("user.home"), "helper-config" + File.separator + "NotificationLog.txt");
			logFolder = new File(logFile.getParent(), "Logs");
			if (!logFile.getParentFile().exists()) {
				logFile.getParentFile().mkdirs();
			}
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			if (!logFolder.exists()) {
				logFolder.mkdirs();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logFile = null;
			logFolder = null;
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
	
//	public void showNotification(String message) {
////		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
////			public void run() {
//				if (toolTip == null) {
//					toolTip = new ToolTip(PlatformUI.getWorkbench()
//							.getActiveWorkbenchWindow().getShell(), SWT.BALLOON
//							| SWT.ICON_INFORMATION);
//				}
//				if (!toolTip.isDisposed()) {
//					toolTip.setMessage(message);
//					EHEventRecorder.getTrayItem().setToolTip(toolTip);
//					toolTip.setVisible(true);
//				}
////			}
////		});
//	}
	
	private void showNotification() {
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
					EHEventRecorder.getInstance().recordCommand(new BalloonClickedCommand(toolTip.getMessage()));
				}
				
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
		}
		if (!toolTip.isDisposed()) {
			toolTip.setMessage(getNotification(false));
			EHEventRecorder.getTrayItem().setToolTip(toolTip);
			toolTip.setVisible(true);
		}
		
//		if (trayIcon != null) {
//			trayIcon.displayMessage("", getNotification(), MessageType.NONE);
//		}
	}
	
	public String getNotification(boolean debug) {
		if (debug) {
			index = 0;
		}
		sb.setLength(0);
		int insert = 0;
		int delete = 0;
		int start = index;
		int end = commands.size();
		long startTime = 0;
		long pauseTime = 0;
		long lastEdit = 0;
		long fineGrainedPauseTime = 0;
		long fineGrainedStartTime = 0;
		long fineGrainedEndTime = 0;
		
		for (; index < end; index++) {
			EHICommand command = commands.get(index);
			if (index == 0 && command instanceof DifficultyCommand) {
				start++;
				continue;
			}
			if (index == start && command instanceof PauseCommand) {
				continue;
			}
			if (fineGrainedStartTime == 0) {
				fineGrainedStartTime = command.getStartTimestamp() + command.getTimestamp();
			}
			fineGrainedEndTime = command.getStartTimestamp() + command.getTimestamp();
			if (command instanceof PauseCommand) {
				long pause = Long.parseLong(command.getDataMap().get("pause"));
				String prevType = command.getDataMap().get("prevType");
				String nextType = command.getDataMap().get("nextType");
				if (pause > pauseMap.get(prevType) && pause > nextPauseMap.get(nextType)) {
					fineGrainedPauseTime += pause;
				}
			}
			if (startTime == 0 && !(command instanceof BalloonCommand)) {
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
		long fineGrainedWorkTime = (fineGrainedEndTime - fineGrainedStartTime - fineGrainedPauseTime) / 60000;
		if (fineGrainedWorkTime < 0) {
			fineGrainedWorkTime = 0;
		} else if (fineGrainedWorkTime >= 59) {
			fineGrainedWorkTime = 60;
		}
		if (workTime == 0 && fineGrainedWorkTime == 0 && insert == 0 && delete == 0) {
			sb.append("Hope you enjoyed your last hour aways from Eclipse-based programming!");
		} else {
			sb.append("Hourly Eclipse Update:\n");
			if (workTime == fineGrainedWorkTime) {
				sb.append("Est. work time: " + workTime + " min\n");
			} else {
				sb.append("Est. work time: " + workTime + " min(fixed)\n" + 
						"                          " + fineGrainedWorkTime + " min(context)\n");
			}
//			sb.append("Worked " + workTime + " minutes(5min)/" + fineGrainedWorkTime + " minutes(fine grained) in last hour\n");
//			sb.append("Number of projects: " + projectWorked.size() + "\n");
//			sb.append("Edited " + fileEdited.size() + " Files\n");
			sb.append("Number of files: " + fileEdited.size() + "\n");
//			sb.append("Made " + insert + " Inserts; " + delete + " Deletes");
			sb.append("Insert: " + insert + "  Delete: " + delete + "\n");
			sb.append("Click here for more details");
//			sb.append("Number of inserts: " + insert + "\n");
//			sb.append("Number of deletes: " + delete + "\n");
			if (!debug) {
				logCommands(workTime, fineGrainedWorkTime, fineGrainedStartTime, fineGrainedEndTime, start, end, insert, delete);
			}
		}
		fileEdited.clear();
		projectWorked.clear();
		return sb.toString();
	}
	
	private void logCommands(long workTime, long fineGrainedWorkTime, long startTime, long endTime, int start, int end, int insert, int delete) {
		if (logFile.exists()) {
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(logFile, true));
				sb2.setLength(0);
				sb2.append("*******************************************\n");
//				sb2.append("Worked " + workTime + " minutes from ");
				sb2.append("Worked " + workTime + " minutes(fixed)/" + fineGrainedWorkTime + " minutes(context based) from\n");
				long currentTime = System.currentTimeMillis();
				if (startTime > 0) {
					sb2.append(new Date(startTime).toString() + " to ");
				} else {
					sb2.append(new Date(currentTime-HOUR).toString() + "to ");
				}
				if (endTime > 0) {
					sb2.append(new Date(endTime).toString() + "\n");
				} else {
					sb2.append(new Date(currentTime).toString() + "\n");
				}
				sb2.append("Edited " + projectWorked.size() + " Projects: \n");
				for (String project : projectWorked) {
					sb2.append("\t" + project + "\n");
				}
				sb2.append("Edited " + fileEdited.size() + " Files: \n");
				for (String file : fileEdited) {
					sb2.append("\t" + file + "\n");
				}
//				sb2.append("Made " + insert + " Inserts; " + delete + " Deletes\n");
				sb2.append("Number of insert: " + insert + "\nNumber of delete: " + delete + "\n");
//				File logFolder = new File(logFile.getParent(), "Logs");
				EHEventRecorder.getInstance().recordCommand(new BalloonCommand(sb2.toString(), workTime, fineGrainedWorkTime, startTime, endTime, insert, delete));
				if (!logFolder.exists()) {
					logFolder.mkdirs();
				}
				if (start < end) {
					File commandLog = new File(logFolder, System.currentTimeMillis()+".xml");
					sb2.append("Commands for this notification stored at Logs" + File.separator + commandLog.getName() + "\n");
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
				} else {
					sb2.append("No Commands recorded in the session");
				}
				bw.write(sb2.toString());
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				if (bw != null) {
					try {
						bw.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				sb2.setLength(0);
				sb2.append(e.toString());
				sb2.append("start = " + start + "\n");
				sb2.append("end = " + end + "\n");
				sb2.append("size = " + commands.size() + "\n");
				if (bw != null) {
					try {
						bw.write(sb2.toString());
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
//			if (startTimestamp == 0) {
//				startTimestamp = EHEventRecorder.getInstance().getStartTimestamp();
//			}
			FileHandler handler = new FileHandler(outputFile.getPath());
			handler.setEncoding("UTF-8");
			handler.setFormatter(formatter);
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
			if (!EditorsUI.getPreferenceStore().getBoolean(Preferences.KEEP_NOTIFICATION_LOGS)) {
				timer.schedule(new TimerTask() {
					public void run() {
						try {
							deleteLogsFromPreviousDay();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 0, DAY);
			}
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
	
	private void deleteLogsFromPreviousDay() {
		if (logFolder != null && logFolder.exists()) {
			long startOfToday = getStartOfDay(System.currentTimeMillis());
			for (File file : logFolder.listFiles()) {
				if (file.lastModified() < startOfToday) {
					file.delete();
				}
			}
		}
	}
	
	private long getStartOfDay(long currentTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(currentTime);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	private void initPauseMap() {
		pauseMap = new HashMap<>();
		nextPauseMap = new HashMap<>();
		for (int i = 0; i < PauseCommand.TYPES.length; i++) {
			pauseMap.put(PauseCommand.TYPES[i], PauseCommand.THRESHOLD[i]==0?FIVE_MIN:PauseCommand.THRESHOLD[i]);
			nextPauseMap.put(PauseCommand.TYPES[i], PauseCommand.NEXT_THRESHOLD[i]==0?FIVE_MIN:PauseCommand.NEXT_THRESHOLD[i]);
		}
	}
}
