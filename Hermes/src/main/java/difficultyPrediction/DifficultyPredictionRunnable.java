package difficultyPrediction;

import org.eclipse.swt.widgets.ToolTip;

import fluorite.commands.EHICommand;

public interface DifficultyPredictionRunnable extends Runnable {
	final String DIFFICULTY_PREDICTION_THREAD_NAME = "Difficulty Prediction Thread";
	final int DIFFICULTY_PREDICTION_THREAD_PRIORITY = Thread.NORM_PRIORITY - 1;
//	public BlockingQueue<ICommand> getPendingCommands() ;
	public Mediator getMediator() ;
	public ToolTip getBallonTip();
	public void add(EHICommand newCommand);
	void showStatusInBallonTip(String status);
	void asyncShowStatusInBallonTip(String status);
	void changeStatusInHelpView(String status);
}
