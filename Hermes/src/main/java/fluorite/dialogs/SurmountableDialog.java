package fluorite.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

//import context.saros.SarosAccessorFactory;
//import dayton.ServerConnection;

public class SurmountableDialog extends
		org.eclipse.jface.dialogs.TitleAreaDialog {

	public SurmountableDialog(Shell parentShell) {
		super(parentShell);
//		SarosAccessorFactory.getSingleton().resetIncomingHandler();
	}

	@Override
	public void create() {
		super.create();

		// Set the title
		setTitle("Solving this problem on my own dialog");
		// Set the message
		setMessage(
				"Please fill in this form and press okay to save the information",
				IMessageProvider.INFORMATION);
	}

	private void saveInput() {
		tryingToDo = tryingToDoText.getText();
		
		if(comboDropDownCausedDifficulty.getSelectionIndex() > -1)
			causedDifficulty = comboDropDownCausedDifficulty
					.getItem(comboDropDownCausedDifficulty.getSelectionIndex());
		otherCausedDifficulty = otherCausedDifficultyText.getText();
		
		if(comboDropDownOvercomeDifficulty.getSelectionIndex() > -1)
			overcomeDifficultyDropDown = comboDropDownOvercomeDifficulty
					.getItem(comboDropDownOvercomeDifficulty.getSelectionIndex());
		otherOverComeDifficultySaveText = otherOvercomeDifficultyText.getText();
		otherMinutes = otherMinutesText.getText();
	}

	public String getTryingToDo() {
		return tryingToDo;
	}

	public String getCausedDifficulty() {
		return causedDifficulty;
	}

	public String getOtherCausedDifficulty() {
		return otherCausedDifficulty;
	}

	public String getOvercomeDifficultyDropDown() {
		return overcomeDifficultyDropDown;
	}

	public String getOtherOverComeDifficultySaveText() {
		return otherOverComeDifficultySaveText;
	}

	public String getOtherMinutes() {
		return otherMinutes;
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
		String[] helpRequest = {getTryingToDo(),
				getCausedDifficulty(),getOtherCausedDifficulty(),getOvercomeDifficultyDropDown(),
				getOtherOverComeDifficultySaveText(),getOtherMinutes()};
		try {
//		ServerConnection.getServerConnection().sendHelpRequest(helpRequest);
			System.out.println("Surmountable difficulties should be sent to teh server");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Text tryingToDoText;
	private Combo comboDropDownCausedDifficulty;
	private Text otherCausedDifficultyText;
	private Combo comboDropDownOvercomeDifficulty;
	private Text otherOvercomeDifficultyText;
	private Text otherMinutesText;

	private String tryingToDo;
	private String causedDifficulty;
	private String otherCausedDifficulty;
	private String overcomeDifficultyDropDown;
	private String otherOverComeDifficultySaveText;
	private String otherMinutes;

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		// layout.horizontalAlignment = GridData.FILL;
		parent.setLayout(layout);

		// The text fields will grow with the size of the dialog
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		Label label1 = new Label(parent, SWT.NONE);
		label1.setText("What were you trying to do?");

		tryingToDoText = new Text(parent, SWT.BORDER);
		tryingToDoText.setLayoutData(gridData);

		Label labelCausedDifficulty = new Label(parent, SWT.NONE);
		labelCausedDifficulty.setText("What caused the difficulty?");

		comboDropDownCausedDifficulty = new Combo(parent, SWT.DROP_DOWN
				| SWT.BORDER);
		comboDropDownCausedDifficulty.add("Exception");
		comboDropDownCausedDifficulty.add("Compiler Error");
		comboDropDownCausedDifficulty.add("Configuration Error");
		comboDropDownCausedDifficulty.add("Incorrect output");
		comboDropDownCausedDifficulty.add("API");
		comboDropDownCausedDifficulty.add("Other");

		Label label2 = new Label(parent, SWT.NONE);
		label2.setText("If you selected Other, please provide more information.");
		// You should not re-use GridData
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		otherCausedDifficultyText = new Text(parent, SWT.BORDER);
		otherCausedDifficultyText.setLayoutData(gridData);

		Label labelTriedToOvercomeDifficulty = new Label(parent, SWT.NONE);
		labelTriedToOvercomeDifficulty
				.setText("How have you tried to overcome the difficulty?");

		comboDropDownOvercomeDifficulty = new Combo(parent, SWT.DROP_DOWN
				| SWT.BORDER);
		comboDropDownOvercomeDifficulty
				.add("I knew how to solve it because it happened before");
		comboDropDownOvercomeDifficulty.add("Looked up solution online");
		comboDropDownOvercomeDifficulty
				.add("I figured out a way to fix it myself without help");
		comboDropDownOvercomeDifficulty
				.add("Found information in another file or in another project");
		comboDropDownOvercomeDifficulty.add("Other");

		Label label3 = new Label(parent, SWT.NONE);
		label3.setText("If you selected Other, please provide more information.");

		// You should not re-use GridData
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		otherOvercomeDifficultyText = new Text(parent, SWT.BORDER);
		otherOvercomeDifficultyText.setLayoutData(gridData);

		Label label4 = new Label(parent, SWT.NONE);
		label4.setText("How many minutes have you been working to overcome this difficulty?");

		// You should not re-use GridData
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		otherMinutesText = new Text(parent, SWT.BORDER);
		otherMinutesText.setLayoutData(gridData);

		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.CENTER;

		parent.setLayoutData(gridData);
		// Create Save button
		// Own method as we need to overview the SelectionAdapter
		createOkButton(parent, OK, "Save", true);
		// Add a SelectionListener

		// Create Cancel button
		Button cancelButton = createButton(parent, CANCEL, "Cancel", false);
		// Add a SelectionListener
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});
	}

	protected Button createOkButton(Composite parent, int id, String label,
			boolean defaultButton) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (isValidInput()) {
					okPressed();
				}
			}
		});
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}

	private boolean isValidInput() {
		boolean valid = true;
		return valid;
	}
}
