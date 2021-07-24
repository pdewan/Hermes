package analyzer.extension;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.unsupervised.attribute.Remove;

/**Prediction tracer for Weka data mining tool<p>
 * 
 * Only works with J48 for now<p>
 *Input parameters:<br> 
 *-train <i>filename</i>: Train with the arfffile file 
 *-test <i>filename</i>: Test with the 
 *
 * This is written by Ben Levine as the name suggests
 * Supports both leave one out, using two files, and 80/20
 * also allows the instances file to have additional data not
 * used for prediction
 * 
 * 
 * */
public class BenPredictionTracker extends APredictionTracker implements PredictionTracker{
	private String trainFile;
	private String testFile;
	private String dataFile;

	private Instances training;
	private Instances testing;
	
	

	private FilteredClassifier classifier;
	
	private long timeThreshold;
	
	private int fold = 0;
	
	private boolean testMode = false; // in test mode one uses artificial data
	private ArrayList<Double> testPredictions = new ArrayList<Double>();
	
	private boolean aggregation;
	
	private boolean useThresholdAsCorrect;
	
	private List<Double> preds = new ArrayList<Double>();
	private List<Double> continuous_preds_aggregate = new ArrayList<Double>();
	private List<Double> truths = new ArrayList<Double>();
	private List<Double> continuous_truths_aggregate = new ArrayList<Double>();
	private List<Long> continuous_times = new ArrayList<Long>();
	
	private long DEFAULT_ERROR_IF_NONE_CORRECT = -1;
	
	private  int base_aggregate_segments = 5;
	
	public ArrayList<Long> errors;
	/*
	 * 
	 */
	//for leave one out
	public BenPredictionTracker(String trainFile, String testFile, 
			long threshold, //the delay threshold (vs distribution threshold
			boolean aggregation, // do we aggregate
			boolean useThresholdAsCorrect//use or not delay
			) {
		super(trainFile, testFile);
		this.trainFile=trainFile;
		this.testFile=testFile;
		this.aggregation = aggregation;
		errors = new ArrayList<Long>();
		this.useThresholdAsCorrect = useThresholdAsCorrect;
		this.timeThreshold = threshold;
		this.dataFile = "";
	}
	
	//for 80/20
	public BenPredictionTracker(String dataFile) {
		super();
		this.dataFile = dataFile;
	}


	//Reload/load instances from the training  arff file and testing arff file
	// gets called for each fold in case of 80/20 and I guess each test data file
	// pair for leave one out
	// called both by buildClassfier and evaluateTesting
	public PredictionTracker loadInstances() {
		
		if (this.dataFile == "") { // this is the case when we have training and test files for leave one out
			try(BufferedReader trainStream=new BufferedReader(new InputStreamReader(new FileInputStream(trainFile)));
					BufferedReader testStream=new BufferedReader(new InputStreamReader(new FileInputStream(testFile)))
					) {
				
				SortedSet<Integer> to_remove = new TreeSet<Integer>(); 
			    
			    int[] toRemove = {0, 1, 2, 3}; //hard code this for now
			    
			    // these seem to be the extra columns ignored by the tester
				
	
				training=new Instances(trainStream);
				
				testing=new Instances(testStream);
				//System.out.println(training.numAttributes());
				
				Remove filter = new Remove();
				filter.setInvertSelection(false);
				filter.setInputFormat(training);
				filter.setAttributeIndicesArray(toRemove);
			    
			    training = Filter.useFilter(training, filter);
			    
			    Remove filterTest = new Remove();
			    filterTest.setInvertSelection(false);
			    filterTest.setInputFormat(testing);
			    filterTest.setAttributeIndicesArray(toRemove);
			    
			    testing = Filter.useFilter(testing, filterTest);
			    
			    //System.out.println(training.numAttributes());
			    //System.out.println(filter.getAttributeIndices());
			    
			    training.setClassIndex(training.numAttributes()-1);
			    testing.setClassIndex(testing.numAttributes()-1);
	
			} catch(Exception e) {
				e.printStackTrace();
	
			}
		} else { // This is where the 80/20 testing is done
		    
		    int[] toRemove = {0, 1, 2, 3}; //hard code this for now
		   
		    try(BufferedReader datastream=new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));){
		    	Instances data = new Instances(datastream);
		    	Random rand = new Random();   // create seeded number generator
		    	Instances randData = new Instances(data);   // create copy of original data
		    	randData.randomize(rand);         // randomize data with number generator
		    	// we are losing the sequencing information through randomization
		    	training = randData.trainCV(5, fold);
		    	Remove filter = new Remove();
				filter.setInvertSelection(false);
				filter.setInputFormat(training);
				filter.setAttributeIndicesArray(toRemove);
			    
			    training = Filter.useFilter(training, filter);
			    
			    
		    	testing = randData.testCV(5, fold);
		    	Remove filterTest = new Remove();
			    filterTest.setInvertSelection(false);
			    filterTest.setInputFormat(testing);
			    filterTest.setAttributeIndicesArray(toRemove);
			    
			    testing = Filter.useFilter(testing, filterTest);
			    
			    training.setClassIndex(training.numAttributes()-1);
			    testing.setClassIndex(testing.numAttributes()-1);
			    fold++; // so what did we do with the last fold? 
			    // fold is a global variable so we are calling this method multiple times I guess
		    } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		    }
		}

		return this;
	}
	protected boolean smoteTests = false;
	
	//build classifier
	public PredictionTracker buildClassifier() {
		//make sure both sets are not null
		if(training==null || testing==null)
			loadInstances();
		
		try {
			int[] toRemove = {0, 1, 2, 3, 4}; //hard code this for now
			FilteredClassifier fc = new FilteredClassifier();
			J48 j48 =new J48();
			classifier = fc;
			SMOTE smote=new SMOTE();
			smote.setPercentage(400); // smote precentage hadrwired!
			smote.setInputFormat(training);
			fc.setClassifier(j48);
			Remove filter = new Remove();
			filter.setInputFormat(training);
			filter.setInvertSelection(false);
			
			filter.setAttributeIndicesArray(toRemove); // this was also done in loadInstances, need to do it again?
			fc.setFilter(filter);
			training = Filter.useFilter(training, smote); // use smote automatically on only the training set
		    if (smoteTests) {
		    	System.out.println ("Smoting Test Data");
		    	SMOTE testSmote=new SMOTE();
		    	testSmote.setPercentage(400);
		    	testSmote.setInputFormat(testing);
		    	testing = Filter.useFilter(testing, testSmote); 
		    }
		
			classifier.buildClassifier(training);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return this;
	}
	
	public List<PredictedInstance> evaluateTesting(){
		if (!this.aggregation) {
			return evaluateTestingSingular();
		} else {
			return evaluateTestingAggregate();
		}
	}
	
	private double calculateAverage(List <Long> marks) {
		  Long sum = (long) 0;
		  if(!marks.isEmpty()) {
		    for (Long mark : marks) {
		        sum += mark;
		    }
		    return sum.doubleValue() / marks.size();
		  }
		  return sum;
	}
	
	public Double getMajorityPredictions(List<Double> predictions) {
		double posPreds = 0;
		for (int i = 0; i < predictions.size(); i++) {
			if (predictions.get(i) == 1.0) {
				posPreds++;
			}
		}
		return (posPreds > this.base_aggregate_segments / 2.0) ? 1.0 : 0.0;
	}
	
	
	public List<PredictedInstance> evaluateTestingAggregate() {
		if(this.classifier==null)
			buildClassifier();
		List<PredictedInstance> results = new ArrayList<PredictedInstance>();
		List<Double> predictions = new ArrayList<Double>();
		List<Double> aggPredictions = new ArrayList<Double>();
		List<Double> truth = new ArrayList<Double>();
		List<Double> aggTruths = new ArrayList<Double>();
		List<Long> times = new ArrayList<Long>();
		List<Long> avgTimes = new ArrayList<Long>();
		int size = this.testMode ? 10 : testing.numInstances();
		for(int i=0;i<size;i++) {
			try {
				double classification = this.testMode ? this.testPredictions.get(i) :
					classifier.classifyInstance(testing.instance(i));
				double trueVal = testing.instance(i).classValue();
				this.continuous_preds_aggregate.add(classification);
				this.continuous_truths_aggregate.add(trueVal);
				this.continuous_times.add(Long.parseLong(testing.instance(i).toString(2)));
				predictions.add(classification);
				truth.add(trueVal);
				times.add(Long.parseLong(testing.instance(i).toString(2)));
				if (times.size() == this.base_aggregate_segments) {
					avgTimes.add(new Double(calculateAverage(times)).longValue());
					aggPredictions.add(this.getMajorityPredictions(predictions));
					aggTruths.add(this.getMajorityPredictions(truth));
					predictions.clear();
					truth.clear();
					times.clear();
				}
			}
			catch (Exception e) {
					e.printStackTrace();
			}
		}
		
		int correct = 0;
		for (int i = 0; i < aggPredictions.size(); i++) {
			double trueVal = aggTruths.get(i);
			double pred = aggPredictions.get(i);
			long time = avgTimes.get(i);
			if (trueVal == pred) {
				correct++;
				this.errors.add((long) 0);
			} else {
				int startIndexForAggregate = i * 5;
				long aggPredTime = avgTimes.get(i);
				boolean withinThreshold = false;
				for (int j = 0; j < 5; j++) {
					double tr = this.continuous_truths_aggregate.get(j + startIndexForAggregate);
					long ti = this.continuous_times.get(j + startIndexForAggregate);
					long timeDiff = Math.abs((aggPredTime - ti));
					if (timeDiff < this.timeThreshold && tr == pred) {
						this.errors.add(timeDiff);
						withinThreshold = true;
						break;
					}
				}
				if (!withinThreshold) {
					this.errors.add((long)-1);
				}
			}
			results.add(new PredictedInstance(trueVal, pred, time));
		}
		
		System.out.println("Aggregate accuracy:");
		double acc = (double) correct / (double)aggTruths.size();
		System.out.println(acc);
		
		this.preds = aggPredictions;
		this.truths = aggTruths;
		
		return null;
	}

	//evaluate testing set
	public List<PredictedInstance> evaluateTestingSingular() {
		if(this.classifier==null)
			buildClassifier();
		
		List<PredictedInstance> results=new ArrayList<>();
		int size = this.testMode ? this.testPredictions.size() : testing.numInstances();
		for(int i=0;i<size;i++) {
			double classification=Double.NaN;
			try {
				classification = this.testMode ? this.testPredictions.get(i) : 
					classifier.classifyInstance(testing.instance(i));
				this.preds.add(classification);
				this.truths.add(testing.instance(i).classValue());
				//System.out.println(testing.instance(i).toString());
				//if not the correct classification, see how far off it was.
				if (classification == 0.0) {
					//System.out.println("predicted difficulty");
				}
				
				if (classification != testing.instance(i).classValue()) {
					boolean withinThreshold = true;
					long currentTime;
					try {
					 currentTime = Long.parseLong(testing.instance(i).toString(2)); 
					} catch (NumberFormatException nfe) {
						currentTime = 0; // we will not use smoted current time
					}
					int j = i + 1;
					while (withinThreshold && j < size) {
//						long nextTime = Long.parseLong(testing.instance(j).toString(2));
						long nextTime;
						try {
							nextTime= Long.parseLong(testing.instance(j).toString(2));
						} catch (NumberFormatException nfe) {
							nextTime = 0;
						}
						long diff = nextTime - currentTime;
						if (diff > this.timeThreshold) {
							withinThreshold = false;
						} else if (testing.instance(j).classValue() == classification) {
							//System.out.println("found error of: " + diff);
							if (classification == 0.0) {
								//System.out.println("found difficulty close");
							}
							errors.add(diff);
							break;
						}
						j++;
					}
					//if we didn't find a matching classification within 3 minutes, then set error of -1 to signify
					if (errors.size() != i + 1) {
						errors.add(DEFAULT_ERROR_IF_NONE_CORRECT);  //add constant -1 error
					}
					
					if (classification == 0.0) {
						//System.out.println("got a negative classification wrong with error: " + errors.get(i));
					}
					
				}else {
					errors.add((long) 0);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			results.add(new PredictedInstance(testing.instance(i),classification));
			//if (testing.instance(i).classValue() == 0.0) {
			//	System.out.println("ground truth difficulty, error added: " + errors.get(i));
			//}
			
		}
		
		return results;

	}
	
	public ArrayList<Double> calculateErrorStats(){
		ArrayList<Double> stats = new ArrayList<Double>();
		double totalCorrectClassifications = 0;
		double totalIncorrectOutOfThreshold = 0;
		double averageErrorIfWithinThreshold = 0;
		double tp = 0;
		double tn = 0;
		double fp = 0;
		double fn = 0;
		double totalPositive = 0;
		double totalNegative = 0;
		//first stat will be percentage of wrong predictions with no correct predictions within 3 minutes
		for (int i = 0; i < this.errors.size(); i++) {
			
			if (this.truths.get(i) == 0) {
				totalNegative++;
			} else {
				totalPositive++;
			}
			if (errors.get(i) == 0) {
				if (this.truths.get(i) == 0) {
					tn++;
				}else {
					tp++;
				}
				totalCorrectClassifications++;
			}
			else {
				//if within threshold and boolean is set, then count these as correct
				if (errors.get(i) != -1 && this.useThresholdAsCorrect) {
					if (this.truths.get(i) == 0) {
						//System.out.println("pred: " + this.preds.get(i) + " truth: " + this.truths.get(i));
						tn++;
					}else {
						tp++;
					}
					
					totalCorrectClassifications++;
				} else {
					if (this.truths.get(i) == 0) {
						fp++;
					} else {
						fn++;
					}
					if (errors.get(i) == -1) {
						totalIncorrectOutOfThreshold++;	
					}
					
				}
				if (errors.get(i) != -1 && errors.get(i) != 0) {				
					averageErrorIfWithinThreshold += errors.get(i);
				}
				
			}
		}
		
		int totalExactIncorrect = 0;
		for (int i = 0; i < this.errors.size(); i++) {
			if (errors.get(i) != 0) {
				totalExactIncorrect++;
			}
		}
		
		System.out.println("tn fp tn fn: " + tp + " " + fp + " " + tn + " " + fn); 
		double totalIncorrectClassifications = errors.size() - totalCorrectClassifications;
		//double totalIncorrectClassifications = totalExactIncorrect;
		averageErrorIfWithinThreshold /= (totalExactIncorrect - totalIncorrectOutOfThreshold);
		tp /= totalPositive;
		tn /= totalNegative;
		fp /= totalNegative;
		fn /= totalPositive;
		stats.add(tp);
		stats.add(tn);
		stats.add(fp);
		stats.add(fn);
		stats.add(totalCorrectClassifications / (double) errors.size()); //percent correct
		stats.add(totalIncorrectClassifications / (double) errors.size()); //percent incorrect
		stats.add(totalIncorrectOutOfThreshold / totalExactIncorrect); //percent incorrect out of threshold
		stats.add(averageErrorIfWithinThreshold); //accurate name of variable
		
		System.out.println("total pos: " + totalPositive + " total neg: " + totalNegative);
		return stats;
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
		
		System.out.println("tp,tn,fp,fn raw numbers: " + tp + "," + tn + "," + fp+ "," + fn);
		
		tp /= totalPos;
		tn /= totalNeg;
		fp /= totalNeg;
		fn /= totalPos;
		
		System.out.println("tp,tn,fp,fn: " + tp + "," + tn + "," + fp+ "," + fn);
		
		return results;

	}
	
	private boolean closeClassification(int ind, double classVal) throws Exception {
		boolean withinThreshold = true;
		long currentTime = Long.parseLong(testing.instance(ind).toString(2)); 
		int j = ind + 1;
		while (withinThreshold && j < testing.numInstances()) {
			long nextTime = Long.parseLong(testing.instance(j).toString(2));
			long diff = nextTime - currentTime;
			if (diff > this.timeThreshold) {
				withinThreshold = false;
			//check if there will soon be a correct prediction to link to
			} else if (classifier.classifyInstance(testing.instance(j)) == classVal) {
				return true;
			}
			j++;
		}
		return false;
	}
	
	
	//this will calculate the iou for difficulty if difficulty=true or non-difficulty if difficulty=false
	public double calculateIOU(boolean difficulty) {
		if(this.classifier==null)
			buildClassifier();
		
		ArrayList<Double> ious = new ArrayList<Double>();
		double focus = difficulty ? 0.0 : 1.0;
		int grlen = 0;
		
		for(int i=0;i<testing.numInstances();i++) {
			double classification=Double.NaN;
			double groundTruth = Double.NaN;
			try {
				classification = classifier.classifyInstance(testing.instance(i));
				groundTruth = this.testing.instance(i).classValue();
				
				//continue region
				if (groundTruth == focus) {
					grlen++;
				} 
				if (groundTruth != focus || i == testing.numInstances() - 1){
					if (grlen > 0) {
						int ind = i - grlen;
						boolean predRegionDone = false;
						int predLen = 0;
						while (!predRegionDone && ind < testing.numInstances()) {
							double currentClassification =classifier.classifyInstance(testing.instance(ind)); 
							if (currentClassification == focus || closeClassification(ind, focus)) {
								predLen++;
							} 
							if (!(predLen == 0 && ind < i && ind != testing.numInstances() - 1) 
									&& !(currentClassification == focus || closeClassification(ind, focus))
									|| testing.numInstances() - 1 == ind) {
								int regionSize = Math.max(grlen, ind -  (i - grlen) + 1);
								
								if (regionSize == 0) {
									break;
								}
								ious.add((double)predLen / (double)regionSize);
								predRegionDone = true;
							}
							ind++;
						}
					}
					grlen = 0;
				}
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (ious.size() == 0) {
			System.out.println("no regions");
		}
		double avgIOU = 0.0;
		for (int i = 0; i < ious.size(); i++) {
			avgIOU += ious.get(i);
		}
		avgIOU /= (double)ious.size();
		
		return avgIOU;
	}

	public void setTestMode() {
		this.testMode = true;
		if(this.classifier==null)
			buildClassifier();
		// so these 10 instances have just a value and class value
		testing.instance(0).setClassValue(0.0);
		testing.instance(1).setClassValue(0.0);
		testing.instance(2).setClassValue(0.0);
		testing.instance(3).setClassValue(1.0);
		testing.instance(4).setClassValue(1.0);
		testing.instance(5).setClassValue(1.0);
		testing.instance(6).setClassValue(0.0);
		testing.instance(7).setClassValue(0.0);
		testing.instance(8).setClassValue(1.0);
		testing.instance(9).setClassValue(1.0);
		// changing value only in index 2
		// wonder what the test values are
		testing.instance(0).setValue(2, 0);
		testing.instance(1).setValue(2, 200000);
		testing.instance(2).setValue(2, 400000);
		testing.instance(3).setValue(2, 550000);
		testing.instance(4).setValue(2, 800000);
		testing.instance(5).setValue(2, 1000000);
		testing.instance(6).setValue(2, 1200000);
		testing.instance(7).setValue(2, 1400000);
		testing.instance(8).setValue(2, 1600000);
		testing.instance(9).setValue(2, 1800000);
		
		// 10 of these
		this.testPredictions.add(0.0);
		this.testPredictions.add(0.0);
		this.testPredictions.add(1.0);
		this.testPredictions.add(0.0);
		this.testPredictions.add(1.0);
		this.testPredictions.add(1.0);
		this.testPredictions.add(0.0);
		this.testPredictions.add(0.0);
		this.testPredictions.add(1.0);
		this.testPredictions.add(0.0);
	}
	public static void evaluateAndPrint (BenPredictionTracker x) {
		x.evaluateTesting();
		ArrayList<Double> stats = x.calculateErrorStats();
		System.out.println(stats);
	}
	public static void testDelay() {
		BenPredictionTracker x = new BenPredictionTracker("data/OutputDataAll/all_testing_16.arff", "data/OutputData/1616.arff", 180000, false, true);
		
		evaluateAndPrint (x);
		
	}
	public static void testDelayTestMode() {
		BenPredictionTracker x = new BenPredictionTracker("data/OutputDataAll/all_testing_16.arff", "data/OutputData/1616.arff", 180000, false, true);
		x.setTestMode();
		evaluateAndPrint (x);
		
	}
	public static void testDelayTestModeIOU() {
		
		BenPredictionTracker x = new BenPredictionTracker("data/OutputDataAll/all_testing_16.arff", "data/OutputData/1616.arff", 180000, false, true);
		x.setTestMode();
		System.out.println("IOU difficulty = " + x.calculateIOU(true));
		System.out.println("IOU not-difficulty = " + x.calculateIOU(false));

//		evaluateAndPrint (x);
		
	}
	public static void testNoDelayNoSmote() {
		BenPredictionTracker x = new BenPredictionTracker("data/OutputDataAll/all_testing_16.arff", "data/OutputData/1616.arff", 180000, false, false);
		
		x.smoteTests = false;
		evaluateAndPrint (x);
		
	}
	public static void testNoDelaySmote() {
		BenPredictionTracker x = new BenPredictionTracker("data/OutputDataAll/all_testing_16.arff", "data/OutputData/1616.arff", 180000, false, false);
		
		x.smoteTests = true;
		evaluateAndPrint (x);
		
	}
	
	public static void testAllCrossValidationSmote() {
		BenPredictionTracker x = new BenPredictionTracker("data/OutputDataAll/all_testing.arff");
		x.smoteTests = true;
		// 5 fold cv
		for (int i = 0; i < 5; i++) {
			x.evaluateTestingTrainTest();	
		}

	}
	public static void testAllCrossValidationNoSmote() {
		BenPredictionTracker x = new BenPredictionTracker("data/OutputDataAll/all_testing.arff");
		x.smoteTests = false;
		// 5 fold cv
		for (int i = 0; i < 5; i++) {
			x.evaluateTestingTrainTest();	
		}

	}
	

	public static void main(String[] args) {
//		testAllCrossValidationSmote();
		testDelayTestMode();
//		testAllCrossValidationNoSmote();

//		/*
//		String[] aUserNames = {"16", "17", "18", "19", "20", "21", "22", "23", "24", "26", "27", "28", "29", "30", "31", "32", "33"};
//		for (String user : aUserNames) {
//			System.out.println(user + ": ***************************");
//			String trainFile = "data/OutputDataAll/all_testing_" + user + ".arff";
//			String testFile = "data/OutputData/" + user + user + ".arff";
//			System.out.println("train file: " + trainFile + " test file: " + testFile);
//			BenPredictionTracker x = new BenPredictionTracker(trainFile, testFile, 180000, true, false);
//			x.setTestMode();
//			List<PredictedInstance> predictions = x.evaluateTesting();
//			
//			//System.out.println("IOU difficulty = " + x.calculateIOU(true));
//			//System.out.println("IOU not-difficulty = " + x.calculateIOU(false));
//			ArrayList<Double> stats = x.calculateErrorStats();
//			System.out.println(stats);
//		}
//		*/
//		
//		
////		BenPredictionTracker x = new BenPredictionTracker("data/OutputDataAll/all_testing.arff");
////		// 5 fold cv
////		for (int i = 0; i < 5; i++) {
////			x.evaluateTestingTrainTest();	
////		}
//		/*
//		 * Use as VM arguments:
//		 * -Xms512M -Xmx5000M
//		 */
//		// use delay
////		BenPredictionTracker x = new BenPredictionTracker("data/OutputDataAll/all_testing_16.arff", "data/OutputData/1616.arff", 180000, false, true);
//		// do not use deyal
//		BenPredictionTracker x = new BenPredictionTracker("data/OutputDataAll/all_testing_16.arff", "data/OutputData/1616.arff", 180000, false, false);
////		x.smoteTests = false;
//		x.smoteTests = true;
////		BenPredictionTracker x = new BenPredictionTracker("data/OutputDataAll/all_testing_16.arff", "data/OutputData/1616.arff", 180000, false, false);
////		x.smoteTests = true;
//
//		x.evaluateTesting();
//		ArrayList<Double> stats = x.calculateErrorStats();
//		System.out.println(stats);
//
////		x.calculateErrorStats();
//
//		
//		
//		//List<PredictedInstance> predictions = x.evaluateTesting();
//		//for(PredictedInstance e: predictions){
//			//System.out.println(e);
//			
//		//}
//		
//		//ArrayList<Double> stats = x.calculateErrorStats();
//		//System.out.println(stats);
//		
//		//for(PredictedInstance e: new BenPredictionTracker("data/OutputDataAll/all_testing_16.arff").evaluateTestingTrainTest()){
//			//System.out.println(e);
//			
//		//}
		

	}

}


