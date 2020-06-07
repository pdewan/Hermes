package analyzer.extension;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Map;

import analyzer.AParticipantTimeLine;
import analyzer.AnAnalyzer;
import analyzer.Analyzer;
import analyzer.AnalyzerFactories;
import analyzer.ParticipantTimeLine;
import analyzer.nils.ANilsAnalyzer;
import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.DifficultyRobot;
import difficultyPrediction.predictionManagement.PredictionManagerStrategy;

/**
 * Class that generates Arff Files from the input ratios via difficulty listener
 * event callbacks and new predictions.
 * <p>
 * Instructions:<br>
 * Listens to the ratios sent from difficulty robot rather than Ratio File
 * Generator
 * 
 * @author wangk1
 *
 */
public class AnArffGenerator extends ARatioFileGenerator implements ArffGenerator {
	public static final String DEFAULT_ARFF_PATH = "data/userStudy";
	boolean processStuckData = true;

	// name of relation to be printed as @relation tag
	public static final String RELATION = "programmer-weka.filters.supervised.instance.SMOTE-C0-K5-P100.0-S1-weka.filters.supervised.instance.SMOTE-C0-K5-P100.0-S1-weka.filters.supervised.instance.SMOTE-C0-K5-P100.0-S1-weka.filters.supervised.instance.SMOTE-C0-K5-P100.0-S1";

	// Insert More features here
	public static final String[] FEATURES = {
			// "insertPercentage","numeric",
			// "deletePercentage","numeric",
			"editPercentage", "numeric", "debugPercentage", "numeric", "navigationPercentage", "numeric",
			"focusPercentage", "numeric", "removePercentage", "numeric", "webLinkTimes", "numeric",
			// "barrierType","{none,\"undesirable output\",design,API}",
			// "correction","{-1,0,1}",
			"stuck", "{YES,NO}"

	};

	// path to save the arff file to. Include the .arff extension
	private String path;
	// Buffered writer for writing out to the arff file
	protected ArffWriter arffWriter;

	// set to keep
	// Is the user currently stuck
	private boolean started;

	private boolean all;

	// set the path of the arff file
	public AnArffGenerator(Analyzer analyzer) {
		super(analyzer);

		this.started = false;

		this.arffWriter = new AnArffGenerator.ArffWriter();

		// register the event listeners
		DifficultyRobot.getInstance().addRatioFeaturesListener(this);
		DifficultyRobot.getInstance().addPluginEventListener(this);
	}

	/** METHODS SECTION */
	@Override
	public void newParticipant(String anId, String aFolder) {
		System.out.println("Extension**New Participant:" + anId);
		// if haven't started before empty the participantTimeline
		if (!this.started) {
			super.emptyTimeLine();
			this.started = true;

		}

		super.currentParticipant = anId;

		// participantTimeLine = new AParticipantTimeLine();
		participantTimeLine = AnalyzerFactories.createParticipantTimeLine();
		participantToTimeLine.put(anId, participantTimeLine);
		((AParticipantTimeLine) participantTimeLine).id = anId; // added by Ben

		// set the right path for writing
		// case I for all
		if (!all) {
			if (anId.equals("All") && aFolder == null) {
				// set path
				// this.path=((Analyzer)
				// this.analyzer).getOutputDirectory()+"/all.arff";
				// modifying this slightly for testing.
				this.path = ((Analyzer) this.analyzer).getOutputDirectory() + "/all_testing.arff"; 
				this.all = true;
				// added by Ben
				if (this.analyzer instanceof ANilsAnalyzer
						&& ((ANilsAnalyzer) this.analyzer).getIdsToIgnore().size() > 0) {
					ANilsAnalyzer a = (ANilsAnalyzer) this.analyzer;
					this.path = ((Analyzer) this.analyzer).getOutputDirectory() + "/all_testing_" // all with left out participant suffix
							+ a.getIdsToIgnore().get(0) + ".arff";
				}

				// else it is individual filess
			} else {
				// this is a bad, output directory should be given as an
				// argument, but let us keep it for now
				// String anOutputDirectory = ((AnAnalyzer)
				// this.analyzer).getOutputDirectory();
				String anOutputDirectory = ((Analyzer) this.analyzer).getOutputDirectory();

				// this.path=((AnAnalyzer)
				// this.analyzer).getOutputDirectory()+"/"+aFolder+"/"+aFolder+".arff";

				this.path = anOutputDirectory + aFolder + ".arff";

			}

			// prep the arfffile and generate headers. Start the writer
			prep();
		}

	}

	/** METHODS SECTION */

	public void newParticipantOld(String anId, String aFolder) {
		System.out.println("Extension**New Participant:" + anId);
		// if haven't started before empty the participantTimeline
		if (!this.started) {
			super.emptyTimeLine();
			this.started = true;

		}

		super.currentParticipant = anId;

		// participantTimeLine = new AParticipantTimeLine();
		participantTimeLine = AnalyzerFactories.createParticipantTimeLine();
		participantToTimeLine.put(anId, participantTimeLine);

		// set the right path for writing
		// case I for all
		if (!all) {
			if (anId.equals("All") && aFolder == null) {
				// set path
				this.path = ((Analyzer) this.analyzer).getOutputDirectory() + "/all.arff";
				this.all = true;

				// else it is individual filess
			} else {
				// this is a bad, output directory should be given as an
				// argument, but let us keep it for now
				// String anOutputDirectory = ((AnAnalyzer)
				// this.analyzer).getOutputDirectory();
				String anOutputDirectory = ((Analyzer) this.analyzer).getOutputDirectory();

				// this.path=((AnAnalyzer)
				// this.analyzer).getOutputDirectory()+"/"+aFolder+"/"+aFolder+".arff";

				this.path = anOutputDirectory + aFolder + ".arff";

			}

			// prep the arfffile and generate headers. Start the writer
			prep();
		}

	}

	/***/
	@Override
	public void finishParticipant(String aId, String aFolder) {
		super.participantTimeLine.setAggregateStatistics();

		if (processStuckData) {
			// add the stuck point data to the arff file
			super.addStuckData(super.participantTimeLine);
		}
		System.out.println("***EXTENSION Participant " + aId + "Completed");

		// set all to false. Only if it is current generating all files
		// and the stop signal aka aId of All and aFolder of null is recieved.
		if (all && aId.equals("All") && aFolder == null) {
			// write the ratios out to arfffile only at the end or else there
			// will be duplicates
			writeToArff(all, aId);

			this.all = false;
			// stop the writer
			arffWriter.stop();

			this.started = false;
			// if not all then just stop the writer.
		} else if (!all) {
			// write the ratios out to arfffile
			writeToArff(all, aId);

			arffWriter.stop();

			this.started = false;
		}

	}

	/** Prep arff file method, called by newParticipant Method */
	private void prep() {
		// if (processStuckData) {
		// //add the stuck point data to the arff file
		// super.addStuckData(super.participantTimeLine);
		// }

		// create a new bufferedwriter, with a different path

		// if no file name exist generate one that does not override other files
		if (path == null) {
			path = findRightFileName();

		}

		Path p = Paths.get(path);

		// create file if not exists
		if (!Files.exists(p) && Files.notExists(p)) {
			try {
				File f = new File(p.toString());
				f.getParentFile().mkdirs();
				f.createNewFile();
				f.setWritable(true);
				f.setReadable(true);

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {

		}

		if (this.all) {
			arffWriter.start(path, true);
			// now write headers out to file
			generateArffHeader();
			// stop is important. Flushes header since the newUser is going to
			// create a new writer
			// this forces the bytes out or the header is not going to be
			// written
		} else {
			arffWriter.start(path, false);
			// now write headers out to file
			generateArffHeader();

		}

	}

	/**
	 * Find the right name for the file if no name is specified by
	 * {@link #setArffFilePath(String)}
	 */
	private String findRightFileName() {
		int i = 0;

		// find the right file to write out to
		while (Files.exists(Paths.get(DEFAULT_ARFF_PATH + i + ".arff"))
				&& !Files.notExists(Paths.get(DEFAULT_ARFF_PATH + i + ".arff"))) {
			i++;

		}

		return DEFAULT_ARFF_PATH + i + ".arff";
	}

	/** Generate the @relation, @attribute header for arff files */
	protected void generateArffHeader() {
		arffWriter.writeRelation(RELATION).writeNewLine();

		// write all features out
		for (int i = 0; i < FEATURES.length - 1; i = i + 2) {
			arffWriter.writeAttribute(FEATURES[i], FEATURES[i + 1]);

		}

		arffWriter.writeNewLine();

	}

	private void writeToArff(boolean all, String aId) {
		if (all) {
			// for each participant
			for (Map.Entry<String, ParticipantTimeLine> e : super.participantToTimeLine.entrySet()) {
				// for each participant's data points

				ParticipantTimeLine p = e.getValue();
				this.arffWriter.writeToArffFile("%Participant" + e.getKey());
				this.arffWriter.writeNewLine();
				outputRatios(p);

			}

			// one person
		} else {
			ParticipantTimeLine l = super.participantTimeLine;
			outputRatios(l);

		}
	}

	protected void outputRatios(ParticipantTimeLine p) {
		for (int i = 0; i < p.getDebugList().size(); i++) {
			// get the correct numerical representation of predicition
			long prediction = p.getPredictionCorrections().get(i) < 0 ? p.getPredictions().get(i)
					: p.getPredictionCorrections().get(i);
			StuckPoint aStuckPoint = p.getStuckPoint().get(i);
			StuckInterval aStuckInterval = p.getStuckInterval().get(i);
			if (aStuckPoint != null || aStuckInterval != null) {
				long aTime = p.getTimeStampList().get(i);
				Date aDate = new Date(aTime);
				System.out.println(aDate.toString() + ":found non null stuck point or stuck interval " + aStuckPoint
						+ " " + aStuckInterval);
				prediction = 1;
			}

			arffWriter.writeData(
					// prediction==0? "NO":"YES",
					prediction == 0 ? PredictionManagerStrategy.PROGRESS_PREDICTION
							: PredictionManagerStrategy.DIFFICULTY_PREDICTION,
					// p.getInsertionList().get(i),
					// p.getDeletionList().get(i),
					Double.toString(p.getEditList().get(i)), Double.toString(p.getDebugList().get(i)),
					Double.toString(p.getNavigationList().get(i)), Double.toString(p.getFocusList().get(i)),
					Double.toString(p.getRemoveList().get(i)),
					Double.toString(p.getWebLinks() == null ? 0 : p.getWebLinks().get(i) == null ? 0
							: p.getWebLinks().get(i).size())
			// (p.getStuckInterval().get(i)==null?"none":p.getStuckInterval().get(i).getBarrierType().contains("
			// ")?"\""+p.getStuckInterval().get(i).getBarrierType()+"\"":
			// p.getStuckInterval().get(i).getBarrierType()),
			// Integer.toString(p.getPredictionCorrections().get(i).intValue())
			);

		}
	}

	/**
	 * Set the output path
	 * 
	 */
	public void setOutputPath(String p) {
		this.path = p;

	}

	/**
	 * Inner static class that encapsulate a buffered stream<br>
	 */
	/**
	 * Allows chaining by returning this<br>
	 * 
	 * Swallows errors
	 */
	public static class ArffWriter {
		private BufferedWriter writer;
		// turns to true when output to the data section started
		boolean datastarted;
		private String path;

		public ArffWriter() {
			this.datastarted = false;

		}

		/**
		 * Must be called before any form of writing<br>
		 * Can be called repeatidly to set to a new path
		 */
		public void start(String path, boolean append) {
			this.datastarted = false;
			this.path = path;

			try {
				// truncate first
				writer = Files.newBufferedWriter(Paths.get(path), Charset.defaultCharset(), StandardOpenOption.WRITE,
						StandardOpenOption.TRUNCATE_EXISTING);

				// if append, start in append mode
				if (append) {
					writer.close();

					writer = Files.newBufferedWriter(Paths.get(path), Charset.defaultCharset(),
							StandardOpenOption.APPEND);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		/** Start the same writer with the same path **/
		public void restart() {
			try {
				writer = Files.newBufferedWriter(Paths.get(path), Charset.defaultCharset(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		/**
		 * Set another file to write to. If append, write to end of the filef
		 */
		public void resetPath(String path, boolean append) {
			start(path, append);

		}

		/** Stop the stream */
		public void stop() {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		/**
		 * Write out a @Relation and its content. Auto append a newline at the
		 * end
		 */
		public ArffWriter writeRelation(String name) {
			this.writeToArffFile("@Relation ").writeToArffFile(name).writeNewLine();

			return this;
		}

		/** Write out @attribute, its name, and its type */
		public ArffWriter writeAttribute(String attrname, String type) {
			this.writeToArffFile("@Attribute ").writeToArffFile(attrname + " ").writeToArffFile(type).writeNewLine();

			return this;
		}

		/** Writers the ratios in data along with the prediction */
		public ArffWriter writeData(String prediction, String... data) {
			if (!this.datastarted) {
				datastarted = true;
				writeToArffFile("@data");
				writeNewLine();

			}

			// write each ratio out
			for (String d : data) {
				writeToArffFile(d + ",");

			}

			// now write the prediction
			writeToArffFile(prediction);
			writeNewLine();

			return this;
		}

		/** Output methods */

		/**
		 * Writes the ratios in the current stack buffer out to the arff file
		 */
		public ArffWriter writeToArffFile(String output) {

			// try to write out
			try {
				writer.append(output);
			} catch (IOException e) {
				e.getCause();

			}

			return this;
		}

		/*** Writer new line to arff file */
		public ArffWriter writeNewLine() {
			try {
				writer.newLine();
			} catch (IOException e) {
				e.getCause();

			}

			return this;
		}

	}

	Object b;

	public static void main(String[] args) {
		DifficultyPredictionSettings.setReplayMode(true);
		Analyzer analyzer = new AnAnalyzer();
		ArffGenerator arffGenerator = new AnArffGenerator(analyzer);
		analyzer.addAnalyzerListener(arffGenerator);
		OEFrame frame = ObjectEditor.edit(analyzer);
		frame.setSize(550, 200);
		// HermesObjectEditorProxy.edit(analyzer, 550, 200);

	}

}
