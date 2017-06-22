package analyzer;

import java.beans.PropertyChangeListener;
import java.util.List;

import difficultyPrediction.MediatorRegistrar;
import analyzer.ui.graphics.RatioFileComponents;
import fluorite.commands.EHICommand;

public interface RatioFilePlayer extends MediatorRegistrar, PropertyChangeListener {

	public abstract void setReplayedData(
			List<List<EHICommand>> aNestedCommandsList, String aRatioFileName);

	public abstract void replay();

	public abstract void fireRatioFileComponents(
			RatioFileComponents aRatioFileComponents);

}