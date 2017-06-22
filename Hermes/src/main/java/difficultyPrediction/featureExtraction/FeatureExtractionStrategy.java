package difficultyPrediction.featureExtraction;

import java.util.List;

import fluorite.commands.EHICommand;

public interface FeatureExtractionStrategy {
	public void performFeatureExtraction(List<EHICommand> actions, RatioBasedFeatureExtractor featureExtractor);
}
