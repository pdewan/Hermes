package difficultyPrediction;

import config.HelperConfigurationManagerFactory;
import difficultyPrediction.predictionManagement.ClassifierSpecification;
import difficultyPrediction.predictionManagement.OversampleSpecification;

public class DifficultyPredictionSettings {
	static String ratiosFileName;
	static boolean ratioFileExists;
	static boolean replayMode;
	static boolean newRatioFiles;
	static boolean replayRatioFiles;
	static ClassifierSpecification classifierSpecification;
//	static OversampleSpecification oversampleSpecification;
//	static String arffFileName;

	
	//boolean that turns on/off ratio files creation
		static boolean createRatioFiles;

	static int segmentLength = 50;

	public DifficultyPredictionSettings() {
	}

	public static boolean isReplayMode() {
		return replayMode;
	}

	public static void setReplayMode(boolean replayMode) {
		DifficultyPredictionSettings.replayMode = replayMode;
	}

	public static int getSegmentLength() {
		return segmentLength;
	}

	public static void setSegmentLength(int segmentLength) {
		DifficultyPredictionSettings.segmentLength = segmentLength;
	}

	public static String getRatiosFileName() {
		return ratiosFileName;
	}

	public static void setRatiosFileName(String ratiosFileName) {
		DifficultyPredictionSettings.ratiosFileName = ratiosFileName;
	}

	public static boolean isRatioFileExists() {
		return ratioFileExists;
	}

	public static void setRatioFileExists(boolean ratioFileExists) {
		DifficultyPredictionSettings.ratioFileExists = ratioFileExists;
	}

	public static boolean isNewRatioFiles() {
		return newRatioFiles;
	}

	public static void setNewRatioFiles(boolean newRatioFiles) {
		DifficultyPredictionSettings.newRatioFiles = newRatioFiles;
	}
	public static boolean isReplayRatioFiles() {
		return replayRatioFiles;
	}
	public static void setReplayRatioFiles(boolean replayRatioFiles) {
		DifficultyPredictionSettings.replayRatioFiles = replayRatioFiles;
	}
	public static boolean shouldCreateRatioFiles() {
		return createRatioFiles;

	}

	public static void setCreateRatioFile(boolean b) {
		createRatioFiles=b;

	}
//	public static String getARFFFileName() {
//		if (arffFileName == null) {
//			arffFileName = HelperConfigurationManagerFactory.getSingleton().getARFFFileName();
//		}
//		return arffFileName;
//	}
//    
//	public static ClassifierSpecification getClassifierSpecification() {
//		if (classifierSpecification == null) {
//			classifierSpecification = HelperConfigurationManagerFactory.getSingleton().getClassifierSpecification();
//		}
//		return classifierSpecification;
//	}
//   
//    
//   	public static OversampleSpecification getOversampleSpecification() {
//   		if (oversampleSpecification == null) {
//   			oversampleSpecification = HelperConfigurationManagerFactory.getSingleton().getOversampleSpecification();
//   		}
//   		return oversampleSpecification;
//   	}
//    public static void setClassifierSpecification(
//			ClassifierSpecification newVal) {
//		classifierSpecification = newVal;
//	}
//    
//	public static void setOversampleSpecification(
//			OversampleSpecification newVal) {
//		oversampleSpecification = newVal;
//	}
    
	
	

}
