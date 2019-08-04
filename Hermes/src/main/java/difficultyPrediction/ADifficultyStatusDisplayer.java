package difficultyPrediction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.PlatformUI;

import config.HelperConfigurationManagerFactory;
import fluorite.model.EHEventRecorder;
import fluorite.model.StatusConsts;
import fluorite.viewpart.HelpViewPart;

public class ADifficultyStatusDisplayer implements DifficultyStatusDisplayer {
	protected ToolTip ballonTip;
	String lastStatus = "";
	@Override
	public ToolTip getBalloonTip() {
		return ballonTip;
	}
	
	boolean showStatus(String aStatus) {
		// a single boolean expressionw will probably be harder to understand
		if (HelperConfigurationManagerFactory.getSingleton().isShowAllStatuses()) {
			return true;
		}
		if (!HelperConfigurationManagerFactory.getSingleton().isShowStatusTransitions()) {
			return false;
		}
		if (aStatus.equals(lastStatus)) {
			return false;
		}
		if (lastStatus.isEmpty() && StatusConsts.MAKING_PROGRESS_STATUS.equals(aStatus))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see difficultyPrediction.DifficultyStatusDisplayer#changeStatusInHelpView(java.lang.String)
	 */
	@Override
	public void changeStatusInHelpView(String status) {
		
//		if (!HelperConfigurationManagerFactory.getSingleton().isShowAllStatuses()) {
//			return;
//		}
		if (!showStatus(status)) {
			lastStatus = status;
			return;
		}
//		lastStatus = HelpViewPart.getStatusInformation();
		lastStatus = status;
//		 if (status.equals(lastStatus)) 
//			 return;
//		System.out.println("Changing status sync");
		showStatusInBallonTip(status);
		HelpViewPart.displayStatusInformation(status);
		lastStatus = status;

	}

	/* (non-Javadoc)
	 * @see difficultyPrediction.DifficultyStatusDisplayer#showStatusInBallonTip(java.lang.String)
	 */
	@Override
	public void showStatusInBallonTip(String status) {
//		if (!HelperConfigurationManagerFactory.getSingleton().isShowAllStatuses()) {
//			return;
//		}
		if (ballonTip == null) {
			ballonTip = new ToolTip(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), SWT.BALLOON
					| SWT.ICON_INFORMATION);

		}

		if (!ballonTip.isDisposed()) {
			// ballonTip.setMessage("Status: " + status);
			ballonTip.setMessage(status);
			ballonTip.setText("Status Change Notification");
			EHEventRecorder.getTrayItem().setToolTip(ballonTip);
			ballonTip.setVisible(true);
		}

	}

	/* (non-Javadoc)
	 * @see difficultyPrediction.DifficultyStatusDisplayer#asyncShowStatusInBallonTip(java.lang.String)
	 */
	@Override
	public void asyncShowStatusInBallonTip(String status) {
		final String currentStatus = status;
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				// changeStatusInHelpView(predictionCommand);
				showStatusInBallonTip(currentStatus);
			}
		});

	}

}
