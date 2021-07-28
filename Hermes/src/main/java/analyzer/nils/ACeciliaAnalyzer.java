
package analyzer.nils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import analyzer.AnAnalyzerParameters;
import analyzer.Analyzer;
import analyzer.AnalyzerFactory;
import analyzer.AnalyzerListener;
import analyzer.AnalyzerParameters;
import analyzer.DiscreteChunksAnalyzer;
import analyzer.MainConsoleUI;
import analyzer.RatioFilePlayerFactory;
import analyzer.TimeStampComputerFactory;
import analyzer.extension.ACSVParser;
import analyzer.extension.AStuckInterval;
import analyzer.extension.AStuckPoint;
import analyzer.extension.ARatioFileGenerator;
import analyzer.extension.RatioFileGeneratorFactory;
import analyzer.extension.CSVParser;
import analyzer.extension.FileReplayAnalyzerProcessorFactory;
import analyzer.extension.StuckInterval;
import analyzer.extension.StuckPoint;
import analyzer.ui.graphics.RatioFileReader;
import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import bus.uigen.attributes.AttributeNames;
//import bus.uigen..AFileSetterModel;
//import bus.uigen..FileSetterModel;
//import bus.uigen..ObjectEditor;
import bus.uigen.models.AFileSetterModel;
import bus.uigen.models.FileSetterModel;
import config.FactorySingletonInitializer;
import difficultyPrediction.ADifficultyPredictionPluginEventProcessor;
import difficultyPrediction.DifficultyPredictionPluginEventProcessor;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.Mediator;
import difficultyPrediction.PredictionParametersSetterSelector;
import difficultyPrediction.eventAggregation.EventAggregator;
import difficultyPrediction.featureExtraction.RatioFeatures;
import fluorite.commands.DifficultyCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.PredictionCommand;
import fluorite.commands.PredictionType;
import fluorite.commands.Status;
import fluorite.commands.WebVisitCommand;
import fluorite.model.EHEventRecorder;
import fluorite.model.EHXMLFormatter;
import fluorite.util.EHLogReader;
import programmatically.AnEclipseProgrammaticController;
import util.annotations.LayoutName;
import util.annotations.Row;
import util.annotations.Visible;
import util.trace.recorder.LogFileCreated;
import util.trace.recorder.LogHandlerBound;

@LayoutName(AttributeNames.GRID_BAG_LAYOUT)
/**
 * Loads the experimental directory. reads specified command logs Combines all
 * log files for an experiment into a nested command list. Replays a flattenned
 * version of the log. Prints the replay Each command is sent to a difficulty
 * processor so that new predictions can be made and visualized The difficulty
 * pipe line will fire events Fires events for replay of stored events also. The
 * stored replay can be used to generate ratio files, Also replays ratio files
 * that can be then visualized.
 *
 * 
 * @author dewan
 *
 */
public class ACeciliaAnalyzer extends ANilsAnalyzer implements Analyzer {
	// private final static Logger ANALYZER_LOGGER =
	// Logger.getLogger(Analyzer.class.getName());

	// public static final String PARTICIPANT_DIRECTORY = "data/";
//	public static final String DEFAULT_PARTICIPANT_DIRECTORY = "data/";
//
//	public static final String EXPERIMENTAL_DATA = "ExperimentalData/";
//	public static final String OUTPUT_DATA = "OutputData/";
//
//	public static final String ECLIPSE_FOLDER = "Eclipse/";
//	public static final String BROWSER_FOLDER = "Browser/";
//	public static final String REPLAYED_LOG_FOLDER = "ReplayedLogs/";
//	public static final String REPLAYED_PROJECT_FOLDER = "ReplayedProjects/";
//	public static final String PROJECT_NAME_PREFIX = "Project";
//
//	// public static final String STUCKPOINT_FILE =
//	// "data/GroundTruth/Stuckpoints.csv";
//	public String stuckPointFile() {
//		return Paths.get(getParticipantsFolderName(), "GroundTruth/Stuckpoints.csv").toString();
//	}
//
//	// public static final String STUCKINTERVAL_FILE = "data/GroundTruth/Stuck
//	// Intervals.csv";
//	public String stuckIntervalsFile() {
//		return Paths.get(getParticipantsFolderName(), "GroundTruth/Stuck Intervals.csv").toString();
//	}
//
//	// public static final String PARTICIPANT_INFORMATION_DIRECTORY =
//	// "data/ExperimentalData/";
//	// public static final String PARTICIPANT_OUTPUT_DIRECTORY =
//	// "data/OutputData/";
//
//	public static final String PARTICIPANT_INFORMATION_FILE = "Participant_Info.csv";
//	public static final String RATIOS_FILE_NAME = "ratios.csv";
//	public static final int SEGMENT_LENGTH = 50;
//	public static final String ALL_PARTICIPANTS = "All";
//	public static final String IGNORE_KEYWORD = "IGNORE";
//	final Hashtable<String, String> participants = new Hashtable<String, String>();
//	protected String outPath;
//
//	// do not make public, we only need to fill these maps once, must uphold
//	// that they are unmodifiable via getter methods
//	private Map<String, Queue<StuckPoint>> stuckPoint = new HashMap<>();
//	private Map<String, Queue<StuckInterval>> stuckInterval = new HashMap<>();
//
//	protected List<WebVisitCommand> sortedWebVisitQueue = new LinkedList<>();
//	protected List<WebVisitCommand> sortedWebVisitCommands = new ArrayList();
//	protected WebVisitCommand lastWebVisitCommandWithoutDuration;
//
//	protected List<String> webVisitsInFile = new ArrayList();
//
//	boolean stuckFileLoaded = false;
//	RatioFileReader ratioFileReader;
//
//	long startTimestamp;
//	long experimentStartTimestamp;
//	protected List<List<EHICommand>> nestedCommandsList;
//
//	FileSetterModel participantsFolder, outputFolder, experimentalData;
//	AnalyzerParameters parameters;
//	EHLogReader reader;
//	// protected Thread difficultyPredictionThread;
//	// protected DifficultyPredictionRunnable difficultyPredictionRunnable;
//	// protected BlockingQueue<ICommand> pendingPredictionCommands;
//	DifficultyPredictionPluginEventProcessor difficultyEventProcessor;
//	List<AnalyzerListener> listeners = new ArrayList<>();
//	PropertyChangeSupport propertyChangeSupport;
//	// int currentParticipant = -1;
//
//	// subdirectory inside of OutputData to put outputs, note that it can be
//	// different for each instance of analyzer
//	private String outputSubdirectory = "";
//	int lastPrediction;
//	int lastCorrection;
//	Mediator mediator;
//	// protected long lastStartTimestamp;
//
//	EventAggregator eventAggregator;
//	protected Set<String> ignoreParticipants = new HashSet<String>(Arrays.asList(new String[] { "33" }));
//
//	// random comment to make sure things can commit
//	public ACeciliaAnalyzer() {
//		propertyChangeSupport = new PropertyChangeSupport(this);
//		// whoever invokes the analyzer should set the replay mode
//		// DifficultyPredictionSettings.setReplayMode(true);
//		DifficultyPredictionSettings.setSegmentLength(SEGMENT_LENGTH);
//
//		reader = new EHLogReader();
//		participantsFolder = new AFileSetterModel(JFileChooser.DIRECTORIES_ONLY);
//		// participantsFolder.setText(DEFAULT_PARTICIPANT_DIRECTORY);
//		participantsFolder.setText(defaultParticipantDirectory());
//		parameters = new AnAnalyzerParameters(this);
//		parameters.getParticipants().addChoice(ALL_PARTICIPANTS);
//		parameters.getParticipants().setValue(ALL_PARTICIPANTS);
//		// difficultyEventProcessor =
//		// ADifficultyPredictionPluginEventProcessor.getInstance();
//
//		FactorySingletonInitializer.configure();
//
//	}
//
//	protected File getOrCreateLogLocation(String aParticipantId) {
//		String aFileName = Paths.get(outPath, aParticipantId, REPLAYED_LOG_FOLDER, aParticipantId).toString();
//		File aFile = new File(aFileName);
//		if (!aFile.exists()) {
//			try {
//				if (!aFile.getParentFile().exists()) {
//					aFile.getParentFile().mkdirs();
//				}
//				aFile.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return aFile;
//	}
//
//	public AnEclipseProgrammaticController programmaticController() {
//		return AnEclipseProgrammaticController.getInstance();
//	}
//
//	public static void emptyDirectory(File aDirectory) {
//		try {
//			Path aPath = Paths.get(aDirectory.getAbsolutePath());
//			Files.newDirectoryStream(aPath).forEach(aFile -> {
//				try {
//					Files.delete(aFile);
//				} catch (IOException e) {
//					throw new UncheckedIOException(e);
//				}
//			});
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	protected String projectName(String aParticipantId) {
//		return PROJECT_NAME_PREFIX + "_" + aParticipantId;
//	}
//
//	protected File getOrCreateEmptyProjectLocation(String aParticipantId) {
//		String aProjectLocation = Paths
//				.get(outPath, aParticipantId, REPLAYED_PROJECT_FOLDER, projectName(aParticipantId)).toString();
//		File aProjectDir = new File(aProjectLocation);
//		if (!aProjectDir.exists()) {
//			try {
//				if (!aProjectDir.exists()) {
//					aProjectDir.mkdirs();
//
//				}
//				// aFile.createNewFile();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		File anSrcFolder = Paths.get(aProjectDir.getAbsolutePath(), "src").toFile();
//		if (!anSrcFolder.exists()) {
//			anSrcFolder.mkdir();
//		}
//		emptyDirectory(anSrcFolder);
//		return aProjectDir;
//
//	}
//
//	protected Logger getLogger() {
//		return Logger.getLogger(Analyzer.class.getName());
//	}
//
//	public void removeLogHandlers(String aParticipantId) {
//		Handler[] aHandlers = getLogger().getHandlers();
//		for (Handler aHandler : aHandlers) {
//			FileHandler aFileHandler = (FileHandler) aHandler;
//			getLogger().removeHandler(aHandler);
//			aFileHandler.close();
//
//		}
//
//	}
//
//	public void resetProject(String aParticipantId, long aStartTimestamp) {
//		File aProjectLocation = getOrCreateEmptyProjectLocation(aParticipantId);
//		programmaticController().getOrCreateProject(projectName(aParticipantId), aProjectLocation.getAbsolutePath());
//
//	}
//
//	public void resetLogger(String aParticipantId, long aStartTimestamp) {
//		// Handler[] aHandlers = getLogger().getHandlers();
//		// for (Handler aHandler:aHandlers) {
//		// getLogger().removeHandler(aHandler);
//		// }
//		getLogger().setLevel(Level.FINE);
//
//		// File outputFile = null;
//		try {
//			File aLogLocation = getOrCreateLogLocation(aParticipantId);
//			// outputFile = new File(logLocation,
//			// EHEventRecorder.getUniqueMacroNameByTimestamp(getStartTimestamp(),
//			// false));
//			LogFileCreated.newCase(aLogLocation.getName(), this);
//
//			FileHandler handler = new FileHandler(aLogLocation.getPath());
//			handler.setEncoding("UTF-8");
//			handler.setFormatter(new EHXMLFormatter(aStartTimestamp));
//			getLogger().addHandler(handler);
//			LogHandlerBound.newCase(handler, this);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	protected void recordCommand(EHICommand aCommand) {
//		getLogger().log(Level.FINE, null, aCommand);
//	}
//
//	protected String defaultParticipantDirectory() {
//		return DEFAULT_PARTICIPANT_DIRECTORY;
//	}
//	// public String participantDirectoryName() {
//	// return participantsFolder.getLabel().getText();
//	// }
//
//	void notifyPre() {
//		propertyChangeSupport.firePropertyChange("this", null, this);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see analyzer.Analyzer#getParticipantsFolder()
//	 */
//	@Override
//	@Row(0)
//	public FileSetterModel getParticipantsFolder() {
//		return participantsFolder;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see analyzer.Analyzer#getParticipantsFolderName()
//	 */
//	@Override
//	@Visible(false)
//	public String getParticipantsFolderName() {
//		return participantsFolder.getText();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see analyzer.Analyzer#setParticipantsFolderName(java.lang.String)
//	 */
//	@Override
//	@Visible(false)
//	public void setParticipantsFolderName(String aName) {
//		participantsFolder.setText(aName);
//		notifyPre();
//	}
//
//	boolean directoryLoaded;
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see analyzer.Analyzer#preLoadDirectory()
//	 */
//	@Override
//	public boolean preLoadDirectory() {
//		return !directoryLoaded;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see analyzer.Analyzer#loadDirectory()
//	 */
//	@Override
//	@Visible(false)
//	public void loadDirectory() {
//		BufferedReader br = null;
//		try {
//			br = new BufferedReader(new FileReader(Paths.get(getParticipantsFolderName(), EXPERIMENTAL_DATA
//			// PARTICIPANT_INFORMATION_DIRECTORY
//					, PARTICIPANT_INFORMATION_FILE).toString()));
//			String word = null;
//			while ((word = br.readLine()) != null) {
//				String[] userInfo = word.split(",");
//				participants.put(userInfo[0].trim(), userInfo[1].trim());
//				parameters.getParticipants().addChoice(userInfo[0]);
//			}
//			br.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		directoryLoaded = true;
//		notifyPre();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see analyzer.Analyzer#preLoadLogs()
//	 */
//	@Override
//	public boolean preLoadLogs() {
//		return directoryLoaded;
//		// && !logsLoaded;
//	}
//
//	boolean logsLoaded;
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see analyzer.Analyzer#loadLogs()
//	 */
//	@Override
//	@Visible(false)
//	public void loadLogs(boolean createNewThread) {
//		if (createNewThread) {
//			final Runnable aRunnable = new Runnable() {
//				public void run() {
//					doLoadLogs();
//				}
//			};
//			Thread aThread = (new Thread(aRunnable));
//			aThread.setName("Replay thread for:" + parameters.getParticipants().getValue());
//
//			aThread.start();
//
//		} else
//			doLoadLogs();
//	}
//
//	/**
//	 * Loads stuck point from the stuckpoint.csv file into the hashmap.
//	 * 
//	 */
//	@Visible(false)
//	public void loadStuckPoint() {
//		CSVParser parser = new ACSVParser();
//		// try {
//		// parser.start(STUCKPOINT_FILE);
//		parser.start(stuckPointFile());
//
//		// } catch (FileNotFoundException e1) {
//		// // TODO Auto-generated catch block
//		// e1.printStackTrace();
//		// }
//
//		parser.getNextLine();
//		String line;
//		while ((line = parser.getNextLine()) != null) {
//			String[] split = line.split(",");
//
//			if (split.length > 0 && !split[0].trim().equals("")) {
//				StuckPoint p = new AStuckPoint();
//
//				String id = getId(split[0]);
//
//				try {
//					p.setDate(new SimpleDateFormat("hh:mm a").parse(split[1]));
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//				p.setType(split[2]);
//
//				if (stuckPoint.get(id) == null) {
//					stuckPoint.put(id, new PriorityQueue<StuckPoint>());
//
//				}
//
//				// get priority queue and add the new stuckpoint
//				stuckPoint.get(id).add((StuckPoint) p);
//
//			}
//
//		}
//
//		parser.stop();
//	}
//
//	private String getId(String participantName) {
//		for (String key : participants.keySet()) {
//			if (participants.get(key).equals(participantName)) {
//				return key;
//
//			}
//
//		}
//
//		return null;
//
//	}
//
//	/**
//	 * Loads stuck interval from the stuck interval csv into the hashmap
//	 * 
//	 * 
//	 */
//	@Visible(false)
//	public synchronized void loadStuckInterval() {
//		CSVParser parser = new ACSVParser();
//		// try {
//		// parser.start(STUCKINTERVAL_FILE);
//		parser.start(stuckIntervalsFile());
//
//		// } catch (FileNotFoundException e1) {
//		// // TODO Auto-generated catch block
//		// e1.printStackTrace();
//		// return;
//		// }
//
//		parser.getNextLine();
//		String line;
//		while ((line = parser.getNextLine()) != null) {
//			String[] split = line.split(",");
//
//			if (split.length > 0 && !split[0].trim().equals("")) {
//				StuckInterval p = new AStuckInterval();
//
//				p.setParticipant(split[0]);
//				String id = getId(split[0]);
//				;
//
//				try {
//					p.setDate(new SimpleDateFormat("HH:mm:ss").parse(split[1]));
//
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//				p.setBarrierType(split[2]);
//				p.setSurmountability(split[3]);
//
//				if (stuckInterval.get(id) == null) {
//					stuckInterval.put(id, new PriorityQueue<StuckInterval>());
//
//				}
//
//				// get the priority queue and add the new stuck interval
//				stuckInterval.get(id).add((StuckInterval) p);
//
//			}
//
//		}
//
//		parser.stop();
//
//	}
//
//	@Visible(false)
//
//	public void doLoadLogs() {
//		// FactorySingletonInitializer.configure();
//
//		String participantId = parameters.getParticipants().getValue();
//		String numberOfSegments = "" + parameters.getPredictionParameters().getSegmentLength();
//		// String numberOfSegments = "" +
//		// parameters.getPredictionParameters().getSegmentLength();
//		// String numberOfSegments = "" +
//		// parameters.getPredictionParameters().getSegmentLength();
//
//		// Queue q=this.stuckPoint.get("19");
//		// while(!q.isEmpty()) {
//		// System.out.println(q.poll());
//		//
//		// }
//
//		if (participantId.equalsIgnoreCase(""))
//			participantId = ALL_PARTICIPANTS;
//		//
//		// if(numberOfSegments.equalsIgnoreCase(""))
//		// numberOfSegments = "" + SEGMENT_LENGTH;
//
//		if (numberOfSegments.equalsIgnoreCase(""))
//			numberOfSegments = "" + SEGMENT_LENGTH;
//
//		// todo need to ask for discrete chunks or sliding window
//		// may d for discrete and s for sliding window
//
//		// scanIn.close();
//
//		// Now get all the participants in a list
//		List<String> participantList = new ArrayList<String>(Arrays.asList(participantId.split(" ")));
//		participantList.removeAll(Collections.singleton(""));
//
//		System.out.println("Processing logs for: " + participantId);
//		List<String> participantIds = new ArrayList<>(parameters.getParticipants().getChoices().size());
//		participantIds.addAll(parameters.getParticipants().getChoices());
//
//		if (!stuckFileLoaded) {
//			// Load the stuck points and such
//			loadStuckInterval();
//			loadStuckPoint();
//			stuckFileLoaded = true;
//		}
//		// if (parameters.isVisualizePrediction()) {
//		// PredictorConfigurer.visualizePrediction();
//		// }
//		// the main subdirectory we are putting files in
//		// String outPath = Paths.get(participantsFolder.getLabel().getText(),
//		// OUTPUT_DATA ).toString();
//		outPath = Paths.get(getParticipantsFolderName(), OUTPUT_DATA).toString();
//
//		// + this.outputSubdirectory;
//		if (participantList.get(0).equals(ALL_PARTICIPANTS)) {
//			// remove all from the participants
//			participantIds.remove(ALL_PARTICIPANTS);
//
//			List<String> allIgnores = new ArrayList<>();
//			// Build the ignore list
//			if (participantList.size() > 1 && participantList.get(1).equalsIgnoreCase(IGNORE_KEYWORD)) {
//				// take out as the ignore function is not actually a participant
//				participantIds.remove(participantId);
//				parameters.getParticipants().getChoices().remove(participantId);
//
//				// remove from the list of participants the ones we want to
//				// ignore
//				for (String ignore : participantList.subList(2, participantList.size())) {
//					List<String> participant = new ArrayList<>(Arrays.asList(ignore.split(",")));
//					participant.removeAll(Collections.singleton(""));
//
//					for (String p : participant) {
//						if (participantIds.contains(p)) {
//							participantIds.remove(p);
//
//							allIgnores.add(p);
//						}
//
//					}
//
//				}
//
//			}
//
//			// calculate new outputdirectory
//			// move this inside the loop so we create a separate output dir for
//			// each participant
//			// this.outputSubdirectory=outPath += participantId+"/";
//			// keep this for AnArffGenerator
//			this.outputSubdirectory = outPath + participantId;
//			notifyNewParticipant(ALL_PARTICIPANTS, null);
//			notifyReplayStarted();
//			// all if first on the list
//
//			for (String aParticipantId : participantIds) {
//				// processParticipant(aParticipantId,outPath,participantsFolder.getText()
//				// + EXPERIMENTAL_DATA +
//				// AnAnalyzer.participants.get(aParticipantId) + "/" +
//				// ECLIPSE_FOLDER,false);
//				if (ignoreParticipants.contains(aParticipantId))
//					continue;
//				this.outputSubdirectory = outPath + aParticipantId + "/";
//				// should there be a notifyNewParticipant here also
//				processParticipant(aParticipantId, this.outputSubdirectory,
//						// participantsFolder.getText() + EXPERIMENTAL_DATA,
//						Paths.get(getParticipantsFolderName(), EXPERIMENTAL_DATA).toString(),
//
//						// + AnAnalyzer.participants.get(aParticipantId) + "/" +
//						// ECLIPSE_FOLDER,
//						false);
//
//			}
//
//			notifyFinishParticipant(ALL_PARTICIPANTS, null);
//			notifyReplayFinished();
//
//		} else {
//			// String aParticipanttFolder = participants.get(participantId);
//			// this.outputSubdirectory = outPath + participantId + "/";
//			this.outputSubdirectory = Paths.get(outPath, participantId).toString();
//
//			processParticipant(participantId, this.outputSubdirectory,
//					Paths.get(getParticipantsFolderName(), EXPERIMENTAL_DATA).toString()
//					// participantsFolder.getText() + EXPERIMENTAL_DATA
//					// + aParticipanttFolder + "/" + ECLIPSE_FOLDER
//					, true);
//
//		}
//		// old stuff, in case we need to revert 12/20/2014
//
//		// if (participantId.equals(ALL_PARTICIPANTS)) {
//		// notifyNewParticipant(ALL_PARTICIPANTS, null);
//		// for (String aParticipantId:participantIds) {
//		// if (aParticipantId.equals(ALL_PARTICIPANTS)) {
//		// continue;
//		// }
//		// // integrated analyzer
//		// processParticipant(aParticipantId);
//		// // waitForParticipantLogsToBeProcessed();
//		//
//		//
//		// // jason's code
//		// // String aParticipanttFolder = participants.get(aParticipantId);
//		// // commandsList = convertXMLLogToObjects(aParticipanttFolder);
//		// // MainConsoleUI.processCommands(participantsFolder.getText(),
//		// commandsList, numberOfSegments,aParticipanttFolder);
//		// }
//		//
//		// notifyFinishParticipant(ALL_PARTICIPANTS, null);
//		// } else {
//		// String aParticipanttFolder = participants.get(participantId);
//		// processParticipant(participantId);
//		// // jason's code, separator mediator
//		// // commandsList = convertXMLLogToObjects(aParticipanttFolder);
//		// // DifficultyPredictionSettings.setRatiosFileName(aParticipanttFolder
//		// + "ratios.csv");
//		// // processParticipant(participantId);
//		// // MainConsoleUI.processCommands(participantsFolder.getText(),
//		// commandsList, numberOfSegments, aParticipanttFolder);
//		// }
//
//		logsLoaded = true;
//	}
//
//	@Visible(false)
//	public void processBrowserHistoryOfFolder(String aFolderName) {
//		String fullName = aFolderName;
//		File folder = new File(fullName);
//		if (!folder.exists()) {
//			System.out.println("folder does not exist:" + fullName);
//			return;
//		}
//		if (!folder.isDirectory()) {
//			System.out.println("folder not a directory:" + fullName);
//			return;
//		}
//		List<String> participantFiles = MainConsoleUI.getFilesForFolder(folder);
//		System.out.println("Particpant " + aFolderName + " has " + participantFiles.size() + " file(s)");
//		// System.out.println();
//		for (int i = 0; i < participantFiles.size(); i++) {
//			String aFileName = fullName + participantFiles.get(i);
//			if (!aFileName.endsWith(".txt"))
//				continue;
//
//			// List<ICommand> commands = reader.readAll(participantDirectory
//			// + participantFiles.get(i));
//			System.out.println("Reading " + aFileName);
//			processBrowserHistoryOfFile(aFileName);
//			//
//
//			// listOfListOFcommands.add(commands);
//		}
//		notifyFinishedBrowserLines();
//
//	}
//
//	@Visible(false)
//	public void storeBrowserHistoryOfFolder(String aFolderName) {
//		String fullName = aFolderName;
//		File folder = new File(fullName);
//		if (!folder.exists()) {
//			System.out.println("folder does not exist:" + fullName);
//			return;
//		}
//		if (!folder.isDirectory()) {
//			System.out.println("folder not a directory:" + fullName);
//			return;
//		}
//		List<String> participantFiles = MainConsoleUI.getFilesForFolder(folder);
//		System.out.println("Particpant " + aFolderName + " has " + participantFiles.size() + " file(s)");
//		// System.out.println();
//		sortedWebVisitQueue.clear();
//		sortedWebVisitCommands.clear();
//		webVisitsInFile.clear();
//		lastWebVisitCommandWithoutDuration = null;
//
//		for (int i = 0; i < participantFiles.size(); i++) {
//			String aFileName = Paths.get(fullName, participantFiles.get(i)).toString();
//			if (!aFileName.endsWith(".txt"))
//				continue;
//
//			// List<ICommand> commands = reader.readAll(participantDirectory
//			// + participantFiles.get(i));
//			System.out.println("Reading " + aFileName);
//			storeBrowserHistoryOfFile(aFileName);
//			//
//
//			// listOfListOFcommands.add(commands);
//		}
//		Collections.sort(sortedWebVisitQueue);
//
//	}
//
//	@Visible(false)
//	public void processBrowserHistoryOfFile(String aFileName) {
//		try {
//			FileInputStream fis = new FileInputStream(aFileName);
//
//			// Construct BufferedReader from InputStreamReader
//			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//
//			String line = null;
//			while ((line = br.readLine()) != null) {
//				// System.out.println(line);
//				notifyNewBrowseLine(line);
//			}
//
//			br.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//
//	}
//
//	@Visible(false)
//	public void storeBrowserHistoryOfFile(String aFileName) {
//		try {
//			FileInputStream fis = new FileInputStream(aFileName);
//
//			// Construct BufferedReader from InputStreamReader
//			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//
//			String line = null;
//			while ((line = br.readLine()) != null) {
//				// System.out.println(line);
//				WebVisitCommand aWebVisitCommand = toWebVisitCommand(line);
//				sortedWebVisitQueue.add(0, aWebVisitCommand);
//				webVisitsInFile.add(line);
//			}
//			sortedWebVisitCommands.addAll(sortedWebVisitQueue); // making
//																// shallow copy
//
//			br.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//
//	}
//
//	void waitForParticipantLogsToBeProcessed() {
//		try {
//			if (difficultyEventProcessor.getDifficultyPredictionThread() != null) {
//				// difficultyPredictionThread.join();
//				difficultyEventProcessor.getDifficultyPredictionThread().join();
//			} else {
//				System.out.println("Cannot wait for difficulty prediction thread to finish");
//			}
//		} catch (InterruptedException e) {
//
//			e.printStackTrace();
//		}
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see analyzer.Analyzer#processParticipant(java.lang.String)
//	 */
	@Override
	// modularize this method
	@Visible(false)
	public void processParticipant(String aParticipantId, String outPath, String dataPath, boolean isIndividualPart) {

		// if (parameters.isVisualizePrediction()) {
		// PredictorConfigurer.visualizePrediction();
		// }
		parameters.getParticipants().setValue(aParticipantId);
		String aParticipantFolder = participants.get(aParticipantId);
		// notifyNewParticipant(aParticipantId);
		// we now get the correct outpath with individual folder
		String aFullParticipantOutputFolderName = outPath;
		// String aFullParticipantOutputFolderName = outPath+(isIndividualPart?
		// aParticipantFolder+"/":"");
		// String aFullParticipantDataFolderName = dataPath + aParticipantFolder
		// + "/" + ECLIPSE_FOLDER;
		String aFullParticipantDataFolderName = Paths.get(dataPath, aParticipantFolder, ECLIPSE_FOLDER).toString();
		File anOutputFolder = new File(aFullParticipantOutputFolderName);
		if (!anOutputFolder.exists())
			anOutputFolder.mkdirs();

		// if (isIndividualPart) {
		//
		// String aFullRatiosFileName = aFullParticipantOutputFolderName
		// + "ratios.csv";
		String aFullRatiosFileName = Paths.get(aFullParticipantOutputFolderName, "ratios.csv").toString();
		File aRatiosFile = new File(aFullRatiosFileName);
		if (aRatiosFile.exists()) {
			DifficultyPredictionSettings.setRatioFileExists(true);

		} else {
			// if (!aRatiosFile.exists())
			try {
				DifficultyPredictionSettings.setRatioFileExists(false);
				aRatiosFile.createNewFile();
			} catch (IOException e1) {

				e1.printStackTrace();
			}
		}

		// erase file if it exists
		// if (aRatiosFile.exists() &&
		// DifficultyPredictionSettings.isNewRatioFiles()) {
		if (DifficultyPredictionSettings.isRatioFileExists() && DifficultyPredictionSettings.isNewRatioFiles()) {

			try {
				FileOutputStream writer = new FileOutputStream(aRatiosFile);
				writer.close();

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
			}

			DifficultyPredictionSettings.setCreateRatioFile(isIndividualPart);
		}
		// we will replay commands in both cases
		nestedCommandsList = convertXMLLogToObjects(aFullParticipantDataFolderName);

		if (DifficultyPredictionSettings.isRatioFileExists() && DifficultyPredictionSettings.isReplayRatioFiles()) {
			// System.out.println
			// ("Need to read ratio file and replay logs");
			RatioFileGeneratorFactory.setSingleton(FileReplayAnalyzerProcessorFactory.getSingleton());
			notifyNewParticipant(aParticipantId, aParticipantFolder); // should
			// probably
			// factor
			// this
			// out
			RatioFilePlayerFactory.getSingleton().setReplayedData(nestedCommandsList, aRatiosFile.getAbsolutePath());
			RatioFilePlayerFactory.getSingleton().replay();
			// ratioFileReader = new ARatioFileReader();
			// ratioFileReader.readFile(aRatiosFile.getAbsolutePath());
		} else {

			// nestedCommandsList =
			// convertXMLLogToObjects(aFullParticipantDataFolderName);
			TimeStampComputerFactory.getSingleton().reset(); // this is called
																// by
																// setRpelayedData
			DifficultyPredictionSettings.setRatiosFileName(aFullRatiosFileName);
			// moving this up in the constructor so we do not configure many
			// times
			// difficultyEventProcessor = new
			// ADifficultyPredictionPluginEventProcessor();
			difficultyEventProcessor = ADifficultyPredictionPluginEventProcessor.getInstance();

			// ADifficultyPredictionPluginEventProcessor
			// .setInstance(difficultyEventProcessor);
			difficultyEventProcessor.commandProcessingStarted();
			mediator = difficultyEventProcessor.getDifficultyPredictionRunnable().getMediator();

			eventAggregator = mediator.getEventAggregator();

			// eventAggregator
			// .setEventAggregationStrategy(new DiscreteChunksAnalyzer(""
			// + PredictionParametersSetterSelector.getSingleton()
			// .getSegmentLength()));

//			eventAggregator.setEventAggregationStrategy(
//					new AWindowAggregator("" + PredictionParametersSetterSelector.getSingleton().getSegmentLength()));

			notifyNewParticipant(aParticipantId, aParticipantFolder);
			storeBrowserHistoryOfFolder(Paths.get(getParticipantsFolderName(), EXPERIMENTAL_DATA,
					// aParticipantFolder + "/"
					aParticipantFolder,

					BROWSER_FOLDER).toString());
			playNestedCommandList();

			difficultyEventProcessor.commandProcessingStopped();
			waitForParticipantLogsToBeProcessed();

			// maybe do this before notifying events so we can use the info in
			// prediction
			// getting rid of call as we have already read the lines
			// processBrowserHistoryOfFolder(participantsFolder.getText()
			// + EXPERIMENTAL_DATA + aParticipantFolder + "/"
			// + BROWSER_FOLDER);

			notifyAllWebVisitsInFile();
			notifyAllWebCommandsInFile();

			notifyFinishParticipant(aParticipantId, aParticipantFolder);

			// }
		}

	}

}
