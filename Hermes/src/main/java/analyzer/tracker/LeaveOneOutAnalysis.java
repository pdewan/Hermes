package analyzer.tracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.unsupervised.attribute.Remove;
import analyzer.extension.ArffFileGeneratorFactory;
import difficultyPrediction.APredictionParameters;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.metrics.ATestRatioCalculator;
import difficultyPrediction.metrics.CommandClassificationSchemeName;
import difficultyPrediction.predictionManagement.ClassifierSpecification;
import difficultyPrediction.predictionManagement.OversampleSpecification;

/**For leave one out analysis
 * 
 * @author wangk1
 *
 */
public class LeaveOneOutAnalysis {
	private static BufferedWriter out=null;

	static{

		try {
			out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("leaveoneout.txt"))));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


	}

//	enum ClassifierSpecification{
//		J48,
//		ADABOOST,
//		BAGGING
//
//	}
//
//	enum Oversample{
//		SMOTE(new double[] {500,1000,2000,3000}),
//		RESAMPLE(new double[] {0.25,0.5,0.75,1.0});
//
//		private double[] levels;
//
//		private Oversample(double[]  levels) {
//			this.levels=levels;
//
//		}
//
//		public double[] getFilterLevels() {
//			return this.levels;
//
//		}
//	}

	private static String trainingDir;
	private static String testingDir;
	//private static String outputAndTestSubDir;

	public static void generateNeccessaryArffFiles(String participantSubDir) {
		ArffFileGeneratorFactory f=new ArffFileGeneratorFactory("");

		f.setOutputSubDirectory(participantSubDir);
		//gen leave one out all.arff
		generateLeaveParticpantOut(f);
		//gen participant.arff
		generateParticipantArff(f);
	}


	public static void generateLeaveParticpantOut(ArffFileGeneratorFactory f) {
		DifficultyPredictionSettings.setNewRatioFiles(false);

		for(int i=1;i<18;i++) {	
			f.insertCommand("All ignore "+String.valueOf(i));
			f.createArffs();

		}

	}

	public static void generateParticipantArff(ArffFileGeneratorFactory f) {
		DifficultyPredictionSettings.setNewRatioFiles(true);
		f.setOutputSubDirectory("");

		for(int i=1;i<18;i++) {	
			f.insertCommand(String.valueOf(i));
			f.createArffs();

		}


		DifficultyPredictionSettings.setNewRatioFiles(false);


	}

	public static void leaveOneOutAnalysis(ClassifierSpecification aClassifierSpecification, OversampleSpecification anOversample,double percentage,boolean ignoreWeblinks) throws Exception {
		//the tracker
		PredictionTracker aPredictionTracker=new APredictionTracker();

		//filter
		weka.filters.Filter aFilter=null;
		aFilter = anOversample.toWekaFilter(percentage);

//		if(anOversample==OversampleSpecification.SMOTE)
//			aFilter=new SMOTE();
//		else {
//			Resample aResample=new Resample();
//			aResample.setBiasToUniformClass(percentage==Double.MIN_VALUE? 0.0:percentage);
//			aFilter=aResample;
//		}

		//outputter we want, wrong predictions that is either predicted as stuck or not stuck
		//PredictionOutputter wrongStuckOutputter=new ClassFilterOutputter(new WrongPredictionOutputter("data/wrongpredictstuck.csv"),"NO");
		//PredictionOutputter wrongNotStuckOutputter=new ClassFilterOutputter(new WrongPredictionOutputter("data/wrongpredictnostuck.csv"),"YES");

		PredictionOutputter rightStuckOutputter=new ClassFilterOutputter("YES",new ResamplingOutputter(10,400,new CorrectPredictionOutputter("data/rightpredictstuck.csv")));
		//PredictionOutputter rightNotStuckOutputter=new ClassFilterOutputter(new CorrectPredictionOutputter("data/rightpredictnostuck.csv"),"NO");
		PredictionOutputter matrixOutputter=new ResultOutputter("YES", "NO",out);

		PredictionOutputter combinedOutputter=new CombinedOutputter(matrixOutputter,rightStuckOutputter);

		double p=0;
		int offSet=0;
		for(int aParticipant=16;aParticipant<33;aParticipant++) {
			//don't delete, just used to compensate for the fact there is no participant 25
			if(aParticipant==25) {
				offSet++;

			}

			// //Participant that is an outlier with regards to number of stuck segments
			//			if(i==28) {
			//				continue;
			//
			//			}

			String training=trainingDir+(aParticipant-15)+"/all.arff";
			String testing=testingDir+(aParticipant+offSet)+"/"+(aParticipant+offSet)+".arff";

			//			out("Training: "+training);
			//			out("Testing: "+testing);

			aPredictionTracker.setTrainingFile(training);
			aPredictionTracker.setTestingFile(testing);

			aPredictionTracker.loadInstances();

			if(ignoreWeblinks) {
				//out("Ignoring weblink");
				Instances train=aPredictionTracker.getTrainingInstances();
				int index=train.attribute("webLinkTimes").index()+1;
				//out(index);
				Remove aRemove=new Remove();
				aRemove.setAttributeIndices(index+"");
				aRemove.setInputFormat(train);
				aPredictionTracker.setTrainingInstance(weka.filters.Filter.useFilter(train, aRemove));

			}


			//try to smote training to 30% of testing
			Instances t_instance=aPredictionTracker.getTrainingInstances();
			p=smote30Percent(t_instance);
			if(anOversample==OversampleSpecification.SMOTE)
				((SMOTE) aFilter).setPercentage(percentage==Double.MIN_VALUE? p:percentage);


			aPredictionTracker.filterTraining(aFilter);


			weka.classifiers.Classifier aWekaClassifier=null;
			aWekaClassifier = aClassifierSpecification.toClassifier();
//			if(aClassifierSpecification==ClassifierSpecification.ADABOOST) {
//				aWekaClassifier=new AdaBoostM1();
//				((AdaBoostM1) aWekaClassifier).setClassifier(new DecisionStump());
//			} else if(aClassifierSpecification==ClassifierSpecification.BAGGING) {
//				aWekaClassifier=new Bagging();
//				((Bagging) aWekaClassifier).setClassifier(new DecisionStump());
//			} else {
//				aWekaClassifier=new J48();
//
//			}

			aPredictionTracker.buildClassifier(aWekaClassifier);

			aPredictionTracker.evaluateTesting();

			aPredictionTracker.outputResults(combinedOutputter, true);
		}

		try {
			out.write("Testing: "+aClassifierSpecification+" Filter: "+anOversample+" Boost: "+p);
			out.newLine();
			out.flush();
		} catch (IOException e1) {

			e1.printStackTrace();
		}


		combinedOutputter.closeStream();
		try {
			out.write("Analysis completed");
			out.newLine();
			out.flush();

		} catch (IOException e1) {

			e1.printStackTrace();
		}
	}




	/**SMOTE minority class and majority class to be equal
	 * 
	 * @param i
	 * @return
	 */
	private static double smoteEvent(Instances i) {
		double c1count=0;
		double c2count=0;

		for(int index=0;index<i.numInstances();index++) {
			Instance instance=i.instance(index);

			if(instance.classValue()==0.0) {
				c1count++;

			} else {
				c2count++;

			}

		}

		return c1count<c2count? c2count/c1count*100:c1count/c2count*100;
	}

	/**SMOTE minority class and majority class to be equal
	 * 
	 * @param i
	 * @return
	 */
	private static double smote30Percent(Instances i) {
		double c1count=0;
		double c2count=0;

		for(int index=0;index<i.numInstances();index++) {
			Instance instance=i.instance(index);

			if(instance.classValue()==0.0) {
				c1count++;

			} else {
				c2count++;

			}

		}

		return c1count<c2count? c2count*0.3/c1count*100:c1count*0.3/c2count*100;
	}


	public static void main(String[] args) {
		CommandClassificationSchemeName[] vals=new CommandClassificationSchemeName[] {CommandClassificationSchemeName.A3};

		//go through each scheme, can't do this for now because of memory leak preventing entire run
		for(CommandClassificationSchemeName s:vals) {
//			ATestRatioCalculator.CURRENT_SCHEME=s;
			APredictionParameters.getInstance().setCommandClassificationScheme(s);

			String outputAndTestSubDir=s.getSubDir();

			trainingDir="data/OutputData/"+outputAndTestSubDir+"All ignore ";
			testingDir="data/OutputData/";
			generateNeccessaryArffFiles(outputAndTestSubDir);

			try {
//				out.write(String.format("**Results for scheme %s**%n",ATestRatioCalculator.CURRENT_SCHEME));
				out.write(String.format("**Results for scheme %s**%n",APredictionParameters.getInstance().getCommandClassificationScheme()));

				out.newLine();
				out.write("Training dir: "+trainingDir);
				out.newLine();
				out.flush();
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			double[] filteringAmount=new double[] {Double.MIN_VALUE};

			//classifier
			for (ClassifierSpecification c: ClassifierSpecification.values()) {

				//for (Oversample p:Oversample.values()) {
				OversampleSpecification anOversampleSpecification=OversampleSpecification.SMOTE;
				//different levels
				for (double l:filteringAmount) {


					//args: classifier, filter, and filter %(only for smote)
					try {
						leaveOneOutAnalysis(c, anOversampleSpecification, l,outputAndTestSubDir.equalsIgnoreCase("leaveoneoutjason/"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				//}

			}

		}
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//can't figure out why the timer thread won't terminate on its own, has something to do with the prediction thread in difficultyPredictor
		System.exit(0);
	}

}
