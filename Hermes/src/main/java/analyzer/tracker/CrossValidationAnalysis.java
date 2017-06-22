package analyzer.tracker;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import difficultyPrediction.predictionManagement.ClassifierSpecification;
import analyzer.extension.ArffFileGeneratorFactory;
import weka.classifiers.Classifier;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;

//note that resample should not be tested with cross validation
public class CrossValidationAnalysis {
	

	public static final int SMOTE=3;

	private static String fileDir;
	private static String outputAndTestSubDir;

	//assuming the output csv and input all.arff is in same dir
	public static void generateAllArff(String outputSubdir) {
		File fi=new File(outputSubdir);
		if(!fi.exists())
			fi.mkdirs();

		ArffFileGeneratorFactory f=new ArffFileGeneratorFactory(outputSubdir);
		f.insertCommand("All");
		f.createArffs();

	}


	public static void crossValidation(String outputSubDir,ClassifierSpecification classifier,double percentage) throws Exception {

		//filter
		SMOTE smote=new SMOTE();

		BufferedReader r=Files.newBufferedReader(Paths.get("data/OutputData/"+outputSubDir+"All/all.arff"));

		Instances d=new Instances(r);
		int seed=0;
		int fold=10;

		PredictionOutputter matrixOutputter=new ResultOutputter("YES", "NO");

		Instances data=new Instances(d);
		data.setClassIndex(data.numAttributes()-1);
		data.randomize(new Random(seed));

		smote.setPercentage(percentage);
		smote.setInputFormat(data);
		data=Filter.useFilter(data, smote);
		//System.out.println(data.numInstances());
		
		
		Instances copy_data=new Instances(data);
		copy_data.setClassIndex(copy_data.numAttributes()-1);
		weka.classifiers.Classifier wekaClassifier=null;
		wekaClassifier = classifier.toClassifier();
//		if(classifier==ClassifierSpecification.ADABOOST) {
//			b=new AdaBoostM1();
//			((AdaBoostM1) b).setClassifier(new DecisionStump());
//		} else if(classifier==ClassifierSpecification.BAGGING) {
//			b=new Bagging();
//			((Bagging) b).setClassifier(new DecisionStump());
//		} else {
//			b=new J48();
//
//		}

		for(int i=0;i<fold;i++) {

			Instances train=copy_data.trainCV(fold, i);
			Instances testing=copy_data.testCV(fold, i);

			wekaClassifier.buildClassifier(train);


			for(int j=0;j<testing.numInstances();j++) {
				double result=wekaClassifier.classifyInstance(testing.instance(j));

				matrixOutputter.output(new PredictedInstance(testing.instance(j),result));
			}

		}
		matrixOutputter.closeStream();


		System.out.println("Analysis completed\n");
	}


	public static void main(String[] args) {
		//regenerate each file once before, something weird is messing them up post generate from other files
		String[] outputDirs=new String[] {"edit/","leaveoneoutjason/","leaveoneoutmix/","leaveoneout/"};
		int[] smoteLevels=new int[] {500,1000,2000,3000};
		//500,1000,2000,3000

		outputAndTestSubDir=outputDirs[1];
		generateAllArff(outputAndTestSubDir);

		//classifier
		for (ClassifierSpecification c: ClassifierSpecification.values()) {

			//different levels
			for (int l:smoteLevels) {
				System.out.println("Testing: "+c+" SMOTE: "+l+"%");

				//args: classifier, filter, and filter %(only for smote)
				try {
					crossValidation(outputAndTestSubDir,c,l);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}



		//can't figure out why the timer thread won't terminate on its own, has something to do with the prediction thread in difficultyPredictor
		System.exit(0);
	}

}
