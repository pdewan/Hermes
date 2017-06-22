package difficultyPrediction;

import org.eclipse.swt.widgets.ToolTip;

public interface DifficultyStatusDisplayer {

	public abstract void changeStatusInHelpView(String status);

	public abstract void showStatusInBallonTip(String status);

	public abstract void asyncShowStatusInBallonTip(String status);

	ToolTip getBalloonTip();

}