package analyzer.extension;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import analyzer.AnAnalyzer;
import analyzer.Analyzer;
import difficultyPrediction.DifficultyPredictionSettings;
import fluorite.commands.DifficultyCommand;
import fluorite.commands.PredictionCommand;
import util.misc.Common;

public class ADifficultyPredictionAndStatusPrinter extends ABasicStoredDataStatistics{
	public ADifficultyPredictionAndStatusPrinter(Analyzer anAnalyzer) {
		super(anAnalyzer);
		// TODO Auto-generated constructor stub
	}

	public static final String DIFFICULTY_FIlE_NAME = "allInteractiveDifficulties.csv";
	protected String outputFileName;
	protected File outputFile;
	public static final String HEADER = " ";
	protected String computeOutputFileName() {
//		return  AnAnalyzer.PARTICIPANT_DIRECTORY + AnAnalyzer.OUTPUT_DATA + DIFFICULTY_FIlE_NAME;
		return  analyzer.participantDirectoryName() + AnAnalyzer.OUTPUT_DATA + DIFFICULTY_FIlE_NAME;

	}
	protected void maybeWriteHeader() {
//		if (outputFile == null) {
//			return;
//		}
//		try {
//			Common.writeText(outputFile, HEADER);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	protected void newDifficultyPrediction(PredictionCommand newParam, Date aDate) {
		if (outputFile == null) {
			return;
		}
		try {
			Common.appendText(outputFile, "Difficulty predicted at " + aDate);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected void newCorrectDifficultyStatus(DifficultyCommand newCommand, Date aDate, boolean surmountable, boolean confirm) {
		if (outputFile == null) {
			return;
		}
		try {
			Common.appendText(outputFile, "Date:" + aDate + " Surmountable?:" + surmountable + " confirm previus prediction?" + confirm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected void newDifficultyCorrection(DifficultyCommand newCommand, Date aDate) {
		if (outputFile == null) {
			return;
		}
		try {
			Common.appendText(outputFile,"Date:" + aDate + " correcting previous difficulty prediction");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void newParticipant(String anId, String aFolder) {
		super.newParticipant(anId, aFolder);
		if (!isWriteFile()) return;
		if (AnAnalyzer.ALL_PARTICIPANTS.equals(anId)) {

			// if (DifficultyPredictionSettings.isNewRatioFiles() &&
			// AnAnalyzer.ALL_PARTICIPANTS.equals(anId)) {
			outputFileName = computeOutputFileName();
			// writeFile = true;
			outputFile = new File(outputFileName);
//			if (!outputFile.exists()) {
				try {
					outputFile.createNewFile();
					Common.writeText(outputFile, " ");
				} catch (IOException e) {
					e.printStackTrace();
				}
//			}
//			maybeWriteHeader();
		}
		try {
			Common.appendText(outputFile, "Participant:" + anId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void main (String[] args) {
		 DifficultyPredictionSettings.setReplayMode(true);
			//
			 Analyzer analyzer = new AnAnalyzer();
			 ADifficultyPredictionAndStatusPrinter analyzerListener = new ADifficultyPredictionAndStatusPrinter(analyzer);
			 analyzer.loadDirectory();
//			 analyzer.getAnalyzerParameters().setNewOutputFiles(true);
			 analyzerListener.setWriteFile(true);
//			 analyzerListener.setWriteFile(false);
			 analyzer.getAnalyzerParameters().getParticipants().setValue("All");
//			 analyzer.getAnalyzerParameters().getParticipants().setValue("16");
			 analyzer.addAnalyzerListener(analyzerListener);
			 analyzer.getAnalyzerParameters().replayLogs();
//			 OEFrame frame = ObjectEditor.edit(analyzer);
	}

}
