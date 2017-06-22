package difficultyPrediction;

public class ADisplayStatusUpdatingRunnable implements Runnable{
	String status;
//	DifficultyPredictionRunnable difficultyPredictionRunnable;
//	public ADisplayStatusUpdatingRunnable(DifficultyPredictionRunnable aDifficultyPredictionRunnable, String aStatus) {
//		System.out.println ("ADisplayStatusUpdatingRunnable created");
//		status = aStatus;
//		difficultyPredictionRunnable = aDifficultyPredictionRunnable;
//	}
	DifficultyStatusDisplayer difficultyStatusDisplayer;

	public ADisplayStatusUpdatingRunnable(DifficultyStatusDisplayer aDifficultyStatusDisplayer, String aStatus) {
	System.out.println ("ADisplayStatusUpdatingRunnable created");
	status = aStatus;
	difficultyStatusDisplayer = aDifficultyStatusDisplayer;
}
	@Override
	public void run() {
		difficultyStatusDisplayer.changeStatusInHelpView(status);

	}
	 

}
