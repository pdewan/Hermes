package analyzer;

import util.annotations.Column;
import util.annotations.ComponentHeight;
import util.annotations.ComponentWidth;
import util.annotations.Explanation;
import util.annotations.Row;
import util.models.DynamicEnum;

import java.beans.PropertyChangeListener;

//import bus.uigen.hermes.HermesDynamicEnumProxy;
import util.models.PropertyListenerRegisterer;
import difficultyPrediction.PredictionParameters;

public interface AnalyzerParameters 
extends PropertyListenerRegisterer 
{

	public abstract PredictionParameters getPredictionParameters();

	public abstract DynamicEnum<String> getParticipants();

	public abstract void setParticipants(DynamicEnum<String> aNewVal);

	//	@Row(1)
	public abstract void loadDirectory();

	public abstract boolean preReplayLogs();

	public abstract boolean preLoadDirectory();

	//	@Row(1)
	public abstract void replayLogs();

	//	@Row(2)
	//	@Column(2)
	public abstract boolean isNewOutputFiles();

	public abstract void setNewOutputFiles(boolean newRatioFiles);

	public abstract boolean preIsReplayOutputFiles();

	//	@Row(2)
	//	@Column(3)
	public abstract boolean isReplayOutputFiles();

	public abstract void setReplayOutputFiles(boolean newVal);

	//	@Column(1)
	public abstract boolean isVisualizePrediction();

	public abstract void setVisualizePrediction(boolean newVal);
	//	public static AnAnalyzerParametersSelector getInstance() {
	//		if (instance == null) {
	//			instance = new AnAnalyzerParametersSelector();
	//		}
	//		return instance;
	//	}

	void visualizePredictions();
	void addPropertyChangeListener(PropertyChangeListener aListener);


//	String getCurrentParticipant();
//
//	void setCurrentParticipant(String currentParticipant);

}