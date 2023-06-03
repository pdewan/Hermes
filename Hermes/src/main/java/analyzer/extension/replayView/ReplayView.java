package analyzer.extension.replayView;

import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.part.ViewPart;

import analyzer.extension.timerTasks.ANotificationBalloon;
import dayton.ellwanger.hermes.preferences.Preferences;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import difficultyPrediction.DifficultyPredictionSettings;
import fluorite.commands.AnnotationCommand;
import fluorite.commands.ConsoleOutput;
import fluorite.commands.Delete;
import fluorite.commands.EHICommand;
import fluorite.commands.EclipseCommand;
import fluorite.commands.ExceptionCommand;
import fluorite.commands.FileOpenCommand;
import fluorite.commands.Insert;
import fluorite.commands.LocalCheckCommand;
import fluorite.commands.MoveCaretCommand;
import fluorite.commands.PauseCommand;
import fluorite.commands.PiazzaPostCommand;
import fluorite.commands.Replace;
import fluorite.commands.RunCommand;
import fluorite.commands.ShellCommand;
import fluorite.commands.ShowStatCommand;
import fluorite.commands.WebCommand;
import fluorite.commands.ZoomChatCommand;
import fluorite.commands.ZoomSessionEndCommand;
import fluorite.commands.ZoomSessionStartCommand;
import fluorite.model.EHEventRecorder;
import fluorite.util.EHUtilities;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ReplayView extends ViewPart {
	ReplayListener replayListener;
	Composite commandListComposite;
	Composite parent;
	ScrolledComposite scrolledComposite;
	private ScrolledComposite sc_1;
	Slider timeline;
	Composite controlComposite;
	Combo stepSelector;
	Label commandLabel;
	Label lblFile;
	public final static String EDIT = "Edit";
	public final static String ONE_MINUTE = "Minute";
	public final static String ONE_HOUR = "Hour";
	public final static String EXCEPTION = "Exception";
	public final static String FIX = "Fix";
	public final static String EXCEPTION_TO_FIX = "Exception->Fix";
	public final static String LOCALCHECKS = "Localchecks";
//	public final static String NEW_FILE = "New file";
	public final static String RUN = "Run";
	public final static String DEBUG = "Debug";
	public final static String OPEN_FILE = "Open file";
	public final static String PIAZZA = "Piazza post";
	public final static String OFFICE_HOUR = "Office hour";
	public final static String DIFFICULTY = "Difficulty";
	public final static String DIFFICULTY_TO_NO_DIFFICULTY = "Difficulty->no difficulty";
	public final static String COMPILE = "Compile";
	public final static String SAVE = "Save";
	public final static String ANNOTATION = "Annotation";
//	public final static String DELETE_FILE = "Delete file";
//	public final static String REFACTOR = "Refactor";
	public final static String PAUSE = "Pause";
	public final static String WEB = "Web";
//	public final static String[] METRICS = {"Prediction", "Date", "File", "BusyTime", "Web Page"};
	private final static String[] steps = {EDIT, ONE_MINUTE, ONE_HOUR, EXCEPTION, EXCEPTION_TO_FIX, FIX, LOCALCHECKS, 
//					  NEW_FILE, DELETE_FILE, REFACTOR, 
					  OPEN_FILE, RUN, DEBUG, 
//					  DIFFICULTY, DIFFICULTY_TO_NO_DIFFICULTY, 
//					  COMPILE, SAVE, 
					  PAUSE, WEB, PIAZZA, OFFICE_HOUR, ANNOTATION};
	public final static String WORKTIME = "Work Time";
	public final static String WALLTIME = "Wall Time";
	public final static long ONE_MIN = 60000;
	private final static String[] TIME = {WORKTIME, WALLTIME}; 
	private static final String NOT_CONNECTED = "Not connected to server.\nTo connect to server, go to Preference->Hermes->Hermes Help->Check Connect to Server.";
	private CTabFolder tabFolder;
	private CTabItem tabItem;
	private TabFolder tabFolder2;
	private TabItem commandsTab;
//	private TabItem metricsTab;
	private ScrolledComposite sc;
//	private Table MetricTable;
	private TableColumn[] tableColumns = new TableColumn[5];
	private Text text;
	boolean showPause = false;
	private Button btnShowPause;
	private Combo timeCombo;
	private Label timelbl;
	private Label absTimeLbl;
	private Button btnExport;
//	private Timer timer;
	
//	public void addReplayListener(ReplayListener replayListener){
//		replayListeners.add(replayListener);
//	}
//	
//	public void removeReplayListener(ReplayListener replayListener){
//		replayListeners.remove(replayListener);
//	}
	
	public ReplayView() {
		replayListener = new ReplayViewController(this);
		UIManager.put("OptionPane.messageFont", new Font("Arial", Font.BOLD, 18));
		UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 14));
		UIManager.put("OptionPane.minimumSize",new Dimension(300,200)); 

//		timer = new Timer();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		this.parent = parent;
		Color backgroundColor = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		
		GridLayout rl_parent = new GridLayout();
		rl_parent.numColumns = 2;
//		rl_parent.wrap = false;
		parent.setLayout(rl_parent);
		
		controlComposite = new Composite(parent, SWT.NONE);
		GridData gd_controlComposite = new GridData(917, 480);
		gd_controlComposite.horizontalAlignment = SWT.FILL;
		gd_controlComposite.verticalAlignment = SWT.FILL;
		gd_controlComposite.grabExcessHorizontalSpace = true;
		gd_controlComposite.grabExcessVerticalSpace = false;
		controlComposite.setLayoutData(gd_controlComposite);
		controlComposite.setBackground(backgroundColor);
		GridLayout rl_controlComposite = new GridLayout();
//		rl_forwardBackComposite.wrap = false;
		controlComposite.setLayout(rl_controlComposite);
		
		Composite timeComposite = new Composite(controlComposite, SWT.NONE);
		GridData gd_timeComposite = new GridData(541, -1);
		gd_timeComposite.horizontalAlignment = SWT.FILL;
		gd_timeComposite.verticalAlignment = SWT.TOP;
		timeComposite.setLayoutData(gd_timeComposite);
		timeComposite.setBackground(backgroundColor);
		GridLayout gd_Composite = new GridLayout();
		gd_Composite.numColumns = 3;
		timeComposite.setLayout(gd_Composite);
		
		timeCombo = new Combo(timeComposite, SWT.READ_ONLY);
		GridData gd_timeCombo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_timeCombo.widthHint = 86;
		timeCombo.setLayoutData(gd_timeCombo);
		timeCombo.setItems(TIME);
		timeCombo.select(0);
		timeCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ANotificationBalloon.getInstance().showNotification();
				if (EditorsUI.getPreferenceStore().getBoolean(Preferences.CONNECT_TO_SERVER)) {
					String text = timeCombo.getText();
					String path = getCurrentProjectPath();
					new Thread(()->{
						replayListener.updateTimeSpent(path, text);
						EHEventRecorder.getInstance().recordCommand(new ShowStatCommand(text));
					}).start();
				} else {
					JOptionPane optionPane = new JOptionPane(NOT_CONNECTED, JOptionPane.WARNING_MESSAGE);
					JDialog dialog = optionPane.createDialog("ERROR");
					dialog.setAlwaysOnTop(true);
					dialog.setVisible(true);
				}
//				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//					public void run() {
						
//					}
//				});
//				String text = timeCombo.getText();
//				replayListener.updateTimeSpent(text);
//				EHEventRecorder.getInstance().recordCommand(new ShowStatCommand(text));
			}
		});
		
//		timer.schedule(new TimerTask() {
//			public void run() {
//				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//					public void run() {
//						//exception, may due to Eclipse versions
//						try {
//							replayListener.updateTimeSpent(timeCombo.getText());
//						} catch (Exception e) {
//							// TODO: handle exception
//						}
//					}
//				});
//			}
//		}, ONE_MIN, ONE_MIN);
		
		timelbl = new Label(timeComposite, SWT.NONE);
		timelbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		absTimeLbl = new Label(timeComposite, SWT.NONE);
		absTimeLbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		tabFolder2 = new TabFolder(controlComposite, SWT.NONE);
		GridData gd_tabFolder2 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_tabFolder2.heightHint = 287;
		tabFolder2.setLayoutData(gd_tabFolder2);
		
		commandsTab = new TabItem(tabFolder2, SWT.NONE);
		commandsTab.setText("Commands");
		
		sc_1 = new ScrolledComposite(tabFolder2, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		commandsTab.setControl(sc_1);
		sc_1.setTouchEnabled(true);
		sc_1.setShowFocusedControl(true);
		sc_1.setAlwaysShowScrollBars(true);
		sc_1.setExpandHorizontal(true);
		sc_1.setExpandVertical(true);
		commandLabel = new Label(sc_1, SWT.NONE);
		sc_1.setMinSize(new Point(5000, 5000));
		sc_1.setContent(commandLabel);
		
//		metricsTab = new TabItem(tabFolder2, SWT.NONE);
//		metricsTab.setText("Metrics");
		
//		sc = new ScrolledComposite(tabFolder2, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
//		sc.setTouchEnabled(true);
//		sc.setShowFocusedControl(true);
//		sc.setExpandVertical(true);
//		sc.setExpandHorizontal(true);
//		sc.setAlwaysShowScrollBars(true);
//		metricsTab.setControl(sc);
//		
//		MetricTable = new Table(sc, SWT.NONE);
//		MetricTable.setLinesVisible(true);
//		MetricTable.setHeaderVisible(true);
//		
//		fillMetricHeaders();
		
//		sc.setContent(MetricTable);
//		sc.setMinSize(MetricTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//		sc.setMinSize(new Point(5000, 5000));
		
		Composite timelineComposite = new Composite(controlComposite, SWT.NONE);
		GridData gd_timelineComposite = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
		gd_timelineComposite.widthHint = 910;
		timelineComposite.setLayoutData(gd_timelineComposite);
		GridLayout timelineLayout = new GridLayout(1, false);
		timelineComposite.setLayout(timelineLayout);
		
		Composite composite = new Composite(timelineComposite, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_composite.widthHint = 1088;
		composite.setLayoutData(gd_composite);
		composite.setLayout(new GridLayout(2, false));
		Label timelineLabel = new Label(composite, SWT.NONE);
		timelineLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		timelineLabel.setText("Timeline ");
		timeline = new Slider(composite, SWT.HORIZONTAL);
		GridData gd_timeline = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_timeline.widthHint = 826;
		timeline.setLayoutData(gd_timeline);
		timeline.setMinimum(0);
		timeline.setMaximum(1000);
		timeline.setSelection(1000);
		timeline.setIncrement(1);
		timeline.setBounds(115, 50, 500, 500);
		timeline.addDragDetectListener(new SliderHandler());
		
		Composite moveComposite = new Composite(controlComposite, SWT.NONE);
		moveComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		moveComposite.setLayout(new GridLayout(4, false));
		
		Button backButton = new Button(moveComposite, SWT.PUSH);
		GridData gd_backButton = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_backButton.widthHint = 70;
		backButton.setLayoutData(gd_backButton);
		backButton.setBackground(backgroundColor);
		backButton.addSelectionListener(new BackButtonHandler());
		backButton.setText("Back");
		
		text = new Text(moveComposite, SWT.BORDER);
		text.setText("1");
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 50;
		text.setLayoutData(gd_text);
		
		stepSelector = new Combo(moveComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		stepSelector.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		stepSelector.setItems(steps);
		stepSelector.setText(EDIT);
		
		Button forwardButton = new Button(moveComposite, SWT.PUSH);
		forwardButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		forwardButton.setBackground(backgroundColor);
		forwardButton.addSelectionListener(new ForwardButtonHandler());
		forwardButton.setBackground(backgroundColor);
		forwardButton.setText("Forward");
		
		Composite utilComposite = new Composite(controlComposite, SWT.NONE);
		utilComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		utilComposite.setLayout(new GridLayout(4, false));
		
		btnShowPause = new Button(utilComposite, SWT.CHECK);
		btnShowPause.setText("Show Pause");
		btnShowPause.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				showPause = !showPause;
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		Button resetButton = new Button(utilComposite, SWT.PUSH);
		GridData gd_resetButton = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_resetButton.widthHint = 70;
		resetButton.setLayoutData(gd_resetButton);
		resetButton.setBackground(backgroundColor);
		resetButton.addSelectionListener(new ResetButtonHandler());
		resetButton.setText("Reset");
		
		Button annotateButton = new Button(utilComposite, SWT.PUSH);
		GridData gd_annotateButton = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_annotateButton.widthHint = 100;
		annotateButton.setLayoutData(gd_annotateButton);
		annotateButton.setBackground(backgroundColor);
		annotateButton.addSelectionListener(new AnnotateButtonHandler());
		annotateButton.setText("Annotate");
		
		btnExport = new Button(utilComposite, SWT.NONE);
		GridData gd_btnExport = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnExport.widthHint = 70;
		btnExport.setLayoutData(gd_btnExport);
		btnExport.setText("Export");
		btnExport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				String path = getCurrentProjectPath();
				IProject project = EHUtilities.getCurrentProject();
				new Thread(()->{
					replayListener.zipCurrentProject(project);
				}).start();
			}
		});
		
		tabFolder = new CTabFolder(parent, SWT.BORDER);
		GridData gd_tabFolder = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_tabFolder.widthHint = 1046;
		tabFolder.setLayoutData(gd_tabFolder);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		tabItem = new CTabItem(tabFolder, SWT.CLOSE);
		tabItem.setText("New Item");
		
		scrolledComposite = new ScrolledComposite(tabFolder, SWT.H_SCROLL | SWT.V_SCROLL);
		tabItem.setControl(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		lblFile = new Label(scrolledComposite, SWT.NONE);
		lblFile.setText("File");
		scrolledComposite.setContent(lblFile);
		scrolledComposite.setMinSize(lblFile.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		parent.layout(true);
	}

	@Override
	public void setFocus() {}

//	public void updateTimeSpent(String totalTime, String currentTime) {
//		totalTimeSpentLabel.setText("Total Time Spent: " + totalTime);
//		timeSpentLabel.setText("Current Time Spent: " + currentTime);
//		totalTimeSpentLabel.requestLayout();
//		timeSpentLabel.requestLayout();
//	}
//	
//	public void updateNumOfExceptions(int numOfCurrentExceptions, int numOfTotalExceptions) {
//		exceptionLabel.setText("Current Exceptions: " + numOfCurrentExceptions);
//		lblTotalExceptions.setText("Total Exceptions: " + numOfTotalExceptions);
//	}
	
	public void createForwardCommandList(List<EHICommand> commandList) {
		createCommandLabel(commandList, false);
		sc_1.setContent(commandLabel);
		sc_1.setMinSize(commandLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	public void createBackCommandList(List<EHICommand> commandList) {
		createCommandLabel(commandList, true);
		sc_1.setContent(commandLabel);
		sc_1.setMinSize(commandLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	public void updateTimeSpent(String time) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				timelbl.setText(time);
				parent.layout(true);
			}
		});
//		timelbl.setText(time);
//		parent.layout(true);
	}
	
	public void updateAbsTimeSpent(String time) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				absTimeLbl.setText("Timestamp: " + time);
				parent.layout(true);
			}
		});
//		timelbl.setText(time);
//		parent.layout(true);
	}
	
	public void updateReplayedFile(String[] text) {
		for (int i = 0; i < tabFolder.getItemCount(); i++){
			if (tabFolder.getItem(i).getText().equals(text[0]) || tabFolder.getItem(i).getText().equals("New Item")) {
				tabItem = tabFolder.getItem(i);
				scrolledComposite = (ScrolledComposite)tabItem.getControl();
				lblFile = (Label)scrolledComposite.getContent();
				
				tabFolder.setSelection(tabItem);
				tabItem.setText(text[0]);
				lblFile.setText(text[1]);
				lblFile.requestLayout();
				scrolledComposite.setContent(lblFile);
				scrolledComposite.setMinSize(lblFile.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				return;
			}
		}
		tabItem = new CTabItem(tabFolder, SWT.CLOSE);

		scrolledComposite = new ScrolledComposite(tabFolder, SWT.H_SCROLL | SWT.V_SCROLL);
		tabItem.setControl(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		lblFile = new Label(scrolledComposite, SWT.NONE);
		tabFolder.setSelection(tabItem);
		tabItem.setText(text[0]);
		lblFile.setText(text[1]);
		lblFile.requestLayout();
		scrolledComposite.setContent(lblFile);
		scrolledComposite.setMinSize(lblFile.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
	}
	
//	private void fillMetricHeaders() {
//		for (int i = 0; i < tableColumns.length; i++) {
//			tableColumns[i] = new TableColumn(MetricTable, SWT.NONE);
//			tableColumns[i].setWidth(100);
//			tableColumns[i].setText(METRICS[i]);
//		}
//	}
	
//	public void updateMetrics(List<List<List<String>>> metrics) {
//		MetricTable.removeAll();
//		for (List<List<String>> list : metrics) {
//			for (List<String> list2 : list) {
//				TableItem item = new TableItem(MetricTable, SWT.NONE);
//				for (int i = 0, j = 0; i < list2.size(); i++) {
//					if (i == 0 || i == 4 || i == 5 || (i >= 7 && i <= 21)) {
//						continue;
//					}
//					String text = list2.get(i);
//					if (i == 2) {
//						Date d;
//						try {
//							d = new SimpleDateFormat("E MMM dd HH:mm:ss zz yyyy").parse(list2.get(i));
//							text = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d)+"\t";
//						} catch (ParseException e) {
//							e.printStackTrace();
//						}
//					} 
//					if (i > 21) {
//						for (;i < list2.size(); i++) 
//							text += '\t' + list2.get(i);
//						text = text.replaceAll("\\[\\]", "");
//					}
//					item.setText(j,text);
//					j++;
//				}
//			}
//		}
//		for (TableColumn tableColumn : tableColumns) {
//			tableColumn.pack();
//		}
//	}
	
	private void createCommandLabel(List<EHICommand> commandList, boolean back) {
		if (commandList == null) {
			return;
		}
		StringBuffer text = new StringBuffer();
		if (back) {
			for(int i = commandList.size()-1; i >= 0; i--) {
				appendLabel(text, commandList.get(i));
			}
		} else {
			for(int i = 0; i < commandList.size(); i++) {
				appendLabel(text, commandList.get(i));
			}
		}
		commandLabel.setText(text.toString());
		((ScrolledComposite)commandLabel.getParent()).setMinSize(new Point(text.length(), text.length()));;
		commandLabel.requestLayout();
		commandLabel.getParent().getParent().redraw();
	}
	
	public void appendLabel(StringBuffer text, EHICommand command) {
		try {
			if (command instanceof EclipseCommand) text.append("Deleted previous character\n");
			if (command instanceof Insert) text.append(command.getCommandType() + " \"" + command.getDataMap().get("text") + "\" at " + command.getAttributesMap().get("offset") + "\n");
			if (command instanceof Replace)	text.append(command.getCommandType() + " \"" + command.getDataMap().get("deletedText") + "\" by \"" + command.getDataMap().get("insertedText") +"\" at " + command.getAttributesMap().get("offset") + "\n");
			if (command instanceof Delete) text.append(command.getCommandType() + " \"" + command.getDataMap().get("text") + "\" at " + command.getAttributesMap().get("offset") + "\n");
			if (command instanceof ExceptionCommand) text.append("Exception: \"" + command.getDataMap().get("exceptionString") + "\"\n");
			if (command instanceof ConsoleOutput) text.append("Console Output: \"" + command.getDataMap().get("outputString") + "\"\n");
			if (command instanceof FileOpenCommand) {
				String fileName = command.getDataMap().get("filePath");
				if (fileName.contains("src")) {
					fileName = fileName.substring(fileName.indexOf("src"));
				}
				if (fileName.contains("Logs")) {
					fileName = fileName.substring(fileName.indexOf("src"));
				}
				text.append("Opened File " + fileName + "\n"); 
			}
			if (command instanceof WebCommand) {
					openWebsite(command);
					text.append("Web Access: " + command.getAttributesMap().get("type") + " keyword = " + command.getDataMap().get("keyword") + " URL = " + command.getDataMap().get("URL")+"\n");
				}
			if (showPause && command instanceof PauseCommand) text.append("Pause for " + command.getDataMap().get("pause")  +"ms\n");
			if (command instanceof LocalCheckCommand) text.append("LocalCheck Testcase: " + command.getDataMap().get("testcase") + " " + command.getDataMap().get("type")+"\n");
			if (command instanceof MoveCaretCommand) text.append("Move Caret to position" + command.getAttributesMap().get("caretOffset"));
			if (command instanceof ShellCommand) {
				if (command.getAttributesMap().get("type").equals("ECLIPSE_LOST_FOCUS")) text.append("Eclipse Lost Focus\n");
				if (command.getAttributesMap().get("type").equals("ECLIPSE_GAINED_FOCUS")) text.append("Eclipse Gained Focus\n");
			}
			if (command instanceof RunCommand) {
				if (command.getAttributesMap().get("type").equals("Run")) text.append("Run Project\n");
				if (command.getAttributesMap().get("type").equals("Debug")) text.append("Debug Project\n");
			}
			if (command instanceof PiazzaPostCommand) {
				text.append(command.getDataMap().get("piazza_post") + "\n");
			}
			if (command instanceof ZoomSessionStartCommand) {
				text.append("Zoom session started: " + command.getDataMap().get("session_start_time") + "\n");
			}
			if (command instanceof ZoomChatCommand) {
				text.append("Zoom chat: \n\t" + command.getDataMap().get("speaktime") + "  " + 
							command.getDataMap().get("speaker") + ": "+ command.getDataMap().get("chat") + "\n");
			}
			if (command instanceof ZoomSessionEndCommand) {
				text.append("Zoom session ended: " + command.getDataMap().get("session_end_time") + "\n");
			}
			if (command instanceof AnnotationCommand) {
				text.append("Annotation: " + command.getDataMap().get("annotation") + "\n");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void openWebsite(EHICommand command) {
		try {
			String os = System.getProperty("os.name").toLowerCase();
			Runtime rt = Runtime.getRuntime();
			String url = command.getDataMap().get("URL");
			if (os.indexOf("win") >= 0) {
				rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
			}
			if (os.indexOf("mac") >= 0) {
				rt.exec("open" + url);
			}
			if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
				String[] browsers = { "epiphany", "firefox", "mozilla", "konqueror", "netscape", "opera", "links", "lynx" };
				StringBuffer cmd = new StringBuffer();
				for (int i = 0; i < browsers.length; i++) {
					if(i == 0) {
						cmd.append(String.format("%s \"%s\"", browsers[i], url));
					} else {
						cmd.append(String.format(" || %s \"%s\"", browsers[i], url));
					}
				}
				rt.exec(new String[] { "sh", "-c", cmd.toString() });
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateTimeline(int index) {
		timeline.setSelection(index);
		timeline.requestLayout();
	}
	
	private String getCurrentProjectPath(){
		IProject currentProject = EHUtilities.getCurrentProject();
		if (currentProject == null) {
			return "";
		}
		return currentProject.getLocation().toOSString();
	}
	
	public void setReplayListener(ReplayListener rl) {
		replayListener = rl;
	}
	
	class ForwardButtonHandler extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
//			for (ReplayListener replayListener : replayListeners) {
			DifficultyPredictionSettings.setReplayMode(true);
			replayListener.forward(text.getText(), stepSelector.getText());
			DifficultyPredictionSettings.setReplayMode(false);
//			}
			parent.layout(true);
		}
	}
	
	class BackButtonHandler extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
//			for (ReplayListener replayListener : replayListeners) {
			DifficultyPredictionSettings.setReplayMode(true);
			replayListener.back(text.getText(), stepSelector.getText());
			DifficultyPredictionSettings.setReplayMode(false);
//			}
			parent.layout(true);
		}
	}
	
	class ResetButtonHandler extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
//			for (ReplayListener replayListener : replayListeners) {
			DifficultyPredictionSettings.setReplayMode(true);
			replayListener.reset();
			DifficultyPredictionSettings.setReplayMode(false);
//			}
			parent.layout(true);
		}
	}
	
	class AnnotateButtonHandler extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
//			for (ReplayListener replayListener : replayListeners) {
			DifficultyPredictionSettings.setReplayMode(true);
			String annotation = JOptionPane.showInputDialog("Enter annotation");
			replayListener.annotate(annotation);
			DifficultyPredictionSettings.setReplayMode(false);
//			}
			parent.layout(true);
		}
	}
	
	class SliderHandler implements DragDetectListener{
		public void dragDetected(DragDetectEvent e) {
//			for (ReplayListener replayListener : replayListeners) {
			DifficultyPredictionSettings.setReplayMode(true);
			replayListener.jumpTo(timeline.getSelection(), stepSelector.getText());
			DifficultyPredictionSettings.setReplayMode(false);
//			}
			parent.layout(true);
		}
	}
	

}