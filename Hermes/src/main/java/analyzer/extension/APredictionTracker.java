package analyzer.extension;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.trees.J48;
import weka.core.Instances;

/**Prediction tracer for Weka data mining tool<p>
 * 
 * Only works with J48 for now<p>
 *Input parameters:<br> 
 *-train <i>filename</i>: Train with the arfffile file 
 *-test <i>filename</i>: Test with the 
 *
 *
 * 
 * */
public class APredictionTracker implements PredictionTracker{
	private String trainFile;
	private String testFile;

	private Instances training;
	private Instances testing;

	private J48 classifier;

	public APredictionTracker(String trainFile, String testFile) {
		this.trainFile=trainFile;
		this.testFile=testFile;

	}


	//Reload/load instances from the training  arff file and testing arff file
	public PredictionTracker loadInstances() {
		try(BufferedReader trainStream=new BufferedReader(new InputStreamReader(new FileInputStream(trainFile)));
				BufferedReader testStream=new BufferedReader(new InputStreamReader(new FileInputStream(testFile)))
				) {

			training=new Instances(trainStream);
			training.setClassIndex(training.numAttributes()-1);
			testing=new Instances(testStream);
			testing.setClassIndex(testing.numAttributes()-1);

		} catch(IOException e) {
			e.printStackTrace();

		}

		return this;
	}

	//build classifier
	public PredictionTracker buildClassifier() {
		//make sure both sets are not null
		if(training==null || testing==null)
			loadInstances();

		classifier=new J48();
		try {
			classifier.buildClassifier(training);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return this;
	}


	//evaluate testing set
	public List<PredictedInstance> evaluateTesting() {
		if(this.classifier==null)
			buildClassifier();
		
		List<PredictedInstance> results=new ArrayList<>();

		for(int i=0;i<testing.numInstances();i++) {
			double classification=Double.NaN;
			try {
				classification = classifier.classifyInstance(testing.instance(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			results.add(new PredictedInstance(testing.instance(i),classification));
			
			
		}
		
		return results;

	}

	

	public static void main(String[] args) {
		for(PredictedInstance e: new APredictionTracker("data/OutputData/all.arff","data/OutputData/16/16.arff").evaluateTesting()){
			System.out.println(e);
			
		}
		

	}

}


