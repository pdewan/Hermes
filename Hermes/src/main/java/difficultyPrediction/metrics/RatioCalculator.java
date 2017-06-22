package difficultyPrediction.metrics;

import java.util.ArrayList;
import java.util.List;

import difficultyPrediction.featureExtraction.RatioFeatures;
import fluorite.commands.EHICommand;

public interface RatioCalculator {

	public abstract boolean isDebugEvent(EHICommand event);

	public abstract boolean isInsertOrEditEvent(EHICommand event);

	public abstract boolean isNavigationEvent(EHICommand event);

	public abstract boolean isFocusEvent(EHICommand event);

	public abstract boolean isAddRemoveEvent(EHICommand event);

	public abstract ArrayList<Double> computeMetrics(List<EHICommand> userActions);

	public abstract ArrayList<Integer> getPercentageData(
			List<EHICommand> userActions);

	public abstract String getFeatureName(EHICommand myEvent);

	RatioFeatures computeRatioFeatures(List<EHICommand> userActions);

	RatioFeatures computeFeatures(List<EHICommand> userActions);

}