package analyzer.extension;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.DecisionStump;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.unsupervised.attribute.Remove;

public class CeciliaPredictionTracker extends APredictionTracker implements PredictionTracker{
	private static String dataFile;
	private static int fold = 10;
	private static boolean smoteTest = false;
	private static int[] toRemove = {};
	private static int smotePercentage = 0;
	private static String modelName; 

	private Instances training;
	private Instances testing;
	
	private FilteredClassifier classifier;
	
	public List<Double> allPreds = new ArrayList<Double>();

	public List<Double> allTruths = new ArrayList<Double>();
	
	public ArrayList<Long> errors;
	
	public CeciliaPredictionTracker() {
		super();
	}


	public static void main(String[] args)  {
		dataFile = "data/OutputDataExtraInterval/all_testing_extra_interval_all.arff";
		fold = 5;  //cross validation fold
		smoteTest = false;
//		toRemove = new int[]{0, 3}; //features in arff file to be removed
//									      //0 => @Attribute currentDate string
//										  //1 => @Attribute currentTime numeric
//										  //2 => @Attribute timeFromStart numeric
//									      //3 => @Attribute sourceDifficulty string
		toRemove = new int[]{0, 1, 3}; //features in arff file to be removed
	      //0 => @Attribute currentDate string
		  //1 => @Attribute currentTime numeric
		  //2 => @Attribute timeFromStart numeric
	      //3 => @Attribute sourceDifficulty string
		smotePercentage = 100;
//		modelName = "j48"; //"decisionStump" or "j48"
		modelName = "decisionStump"; //"decisionStump" or "j48"

		testAllCrossValidationSmote();
	}

	
	//Reload/load instances from the arff file
	public PredictionTracker loadInstances() {
	    try(BufferedReader datastream=new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));){
	    	Instances data = new Instances(datastream);
	    	Random rand = new Random();   // create seeded number generator
	    	Instances randData = new Instances(data);   // create copy of original data
	    	randData.randomize(rand);         // randomize data with number generator
	    	training = randData.trainCV(fold, 0);
	    	testing = randData.testCV(fold, 0);
		    
		    training.setClassIndex(training.numAttributes()-1);
		    testing.setClassIndex(testing.numAttributes()-1);
	    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    }
		return this;
	}
	
	//build classifier
	public PredictionTracker buildClassifier() {
		//make sure both sets are not null
		if(training==null || testing==null)
			loadInstances();
		
		try {
			FilteredClassifier fc = new FilteredClassifier();
			classifier = fc;
			if (modelName == "j48" ){
				J48 model = new J48();
				fc.setClassifier(model);
			} else if (modelName == "decisionStump"){
				DecisionStump model = new DecisionStump();
				fc.setClassifier(model);
			}
			SMOTE smote=new SMOTE();
			smote.setPercentage(smotePercentage);
			smote.setInputFormat(training);
			Remove filter = new Remove();
			filter.setInputFormat(training);
			filter.setInvertSelection(false);
			filter.setAttributeIndicesArray(toRemove);
			fc.setFilter(filter);
			training = Filter.useFilter(training, smote); // use smote on only the training set
		    
			if (smoteTest) {
		    	SMOTE testSmote=new SMOTE();
		    	testSmote.setPercentage(smotePercentage);
		    	testSmote.setInputFormat(testing);
		    	testing = Filter.useFilter(testing, testSmote); 
		    }
		
			classifier.buildClassifier(training);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return this;
	}
	
	public List<PredictedInstance> evaluateTestingTrainTest() {
		loadInstances();
		buildClassifier();
		
		List<PredictedInstance> results=new ArrayList<>();
		int totalPos = 0;
		int totalNeg = 0;
		double tp = 0, tn = 0, fp = 0, fn =0;
		

		for(int i=0;i<testing.numInstances();i++) {
			
			
			double classification=Double.NaN;
			try {
				classification = classifier.classifyInstance(testing.instance(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (testing.instance(i).classValue() == 0) {
				totalNeg++;
				if (testing.instance(i).classValue() == classification) {
					tn++;
				} else {
					fp++;
				}
			} else {
				totalPos++;
				if (testing.instance(i).classValue() == classification) {
					tp++;
				} else {
					fn++;
				}
			}
			
			results.add(new PredictedInstance(testing.instance(i),classification));
			
			
		}
		System.out.println ("-------------------------------------------");
		System.out.println("totalNum: " + (totalPos+totalNeg));
		System.out.println("total positive: " + totalPos);
		System.out.println("total negative: " + totalNeg);
		System.out.println("tp,tn,fp,fn raw numbers: " + tp + "," + tn + "," + fp+ "," + fn);
		
		double aggregate =(tp + tn)/(totalPos + totalNeg);
		tp /= totalPos;
		tn /= totalNeg;
		fp /= totalNeg;
		fn /= totalPos;
		
		System.out.println("Aggregate accuracy:" + aggregate);
		System.out.println("tp,tn,fp,fn: " + tp + "," + tn + "," + fp+ "," + fn);
		
		return results;

	}
	

	
	public static void testAllCrossValidationSmote() {
//		BenPredictionTracker x = new BenPredictionTracker("data/OutputDataAll/all_testing.arff");
		CeciliaPredictionTracker x = new CeciliaPredictionTracker();
		for (int i = 0; i < fold; i++) {
			x.evaluateTestingTrainTest();	
		}

	}

	
}
