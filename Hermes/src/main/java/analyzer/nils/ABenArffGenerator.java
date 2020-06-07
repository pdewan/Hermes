/**
 * @author Nils Persson
 * @date 2018-Oct-24 7:04:18 PM 
 */
package analyzer.nils;


import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import analyzer.AParticipantTimeLine;
import analyzer.Analyzer;
import analyzer.AnalyzerFactories;
import analyzer.ParticipantTimeLine;
import analyzer.extension.AnArffGenerator;
import analyzer.extension.ArffGenerator;
import analyzer.extension.StuckInterval;
import analyzer.extension.StuckPoint;
import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import difficultyPrediction.APredictionParameters;
import difficultyPrediction.DifficultyPredictionRunnable;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.predictionManagement.PredictionManagerStrategy;
import fluorite.commands.CompilationCommand;
import fluorite.commands.CopyCommand;
import fluorite.commands.Delete;
import fluorite.commands.DifficultyCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.Insert;
import fluorite.commands.PasteCommand;
import fluorite.commands.PredictionCommand;
import fluorite.commands.PredictionType;
import fluorite.commands.Status;

/**
 * 
 */
public class ABenArffGenerator extends AnArffGenerator implements ArffGenerator {
	protected int predictionMade = 0;
	protected EHICommand lastCommand;
	protected boolean modifyPredictionsByThreshold = false;
	protected boolean modifyPredictionsByExtraIntervals = false;


	// int count = 0;
	// protected List<EHICommand> compileListIdx17 = new ArrayList<>();
	// protected int[] commandCounts = new int[196];

	protected long difficultyTimeThreshold = 180000; //3 minutes
//	int count = 0;
//	protected List<EHICommand> compileListIdx17 = new ArrayList<>();
//	protected int[] commandCounts = new int[196];

	/**
	 * @param analyzer
	 */
	public ABenArffGenerator(Analyzer analyzer) {
		super(analyzer);
	}

	// @Override
	// public void newStoredCommand(EHICommand aNewCommand, long
	// aStartRelativeTime, long aDuration) {
	// // TODO Auto-generated method stub
	//
	// }
	
	public void setModifyPredictionsByTreshold(boolean val) {
		this.modifyPredictionsByThreshold = val;
	}
	
	@Override
	public void newCorrectStatus(DifficultyCommand newCommand, Status aStatus, long aStartRelativeTime,
			long aDuration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void newPrediction(PredictionCommand newParam, PredictionType aPredictionType, long aStartRelativeTime,
			long aDuration) {
		// TODO Auto-generated method stub

	}

	public static final String[] FEATURES = {
			// "insertPercentage","numeric",
			// "deletePercentage","numeric",
			"currentDate", "string", "currentTime", "numeric", "timeFromStart", "numeric", "percentageIntoTask", "numeric",
			"sourceDifficulty", "string",
			"editOrInsertPercentage", "numeric", "debugPercentage", "numeric", "navigationPercentage", "numeric",
			"focusPercentage", "numeric", "removePercentage", "numeric",
			// "webLinkTimes","numeric",
			/*
			 * "pasteTimes","numeric", "copyTimes","numeric",
			 * "longestDelete","numeric", "longestInsert","numeric",
			 * "compileErrors","numeric",
			 */
			"stuck", "{YES,NO}" };
	
	public static final String DEFAULT_SOURCE_DIFFICULTY = "none";
	public static final String STUCK_POINT_SOURCE = "stuck_point";
	public static final String PREDICTION_SOURCE = "prediction";
	public static final String PREDICTION_CORRECTION_SOURCE = "predictionCorrection";
	public static final String EXTRA_INTERVAL_SOURCE = "extraInterval";
	public static final String THRESHOLD_SOURCE = "threshold";

	@Override
	protected void generateArffHeader() {
		arffWriter.writeRelation(RELATION).writeNewLine();

		// write all features out
		for (int i = 0; i < FEATURES.length - 1; i = i + 2) {
			arffWriter.writeAttribute(FEATURES[i], FEATURES[i + 1]);

		}

		arffWriter.writeNewLine();
	}

	protected NilsCommandCategory getCommandCategory(EHICommand command) {
		if (command instanceof PasteCommand) {
			return NilsCommandCategory.PASTE;
		} else if (command instanceof CopyCommand) { // cut commands?
			return NilsCommandCategory.COPY;
		} else if (command instanceof Delete) {
			return NilsCommandCategory.DELETE;
		} else if (command instanceof Insert) {
			return NilsCommandCategory.INSERT;
		} else if (command instanceof CompilationCommand) {
			return NilsCommandCategory.COMPILE;
		} else {
			return NilsCommandCategory.OTHER;
		}
	}

	// @Override
	// protected void maybeProcessPrediction(EHICommand newCommand) {
	// if (newCommand instanceof PredictionCommand) {
	// System.out.println("Last prediction changed: " + newCommand + " ; index:
	// " + participantTimeLine.getTimeStampList().size());
	// lastPrediction = toInt((PredictionCommand) newCommand);
	// predictionMade = 1; // added to keep track of when predictions are made
	// }
	// }
	//
	// @Override
	// protected void insertEntriesForPreviousTimeStamp() {
	// if (participantTimeLine.getTimeStampList().size() == 0) return;// no
	// previous time stamp
	//
	// System.out.println("Prediction: " + lastPrediction + "; Prediction added
	// at: " +
	// participantTimeLine.getPredictions().size() + "; Correction: " +
	// lastCorrection +
	// "; Correction added at: " +
	// participantTimeLine.getPredictionCorrections().size());
	//
	// participantTimeLine.getPredictions().add(lastPrediction); // do not reset
	// as it is not a status
	// participantTimeLine.getPredictionCorrections().add(lastCorrection);
	// ((NilsParticipantTimeLine)participantTimeLine).getPredictionIndexesList().add(predictionMade);
	//
	// lastCorrection = -1; // reset it as this is an event rather than a status
	// predictionMade = 0; // reset to track next prediction command index
	// }

	@Override
	public void newCommand(EHICommand newCommand) {
		// System.out.println("newCommand current Thread: " +
		// Thread.currentThread().getName() );
		if (!Thread.currentThread().getName().equals(DifficultyPredictionRunnable.DIFFICULTY_PREDICTION_THREAD_NAME))
			return;
		super.newCommand(newCommand);

		// System.out.println("Thread: " + Thread.currentThread());

		// find out what command it was
		NilsCommandCategory commandCategory = getCommandCategory(newCommand);

		// find out which segment it belongs too
		long aTimeStamp = startTime + newCommand.getTimestamp();
		int anIndex = participantTimeLine.getIndexBefore(aTimeStamp);
		double newRatio;
		int segmentLength = APredictionParameters.getInstance().getSegmentLength();
		// commandCounts[anIndex]++;
		// System.out.println("timestamp: " + new Date(aTimeStamp) + " ;
		// relativeTime: " + newCommand.getTimestamp() + " ; index: " + anIndex
		// + " ; commandCounts: " + commandCounts[anIndex] + " ; " +
		// newCommand);

		// add to that segment idx of respective list
		Double listValue;
		if (anIndex != -1) {
			switch (commandCategory) {
			case PASTE:
				listValue = ((NilsParticipantTimeLine) participantTimeLine).getPasteList().get(anIndex);
				newRatio = 100 * ((listValue / 100) * segmentLength + 1) / segmentLength;
				((NilsParticipantTimeLine) participantTimeLine).getPasteList().set(anIndex, newRatio);
				break;
			case COPY:
				listValue = ((NilsParticipantTimeLine) participantTimeLine).getCopyList().get(anIndex);
				newRatio = 100 * ((listValue / 100) * segmentLength + 1) / segmentLength;
				((NilsParticipantTimeLine) participantTimeLine).getCopyList().set(anIndex, newRatio);
				break;
			case DELETE:
				listValue = ((NilsParticipantTimeLine) participantTimeLine).getLongestDeleteList().get(anIndex);
				((NilsParticipantTimeLine) participantTimeLine).getLongestDeleteList().set(anIndex,
						Math.max(listValue, ((Delete) newCommand).getLength()));
				break;
			case INSERT:
				listValue = ((NilsParticipantTimeLine) participantTimeLine).getLongestInsertList().get(anIndex);
				((NilsParticipantTimeLine) participantTimeLine).getLongestInsertList().set(anIndex,
						Math.max(listValue, ((Insert) newCommand).getLength()));
				break;
			case COMPILE:
				listValue = ((NilsParticipantTimeLine) participantTimeLine).getCompileList().get(anIndex);
				newRatio = 100 * ((listValue / 100) * segmentLength + 1) / segmentLength;
				((NilsParticipantTimeLine) participantTimeLine).getCompileList().set(anIndex, newRatio);
				break;
			default:
				break;
			}
		}

		this.lastCommand = newCommand;
	}

	@Override
	public void newRatios(RatioFeatures newVal) {
		// System.out.println("newRatios current Thread: " +
		// Thread.currentThread().getName() );
		if (!Thread.currentThread().getName().equals(DifficultyPredictionRunnable.DIFFICULTY_PREDICTION_THREAD_NAME))
			return;

		System.out.println("************* NILS NEW RATIOS ****************");
		insertEntriesForPreviousTimeStamp();
		currentTime = startTime + newVal.getSavedTimeStamp();

		participantTimeLine.getEditList().add(newVal.getEditRatio());
		participantTimeLine.getTimeStampList().add(currentTime);
		participantTimeLine.getDebugList().add(newVal.getDebugRatio());
		participantTimeLine.getDeletionList().add(newVal.getDeletionRatio());
		participantTimeLine.getFocusList().add(newVal.getFocusRatio());
		participantTimeLine.getInsertionList().add(newVal.getInsertionRatio());
		participantTimeLine.getNavigationList().add(newVal.getNavigationRatio());
		participantTimeLine.getRemoveList().add(newVal.getRemoveRatio());
		participantTimeLine.getWebLinks().add(null);

		// bad cast fix this later
		((NilsParticipantTimeLine) participantTimeLine).getPasteList().add(0.0);
		((NilsParticipantTimeLine) participantTimeLine).getCopyList().add(0.0);
		((NilsParticipantTimeLine) participantTimeLine).getLongestDeleteList().add(0.0);
		((NilsParticipantTimeLine) participantTimeLine).getLongestInsertList().add(0.0);
		((NilsParticipantTimeLine) participantTimeLine).getCompileList().add(0.0);
		// System.out.println("********** count: " + count + "**********");
		// System.out.println(participantTimeLine.getEditList().size());
		// System.out.println("currentTime: " + new Date(currentTime) + " ;
		// savedTimeStamp: " + newVal.getSavedTimeStamp());
	}

	// protected List<Integer> createFinalPredictions(ParticipantTimeLine p){
	// List<Integer> finalPredictions = new ArrayList<>();
	// for(int i = p.getPredictionCorrections().size() - 1; i >= 0; i--){
	// if(p.getPredictionCorrections().get(i) == -1){
	// finalPredictions.add(p.getPredictions().get(i));
	// }
	// else{
	// int correctionIndex = i;
	// while(i >= 0 &&
	// ((NilsParticipantTimeLine)p).getPredictionIndexesList().get(i) != 1){
	// finalPredictions.add(p.getPredictionCorrections().get(correctionIndex));
	// i--;
	// }
	// finalPredictions.add(p.getPredictionCorrections().get(correctionIndex));
	// }
	// }
	// Collections.reverse(finalPredictions); // I'm sorry for this future
	// person (but not sorry enough)
	// return finalPredictions;
	// }
	protected void printArffEntryTimestamps(ParticipantTimeLine p) {
		System.out.println("Running printArffEntryTimestamp");
		for (int i = 0; i < p.getTimeStampList().size(); i++) {
			Date date = new Date(p.getTimeStampList().get(i));
			long prediction = p.getPredictionCorrections().get(i) < 0 ? 0 : p.getPredictionCorrections().get(i);
			//System.out.println(
			//		"Prediction for arff entry: " + i + " is " + prediction + " and the timestamp for this entry is: "
			//				+ date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
		}
	}
	
//	protected List<Integer> createFinalPredictions(ParticipantTimeLine p){
//		List<Integer> finalPredictions = new ArrayList<>();
//		for(int i = p.getPredictionCorrections().size() - 1; i >= 0; i--){
//			if(p.getPredictionCorrections().get(i) == -1){
//				finalPredictions.add(p.getPredictions().get(i));
//			}
//			else{
//				int correctionIndex = i;
//				while(i >= 0 && ((NilsParticipantTimeLine)p).getPredictionIndexesList().get(i) != 1){
//					finalPredictions.add(p.getPredictionCorrections().get(correctionIndex));
//					i--;
//				}  
//				finalPredictions.add(p.getPredictionCorrections().get(correctionIndex));
//			}
//		}
//		Collections.reverse(finalPredictions); // I'm sorry for this future person (but not sorry enough)
//		return finalPredictions;
//	}
	
	private long convertHMSToMS(String fullTime) {
		String[] hms = fullTime.split(":");
		int hours = Integer.parseInt(hms[0]);
		int minutes = Integer.parseInt(hms[1]);
		int seconds = Integer.parseInt(hms[2]);
		return hours * 3600000 + minutes * 60000 + seconds * 1000;
	}
	
	public ArrayList<Date> getExtraIntervals(String participant){
		HashMap<String, ArrayList<Date>> map = new HashMap<String, ArrayList<Date>>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("data/GroundTruth/ExtraIntervals.csv"));
			String line = reader.readLine();
			while (line != null) {
				String[] interval = line.split(",");
				String pid = interval[0];
				String start = interval[1];
				String end = interval[2];
				Date startInterval = new Date(convertHMSToMS(start) + this.startTime);
				Date endInterval = new Date(convertHMSToMS(end) + this.startTime);
				
				if (!map.containsKey(pid)) {
					map.put(pid, new ArrayList<Date>());
				}
				
				map.get(pid).add(startInterval);
				map.get(pid).add(endInterval);
				
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (!map.containsKey(participant)) {
			return new ArrayList<Date>();
		}
		
		Date date = map.get(participant).get(0);
		
		return map.get(participant);
	}
	
	private boolean checkIfTimeInExtraIntervals(long time, ArrayList<Date> intervals) {
		for (int i = 0; i < intervals.size(); i += 2) {
			long startInterval = intervals.get(i).getTime();
			long endInterval = intervals.get(i + 1).getTime(); //asumming arraylist should be evenly sized
			//System.out.println(new Date(endInterval));
			//System.out.println(new Date(time));
			if (time >= startInterval && time <= endInterval) {
				System.out.println("returned true *********************");
				return true;
			}
		}
		return false;
	}
	
	private boolean checkIfWithinDifficultyThreshold(ParticipantTimeLine p, int i) {
		long timestamp = p.getTimeStampList().get(i);
		//first try going forward
		for (++i;i < p.getTimeStampList().size(); i++) {
			long nextTimestamp = p.getTimeStampList().get(i);
			//if we have already moved out of the threshold, then just reutn false
			if (nextTimestamp - timestamp > this.difficultyTimeThreshold) {
				return false;
			}
			long nextPrediction = p.getPredictionCorrections().get(i)<0 ? p.getPredictions().get(i) : p.getPredictionCorrections().get(i);
			//if within threshold, then if the prediction is 1, count this one as 1
			if (nextPrediction == 1) {
				System.out.println("modified by proximity*******");
				return true;
			}
		}
		//now try going backwards
		for (--i;i >= 0; i--) {
			long prevTimestamp = p.getTimeStampList().get(i);
			//if we have already moved out of the threshold, then just reutn false
			if (timestamp - prevTimestamp > this.difficultyTimeThreshold) {
				return false;
			}
			long prevPrediction = p.getPredictionCorrections().get(i)<0 ? p.getPredictions().get(i) : p.getPredictionCorrections().get(i);
			//if within threshold, then if the prediction is 1, count this one as 1
			if (prevPrediction == 1) {
				System.out.println("modified by proximity*******");
				return true;
			}
		}
		return false;
		
	}
	
	@Override
	protected void outputRatios(ParticipantTimeLine p) {
		System.out.println("************* BEN OUTPUT RATIOS ****************");
		
		ArrayList<Date> extraIntervals = getExtraIntervals(((AParticipantTimeLine)p).id);
		int counter = 0;
//		List<Integer> finalPredictions = createFinalPredictions(p);
		//this.printArffEntryTimestamps(p);
		for(int i=0;i<p.getDebugList().size();i++) {
			String difficulty_source = DEFAULT_SOURCE_DIFFICULTY;
//			//get the correct numerical representation of prediction
			long prediction = p.getPredictionCorrections().get(i)<0 ? p.getPredictions().get(i) : p.getPredictionCorrections().get(i);
			//long prediction = p.getPredictionCorrections().get(i)<0 ? 0 : p.getPredictionCorrections().get(i);
			StuckPoint aStuckPoint = p.getStuckPoint().get(i);
			StuckInterval aStuckInterval = p.getStuckInterval().get(i);
			
			if (p.getPredictionCorrections().get(i) < 0) {
				difficulty_source = PREDICTION_SOURCE;
			} else {
				difficulty_source = PREDICTION_CORRECTION_SOURCE;
			}
			
			if (aStuckPoint != null || aStuckInterval != null) {
				long aTime = p.getTimeStampList().get(i);
				Date aDate = new Date(aTime);
				System.out.println (aDate.toString() + ":found non null stuck point or stuck interval " + aStuckPoint + " " + aStuckInterval);
				prediction = 1;
				difficulty_source = STUCK_POINT_SOURCE;
			}
			if (checkIfTimeInExtraIntervals(p.getTimeStampList().get(i), extraIntervals)
					 && this.modifyPredictionsByExtraIntervals) {
				difficulty_source = EXTRA_INTERVAL_SOURCE;
				prediction = 1;
				counter++;
			}
			
			if (this.modifyPredictionsByThreshold && this.checkIfWithinDifficultyThreshold(p, i)) {
				difficulty_source = THRESHOLD_SOURCE;
				prediction = 1;
				counter++;
			}
			// double prediction = finalPredictions.get(i);
			
			long currentTime = p.getTimeStampList().get(i);
			long timeFromStart = p.getTimeStampList().get(i) - startTime;
			//expecting this.currentTime to hold the end time here
			long percentageIntoTask = (p.getTimeStampList().get(i) - startTime) / ( startTime - this.currentTime);
			Date date = new Date(currentTime);
			Format f = new SimpleDateFormat("HH.mm.ss");
			String strDate = f.format(date);
//			 double prediction = finalPredictions.get(i);
			
			arffWriter.writeData(
					// prediction==0? "NO":"YES",
					prediction == 0 ? PredictionManagerStrategy.PROGRESS_PREDICTION
							: PredictionManagerStrategy.DIFFICULTY_PREDICTION,
					// Double.toString(p.getInsertionList().get(i)),
					// Double.toString(p.getDeletionList().get(i)),
					// Double.toString(p.getTimeStampList().get(i)),
					
					strDate, Long.toString(currentTime), 
					Long.toString(timeFromStart),
					Long.toString(percentageIntoTask), //these fields are meta data
					difficulty_source,
					Double.toString(p.getEditList().get(i)), // actually insert
					Double.toString(p.getDebugList().get(i)), Double.toString(p.getNavigationList().get(i)),
					Double.toString(p.getFocusList().get(i)), Double.toString(p.getRemoveList().get(i)) // actually
																										// delete
			// Double.toString(p.getWebLinks()==null?0:p.getWebLinks().get(i)==null?0:p.getWebLinks().get(i).size()),
			// (p.getStuckInterval().get(i)==null?"none":p.getStuckInterval().get(i).getBarrierType().contains("
			// ")?"\""+p.getStuckInterval().get(i).getBarrierType()+"\"":
			// p.getStuckInterval().get(i).getBarrierType()),
			// Integer.toString(p.getPredictionCorrections().get(i).intValue())

			/*
			 * 
			 * Double.toString(((NilsParticipantTimeLine)p).getPasteList().get(i
			 * )),
			 * Double.toString(((NilsParticipantTimeLine)p).getCopyList().get(i)
			 * ),
			 * Double.toString(((NilsParticipantTimeLine)p).getLongestDeleteList
			 * ().get(i)),
			 * Double.toString(((NilsParticipantTimeLine)p).getLongestInsertList
			 * ().get(i)) //
			 * Double.toString(((NilsParticipantTimeLine)p).getCompileList().get
			 * (i))
			 * 
			 */
			);

		}
		
		System.out.println("amount modified by extra intervals: " + counter);
	}

	public static void main(String[] args) {
		DifficultyPredictionSettings.setReplayMode(true);
		// Analyzer analyzer = new AnAnalyzer();
		Analyzer analyzer = new ANilsAnalyzer();
		ABenArffGenerator arffGenerator = new ABenArffGenerator(analyzer);
		analyzer.addAnalyzerListener(arffGenerator);
		AnalyzerFactories.setParticipantTimeLineFactory(new ANilsParticipantTimeLineFactory());
		// DifficultyRobot.getInstance().getEventAggregator().setEventAggregationStrategy(new
		// AWindowAggregator());

		// Mediator mediator = DifficultyRobot.getInstance();
		// RatioBasedFeatureExtractor featureExtractor = new
		// ANilsFeatureExtractor(mediator);
		// FeatureExtractionStrategy featureExtractionStrategy = new
		// ANilsFeatureExtractionStrategy();
		// featureExtractor.setFeatureExtractionStrategy(featureExtractionStrategy);
		// mediator.setFeatureExtractor(featureExtractor);

		OEFrame frame = ObjectEditor.edit(analyzer);
		frame.setSize(550, 200);
		// HermesObjectEditorProxy.edit(analyzer, 550, 200);
	}

}
