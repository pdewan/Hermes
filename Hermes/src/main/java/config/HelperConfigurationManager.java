package config;

import org.apache.commons.configuration.PropertiesConfiguration;

import difficultyPrediction.metrics.CommandClassificationSchemeName;
import difficultyPrediction.predictionManagement.ClassifierSpecification;
import difficultyPrediction.predictionManagement.OversampleSpecification;

public interface HelperConfigurationManager {
	 public static final String RECORDER_JAVA = "recorder.javalocation";
	    public static final String PLAYER_JAVA = "player.javalocation";
	    public static final String ARFF_FILE= "predictor.arffLocation";
	    public static final String CLASSIFIER= "predictor.classifier";
	    public static final String SHOW_REPLAYER = "showReplayer";
	    public static final String VISUALIZE_PREDICTIONS = "visualizePredictions";
	    public static final String TRACE_INFO = "traceInfo";
	    public static final String LOG_WORKSPACE = "logWorksace";
	    public static final String LOG_PRJECT = "logProject";
	    


	    public static final String CONFIG_DIR = "config";
	    public static final String CONFIG_FILE = "config.properties";
	    public static final String STATIC_CONFIGURATION_FILE_NAME = "helper-config/helper-config.properties";
	    public static final String OVERSAMPLE= "predictor.oversample";
	    public static final String COMMAND_CLASIFICATION_SCHEME= "predictor.commandClassification";
	    public static final String SEGMENT_LENGTH= "predictor.segmentLength";
	    public static final String STARTUP_LAG= "predictor.startupLag";
	    public static final String STATUS_AGGREGATED= "predictor.statusAggregated";


	    public static final String SAVE_EACH_RATIO = "saveEachRatio";
	    public static final String SHOW_STATUS = "showStatus";
	    public static final String IDLE_TIME = "idleTime";

	    

	    
	    static Boolean DEFAULT_SHOW_REPLAYER = false;
		static Boolean DEFAULT_VISUALIZE_PREDICTIONS = false;
		public static final String DEFAULT_ARFF_FILE_LOCATION = "data/userStudy2010.arff";
		public static final ClassifierSpecification DEFAULT_CLASSIFIER_SPECIFICATION = ClassifierSpecification.J48;
		public static final OversampleSpecification DEFAULT_OVERSAMPLE_SPECIFICATION = OversampleSpecification.SMOTE;
		public static final CommandClassificationSchemeName DEFAULT_RATIO_SCHEME = CommandClassificationSchemeName.A1;
		public static final String DEFAULT_JAVA_PATH = "java";
		 public static final boolean DEFAULT_TRACE_INFO = false;

		 public static final boolean DEFAULT_LOG_WORKSPACE = true;
		 public static final boolean DEFAULT_LOG_PROJECT = true;
		 public static final boolean DEFAULT_SAVE_EACH_RATIO = true;
		 public static final int DEFAULT_SEGMENT_LENGTH = 25;
		 public static final int DEFAULT_STARUP_LAG = 50;
		 public static final int DEFAULT_STATUS_AGGREGATED = 5;
		 public static final boolean DEFAULT_SHOW_STATUS = false;
		 public static final int DEFAULT_IDLE_TIME = 10000; //ms



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

	CommandClassificationSchemeName getCommandClassificationScheme();
     Boolean isVisualizePredictions() ;
     
     Boolean getBooleanProperty(String aPropertyName, Boolean aDefaultValue);
     String getStringProperty(String aPropertyName, String aDefaultValue);
     Integer getIntegerProperty(String aPropertyName, Integer aDefaultValue);

	
	 Boolean isShowReplayer() ;
	 Boolean isTraceInfo();
	 Boolean isLogWorkspace();
	 Boolean isLogProject();
	 Boolean isLogRatio();
	Integer getSegmentLength();
	
	Integer getStartupLag();
	void setStartupLag(int newVal);
	Integer getStatusAggregated();
	void setStatusAggregated(int newVal);
	void setSegmentLength(int newVal);
	boolean isSaveEachRatio();
	boolean isShowStatus();
	int getIdleTime();

	  

}
