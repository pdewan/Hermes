package difficultyPrediction.predictionManagement;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import config.HelperConfigurationManagerFactory;
import dayton.ellwanger.hermes.HermesActivator;
import difficultyPrediction.APredictionParameters;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.metrics.CommandCategory;
import difficultyPrediction.metrics.CommandCategoryMapping;
import util.trace.Tracer;
import weka.classifiers.Classifier;

public class ModularDecisionTreeModel extends DecisionTreeModel implements PredictionManagerStrategy {
	

	private String wekaDataFileLocation = "data/userStudy2010.arff";
	
	

	public ModularDecisionTreeModel() {
		super ();
		
	}
	
//	protected String wekaDataFileLocation() {
//		 String aSpecifiedLocation = HelperConfigurationManagerFactory.getSingleton().getARFFFileName();
//		 return aSpecifiedLocation;
////		return APredictionParameters.getInstance().getClassificationParameters().getARFFFileName();
//
//	}
	
//	protected Classifier getClassifier() {
//		return APredictionParameters.getInstance().getClassificationParameters().getClassifierSpecification().toClassifier();
////		return DifficultyPredictionSettings.getClassifierSpecification().toClassifier();
////		return j48Model;
//	}
	
	protected InputStream getArffInputStream() throws Exception  {
		URL url;
		InputStream inputStream;
		if (DifficultyPredictionSettings.isReplayMode() || HermesActivator.getDefault() == null ||
				HelperConfigurationManagerFactory.getSingleton().isARFFFileNameIsAbsolute()
				
				){
			inputStream = new FileInputStream( wekaDataFileLocation());
		} else {

		url = new URL(HermesActivator.getInstallURL(), wekaDataFileLocation());

		inputStream = url.openConnection().getInputStream();
		}
		return inputStream;
	}

	protected void buildClassifierModel() throws Exception {
		weka.core.Instances trainingSet;
		InputStream inputStream = getArffInputStream();
//		URL url;
//
////		try {
//			//platform:/plugin/
//			InputStream inputStream;
////			if (DifficultyPredictionSettings.isReplayMode()) {
//			if (DifficultyPredictionSettings.isReplayMode() || HermesActivator.getDefault() == null ||
//					HelperConfigurationManagerFactory.getSingleton().isARFFFileNameIsAbsolute()
//					
//					){
//				inputStream = new FileInputStream( wekaDataFileLocation());
//			} else {
//
//			url = new URL(HermesActivator.getInstallURL(), wekaDataFileLocation());
//
//			inputStream = url.openConnection().getInputStream();
//			}


			trainingSet = new weka.core.Instances(new BufferedReader(
					new InputStreamReader(inputStream)));
			trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
			getClassifier().buildClassifier(trainingSet);
			classifierBuilt.put(getClassifier(), true);



	}	

}
