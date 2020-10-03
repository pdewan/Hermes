package analyzer.extension.replayView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;

import dayton.ellwanger.hermes.SubView;
import difficultyPrediction.DifficultyPredictionSettings;
import fluorite.commands.EHICommand;
import fluorite.commands.EclipseCommand;

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

public class ReplayView extends ViewPart implements SubView{
	ReplayListener replayListener;
	Label timeSpentLabel;
	Composite commandListComposite;
	Composite parent;
	ScrolledComposite scrolledComposite;
	private ScrolledComposite sc_1;
	Slider timeline;
	Composite forwardBackComposite;
	Label exceptionLabel;
	Label totalTimeSpentLabel;
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
	public final static String NEW_FILE = "New file";
	public final static String RUN = "Run";
	public final static String DEBUG = "Debug";
	public final static String OPEN_FILE = "Open file";
	public final static String DIFFICULTY = "Difficulty";
	public final static String DIFFICULTY_TO_NO_DIFFICULTY = "Difficulty->no difficulty";
	public final static String COMPILE = "Compile";
	public final static String SAVE = "Save";
	public final static String DELETE_FILE = "Delete file";
	public final static String REFACTOR = "Refactor";
	public final static String[] METRICS = {"Prediction", "Date", "File", "BusyTime", "Web Page"};
	private String[] steps = {EDIT, ONE_MINUTE, ONE_HOUR, EXCEPTION, EXCEPTION_TO_FIX, FIX, LOCALCHECKS, 
					  NEW_FILE, DELETE_FILE, REFACTOR, OPEN_FILE, RUN, DEBUG, DIFFICULTY, DIFFICULTY_TO_NO_DIFFICULTY, COMPILE, SAVE};
	private CTabFolder tabFolder;
	private CTabItem tabItem;
	private Composite composite_2;
	private Label lblTotalExceptions;
	private TabFolder tabFolder2;
	private TabItem commandsTab;
	private TabItem metricsTab;
	private ScrolledComposite sc;
	private Table MetricTable;
	private TableColumn[] tableColumns = new TableColumn[5];
	private Text text;
	
//	public void addReplayListener(ReplayListener replayListener){
//		replayListeners.add(replayListener);
//	}
//	
//	public void removeReplayListener(ReplayListener replayListener){
//		replayListeners.remove(replayListener);
//	}
	
	public ReplayView() {
		replayListener = new ReplayViewController(this);
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
		
		forwardBackComposite = new Composite(parent, SWT.NONE);
		GridData gd_forwardBackComposite = new GridData(917, 480);
		gd_forwardBackComposite.horizontalAlignment = SWT.FILL;
		gd_forwardBackComposite.verticalAlignment = SWT.FILL;
		gd_forwardBackComposite.grabExcessHorizontalSpace = true;
		gd_forwardBackComposite.grabExcessVerticalSpace = true;
		forwardBackComposite.setLayoutData(gd_forwardBackComposite);
		forwardBackComposite.setBackground(backgroundColor);
		GridLayout rl_forwardBackComposite = new GridLayout();
//		rl_forwardBackComposite.wrap = false;
		forwardBackComposite.setLayout(rl_forwardBackComposite);
		
		Composite forwardBackTimelineComposite = new Composite(forwardBackComposite, SWT.NONE);
		GridData gd_forwardBackTimelineComposite = new GridData(3424, 80);
		gd_forwardBackTimelineComposite.horizontalAlignment = SWT.FILL;
		gd_forwardBackTimelineComposite.verticalAlignment = SWT.TOP;
		gd_forwardBackTimelineComposite.grabExcessHorizontalSpace = true;
		forwardBackTimelineComposite.setLayoutData(gd_forwardBackTimelineComposite);
		forwardBackTimelineComposite.setBackground(backgroundColor);
		forwardBackTimelineComposite.setLayout(new GridLayout());
		
		totalTimeSpentLabel= new Label(forwardBackTimelineComposite, SWT.NONE);
		totalTimeSpentLabel.setBackground(backgroundColor);
		totalTimeSpentLabel.setText("Total Time Spent : ");
		
		timeSpentLabel = new Label(forwardBackTimelineComposite, SWT.NONE);
		GridData gd_timeSpentLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_timeSpentLabel.widthHint = 897;
		timeSpentLabel.setLayoutData(gd_timeSpentLabel);
		timeSpentLabel.setBackground(backgroundColor);
		timeSpentLabel.setText("Current Time Spent : ");
		
		composite_2 = new Composite(forwardBackTimelineComposite, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite_2.setLayout(new GridLayout(11, false));
		
		exceptionLabel = new Label(composite_2, SWT.NONE);
		exceptionLabel.setBackground(backgroundColor);
		exceptionLabel.setText("Current Exceptions: ");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		lblTotalExceptions = new Label(composite_2, SWT.NONE);
		lblTotalExceptions.setText("Total Exceptions:");
		
		tabFolder2 = new TabFolder(forwardBackComposite, SWT.NONE);
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
		
		metricsTab = new TabItem(tabFolder2, SWT.NONE);
		metricsTab.setText("Metrics");
		
		sc = new ScrolledComposite(tabFolder2, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setTouchEnabled(true);
		sc.setShowFocusedControl(true);
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);
		sc.setAlwaysShowScrollBars(true);
		metricsTab.setControl(sc);
		
		MetricTable = new Table(sc, SWT.NONE);
		MetricTable.setLinesVisible(true);
		MetricTable.setHeaderVisible(true);
		
		fillMetricHeaders();
		
		sc.setContent(MetricTable);
		sc.setMinSize(MetricTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sc.setMinSize(new Point(5000, 5000));
		
		Composite timelineComposite = new Composite(forwardBackComposite, SWT.NONE);
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
		
		Composite composite_1 = new Composite(timelineComposite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		composite_1.setLayout(new GridLayout(4, false));
		
		Button backButton = new Button(composite_1, SWT.PUSH);
		GridData gd_backButton = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_backButton.widthHint = 70;
		backButton.setLayoutData(gd_backButton);
		backButton.setBackground(backgroundColor);
		backButton.addSelectionListener(new BackButtonHandler());
		backButton.setText("Back");
		
		text = new Text(composite_1, SWT.BORDER);
		text.setText("1");
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 50;
		text.setLayoutData(gd_text);
		
		stepSelector = new Combo(composite_1, SWT.DROP_DOWN | SWT.READ_ONLY);
		stepSelector.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		stepSelector.setItems(steps);
		stepSelector.setText(EDIT);
		
		Button forwardButton = new Button(composite_1, SWT.PUSH);
		forwardButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		forwardButton.setBackground(backgroundColor);
		forwardButton.addSelectionListener(new ForwardButtonHandler());
		forwardButton.setBackground(backgroundColor);
		forwardButton.setText("Forward");
		
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

	public void updateTimeSpent(String totalTime, String currentTime) {
		totalTimeSpentLabel.setText("Total Time Spent: " + totalTime);
		timeSpentLabel.setText("Current Time Spent: " + currentTime);
		totalTimeSpentLabel.requestLayout();
		timeSpentLabel.requestLayout();
	}
	
	public void updateNumOfExceptions(int numOfCurrentExceptions, int numOfTotalExceptions) {
		exceptionLabel.setText("Current Exceptions: " + numOfCurrentExceptions);
		lblTotalExceptions.setText("Total Exceptions: " + numOfTotalExceptions);
	}
	
	public void createForwardCommandList(ArrayList<EHICommand> commandList) {
		createCommandLabel(commandList, false);
		sc_1.setContent(commandLabel);
		sc_1.setMinSize(commandLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	public void createBackCommandList(ArrayList<EHICommand> commandList) {
		createCommandLabel(commandList, true);
		sc_1.setContent(commandLabel);
		sc_1.setMinSize(commandLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
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
	
	private void fillMetricHeaders() {
		for (int i = 0; i < tableColumns.length; i++) {
			tableColumns[i] = new TableColumn(MetricTable, SWT.NONE);
			tableColumns[i].setWidth(100);
			tableColumns[i].setText(METRICS[i]);
		}
	}
	
	public void updateMetrics(List<List<List<String>>> metrics) {
		MetricTable.removeAll();
		for (List<List<String>> list : metrics) {
			for (List<String> list2 : list) {
				TableItem item = new TableItem(MetricTable, SWT.NONE);
				for (int i = 0, j = 0; i < list2.size(); i++) {
					if (i == 0 || i == 4 || i == 5 || (i >= 7 && i <= 21)) {
						continue;
					}
					String text = list2.get(i);
					if (i == 2) {
						Date d;
						try {
							d = new SimpleDateFormat("E MMM dd HH:mm:ss zz yyyy").parse(list2.get(i));
							text = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d)+"\t";
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} 
					if (i > 21) {
						for (;i < list2.size(); i++) 
							text += '\t' + list2.get(i);
						text = text.replaceAll("\\[\\]", "");
					}
					item.setText(j,text);
					j++;
				}
			}
		}
		for (TableColumn tableColumn : tableColumns) {
			tableColumn.pack();
		}
	}
	
	private void createCommandLabel(ArrayList<EHICommand> commandList, boolean back) {
		if (commandList == null) {
			return;
		}
		String commandType;
		String text = "";
		if (back) {
			for(int i = commandList.size()-1; i >= 0; i--) {
				EHICommand command = commandList.get(i);
				if (command instanceof EclipseCommand) 
					commandType = "Delete previous";
				else
					commandType = command.getCommandType();
				switch (commandType) {
				case "Insert":
					text += commandType + " \"" + command.getDataMap().get("text") + "\" at " + command.getAttributesMap().get("offset") + "\n";
					break;
				case "Replace":
					text += commandType + " \"" + command.getDataMap().get("deletedText") + "\" by \"" + command.getDataMap().get("insertedText") +"\" at " + command.getAttributesMap().get("offset") + "\n";
					break;
				case "Delete":
					text += commandType + " \"" + command.getDataMap().get("text") + "\" at " + command.getAttributesMap().get("offset") + "\n";
					break;
				case "Delete previous":
					text += commandType + " character or selected text\n";
					break;
				case "ExceptionCommand":
					text += "Exception: \"" + command.getDataMap().get("exceptionString") + "\"\n";
					break;
				case "ConsoleOutput":
					text += "Console Output: \"" + command.getDataMap().get("outputString") + "\"\n";
					break;
				default:
					break;
				}	
			}
		} else {
			for(int i = 0; i < commandList.size(); i++) {
				EHICommand command = commandList.get(i);
				if (command instanceof EclipseCommand) 
					commandType = "Delete previous";
				else
					commandType = command.getCommandType();
				switch (commandType) {
				case "Insert":
					text += commandType + " \"" + command.getDataMap().get("text") + "\" at " + command.getAttributesMap().get("offset") + "\n";
					break;
				case "Replace":
					text += commandType + " \"" + command.getDataMap().get("deletedText") + "\" by \"" + command.getDataMap().get("insertedText") +"\" at " + command.getAttributesMap().get("offset") + "\n";
					break;
				case "Delete":
					text += commandType + " \"" + command.getDataMap().get("text") + "\" at " + command.getAttributesMap().get("offset") + "\n";
					break;
				case "Delete previous":
					text += commandType + " character of selected text\n";
					break;
				case "ExceptionCommand":
					text += "Exception: \"" + command.getDataMap().get("exceptionString") + "\"\n";
					break;
				case "ConsoleOutput":
					text += "Console Output: \"" + command.getDataMap().get("outputString") + "\"\n";
					break;
				default:
					break;
				}	
			}
		}
		commandLabel.setText(text);
		commandLabel.setSize(1080, 50*commandList.size());
		commandLabel.requestLayout();
	}
	
	public void updateTimeline(int index) {
		timeline.setSelection(index);
		timeline.requestLayout();
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