package difficultyPrediction;

import java.beans.PropertyChangeListener;

import util.models.PropertyListenerRegisterer;
import analyzer.Resettable;
//import bus.uigen.hermes.HermesPropertyListenerRegistererProxy;
import difficultyPrediction.metrics.CommandClassificationSchemeName;

public interface PredictionParameters extends Resettable,
PropertyListenerRegisterer
{
	public int getSegmentLength() ;
	public void setSegmentLength(int newVal);
	public int getStartupLag() ;
	public void setStartupLag(int startupLag) ;
	public int getStatusAggregated() ;
	public void setStatusAggregated(int statusesAggregated);
//	void setClassifierSpecification(ClassifierSpecification newVal);
//	OversampleSpecification getOversampleSpecification();
//	ClassifierSpecification getClassifierSpecification();
	CommandClassificationSchemeName getCommandClassificationScheme();
	void setCommandClassificationScheme(CommandClassificationSchemeName ratioScheme);

//	FileSetterModel getARFFFileName();
//	String getARFFFileName();
	ClassificationParameters getClassificationParameters();
//	void setClassificationParameters(ClassificationParameters newVal);
	void commandMapping();
	void addPropertyChangeListener(PropertyChangeListener aListener);


}
