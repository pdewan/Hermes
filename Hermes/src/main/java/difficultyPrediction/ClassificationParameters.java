package difficultyPrediction;

import java.beans.PropertyChangeListener;

import difficultyPrediction.predictionManagement.ClassifierSpecification;
import difficultyPrediction.predictionManagement.OversampleSpecification;
import util.models.PropertyListenerRegisterer;

public interface ClassificationParameters 
	extends PropertyListenerRegisterer 
	{
	void setClassifierSpecification(ClassifierSpecification newVal);
	OversampleSpecification getOversampleSpecification();
	ClassifierSpecification getClassifierSpecification();
//	CommandClassificationSchemeName getCommandClassificationScheme();
//	FileSetterModel getARFFFileName();
	String getARFFFileName();
	void setARFFFileName(String newVal);
	void addPropertyChangeListener(PropertyChangeListener aListener);


}
