package difficultyPrediction.predictionManagement;

import java.util.HashMap;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;

public enum ClassifierSpecification {
	J48,
	ADABOOST,
	BAGGING;
	static Map<ClassifierSpecification, Classifier> specificationToClassifier = new HashMap();
	static {
		specificationToClassifier.put(J48, new J48());
		AdaBoostM1 anAdaBoost = new AdaBoostM1();
		((AdaBoostM1) anAdaBoost).setClassifier(new DecisionStump());
		specificationToClassifier.put(ADABOOST, anAdaBoost);
		Bagging aBagging = new Bagging();
		((Bagging) aBagging).setClassifier(new DecisionStump());
		specificationToClassifier.put(BAGGING, aBagging);		
	}
	public Classifier toClassifier() {
		return specificationToClassifier.get(this);
	}
	public static void associate (ClassifierSpecification aSpecification, Classifier aClassifier) {
		specificationToClassifier.put(aSpecification, aClassifier);
	}
	
}
