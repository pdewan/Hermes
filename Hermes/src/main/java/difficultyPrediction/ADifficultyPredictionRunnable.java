package difficultyPrediction;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.PlatformUI;

import analyzer.extension.AnAnalyzerProcessor;
import config.PredictorConfigurer;
//import dayton.ServerConnection;
import fluorite.commands.EHDifficulyStatusCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.EHPredictionCommand;
import fluorite.model.StatusConsts;
import util.trace.difficultyPrediction.AddedCommandToPredictionQueue;
import util.trace.difficultyPrediction.CommandIgnoredBecauseQueueFull;
import util.trace.difficultyPrediction.RemovedCommandFromPredictionQueue;

public class ADifficultyPredictionRunnable implements
		DifficultyPredictionRunnable {
	public static final int NUM_PENDING_SEGMENTS = 4;
	public static final int NUM_PENDING_COMMANDS = 40096;
	// NUM_PENDING_SEGMENTS*ADisjointDiscreteChunks.DEFAULT_IGNORE_NUM;
	BlockingQueue<EHICommand> pendingCommands = new LinkedBlockingQueue(
			NUM_PENDING_COMMANDS);
	// BlockingQueue<ICommand> pendingCommands = new LinkedBlockingQueue();

	protected Mediator mediator = null;
	EHICommand newCommand;
//	protected ToolTip ballonTip;
	// protected TrayItem trayItem;
	static DifficultyPredictionRunnable instance;
	DifficultyStatusDisplayer difficultyStatusDisplayer;

	public ADifficultyPredictionRunnable() {
		// mediator = new DifficultyRobot("");
		mediator = DifficultyRobot.getInstance();
		instance = this;
		difficultyStatusDisplayer = new ADifficultyStatusDisplayer();
	}

	// void startExternalComponents() {
	// LineGraphComposer.composeUI();
	// ObjectEditor.edit(APredictionParameters.getInstance());
	// }

	public void run() {
		PredictorConfigurer.configure(); // comment this out if do not want the
											// OE UI
		while (true) {
			try {
				newCommand = pendingCommands.take();
//				System.out.println("Pending commands size"
//						+ pendingCommands.size() + " command:" + newCommand);
				RemovedCommandFromPredictionQueue.newCase(newCommand, pendingCommands.toString(), this);
				// if (pendingCommands.size() > NUM_PENDING_COMMANDS) {
				// System.err.printf("Number of commands > " +
				// NUM_PENDING_COMMANDS + "ending dfficulyty prediction");
				// System.out.println("LARGe NUMBER OF BUFFERED COMMANDS");
				// break;
				//
				// }
				// System.out.println("Taken command:" + newCommand);
				if (newCommand instanceof EHDifficulyStatusCommand
						&& !DifficultyPredictionSettings.isReplayMode()) { // should
																			// handle
																			// this
																			// config
//					ServerConnection.getServerConnection().updateStatus(
//							((DifficulyStatusCommand) newCommand).getStatus()
//									.toString());
				}
				if (newCommand instanceof AnEndOfQueueCommand) {// stop event
					DifficultyRobot.getInstance().notifyStopCommand();
					break;
				} else if (newCommand instanceof AStartOfQueueCommand) {
					DifficultyRobot.getInstance().notifyStartCommand();
					continue;

				}
				// moving this below after update
//				DifficultyRobot.getInstance().notifyNewCommand(newCommand);
				if (!newCommand.getCommandType().equals("PredictionCommand")
						&& !newCommand.getCommandType().equals(
								"DifficultyStatusCommand")
						&& !(newCommand instanceof EHPredictionCommand)
						&& !(newCommand instanceof EHDifficulyStatusCommand)) {
					mediator.processEvent(newCommand);
				} else if (!(newCommand instanceof EHPredictionCommand)) {
					System.out.println("Ignoreing difficulty status Command "
							+ newCommand);
				} else {
					// String lastStatus = null;
					// try {
					// lastStatus = HelpViewPart.getStatusInformation();
					// } catch (Exception e) {
					// System.out.println ("Could not get last status");
					// }
					final String currentStatus = getStatus((EHPredictionCommand) newCommand);
					// if (!currentStatus.equals(lastStatus)) {
					if (DifficultyPredictionSettings.isReplayMode()) {
						DifficultyRobot.getInstance().notifyNewReplayedStatus(AnAnalyzerProcessor.toInt(((EHPredictionCommand) newCommand).getPredictionType()));;
						// try {
						System.out.println("Prediction: "
								+ ((EHPredictionCommand) newCommand)
										.getPredictionType() + " comd " + newCommand + " " + newCommand.getTimestamp());
						// } catch (ClassCastException e) {
						// e.printStackTrace();
						// System.out.println ("this shouldnot have happened");
						//
						// }
					} else {
						// need to display prediction, but this should be done
						// on
						// the UI thread
						// need to so this in listener
						// move this to after the async command
//						if (!DifficultyPredictionSettings.isReplayMode())
//							ServerConnection.getServerConnection()
//									.updateStatus(
//											((PredictionCommand) newCommand)
//													.getName());
						System.out.println("Async Display Status Update:" + newCommand);
						PlatformUI.getWorkbench().getDisplay()
						.asyncExec(new ADisplayStatusUpdatingRunnable(difficultyStatusDisplayer, currentStatus));
						
//						PlatformUI.getWorkbench().getDisplay()
//								.asyncExec(new Runnable() {
//									@Override
//									public void run() {
//										// PredictionCommand predictionCommand =
//										// (PredictionCommand) newCommand;
//										// changeStatusInHelpView(predictionCommand);
//										changeStatusInHelpView(currentStatus);
//									}
//								});
						
						
						
						
						// PlatformUI.getWorkbench().getDisplay()
						// .asyncExec(new Runnable() {
						// @Override
						// public void run() {
						// PredictionCommand predictionCommand =
						// (PredictionCommand) newCommand;
						// changeStatusInHelpView(predictionCommand);
						// }
						// });

					}
//					if (!DifficultyPredictionSettings.isReplayMode())
//						ServerConnection.getServerConnection()
//								.updateStatus(
//										((PredictionCommand) newCommand)
//												.getName());
				}
				// moving this below
				DifficultyRobot.getInstance().notifyNewCommand(newCommand);

				// }

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// public void processCommand(ICommand newCommand) {
	// if (!newCommand.getCommandType().equals("PredictionCommand"))
	// statusPredictor.processEvent(newCommand);
	// else {
	// // need to display prediction, but this should be done on
	// // the UI thread
	// PlatformUI.getWorkbench().getDisplay()
	// .asyncExec(new Runnable() {
	// @Override
	// public void run() {
	// PredictionCommand predictionCommand = (PredictionCommand) newCommand;
	// changeStatusInHelpView(predictionCommand);
	// }
	// });
	// PlatformUI.getWorkbench().getDisplay()
	// .asyncExec(new Runnable() {
	// @Override
	// public void run() {
	// PredictionCommand predictionCommand = (PredictionCommand) newCommand;
	// changeStatusInHelpView(predictionCommand);
	// }
	// });
	//
	// }
	//
	// }
	String lastStatus = "";

	public static String getStatus(EHPredictionCommand predictionCommand) {
		String status = "";
		switch (predictionCommand.getPredictionType()) {
		case MakingProgress:
			status = StatusConsts.MAKING_PROGRESS_STATUS;
			break;
		case HavingDifficulty:
			status = StatusConsts.SLOW_PROGRESS_STATUS;
			break;
		case Indeterminate:
			status = StatusConsts.INDETERMINATE;
			break;
		}
		return status;
	}

	public void changeStatusInHelpView(EHPredictionCommand predictionCommand) {
		// String status = "";
		// switch (predictionCommand.getPredictionType()) {
		// case MakingProgress:
		// status = StatusConsts.MAKING_PROGRESS_STATUS;
		// break;
		// case HavingDifficulty:
		// status = StatusConsts.SLOW_PROGRESS_STATUS;
		// break;
		// case Indeterminate:
		// status = StatusConsts.INDETERMINATE;
		// break;
		// }
		// if (status.equals(lastStatus)) return;
		// showStatusInBallonTip(status);
		// HelpViewPart.displayStatusInformation(status);
		// lastStatus = status;
		changeStatusInHelpView(getStatus(predictionCommand));

	}

	@Override
	public void changeStatusInHelpView(String status) {
		difficultyStatusDisplayer.changeStatusInHelpView(status);
//		lastStatus = HelpViewPart.getStatusInformation();
//		 if (status.equals(lastStatus)) return;
//		System.out.println("Changing status sync");
//		showStatusInBallonTip(status);
//		HelpViewPart.displayStatusInformation(status);
//		lastStatus = status;

	}

	@Override
	public void showStatusInBallonTip(String status) {
		difficultyStatusDisplayer.showStatusInBallonTip(status);
//		if (ballonTip == null) {
//			ballonTip = new ToolTip(PlatformUI.getWorkbench()
//					.getActiveWorkbenchWindow().getShell(), SWT.BALLOON
//					| SWT.ICON_INFORMATION);
//
//		}
//
//		if (!ballonTip.isDisposed()) {
//			// ballonTip.setMessage("Status: " + status);
//			ballonTip.setMessage(status);
//			ballonTip.setText("Status Change Notification");
//			EventRecorder.getTrayItem().setToolTip(ballonTip);
//			ballonTip.setVisible(true);
//		}

	}

	@Override
	public void asyncShowStatusInBallonTip(String status) {
		difficultyStatusDisplayer.asyncShowStatusInBallonTip(status);
//		final String currentStatus = status;
//		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				// changeStatusInHelpView(predictionCommand);
//				showStatusInBallonTip(currentStatus);
//			}
//		});

	}

	// public BlockingQueue<ICommand> getPendingCommands() {
	// return pendingCommands;
	// }
	public Mediator getMediator() {
		return mediator;
	}

	public ToolTip getBallonTip() {
		return difficultyStatusDisplayer.getBalloonTip();
//		return ballonTip;
	}

	boolean full;

	@Override
	public void add(EHICommand newCommand) {
		try {
			if (!(newCommand instanceof AnEndOfQueueCommand)
					&& pendingCommands.size() > NUM_PENDING_COMMANDS - 2) {
				System.err
						.printf("Number of commands > " + NUM_PENDING_COMMANDS
								+ "ending dfficulyty prediction");
				System.out.println("LARGe NUMBER OF BUFFERED COMMANDS");
				return;
				// break;

			}
//			System.out.println ("Adding command to queue:" + newCommand);
			pendingCommands.add(newCommand);
			AddedCommandToPredictionQueue.newCase(newCommand, pendingCommands.toString(), this);
			full = false;
		} catch (IllegalStateException e) {
			if (!full) {
				CommandIgnoredBecauseQueueFull.newCase(this);
				full = true;
			}

		}

	}

	public static DifficultyPredictionRunnable getInstance() {
		if (instance == null)
			new ADifficultyPredictionRunnable();
		return instance;
	}

}
