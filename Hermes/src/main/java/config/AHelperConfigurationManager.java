package config;


import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.exception.NestableException;

import difficultyPrediction.metrics.CommandClassificationSchemeName;
import difficultyPrediction.predictionManagement.ClassifierSpecification;
import difficultyPrediction.predictionManagement.OversampleSpecification;

public class AHelperConfigurationManager implements HelperConfigurationManager {
	public static final String DEFAULT_ARFF_FILE_LOCATION = "data/userStudy2010.arff";
	public static final ClassifierSpecification DEFAULT_CLASSIFIER_SPECIFICATION = ClassifierSpecification.J48;
	public static final OversampleSpecification DEFAULT_OVERSAMPLE_SPECIFICATION = OversampleSpecification.SMOTE;
	public static final CommandClassificationSchemeName DEFAULT_RATIO_SCHEME = CommandClassificationSchemeName.A1;


    public static final String CONFIG_DIR = "config";
    public static final String CONFIG_FILE = "config.properties";
    public static final String STATIC_CONFIGURATION_FILE_NAME = "helper-config/helper-config.properties";
    public static final String RECORDER_JAVA = "recorder.javalocation";
    public static final String PLAYER_JAVA = "player.javalocation";
    public static final String ARFF_FILE= "predictor.arffLocation";
    public static final String CLASSIFIER= "predictor.classifier";
//    public static final String CLASSIFIER= "classifier";

    public static final String OVERSAMPLE= "predictor.oversample";
    public static final String COMMAND_CLASIFICATION_SCHEME= "predictor.commandClassification";


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
    	
       return staticConfiguration == null?null:staticConfiguration.getString(RECORDER_JAVA, "java");

    }
    @Override
    public String getPlayerJavaPath() {
    	
       return staticConfiguration == null?null:staticConfiguration.getString(PLAYER_JAVA, "java");

    }
    @Override
	public String getARFFFileName() {
		return staticConfiguration == null?DEFAULT_ARFF_FILE_LOCATION:staticConfiguration.getString(ARFF_FILE, DEFAULT_ARFF_FILE_LOCATION);
	}
    @Override
	public ClassifierSpecification getClassifierSpecification() {
//		if (classifierSpecification == null) {
//			classifierSpecification = getStaticClassifierSpecification();
//		}
//		return classifierSpecification;
    	return getStaticClassifierSpecification();
	}
    protected ClassifierSpecification getStaticClassifierSpecification() {
		try {
		
			return staticConfiguration == null?
					DEFAULT_CLASSIFIER_SPECIFICATION:
					ClassifierSpecification.valueOf(
						staticConfiguration.getString(CLASSIFIER, DEFAULT_CLASSIFIER_SPECIFICATION.toString()));
		} catch (Exception e) {
			return DEFAULT_CLASSIFIER_SPECIFICATION; // in case valueOf fails
		}
	}
    @Override
   	public OversampleSpecification getOversampleSpecification() {
//   		if (oversampleSpecification == null) {
//   			oversampleSpecification = getStaticOversampleSpecification();
//   		}
//   		return oversampleSpecification;
    	return getStaticOversampleSpecification();
   	}
    @Override
    public CommandClassificationSchemeName getCommandClassificationScheme() {
    	return getStaticCommandClassificationScheme();
    }
//    @Override
//    public void setClassifierSpecification(
//			ClassifierSpecification classifierSpecification) {
//		this.classifierSpecification = classifierSpecification;
//	}
//    @Override
//	public void setOversampleSpecification(
//			OversampleSpecification oversampleSpecification) {
//		this.oversampleSpecification = oversampleSpecification;
//	}
    
	protected OversampleSpecification getStaticOversampleSpecification() {
   		try {
   			return staticConfiguration == null?
   					DEFAULT_OVERSAMPLE_SPECIFICATION:
   					OversampleSpecification.valueOf(
   						staticConfiguration.getString(OVERSAMPLE, DEFAULT_OVERSAMPLE_SPECIFICATION.toString()));
   		} catch (Exception e) {
   			return DEFAULT_OVERSAMPLE_SPECIFICATION; // in case valueOf fails
   		}
   	}
	
	protected CommandClassificationSchemeName getStaticCommandClassificationScheme() {
   		try {
   			return staticConfiguration == null?
   					DEFAULT_RATIO_SCHEME:
   					CommandClassificationSchemeName.valueOf(
   						staticConfiguration.getString(COMMAND_CLASIFICATION_SCHEME, DEFAULT_RATIO_SCHEME.toString()));
   		} catch (Exception e) {
   			return DEFAULT_RATIO_SCHEME; // in case valueOf fails
   		}
   	}
	

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
//			 PropertiesConfiguration configuration = new PropertiesConfiguration("./config/config.properties");
//			 PropertiesConfiguration configuration = new PropertiesConfiguration(STATIC_CONFIGURATION_FILE_NAME);
//        	System.out.println ("Working directory ="+ System.getProperty("user.dir"));
//        	System.out.println ("Home directory ="+ System.getProperty("user.home"));

        	
            PropertiesConfiguration configuration = createStaticConfiguration();

//            StaticConfigurationFileRead.newCase(STATIC_CONFIGURATION_FILE_NAME, this);
            setStaticConfiguration(configuration);
//            if (configuration == null)
//            	return;
//            String dynamicConfigurationName = configuration.getString("helper.dynamicConfiguration", "dynamicconfig.properties");
//            
//            File file = new File(dynamicConfigurationName);
//            if (!file.exists()) {
//                file.createNewFile();
////                DynamicConfigurationFileCreated.newCase(dynamicConfigurationName, this);
////	         	convertToDynamicConfiguration();
//            }
//            dynamicConfiguration = new PropertiesConfiguration(dynamicConfigurationName);
//            DynamicConfigurationFileRead.newCase(dynamicConfigurationName, this);

//	         GraderSettings.get().convertToDynamicConfiguration();
        } catch (Exception e) {
//            StaticConfigurationFileNotRead.newCase(STATIC_CONFIGURATION_FILE_NAME, this);
            System.err.println("Error loading config file.");
            System.err.println(e.getMessage());
            e.printStackTrace();

//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
        }
    }

    PropertiesConfiguration createStaticConfiguration() {
    	try {
    		File file= new File (System.getProperty("user.home") + "/" + STATIC_CONFIGURATION_FILE_NAME);
    		if (!file.exists()) {
    			System.err.println("Configuration file not found at:" + file.getAbsolutePath());
			    file= new File (STATIC_CONFIGURATION_FILE_NAME);
    		}
    		if (!file.exists()) {
    			System.err.println("Configuration file not found at:" + file.getAbsolutePath());
    			return null;
    		} else {
    			System.out.println("Configuration file found at:" + file.getAbsolutePath());

    		}
//    		File file = new File (STATIC_CONFIGURATION_FILE_NAME);
//    		if (!file.exists())
//    			file= new File (System.getProperty("user.home") + "/" + STATIC_CONFIGURATION_FILE_NAME);
//    		if (!file.exists()) {
//    			System.err.println("Configuration file not found at:" + file.getAbsolutePath());
//    			return null;
//    		}
    		
			return new PropertiesConfiguration(file.getAbsolutePath());
		} catch (ConfigurationException e) {
			System.err.println("Could not getproperties configuration");
			return null;
		}
    }
	
	        
   
}
