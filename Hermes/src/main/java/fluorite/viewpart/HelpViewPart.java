package fluorite.viewpart;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

//import context.saros.SarosAccessorFactory;
import analyzer.ui.APredictionController;
import fluorite.commands.DifficultyCommand;
import fluorite.commands.DifficultyCommand.Status;
import fluorite.dialogs.InsurmountableDialog;
import fluorite.dialogs.SurmountableDialog;
import fluorite.model.EHEventRecorder;
import fluorite.model.StatusConsts;
import util.trace.view.help.HelpViewCreated;

public class HelpViewPart extends ViewPart {
	public HelpViewPart() {
	}

	private static Label lblStatusValue;
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());

		final Label lblStatus = new Label(parent, SWT.NONE);
		FormData fd_lblStatus = new FormData();
		fd_lblStatus.top = new FormAttachment(0, 5);
		fd_lblStatus.left = new FormAttachment(0, 5);
		lblStatus.setLayoutData(fd_lblStatus);
		lblStatus.setText("Status:");

		lblStatusValue = new Label(parent, SWT.NONE);
		FormData fd_lblStatusValue = new FormData();
		fd_lblStatusValue.bottom = new FormAttachment(0, 19);
		fd_lblStatusValue.right = new FormAttachment(0, 455);
		fd_lblStatusValue.top = new FormAttachment(0, 5);
		fd_lblStatusValue.left = new FormAttachment(0, 159);
		lblStatusValue.setLayoutData(fd_lblStatusValue);
		lblStatusValue.setSize(500, lblStatusValue.getSize().y);
		lblStatusValue.setText("Session Started");

		final Shell shell = this.getSite().getWorkbenchWindow().getShell();
		Button btnMakingProgress = new Button(parent, SWT.NONE);
		FormData fd_btnMakingProgress = new FormData();
		fd_btnMakingProgress.top = new FormAttachment(0, 62);
		fd_btnMakingProgress.left = new FormAttachment(0, 5);
		btnMakingProgress.setLayoutData(fd_btnMakingProgress);
		btnMakingProgress.setText(StatusConsts.PROGRESS_TEXT);
		HelpViewCreated.newCase(parent, this);
		btnMakingProgress.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DifficultyCommand command = new DifficultyCommand(Status.Making_Progress);
				EHEventRecorder.getInstance().recordCommand(command);
				lblStatusValue.setText(StatusConsts.PROGRESS_TEXT);
			}
		});

		Button btnSurmountable = new Button(parent, SWT.NONE);
		FormData fd_btnSurmountable = new FormData();
		fd_btnSurmountable.top = new FormAttachment(0, 62);
		int width = fd_btnMakingProgress.width;
//		int width = btnSurmountable.getS
//		Point size = fd_btnMakingProgress.
		fd_btnSurmountable.left = new FormAttachment(0, 159);
		btnSurmountable.setLayoutData(fd_btnSurmountable);
		btnSurmountable.setText(StatusConsts.SURMOUNTABLE_TEXT);
		btnSurmountable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SurmountableDialog dialog = new SurmountableDialog(shell);
				dialog.create();
				if (dialog.open() == Window.OK) {
					DifficultyCommand command =
							new DifficultyCommand(Status.Surmountable,dialog.getTryingToDo(), dialog.getCausedDifficulty(), 
									dialog.getOtherCausedDifficulty(), dialog.getOvercomeDifficultyDropDown(), 
									dialog.getOtherOverComeDifficultySaveText(), dialog.getOtherMinutes());
					EHEventRecorder.getInstance().recordCommand(command);
					lblStatusValue.setText(StatusConsts.SURMOUNTABLE_TEXT);
				}
			}
		});

		Button btnInsurmountable = new Button(parent, SWT.NONE);
		FormData fd_btnInsurmountable = new FormData();
		fd_btnInsurmountable.top = new FormAttachment(0, 62);
		fd_btnInsurmountable.left = new FormAttachment(0, 394);
		btnInsurmountable.setLayoutData(fd_btnInsurmountable);
		btnInsurmountable.setText(StatusConsts.INSURMOUNTABLE_TEXT);
		btnInsurmountable.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				InsurmountableDialog dialog = new InsurmountableDialog(shell);
				dialog.create();
				if (dialog.open() == Window.OK) {
					DifficultyCommand command =
							new DifficultyCommand(Status.Insurmountable,dialog.getTryingToDo(), dialog.getCausedDifficulty(), 
									dialog.getOtherCausedDifficulty(), dialog.getOvercomeDifficultyDropDown(), 
									dialog.getOtherOverComeDifficultySaveText(), dialog.getOtherMinutes(), 
									dialog.getPersonAskedForHelp());
					EHEventRecorder.getInstance().recordCommand(command);
					lblStatusValue.setText(StatusConsts.INSURMOUNTABLE_TEXT);
				}
			}
		});
		Button btnTestbed = new Button(parent, SWT.NONE);
		FormData fd_btnTestbed = new FormData();
		fd_btnTestbed.top = new FormAttachment(0, 124);
		fd_btnTestbed.left = new FormAttachment(0, 5);
		btnTestbed.setLayoutData(fd_btnTestbed);
		btnTestbed.setText("Analytics");
//		HelpViewCreated.newCase(parent, this);
		btnTestbed.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				APredictionController.createUI();
//				LiveModePredictionConfigurer.visualizePrediction();
			}
		});
		
		Button btnExportWorkspace = new Button(parent, SWT.NONE);
		FormData fd_btnExportWorkspace = new FormData();
		fd_btnExportWorkspace.top = new FormAttachment(0, 124);
		fd_btnExportWorkspace.left = new FormAttachment(0, 159);
		btnExportWorkspace.setLayoutData(fd_btnExportWorkspace);
		btnExportWorkspace.setText("Export Workspace");
//		HelpViewCreated.newCase(parent, this);
		btnExportWorkspace.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				SarosAccessorFactory.getSingleton().resetIncomingHandler();

//				APredictionController.createUI();
//				LiveModePredictionConfigurer.visualizePrediction();
			}
		});
		
		Button btnGetWorkspace = new Button(parent, SWT.NONE);
		FormData fd_btnGetWorkspace = new FormData();
		fd_btnGetWorkspace.top = new FormAttachment(0, 124);
		fd_btnGetWorkspace.left = new FormAttachment(0, 394);
		btnGetWorkspace.setLayoutData(fd_btnGetWorkspace);
		btnGetWorkspace.setText("GetWorkspace");
//		HelpViewCreated.newCase(parent, this);
		btnGetWorkspace.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				SarosAccessorFactory.getSingleton().shareFixedProjectWithFixedUser();

//				APredictionController.createUI();
//				LiveModePredictionConfigurer.visualizePrediction();
			}
		});
	}
	
	public static String getStatusInformation()
	{
		// help view may not be displayed
		if (lblStatusValue != null && !lblStatusValue.isDisposed())
		{
			if (lblStatusValue != null)
				return lblStatusValue.getText();
		}
		return "";
	}
	
	public static void displayStatusInformation(String status)
	{
		// help view may not be displayed
		if (lblStatusValue != null && !lblStatusValue.isDisposed())
		{
			if (lblStatusValue != null)
				lblStatusValue.setText(status);
		}
	}

	@Override
	public void setFocus() {
	}
}
