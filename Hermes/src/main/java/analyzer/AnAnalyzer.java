package analyzer;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

import javax.swing.JFileChooser;

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
import fluorite.util.EHLogReader;
import util.annotations.LayoutName;
import util.annotations.Row;
import util.annotations.Visible;

@LayoutName(AttributeNames.GRID_BAG_LAYOUT)
/**
 * Loads the experimental directory.
 * reads specified command logs
 * Combines all log files for an experiment into a nested command list.
 * Replays a flattenned version of the log.
 * Prints the replay
 * Each command is sent to a difficulty processor so that new predictions can be made and visualized
 * The difficulty pipe line will fire events
 * Fires events for  replay of stored events also.
 * The stored replay can be used to generate ratio files,
 * Also replays ratio files that can be then visualized.
 *
 * 
 * @author dewan
 *
 */
public class AnAnalyzer implements Analyzer {
	public static final String PARTICIPANT_DIRECTORY = "data/";
	public static final String EXPERIMENTAL_DATA = "ExperimentalData/";
	public static final String OUTPUT_DATA = "OutputData/";

	public static final String ECLIPSE_FOLDER = "Eclipse/";
	public static final String BROWSER_FOLDER = "Browser/";

	public static final String STUCKPOINT_FILE = "data/GroundTruth/Stuckpoints.csv";
	public static final String STUCKINTERVAL_FILE = "data/GroundTruth/Stuck Intervals.csv";

	public static final String PARTICIPANT_INFORMATION_DIRECTORY = "data/ExperimentalData/";
	public static final String PARTICIPANT_OUTPUT_DIRECTORY = "data/OutputData/";

	public static final String PARTICIPANT_INFORMATION_FILE = "Participant_Info.csv";
	public static final String RATIOS_FILE_NAME = "ratios.csv";
	public static final int SEGMENT_LENGTH = 50;
	public static final String ALL_PARTICIPANTS = "All";
	public static final String IGNORE_KEYWORD = "IGNORE";
	static final Hashtable<String, String> participants = new Hashtable<String, String>();

	// do not make public, we only need to fill these maps once, must uphold
	// that they are unmodifiable via getter methods
	private static Map<String, Queue<StuckPoint>> stuckPoint = new HashMap<>();
	private static Map<String, Queue<StuckInterval>> stuckInterval = new HashMap<>();

	static boolean stuckFileLoaded = false;
	static RatioFileReader ratioFileReader;

	static long startTimeStamp;
	List<List<EHICommand>> nestedCommandsList;

	FileSetterModel participantsFolder, outputFolder, experimentalData;
	AnalyzerParameters parameters;
	EHLogReader reader;
	// protected Thread difficultyPredictionThread;
	// protected DifficultyPredictionRunnable difficultyPredictionRunnable;
	// protected BlockingQueue<ICommand> pendingPredictionCommands;
	DifficultyPredictionPluginEventProcessor difficultyEventProcessor;
	List<AnalyzerListener> listeners = new ArrayList<>();
	PropertyChangeSupport propertyChangeSupport;
	// int currentParticipant = -1;

	// subdirectory inside of OutputData to put outputs, note that it can be
	// different for each instance of analyzer
	private String outputSubdirectory = "";
	int lastPrediction;
	int lastCorrection;
	Mediator mediator;

	EventAggregator eventAggregator;

	// random comment to make sure things can commit
	public AnAnalyzer() {
		propertyChangeSupport = new PropertyChangeSupport(this);
		// whoever invokes the analyzer should set the replay mode
		// DifficultyPredictionSettings.setReplayMode(true);
		DifficultyPredictionSettings.setSegmentLength(SEGMENT_LENGTH);

		reader = new EHLogReader();
		participantsFolder = new AFileSetterModel(JFileChooser.DIRECTORIES_ONLY);
		participantsFolder.setText(PARTICIPANT_DIRECTORY);
		parameters = new AnAnalyzerParameters(this);
		parameters.getParticipants().addChoice(ALL_PARTICIPANTS);
		parameters.getParticipants().setValue(ALL_PARTICIPANTS);
//		difficultyEventProcessor = ADifficultyPredictionPluginEventProcessor.getInstance();

		FactorySingletonInitializer.configure();


	}

	void notifyPre() {
		propertyChangeSupport.firePropertyChange("this", null, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see analyzer.Analyzer#getParticipantsFolder()
	 */
	@Override
	@Row(0)
	public FileSetterModel getParticipantsFolder() {
		return participantsFolder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see analyzer.Analyzer#getParticipantsFolderName()
	 */
	@Override
	@Visible(false)
	public String getParticipantsFolderName() {
		return participantsFolder.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see analyzer.Analyzer#setParticipantsFolderName(java.lang.String)
	 */
	@Override
	@Visible(false)
	public void setParticipantsFolderName(String aName) {
		participantsFolder.setText(aName);
		notifyPre();
	}

	boolean directoryLoaded;

	/*
	 * (non-Javadoc)
	 * 
	 * @see analyzer.Analyzer#preLoadDirectory()
	 */
	@Override
	public boolean preLoadDirectory() {
		return !directoryLoaded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see analyzer.Analyzer#loadDirectory()
	 */
	@Override
	@Visible(false)
	public void loadDirectory() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(participantsFolder
					.getLabel().getText() + EXPERIMENTAL_DATA
			// PARTICIPANT_INFORMATION_DIRECTORY
					+ PARTICIPANT_INFORMATION_FILE));
			String word = null;
			while ((word = br.readLine()) != null) {
				String[] userInfo = word.split(",");
				participants.put(userInfo[0].trim(), userInfo[1].trim());
				parameters.getParticipants().addChoice(userInfo[0]);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		directoryLoaded = true;
		notifyPre();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see analyzer.Analyzer#preLoadLogs()
	 */
	@Override
	public boolean preLoadLogs() {
		return directoryLoaded;
		// && !logsLoaded;
	}

	boolean logsLoaded;

	/*
	 * (non-Javadoc)
	 * 
	 * @see analyzer.Analyzer#loadLogs()
	 */
	@Override
	@Visible(false)
	public void loadLogs(boolean createNewThread) {
		if (createNewThread) {
			final Runnable aRunnable = new Runnable() {
				public void run() {
					doLoadLogs();
				}
			};
			Thread aThread = (new Thread(aRunnable));
			aThread.setName("Replay thread for:"
					+ parameters.getParticipants().getValue());

			aThread.start();

		} else
			doLoadLogs();
	}

	/**
	 * Loads stuck point from the stuckpoint.csv file into the hashmap.
	 * 
	 */
	public void loadStuckPoint() {
		CSVParser parser = new ACSVParser();
		// try {
		parser.start(STUCKPOINT_FILE);
		// } catch (FileNotFoundException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		parser.getNextLine();
		String line;
		while ((line = parser.getNextLine()) != null) {
			String[] split = line.split(",");

			if (split.length > 0 && !split[0].trim().equals("")) {
				StuckPoint p = new AStuckPoint();

				String id = getId(split[0]);

				try {
					p.setDate(new SimpleDateFormat("hh:mm a").parse(split[1]));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				p.setType(split[2]);

				if (stuckPoint.get(id) == null) {
					stuckPoint.put(id, new PriorityQueue<StuckPoint>());

				}

				// get priority queue and add the new stuckpoint
				stuckPoint.get(id).add((StuckPoint) p);

			}

		}

		parser.stop();
	}

	private String getId(String participantName) {
		for (String key : participants.keySet()) {
			if (participants.get(key).equals(participantName)) {
				return key;

			}

		}

		return null;

	}

	/**
	 * Loads stuck interval from the stuck interval csv into the hashmap
	 * 
	 * 
	 */
	public synchronized void loadStuckInterval() {
		CSVParser parser = new ACSVParser();
		// try {
		parser.start(STUCKINTERVAL_FILE);
		// } catch (FileNotFoundException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// return;
		// }

		parser.getNextLine();
		String line;
		while ((line = parser.getNextLine()) != null) {
			String[] split = line.split(",");

			if (split.length > 0 && !split[0].trim().equals("")) {
				StuckInterval p = new AStuckInterval();

				p.setParticipant(split[0]);
				String id = getId(split[0]);
				;

				try {
					p.setDate(new SimpleDateFormat("HH:mm:ss").parse(split[1]));

				} catch (ParseException e) {
					e.printStackTrace();
				}
				p.setBarrierType(split[2]);
				p.setSurmountability(split[3]);

				if (AnAnalyzer.stuckInterval.get(id) == null) {
					AnAnalyzer.stuckInterval.put(id,
							new PriorityQueue<StuckInterval>());

				}

				// get the priority queue and add the new stuck interval
				AnAnalyzer.stuckInterval.get(id).add((StuckInterval) p);

			}

		}

		parser.stop();

	}

	public void doLoadLogs() {
//		FactorySingletonInitializer.configure();

		String participantId = parameters.getParticipants().getValue();
		String numberOfSegments = ""
				+ parameters.getPredictionParameters().getSegmentLength();
		// String numberOfSegments = "" +
		// parameters.getPredictionParameters().getSegmentLength();
		// String numberOfSegments = "" +
		// parameters.getPredictionParameters().getSegmentLength();

		// Queue q=this.stuckPoint.get("19");
		// while(!q.isEmpty()) {
		// System.out.println(q.poll());
		//
		// }

		if (participantId.equalsIgnoreCase(""))
			participantId = ALL_PARTICIPANTS;
		//
		// if(numberOfSegments.equalsIgnoreCase(""))
		// numberOfSegments = "" + SEGMENT_LENGTH;

		if (numberOfSegments.equalsIgnoreCase(""))
			numberOfSegments = "" + SEGMENT_LENGTH;

		// todo need to ask for discrete chunks or sliding window
		// may d for discrete and s for sliding window

		// scanIn.close();

		// Now get all the participants in a list
		List<String> participantList = new ArrayList<String>(
				Arrays.asList(participantId.split(" ")));
		participantList.removeAll(Collections.singleton(""));

		System.out.println("Processing logs for: " + participantId);
		List<String> participantIds = new ArrayList<>(parameters
				.getParticipants().getChoices().size());
		participantIds.addAll(parameters.getParticipants().getChoices());

		if (!stuckFileLoaded) {
			// Load the stuck points and such
			loadStuckInterval();
			loadStuckPoint();
			stuckFileLoaded = true;
		}
		// if (parameters.isVisualizePrediction()) {
		// PredictorConfigurer.visualizePrediction();
		// }
		// the main subdirectory we are putting files in
		String outPath = participantsFolder.getText() + OUTPUT_DATA;
		// + this.outputSubdirectory;
		if (participantList.get(0).equals(ALL_PARTICIPANTS)) {
			// remove all from the participants
			participantIds.remove(ALL_PARTICIPANTS);

			List<String> allIgnores = new ArrayList<>();
			// Build the ignore list
			if (participantList.size() > 1
					&& participantList.get(1).equalsIgnoreCase(IGNORE_KEYWORD)) {
				// take out as the ignore function is not actually a participant
				participantIds.remove(participantId);
				parameters.getParticipants().getChoices().remove(participantId);

				// remove from the list of participants the ones we want to
				// ignore
				for (String ignore : participantList.subList(2,
						participantList.size())) {
					List<String> participant = new ArrayList<>(
							Arrays.asList(ignore.split(",")));
					participant.removeAll(Collections.singleton(""));

					for (String p : participant) {
						if (participantIds.contains(p)) {
							participantIds.remove(p);

							allIgnores.add(p);
						}

					}

				}

			}

			// calculate new outputdirectory
			// move this inside the loop so we create a separate output dir for
			// each participant
			// this.outputSubdirectory=outPath += participantId+"/";
			// keep this for AnArffGenerator
			this.outputSubdirectory = outPath + participantId;
			notifyNewParticipant(ALL_PARTICIPANTS, null);
			// all if first on the list

			for (String aParticipantId : participantIds) {
				// processParticipant(aParticipantId,outPath,participantsFolder.getText()
				// + EXPERIMENTAL_DATA +
				// AnAnalyzer.participants.get(aParticipantId) + "/" +
				// ECLIPSE_FOLDER,false);
				this.outputSubdirectory = outPath + aParticipantId + "/";
				// should there be a notifyNewParticipant here also
				processParticipant(aParticipantId, this.outputSubdirectory,
						participantsFolder.getText() + EXPERIMENTAL_DATA,
						// + AnAnalyzer.participants.get(aParticipantId) + "/" +
						// ECLIPSE_FOLDER,
						false);

			}

			notifyFinishParticipant(ALL_PARTICIPANTS, null);

		} else {
			// String aParticipanttFolder = participants.get(participantId);
			this.outputSubdirectory = outPath + participantId + "/";
			processParticipant(participantId, this.outputSubdirectory,
					participantsFolder.getText() + EXPERIMENTAL_DATA
					// + aParticipanttFolder + "/" + ECLIPSE_FOLDER
					, true);

		}
		// old stuff, in case we need to revert 12/20/2014

		// if (participantId.equals(ALL_PARTICIPANTS)) {
		// notifyNewParticipant(ALL_PARTICIPANTS, null);
		// for (String aParticipantId:participantIds) {
		// if (aParticipantId.equals(ALL_PARTICIPANTS)) {
		// continue;
		// }
		// // integrated analyzer
		// processParticipant(aParticipantId);
		// // waitForParticipantLogsToBeProcessed();
		//
		//
		// // jason's code
		// // String aParticipanttFolder = participants.get(aParticipantId);
		// // commandsList = convertXMLLogToObjects(aParticipanttFolder);
		// // MainConsoleUI.processCommands(participantsFolder.getText(),
		// commandsList, numberOfSegments,aParticipanttFolder);
		// }
		//
		// notifyFinishParticipant(ALL_PARTICIPANTS, null);
		// } else {
		// String aParticipanttFolder = participants.get(participantId);
		// processParticipant(participantId);
		// // jason's code, separator mediator
		// // commandsList = convertXMLLogToObjects(aParticipanttFolder);
		// // DifficultyPredictionSettings.setRatiosFileName(aParticipanttFolder
		// + "ratios.csv");
		// // processParticipant(participantId);
		// // MainConsoleUI.processCommands(participantsFolder.getText(),
		// commandsList, numberOfSegments, aParticipanttFolder);
		// }

		logsLoaded = true;
	}

	public void processBrowserHistoryOfFolder(String aFolderName) {
		String fullName = aFolderName;
		File folder = new File(fullName);
		if (!folder.exists()) {
			System.out.println("folder does not exist:" + fullName);
			return;
		}
		if (!folder.isDirectory()) {
			System.out.println("folder not a directory:" + fullName);
			return;
		}
		List<String> participantFiles = MainConsoleUI.getFilesForFolder(folder);
		System.out.println("Particpant " + aFolderName + " has "
				+ participantFiles.size() + " file(s)");
		// System.out.println();
		for (int i = 0; i < participantFiles.size(); i++) {
			String aFileName = fullName + participantFiles.get(i);
			if (!aFileName.endsWith(".txt"))
				continue;

			// List<ICommand> commands = reader.readAll(participantDirectory
			// + participantFiles.get(i));
			System.out.println("Reading " + aFileName);
			processBrowserHistoryOfFile(aFileName);
			//

			// listOfListOFcommands.add(commands);
		}
		notifyFinishedBrowserLines();

	}

	public void processBrowserHistoryOfFile(String aFileName) {
		try {
			FileInputStream fis = new FileInputStream(aFileName);

			// Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			String line = null;
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				notifyNewBrowseLine(line);
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	void waitForParticipantLogsToBeProcessed() {
		try {
			// difficultyPredictionThread.join();
			difficultyEventProcessor.getDifficultyPredictionThread().join();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see analyzer.Analyzer#processParticipant(java.lang.String)
	 */
	@Override
	// modularize this method
	public void processParticipant(String aParticipantId, String outPath,
			String dataPath, boolean isIndividualPart) {

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
		String aFullParticipantDataFolderName = dataPath + aParticipantFolder
				+ "/" + ECLIPSE_FOLDER;
		File anOutputFolder = new File(aFullParticipantOutputFolderName);
		if (!anOutputFolder.exists())
			anOutputFolder.mkdirs();

		// if (isIndividualPart) {

		String aFullRatiosFileName = aFullParticipantOutputFolderName
				+ "ratios.csv";
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
		if (DifficultyPredictionSettings.isRatioFileExists()
				&& DifficultyPredictionSettings.isNewRatioFiles()) {

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

		if (DifficultyPredictionSettings.isRatioFileExists()
				&& DifficultyPredictionSettings.isReplayRatioFiles()) {
			// System.out.println
			// ("Need to read ratio file and replay logs");
			RatioFileGeneratorFactory
					.setSingleton(FileReplayAnalyzerProcessorFactory
							.getSingleton());
			notifyNewParticipant(aParticipantId, aParticipantFolder); // should
																		// probably
																		// factor
																		// this
																		// out
			RatioFilePlayerFactory.getSingleton().setReplayedData(
					nestedCommandsList, aRatiosFile.getAbsolutePath());
			RatioFilePlayerFactory.getSingleton().replay();
			// ratioFileReader = new ARatioFileReader();
			// ratioFileReader.readFile(aRatiosFile.getAbsolutePath());
		} else {

			// nestedCommandsList =
			// convertXMLLogToObjects(aFullParticipantDataFolderName);
			DifficultyPredictionSettings.setRatiosFileName(aFullRatiosFileName);
			//moving this up in the constructor so we do not configure many times
			difficultyEventProcessor = new ADifficultyPredictionPluginEventProcessor();
			ADifficultyPredictionPluginEventProcessor
					.setInstance(difficultyEventProcessor);
			difficultyEventProcessor.commandProcessingStarted();
			 mediator = difficultyEventProcessor
					.getDifficultyPredictionRunnable().getMediator();

			 eventAggregator = mediator.getEventAggregator();
			eventAggregator
					.setEventAggregationStrategy(new DiscreteChunksAnalyzer(""
							+ PredictionParametersSetterSelector.getSingleton()
									.getSegmentLength()));
			notifyNewParticipant(aParticipantId, aParticipantFolder);
			playNestedCommandList();

//			startTimeStamp = 0;
//			for (int index = 0; index < nestedCommandsList.size(); index++) {
//				List<ICommand> commands = nestedCommandsList.get(index);
//				for (int i = 0; i < commands.size(); i++) {
//					ICommand aCommand = commands.get(i);
//					maybeProcessPrediction(aCommand);
//					maybeProcessCorrection(aCommand);
//
//					if ((aCommand.getTimestamp() == 0)
//							&& (aCommand.getTimestamp2() > 0)) { // this is
//																	// always a
//																	// difficulty
//																	// status
//																	// command
//						startTimeStamp = commands.get(i).getTimestamp2();
//						difficultyEventProcessor.newCommand(aCommand);
//
//						notifyStartTimeStamp(startTimeStamp);
//
//					} else {
//						eventAggregator.setStartTimeStamp(startTimeStamp); // not
//																			// sure
//																			// this
//																			// is
//																			// ever
//																			// useful
//						try {
//							// pendingPredictionCommands.put(commands.get(i));
//							// System.out.println("Put command:" +
//							// commands.get(i));
//							// difficultyEventProcessor.recordCommand(commands.get(i));
//							difficultyEventProcessor.newCommand(aCommand);
//
//							// } catch (InterruptedException e) {
//						} catch (Exception e) {
//
//							e.printStackTrace();
//						}
//
//						// eventAggregator.getEventAggregationStrategy().performAggregation(commands.get(i),
//						// eventAggregator);
//
//					}
//
//				}
				// }

				difficultyEventProcessor.commandProcessingStopped();
				waitForParticipantLogsToBeProcessed();

				// pendingPredictionCommands.add(new AnEndOfQueueCommand());
				// try {
				// // difficultyPredictionThread.join();
				// difficultyEventProcessor.getDifficultyPredictionThread().join();
				// } catch (InterruptedException e) {
				//
				// e.printStackTrace();
				// }
				//maybe do this before notifying events so we can use the info in prediction
				processBrowserHistoryOfFolder(participantsFolder.getText()
						+ EXPERIMENTAL_DATA + aParticipantFolder + "/"
						+ BROWSER_FOLDER);

				notifyFinishParticipant(aParticipantId, aParticipantFolder);
				// for (ICommand aCommand: commandsList) {
				//
				// }
				
//			}
		}

	}
	
	protected void playNestedCommandList() {
		startTimeStamp = 0;
		for (int index = 0; index < nestedCommandsList.size(); index++) {
			List<EHICommand> commands = nestedCommandsList.get(index);
			for (int i = 0; i < commands.size(); i++) {
				EHICommand aCommand = commands.get(i);
				processStoredCommand(aCommand);

				boolean isPrediction = maybeProcessPrediction(aCommand);
				boolean isCorrection = maybeProcessCorrection(aCommand);
				if (!isPrediction && !isCorrection) {
					processStoredInputCommand(aCommand);
				}
				if (!DifficultyPredictionSettings.isMakePredictions())
					continue;
				// should we replay difficulty corrections since these were stored
				// and thus make sense only if diifculty predictions were wrong
				// they are certainly correct status so perhaps not a bad idea
				// the difficulty predictions certainly should not be replayed since
				// we make new predictions
				if ((aCommand.getTimestamp() == 0)
						&& (aCommand.getTimestamp2() > 0)) { // this is
																// always a
																// difficulty
																// status
																// command
					startTimeStamp = commands.get(i).getTimestamp2();
					if (!isPrediction) { // added this
					difficultyEventProcessor.newCommand(aCommand);
					}

					notifyStartTimeStamp(startTimeStamp);

				} else {
					eventAggregator.setStartTimeStamp(startTimeStamp); // not
																		// sure
																		// this
																		// is
																		// ever
																		// useful
					try {
						// pendingPredictionCommands.put(commands.get(i));
						// System.out.println("Put command:" +
						// commands.get(i));
						// difficultyEventProcessor.recordCommand(commands.get(i));
						if (!isPrediction) {
						difficultyEventProcessor.newCommand(aCommand);
						}

						// } catch (InterruptedException e) {
					} catch (Exception e) {

						e.printStackTrace();
					}

					// eventAggregator.getEventAggregationStrategy().performAggregation(commands.get(i),
					// eventAggregator);

				}

			}
		}
	}
		

	/*
	 * (non-Javadoc)
	 * 
	 * @see analyzer.Analyzer#convertXMLLogToObjects(java.lang.String)
	 */
	@Override
	public List<List<EHICommand>> convertXMLLogToObjects(String aFolderName) {

		List<List<EHICommand>> listOfListOFcommands = new Vector<List<EHICommand>>();
		// String fullName = participantsFolder.getText()
		// + aFolderName + "/";
		String fullName = aFolderName;
		File folder = new File(fullName);
		if (!folder.exists()) {
			System.out.println("folder does not exist:" + fullName);
			return listOfListOFcommands;
		}
		if (!folder.isDirectory()) {
			System.out.println("folder not a directory:" + fullName);
			return listOfListOFcommands;
		}
		List<String> participantFiles = MainConsoleUI.getFilesForFolder(folder);
		System.out.println("Particpant " + aFolderName + " has "
				+ participantFiles.size() + " file(s)");

		for (int i = 0; i < participantFiles.size(); i++) {
			String aFileName = fullName + participantFiles.get(i);
			if (!aFileName.endsWith(".xml"))
				continue;

			// List<ICommand> commands = reader.readAll(participantDirectory
			// + participantFiles.get(i));
			System.out.println("Reading " + aFileName);
			// List<ICommand> commands;
			try {
//				List<EHICommand> commands = reader.readAll(aFileName);
				List<EHICommand> commands = reader.readAll(aFileName);

				listOfListOFcommands.add(commands);

			} catch (Exception e) {
				System.out.println("Could not read file" + aFileName + e);

			}

			// listOfListOFcommands.add(commands);
		}

		return listOfListOFcommands;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see analyzer.Analyzer#getParameters()
	 */
	@Override
	@Row(1)
	// @Visible(false)
	public AnalyzerParameters getAnalyzerParameters() {
		return parameters;
	}

	// let us do this in the analyzerprocessor
	public static void maybeRecordFeatures(RatioFeatures details) {

		if (!DifficultyPredictionSettings.isNewRatioFiles()
				&& DifficultyPredictionSettings.isRatioFileExists())
			return;
		return;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see analyzer.Analyzer#getDifficultyEventProcessor()
	 */
	@Override
	@Visible(false)
	public DifficultyPredictionPluginEventProcessor getDifficultyEventProcessor() {
		return difficultyEventProcessor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see analyzer.Analyzer#setDifficultyEventProcessor(difficultyPrediction.
	 * DifficultyPredictionPluginEventProcessor)
	 */
	@Override
	public void setDifficultyEventProcessor(
			DifficultyPredictionPluginEventProcessor difficultyEventProcessor) {
		this.difficultyEventProcessor = difficultyEventProcessor;
	}

	@Override
	public void addAnalyzerListener(AnalyzerListener aListener) {
		listeners.add(aListener);
	}

	@Override
	public void removeAnalyzerListener(AnalyzerListener aListener) {
		listeners.remove(aListener);
	}

	@Override
	public void notifyNewCorrectStatus(int aStatus) {
		for (AnalyzerListener aListener : listeners) {
			aListener.newCorrectStatus(aStatus);
		}
	}
	@Override
	public void notifyNewPrediction(PredictionType aPredictionType) {
		for (AnalyzerListener aListener : listeners) {
			aListener.newPrediction(aPredictionType);

		}
	}
	
	@Override
	public void notifyNewCorrectStatus(Status aStatus) {
		for (AnalyzerListener aListener : listeners) {
			aListener.newCorrectStatus(aStatus);
		}
	}
	
	@Override
	public void notifyNewStoredCommand(EHICommand aCommand) {
		for (AnalyzerListener aListener : listeners) {
			aListener.newStoredCommand(aCommand);
		}
		
	}
	
	@Override
	public void notifyNewStoredInputCommand(EHICommand aCommand) {
		for (AnalyzerListener aListener : listeners) {
			aListener.newStoredInputCommand(aCommand);
		}
		
	}

	@Override
	public void notifyNewParticipant(String anId, String aFolder) {
		for (AnalyzerListener aListener : listeners) {
			aListener.newParticipant(anId, aFolder);
		}
	}

	@Override
	public void notifyFinishParticipant(String anId, String aFolder) {
		for (AnalyzerListener aListener : listeners) {
			aListener.finishParticipant(anId, aFolder);
		}
	}

	@Override
	public void notifyStartTimeStamp(long aStartTimeStamp) {
		for (AnalyzerListener aListener : listeners) {
			aListener.startTimeStamp(aStartTimeStamp);
		}
	}

	@Override
	public void notifyNewBrowseLine(String aLine) {
		for (AnalyzerListener aListener : listeners) {
			aListener.newBrowseLine(aLine);
			String[] parts = aLine.split("\t");
			String[] dateParts = parts[0].split(" ");
			String dateString = dateParts[0] + " " + dateParts[1];
			Date aDate = new Date(dateString);
			aListener.newBrowseEntries(aDate, parts[1], parts[2]);
		}
	}

	public void notifyFinishedBrowserLines() {
		for (AnalyzerListener aListener : listeners) {
			aListener.finishedBrowserLines();

		}
	}

	static Analyzer instance;

	@Visible(false)
	public static Analyzer getInstance() {
		return AnalyzerFactory.getSingleton();
		// if (instance == null) {
		// instance = new AnAnalyzer();
		// }
		// return instance;
	}

	@Override
	@Visible(false)
	public Map<String, Queue<StuckPoint>> getStuckPointMap() {
		return Collections.unmodifiableMap(stuckPoint);

	}

	@Visible(false)
	public Map<String, Queue<StuckInterval>> getStuckIntervalMap() {
		return Collections.unmodifiableMap(stuckInterval);

	}

	@Override
	public void setOutputSubDirectory(String outputDir) {
		this.outputSubdirectory = outputDir;

	}

	/**
	 * Grab the output directory
	 * 
	 */
	@Override
	@Visible(false)
	public String getOutputDirectory() {
		return this.outputSubdirectory;

	}

	@Override
	@Visible(false)
	public AnalyzerParameters getParameterSelector() {
		return this.parameters;

	}

	@Visible(false)
	public static void main(String[] args) {

		// Analyzer analyzer = new AnAnalyzer();
		DifficultyPredictionSettings.setReplayMode(true);

		OEFrame frame = ObjectEditor.edit(AnAnalyzer.getInstance());
		frame.setSize(500, 335);
//		ObjectEditor.edit(AnAnalyzer.getInstance(), 500, 335);

	}

	boolean maybeProcessPrediction(EHICommand newCommand) {
		if (newCommand instanceof PredictionCommand) {
			lastPrediction = ARatioFileGenerator
					.toInt((PredictionCommand) newCommand);
//			System.out.println("Prediction command at time stamp:" + newCommand + " " + newCommand.getTimestamp());
//			notifyNewCorrectStatus(lastPrediction);
			notifyNewPrediction(((PredictionCommand) newCommand).getPredictionType());
			return true;
		}
		return false;
	}
	
	void processStoredCommand(EHICommand aCommand) {
		notifyNewStoredCommand(aCommand);
	}
	
	void processStoredInputCommand(EHICommand aCommand) {
		notifyNewStoredInputCommand(aCommand);
	}

	boolean maybeProcessCorrection(EHICommand newCommand) {
		if (newCommand instanceof DifficultyCommand
		// && ((DifficulyStatusCommand) newCommand).getStatus() != null
		) {
			lastCorrection = ARatioFileGenerator
					.toInt((DifficultyCommand) newCommand);
			notifyNewCorrectStatus(lastCorrection);
			notifyNewCorrectStatus(((DifficultyCommand) newCommand).getStatus());
			return true;

		}
		return false;
	}
	
	boolean processStored(EHICommand newCommand) {
		if (newCommand instanceof DifficultyCommand
		// && ((DifficulyStatusCommand) newCommand).getStatus() != null
		) {
			lastCorrection = ARatioFileGenerator
					.toInt((DifficultyCommand) newCommand);
			notifyNewCorrectStatus(lastCorrection);
			return true;

		}
		return false;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener arg0) {
		propertyChangeSupport.addPropertyChangeListener(arg0);

	}

}
