package difficultyPrediction;

import fluorite.commands.PredictionCommand;

public interface DifficultyPredictionPluginEventProcessor extends PluginEventListener{

	public abstract PredictorThreadOption getPredictorThreadOption();

	public abstract void setPredictorThreadOption(
			PredictorThreadOption predictorThreadOption);

	public abstract Thread getDifficultyPredictionThread();

	public abstract void setDifficultyPredictionThread(
			Thread difficultyPredictionThread);

	public abstract DifficultyPredictionRunnable getDifficultyPredictionRunnable();

	public abstract void setDifficultyPredictionRunnable(
			DifficultyPredictionRunnable difficultyPredictionRunnable);

//	public abstract BlockingQueue<ICommand> getPendingPredictionCommands();
//
//	public abstract void setPendingPredictionCommands(
//			BlockingQueue<ICommand> pendingPredictionCommands);

//	public abstract void recordCommand(ICommand newCommand);

	public abstract void changeStatusInHelpView(
			PredictionCommand predictionCommand);

//	public abstract void start();
//
//	public abstract void stop();

//	public abstract void addPluginEventEventListener(
//			PluginEventListener aListener);
//
//	public abstract void removePluginEventListener(
//			PluginEventListener aListener);
//
//	public abstract void notifyStartCommand();
//
//	public abstract void notifyStopCommand();
//
//	public abstract void notifyRecordCommand(ICommand aCommand);

//	public abstract void notifyNewRatios(RatioFeatures aFeatures);

}