package analyzer.tracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import difficultyPrediction.APredictionParameters;
import difficultyPrediction.metrics.ATestRatioCalculator;
import difficultyPrediction.metrics.CommandClassificationSchemeName;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVSaver;
import weka.core.converters.Saver;
import analyzer.extension.ArffFileGeneratorFactory;

public class AllArffAnalysis {
	//assuming the output csv and input all.arff is in same dir
	public static void generateAllArff(CommandClassificationSchemeName scheme) {
		String outputSubdir=scheme.getSubDir();
//		ATestRatioCalculator.CURRENT_SCHEME=scheme;
		APredictionParameters.getInstance().setCommandClassificationScheme(scheme);
		
		File fi=new File(outputSubdir);
		if(!fi.exists())
			fi.mkdirs();
		
		ArffFileGeneratorFactory f=new ArffFileGeneratorFactory(outputSubdir);
		f.insertCommand("All");
		f.createArffs();
		
	}
	
	public static void createCSV(String dir) {
		File f=new File(dir);
		if(!f.exists())
			f.mkdirs();
		
		try(BufferedReader r=Files.newBufferedReader(Paths.get(dir+"all.arff"))) {
			Instances all=new Instances(r);
			all.setClassIndex(all.numAttributes()-1);
			
			CSVSaver neg=new CSVSaver();
			neg.setFile(new File(dir+"notstuck.csv"));
			neg.setStructure(all);
			neg.setRetrieval(Saver.INCREMENTAL);
			
			CSVSaver pos=new CSVSaver();
			pos.setFile(new File(dir+"stuck.csv"));
			pos.setStructure(all);
			pos.setRetrieval(Saver.INCREMENTAL);
			
			for(int i=0;i<all.numInstances();i++) {
				Instance inst=all.instance(i);
		
				
				if(inst.classAttribute().value((int) inst.classValue()).equalsIgnoreCase("YES")) {
					pos.writeIncremental(inst);
					
				} else
					neg.writeIncremental(inst);
			
			}
			
			//force flush and close
			pos.writeIncremental(null);
			neg.writeIncremental(null);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
		//generate all.arff
		
		
		generateAllArff(CommandClassificationSchemeName.A1);
		
		//createCSV("data/OutputData/"+outputSubDir);
		System.exit(0);
	}
	
}
