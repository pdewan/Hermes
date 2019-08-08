package config;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.runtime.Platform;

import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.metrics.CommandClassificationSchemeName;
import difficultyPrediction.predictionManagement.ClassifierSpecification;
import difficultyPrediction.predictionManagement.OversampleSpecification;

public class AHelperConfigurationManager implements HelperConfigurationManager {
//	static Boolean DEFAULT_SHOW_REPLAYER = false;
//	static Boolean DEFAULT_SHOW_PREDICTION_CONTROLLER = false;
//	public static final String DEFAULT_ARFF_FILE_LOCATION = "data/userStudy2010.arff";
//	public static final ClassifierSpecification DEFAULT_CLASSIFIER_SPECIFICATION = ClassifierSpecification.J48;
//	public static final OversampleSpecification DEFAULT_OVERSAMPLE_SPECIFICATION = OversampleSpecification.SMOTE;
//	public static final CommandClassificationSchemeName DEFAULT_RATIO_SCHEME = CommandClassificationSchemeName.A1;
//
//
//    public static final String CONFIG_DIR = "config";
//    public static final String CONFIG_FILE = "config.properties";
//    public static final String STATIC_CONFIGURATION_FILE_NAME = "helper-config/helper-config.properties";
//    public static final String RECORDER_JAVA = "recorder.javalocation";
//    public static final String PLAYER_JAVA = "player.javalocation";
//    public static final String ARFF_FILE= "predictor.arffLocation";
//    public static final String CLASSIFIER= "predictor.classifier";
//    public static final String SHOW_REPLAYER = "showReplayer";
//    public static final String SHOW_PREDICTION_CONTROLLER = "showPredictionController";


//    public static final String OVERSAMPLE= "predictor.oversample";
//    public static final String COMMAND_CLASIFICATION_SCHEME= "predictor.commandClassification";


    protected static PropertiesConfiguration staticConfiguration;
    static File userPropsFile;
//    protected ClassifierSpecification classifierSpecification;
//	protected OversampleSpecification oversampleSpecification;

    PropertiesConfiguration dynamicConfiguration;

    public AHelperConfigurationManager() {
		init();

    }
    @Override
    public String getRecorderJavaPath() {
    	return getStringProperty(RECORDER_JAVA, DEFAULT_JAVA_PATH );
//       return staticConfiguration == null?null:staticConfiguration.getString(RECORDER_JAVA, "java");

    }
    @Override
    public String getPlayerJavaPath() {
    	return getStringProperty(PLAYER_JAVA, DEFAULT_JAVA_PATH );

//       return staticConfiguration == null?null:staticConfiguration.getString(PLAYER_JAVA, DEFAULT_JAVA_PATH );

    }
    @Override
	public String getARFFFileName() {
    	return getStringProperty(ARFF_FILE, DEFAULT_ARFF_FILE_LOCATION );

//		return staticConfiguration == null?DEFAULT_ARFF_FILE_LOCATION:staticConfiguration.getString(ARFF_FILE, DEFAULT_ARFF_FILE_LOCATION);
	}
    @Override
	public ClassifierSpecification getClassifierSpecification() {
    	String aString = getStringProperty(CLASSIFIER, null);
    	if (aString == null) {
    		return DEFAULT_CLASSIFIER_SPECIFICATION;
    	}
    	try {    		
			return					
					ClassifierSpecification.valueOf(
							aString);
		} catch (Exception e) {
			return DEFAULT_CLASSIFIER_SPECIFICATION; // in case valueOf fails
		}
//    	return getStaticClassifierSpecification();
	}
//    protected ClassifierSpecification getStaticClassifierSpecification() {
//		try {
//		
//			return staticConfiguration == null?
//					DEFAULT_CLASSIFIER_SPECIFICATION:
//					ClassifierSpecification.valueOf(
//						staticConfiguration.getString(CLASSIFIER, DEFAULT_CLASSIFIER_SPECIFICATION.toString()));
//		} catch (Exception e) {
//			return DEFAULT_CLASSIFIER_SPECIFICATION; // in case valueOf fails
//		}
//	}
    
    @Override
   	public OversampleSpecification getOversampleSpecification() {
    	String aString = getStringProperty(OVERSAMPLE, null);
    	if (aString == null) {
    		return DEFAULT_OVERSAMPLE_SPECIFICATION;
    	}
    	try {    		
			return					
					OversampleSpecification.valueOf(
							aString);
		} catch (Exception e) {
			return DEFAULT_OVERSAMPLE_SPECIFICATION; // in case valueOf fails
		}
//    	return getStaticOversampleSpecification();
   	}
//    protected OversampleSpecification getStaticOversampleSpecification() {
//   		try {
//   			return staticConfiguration == null?
//   					DEFAULT_OVERSAMPLE_SPECIFICATION:
//   					OversampleSpecification.valueOf(
//   						staticConfiguration.getString(OVERSAMPLE, DEFAULT_OVERSAMPLE_SPECIFICATION.toString()));
//   		} catch (Exception e) {
//   			return DEFAULT_OVERSAMPLE_SPECIFICATION; // in case valueOf fails
//   		}
//   	}
    @Override
    public CommandClassificationSchemeName getCommandClassificationScheme() {
    	String aString = getStringProperty(COMMAND_CLASIFICATION_SCHEME, null);
    	if (aString == null) {
    		return DEFAULT_RATIO_SCHEME;
    	}
    	try {    		
			return					
					CommandClassificationSchemeName.valueOf(
							aString);
		} catch (Exception e) {
			return DEFAULT_RATIO_SCHEME; // in case valueOf fails
		}
//    	return getStaticCommandClassificationScheme();
    }
//    protected CommandClassificationSchemeName getStaticCommandClassificationScheme() {
//   		try {
//   			return staticConfiguration == null?
//   					DEFAULT_RATIO_SCHEME:
//   					CommandClassificationSchemeName.valueOf(
//   						staticConfiguration.getString(COMMAND_CLASIFICATION_SCHEME, DEFAULT_RATIO_SCHEME.toString()));
//   		} catch (Exception e) {
//   			return DEFAULT_RATIO_SCHEME; // in case valueOf fails
//   		}
//   	}

    
	
	
	
	

    public PropertiesConfiguration getDynamicConfiguration() {
        return dynamicConfiguration;
    }

    public void setDynamicConfiguration(
            PropertiesConfiguration dynamicConfiguration) {
        this.dynamicConfiguration = dynamicConfiguration;
    }

    public PropertiesConfiguration getStaticConfiguration() {
        return staticConfiguration;
    }

    public void setStaticConfiguration(PropertiesConfiguration staticConfiguration) {
        this.staticConfiguration = staticConfiguration;
    }
    

    public void init() {
        try {


        	
            PropertiesConfiguration configuration = createStaticConfiguration();
            

            setStaticConfiguration(configuration);

        } catch (Exception e) {
//            StaticConfigurationFileNotRead.newCase(STATIC_CONFIGURATION_FILE_NAME, this);
            System.err.println("Error loading config file.");
            System.err.println(e.getMessage());
            e.printStackTrace();


        }
    }
    public static void getPluginath() {
//    	 Platform.asLocalURL();

    }
    PropertiesConfiguration createStaticConfiguration() {
    	String aLocalDirectory = System.getProperty("user.dir");
    	try {
    		File file= new File (System.getProperty("user.home") + "/" + STATIC_CONFIGURATION_FILE_NAME);
    		if (!file.exists()) {
//    			System.out.println("Configuration file not found at:" + file.getAbsolutePath());
			    file= new File (STATIC_CONFIGURATION_FILE_NAME);
    		}
    		if (!file.exists()) {
//    			System.err.println("Configuration file not found at:" + file.getAbsolutePath());
//    			return null;
    			return new PropertiesConfiguration(); // so we do not have to check it for null
    		} else {
    			// will put some kind of trace later
//    			System.out.println("Configuration file found at:" + file.getAbsolutePath());

    		}

    		
			return new PropertiesConfiguration(file.getAbsolutePath());
		} catch (ConfigurationException e) {
//			System.err.println("Could not getproperties configuration");
			return null;
		}
    }
	@Override
	public Boolean isVisualizePredictions() {
		return getBooleanProperty(VISUALIZE_PREDICTIONS, DEFAULT_VISUALIZE_PREDICTIONS);
//		return getStaticConfiguration().getBoolean(SHOW_PREDICTION_CONTROLLER, null);
	}
	@Override
	public Boolean isShowReplayer() {
		return getBooleanProperty(SHOW_REPLAYER, DEFAULT_SHOW_REPLAYER);

//		return getStaticConfiguration().getBoolean(SHOW_REPLAYER, null);
	}
	protected Map<String, Boolean> booleanProperties = new HashMap<>();
	protected Map<String, Integer> integerProperties = new HashMap<>();
	protected Map<String, String> stringProperties = new HashMap<>();
	protected Map<String, List> listProperties = new HashMap<>();
	@Override
	public Boolean getBooleanProperty(String aPropertyName, Boolean aDefaultValue) {
		Boolean retVal = booleanProperties.get(aPropertyName);
		if (retVal == null) {
			retVal =  staticConfiguration.getBoolean(aPropertyName, aDefaultValue);
		}
		return retVal;
	}
	@Override
	public String getStringProperty(String aPropertyName, String aDefaultValue) {
		String retVal = stringProperties.get(aPropertyName);
		if (retVal == null) {
			retVal =  staticConfiguration.getString(aPropertyName, aDefaultValue);
		}
		return retVal;
	}
	@Override
	public List getListProperty(String aPropertyName, List aDefaultValue) {
		List retVal = listProperties.get(aPropertyName);
		if (retVal == null) {
			retVal =  staticConfiguration.getList(aPropertyName, aDefaultValue);
		}
		return retVal;
	}
	@Override
	public Integer getIntegerProperty(String aPropertyName, Integer aDefaultValue) {
		Integer retVal = integerProperties.get(aPropertyName);
		if (retVal == null) {
			retVal =  staticConfiguration.getInteger(aPropertyName, aDefaultValue);
		}
		return retVal;
	}
	@Override
	public Boolean isTraceInfo() {
		return getBooleanProperty(TRACE_INFO, DEFAULT_TRACE_INFO);
	}
	@Override
	public Boolean isLogWorkspace() {
		return getBooleanProperty(LOG_WORKSPACE, DEFAULT_LOG_WORKSPACE);
	}
	@Override
	public Boolean isLogProject() {
		return getBooleanProperty(LOG_PRJECT, DEFAULT_LOG_PROJECT);
	}
	@Override
	public Boolean isLogRatio() {
		return getBooleanProperty(SAVE_EACH_RATIO, DEFAULT_SAVE_EACH_RATIO);
	}
	@Override
	public Integer getSegmentLength() {
		return getIntegerProperty(SEGMENT_LENGTH, DEFAULT_SEGMENT_LENGTH);
	}
	@Override
	public void setSegmentLength(int newVal) {
		integerProperties.put(SEGMENT_LENGTH, newVal);
	}
	@Override
	public Integer getStartupLag() {
		return getIntegerProperty(STARTUP_LAG, DEFAULT_STARUP_LAG);
	}
	@Override
	public void setStartupLag(int newVal) {
		integerProperties.put(STARTUP_LAG, newVal);
	}
	@Override
	public Integer getStatusAggregated() {
		return getIntegerProperty(STATUS_AGGREGATED, DEFAULT_STATUS_AGGREGATED);
	}
	@Override
	public void setStatusAggregated(int newVal) {
		integerProperties.put(STATUS_AGGREGATED, newVal);
	}
	@Override
	public boolean isSaveEachRatio() {
		return getBooleanProperty(SAVE_EACH_RATIO, DEFAULT_SAVE_EACH_RATIO);
	}
	@Override
	public boolean isShowAllStatuses() {
		return getBooleanProperty(SHOW_ALL_STATUSES, DEFAULT_SHOW_ALL_STATUSES);

	}
	@Override
	public boolean isShowStatusTransitions() {
		return getBooleanProperty(SHOW_STATUS_TRANSITIONS, DEFAULT_SHOW_STATUS_TRANSITIONS);

	}
	@Override
	public int getIdleTime() {
		return getIntegerProperty(IDLE_TIME, DEFAULT_IDLE_TIME);
	}
	@Override
	public List<String> getTechnicalTerms() {
		return getListProperty(TECHNICAL_TERMS, DEFAULT_TECHNICAL_TERMS);
	}
	@Override
	public List<String> getNonTechnicalTerms() {
		return getListProperty(NON_TECHNICAL_TERMS, DEFAULT_NON_TECHNICAL_TERMS);
	}
	@Override
	public void setARFFFileName(String newVal) {
		stringProperties.put(ARFF_FILE, newVal);
	}
	@Override
	public Boolean isLogMetrics() {
		return getBooleanProperty(LOG_METRICS, DEFAULT_LOG_METRICS);
	}
	
	
	
	        
   
}
