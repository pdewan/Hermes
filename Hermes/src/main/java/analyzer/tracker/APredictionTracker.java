package analyzer.tracker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.meta.Bagging;
import weka.core.Instances;
import weka.filters.Filter;

/**Prediction tracer for Weka data mining tool<p>
 * 
 * Only works with J48 for now<p>
 *Input parameters:<br> 
 *-train <i>filename</i>: Train with the arfffile file 
 *-test <i>filename</i>: Test with the 
 *<p>
 *Instructions and steps:<br>
 *1. Create and set training file and testing file string paths <br>
 *2. Call loadInstances to load the instances from files<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2a. Can also call filter to filter the training instances<br>
 *3. Call Build Classifier to build the classifier<br>
 *4. Call evaluateTesting to test the classifier on testing instances<br>
 *5. Call output with the appropriate outputter to output testing results<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5a. Can also output the results as list of {@link PredictedInstance} <br>
 *
 * 
 * */
public class APredictionTracker implements PredictionTracker{
	
	private String trainFile;
	private String testFile;

	private Instances training;
	private Instances testing;

	private weka.classifiers.Classifier classifier;

	private List<PredictedInstance> result;
	
	public APredictionTracker(String trainFile, String testFile) {
		this.trainFile=trainFile;
		this.testFile=testFile;
		this.result=new ArrayList<>();
		
	}
	
	public APredictionTracker() {
		this(null,null);
		
	}


	//Reload/load instances from the training  arff file and testing arff file
	@Override
	public void loadInstances() {
		try(BufferedReader trainStream=new BufferedReader(new InputStreamReader(new FileInputStream(this.trainFile)));
				BufferedReader testStream=new BufferedReader(new InputStreamReader(new FileInputStream(this.testFile)))
				) {
			
			
			this.training=new Instances(trainStream);
			this.training.setClassIndex(training.numAttributes()-1);
			
			this.testing=new Instances(testStream);
			this.testing.setClassIndex(this.testing.numAttributes()-1);

		} catch(IOException e) {
			e.printStackTrace();

		}

//		return this;
	}
	
	@Override
	public void setTestingFile(String testingInstanceFile) {
		this.testFile=testingInstanceFile;
		this.testing=null;
		
//		return this;
	}
	
	@Override
	public void setTrainingFile(String trainingInstanceFile) {
		this.trainFile=trainingInstanceFile;
		this.training=null;
		
//		return this;
	}

	
	
	//filter training sample
	@Override
	public void filterTraining(Filter f) {
		checkTestingAndTraining();
		
		try {
			f.setInputFormat(this.training);
			this.training=Filter.useFilter(this.training, f);
			//we need to rebuild classifier
			this.classifier=null;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	//build classifier
	@Override
	public PredictionTracker buildClassifier(Classifier c) {
		//make sure both sets are not null
		checkTestingAndTraining();

		this.classifier=c;
		
		
		try {
			classifier.buildClassifier(training);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return this;
	}



	//evaluate testing set
	@Override
	public PredictionTracker evaluateTesting() {
		if(this.classifier==null)
			return this;

		this.result=new ArrayList<>();

		for(int i=0;i<this.testing.numInstances();i++) {
			double classification=Double.NaN;
			try {
				classification = this.classifier.classifyInstance(this.testing.instance(i));
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.result.add(new PredictedInstance(this.testing.instance(i),classification));


		}

		return this;

	}

	

	@Override
	public PredictionOutputter outputResults(PredictionOutputter p,boolean...keepStreamOpen) {
		if(this.result.size()==0) {
			this.evaluateTesting();
			
		}
		
		for(PredictedInstance i:this.result) {
			p.output(i);
			
		}
		
		if(keepStreamOpen.length>0&&!keepStreamOpen[0]) {
			p.predictionOutputtingEnded();
			p.closeStream();
			
		}

		return p;
	}
	
	
	@Override
	public List<PredictedInstance> outputResultsAsList() {
		if(this.result.size()==0) {
			this.evaluateTesting();
			
		}
		
		return this.result;
		
	}
	
	//Private helper methods section
	
	private void checkTestingAndTraining() {
		//make sure both sets are not null
		if(this.training==null || this.testing==null) {
			loadInstances();
		}	
			
		
	}

	//GETTER AND SETTER SECTION
	
	
	@Override
	public Instances getTestingInstances() {
		return this.testing;
		
	}

	@Override
	public Instances getTrainingInstances() {
		return this.training;
		
	}

	@Override
	public PredictionTracker setTrainingInstance(Instances i) {
		this.training=i;
		this.training.setClassIndex(this.training.numAttributes()-1);
		return this;
	}


}


