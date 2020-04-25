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
	public static void useNilsCode(String aUserName) {
		Analyzer analyzer = new ANilsAnalyzer();
		ANilsArffGenerator arffGenerator = new ANilsArffGenerator(analyzer);
		analyzer.addAnalyzerListener(arffGenerator);
		AnalyzerFactories.setParticipantTimeLineFactory(new ANilsParticipantTimeLineFactory());		
		analyzer.loadDirectory();
		analyzer.getAnalyzerParameters().getParticipants().setValue(aUserName);	

//		analyzer.getAnalyzerParameters().getParticipants().setValue("All");	
//		analyzer.getAnalyzerParameters().getParticipants().setValue("17");		

		DifficultyPredictionSettings.setReplayMode(true);
		analyzer.getAnalyzerParameters().setMakePredictions(true);		
		analyzer.getAnalyzerParameters().replayLogs();
	}
	public static void createRatios() {
		Analyzer analyzer = new AnAnalyzer();
		RatioFileGenerator aRatioFileGenerator = new ARatioFileGenerator();
		analyzer.addAnalyzerListener(aRatioFileGenerator);

		analyzer.loadDirectory();
//		analyzer.getAnalyzerParameters().getParticipants().setValue("All");
		analyzer.getAnalyzerParameters().getParticipants().setValue("17");

		analyzer.getAnalyzerParameters().setMakePredictions(true);

		DifficultyPredictionSettings.setReplayMode(true);
		analyzer.getAnalyzerParameters().setMakePredictions(true);	
		analyzer.getAnalyzerParameters().replayLogs();
	}
	public static  void main (String[] args) {
		String[] aUserNames = {"16", "17", "18", "19", "20", "21", "22", "23", "24", "26", "27", "28", "29", "30", "31", "32", "33"};
//		createRatios();
//		for (String aUserName:aUserNames)
//			useNilsCode(aUserName);
//		}
	useNilsCode("16");
	}

}
