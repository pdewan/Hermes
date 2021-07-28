package analyzer.extension;

import java.util.Date;

import analyzer.AnAnalyzer;
import analyzer.Analyzer;
import analyzer.AnalyzerFactories;
import analyzer.AnalyzerListener;
import analyzer.nils.ABenArffGenerator;
import analyzer.nils.ACeciliaAnalyzer;
import analyzer.nils.ACeciliaArffGenerator;
import analyzer.nils.ANilsAnalyzer;
import analyzer.nils.ANilsArffGenerator;
import analyzer.nils.ANilsParticipantTimeLineFactory;
import analyzer.nils.AWindowAggregator;
import difficultyPrediction.ADifficultyPredictionPluginEventProcessor;
import difficultyPrediction.APredictionParameters;
import difficultyPrediction.DifficultyPredictionPluginEventProcessor;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.Mediator;
import difficultyPrediction.PredictionParametersSetterSelector;
import difficultyPrediction.eventAggregation.ADisjointDiscreteChunks;
import difficultyPrediction.eventAggregation.EventAggregator;
import difficultyPrediction.metrics.AnA0CommandCategories;
import difficultyPrediction.metrics.CommandClassificationSchemeName;

public class CeciliaArffGeneratorTester {
	//changed by Cecilia
	protected static boolean distributeDifficulties = false;
	public static void useNilsCode(String aUserName, String toIgnore) {
//		ANilsAnalyzer analyzer = new ANilsAnalyzer();
		ACeciliaAnalyzer analyzer = new ACeciliaAnalyzer();
//		ABenArffGenerator arffGenerator = new ABenArffGenerator(analyzer);
		ACeciliaArffGenerator arffGenerator = new ACeciliaArffGenerator(analyzer);
		analyzer.addAnalyzerListener(arffGenerator);
		AnalyzerFactories.setParticipantTimeLineFactory(new ANilsParticipantTimeLineFactory());		
		analyzer.loadDirectory();
		// set this to false if YES values not distributed
		arffGenerator.setModifyPredictionsByTreshold(distributeDifficulties); //*this uses threshold for difficulty
		arffGenerator.setModifyPredictionsByExtraIntervals(true); //*this uses extrainterval for difficulty
		analyzer.getAnalyzerParameters().getParticipants().setValue(aUserName);	
		if (toIgnore != "") {
			analyzer.getIdsToIgnore().add(toIgnore);
		}
		
//		analyzer.getAnalyzerParameters().getParticipants().setValue("All");	
//		analyzer.getAnalyzerParameters().getParticipants().setValue("17");		

		DifficultyPredictionSettings.setReplayMode(true);
		analyzer.getAnalyzerParameters().setMakePredictions(true);		
		analyzer.getAnalyzerParameters().replayLogs();
	}	
	
	public static void useKevinCode(String aUserName, String toIgnore) {
		ANilsAnalyzer analyzer = new ANilsAnalyzer();
		AnArffGenerator arffGenerator = new AnArffGenerator(analyzer);
		analyzer.addAnalyzerListener(arffGenerator);
//		AnalyzerFactories.setParticipantTimeLineFactory(new ANilsParticipantTimeLineFactory());		
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
		analyzer.getAnalyzerParameters().getParticipants().setValue("16");

		analyzer.getAnalyzerParameters().setMakePredictions(true);

		DifficultyPredictionSettings.setReplayMode(true);
		analyzer.getAnalyzerParameters().setMakePredictions(true);	
		analyzer.getAnalyzerParameters().replayLogs();
	}
	public static void generateForLeaveOneOut() {
		String[] aUserNames = {"16", "17", "18", "19", "20", "21", "22", "23", "24", "26", "27", "28", "29", "30", "31", "32", "33"};
//		createRatios();
		/*
		for (String aUserName:aUserNames) {
			useNilsCode("All", aUserName);
			useNilsCode(aUserName, "");
		}
		*/
		

		//useNilsCode("All", "33");
//		useNilsCode("All", ""); // runs all
//		useNilsCode("All", "16"); // except 16
		useNilsCode("16", ""); // only 16
//		useNilsCode("All", "17");
		
	}
	public static void generateForCrossValidation() {
		useNilsCode("All", "");

	}
	public static void generateForCrossValidationNoDistribution() {
		distributeDifficulties = false;
		useNilsCode("All", "");

	}
	public static  void main (String[] args) {
//		generateForLeaveOneOut();
//		generateForCrossValidation();
//		
//		String[] aUserNames = {"16", "17", "18", "19", "20", "21", "22", "23", "24", "26", "27", "28", "29", "30", "31", "32", "33"};
//		createRatios();
//		for (String aUserName:aUserNames) {
//			useNilsCode("All", aUserName);
//		}
		
//		//useNilsCode("All", "33");
//		useNilsCode("All", ""); // runs all
//		useNilsCode("All", "16"); // except 16
//		APredictionParameters.getInstance().setCommandClassificationScheme(CommandClassificationSchemeName.A0);
		
		
		// command classification changed by ceclia
		APredictionParameters.getInstance().setCommandClassificationScheme(CommandClassificationSchemeName.A0Web);
		APredictionParameters.getInstance().setSegmentLength(50);
		useNilsCode("29", ""); // only 16
//		useNilsCode("All", "16"); // except 16
////		useNilsCode("All", "17");
//
//
////		useKevinCode("16");
//
//	//useNilsCode("All");
	}

}
