package difficultyPrediction.predictionManagement;

import java.util.HashMap;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;

public enum OversampleSpecification {
	SMOTE(new double[] {500,1000,2000,3000}, new SMOTE()),
	RESAMPLE(new double[] {0.25,0.5,0.75,1.0}, new Resample());

	private double[] levels;
	
	 Filter wekaFilter = new SMOTE();
	 static Map<OversampleSpecification, Filter> specificationToFilter = new HashMap();
		static {
			specificationToFilter.put(SMOTE, new SMOTE());
			Filter aResample = new Resample();
			specificationToFilter.put(RESAMPLE, aResample);
			
		}

	private OversampleSpecification(double[]  levels, Filter aSmoteFilter) {
		this.levels=levels;

	}
	// not called
	public double[] getFilterLevels() {
		return this.levels;

	}
	public Filter toWekaFilter() {
		return wekaFilter;
	}
	public Filter toWekaFilter(double aPercentage) {
		switch (this) {
		case RESAMPLE: 
			Resample aResample = new Resample();
			aResample.setBiasToUniformClass(aPercentage==Double.MIN_VALUE? 0.0:aPercentage);
			return aResample;
		
		case SMOTE:
			SMOTE aSmote = new SMOTE();
			aSmote.setPercentage(aPercentage);
			return aSmote;
		default:			
			return wekaFilter;
		}
	}
}
