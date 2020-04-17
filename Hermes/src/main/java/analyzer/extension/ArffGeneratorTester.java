package analyzer.extension;

import analyzer.AnAnalyzer;
import analyzer.Analyzer;
import analyzer.AnalyzerFactories;
import analyzer.AnalyzerListener;
import analyzer.nils.ANilsAnalyzer;
import analyzer.nils.ANilsArffGenerator;
import analyzer.nils.ANilsParticipantTimeLineFactory;
import difficultyPrediction.DifficultyPredictionSettings;

public class ArffGeneratorTester {
	public static void useNilsCode() {
		Analyzer analyzer = new ANilsAnalyzer();
		ANilsArffGenerator arffGenerator = new ANilsArffGenerator(analyzer);
		analyzer.addAnalyzerListener(arffGenerator);
		AnalyzerFactories.setParticipantTimeLineFactory(new ANilsParticipantTimeLineFactory());		
		analyzer.loadDirectory();
		analyzer.getAnalyzerParameters().getParticipants().setValue("17");		
		DifficultyPredictionSettings.setReplayMode(true);
		analyzer.getAnalyzerParameters().setMakePredictions(true);		
		analyzer.getAnalyzerParameters().replayLogs();
	}
	public static void createRatios() {
		Analyzer analyzer = new AnAnalyzer();
		RatioFileGenerator aRatioFileGenerator = new ARatioFileGenerator();
		analyzer.addAnalyzerListener(aRatioFileGenerator);

		analyzer.loadDirectory();
		analyzer.getAnalyzerParameters().getParticipants().setValue("17");
		analyzer.getAnalyzerParameters().setMakePredictions(true);

		DifficultyPredictionSettings.setReplayMode(true);
		analyzer.getAnalyzerParameters().setMakePredictions(true);	
		analyzer.getAnalyzerParameters().replayLogs();
	}
	public static  void main (String[] args) {
//		createRatios();
		useNilsCode();
	}

}
