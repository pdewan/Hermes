package config;

import org.apache.commons.configuration.PropertiesConfiguration;

import difficultyPrediction.metrics.CommandClassificationSchemeName;
import difficultyPrediction.predictionManagement.ClassifierSpecification;
import difficultyPrediction.predictionManagement.OversampleSpecification;

public interface HelperConfigurationManager {
	public  PropertiesConfiguration getStaticConfiguration() ;
	public  void setStaticConfiguration(
			PropertiesConfiguration staticConfiguration) ;
	public  PropertiesConfiguration getDynamicConfiguration() ;
	public  void setDynamicConfiguration(
			PropertiesConfiguration dynamicConfiguration) ;
	void init();
	String getRecorderJavaPath();
	String getPlayerJavaPath();
	String getARFFFileName();
	ClassifierSpecification getClassifierSpecification();
	OversampleSpecification getOversampleSpecification();
//	void setClassifierSpecification(
//			ClassifierSpecification classifierSpecification);
//	void setOversampleSpecification(
//			OversampleSpecification oversampleSpecification);
	CommandClassificationSchemeName getCommandClassificationScheme();

}
