package dayton.ellwanger.helpbutton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import dayton.ellwanger.helpbutton.preferences.HelpPreferencePage;
import dayton.ellwanger.hermes.SubView;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.eclipse.wb.swt.SWTResourceManager;

public class HelperView extends ViewPart {

	private HelperListener helperListener;
	private Text message;
	private Text reply;
	private Text emailText;
	private Text pwText;
	private List<JSONObject> requests = new ArrayList<>();
	private int index;
	private Combo courseCombo;
	private Combo assignCombo;
	private Combo problemCombo;
	private Combo termCombo;
	private Combo requestCombo;
	private Label lblSEmail;
	private Label lblSTerm;
	private Label lblSCourse;
	private Label lblSErrorType;
	private Label lblSErrorMessage;
	private Label lblSdifficulty;
	private Label lblSAssign;
	private Button btnCreateProject;
	private Button replyButton;
	private Button btnPrev;
	private Button btnNext;
	private Button pullRequestsButton;
//	private static final String[] TERMS = {"2018 Spring", "2018 Fall", "2019 Spring", "2019 Fall", "2020 Spring", "2020 Fall"};
//	private static final String[] COURSES = {"comp401", "comp410", "comp411"};
//	private static final String[] COMP401ASSIGNMENTS = {"Assignment1", "Assignment2", "Assignment3"};
//	private static final String[] COMP410ASSIGNMENTS = {"Assignmnet1", "Assignment2"};
//	private static final String[] COMP411ASSIGNMENTS = {"Assignment1", "Assignment2", "Assignment3", "Assignment4"};
//	private static final String[][] ASSIGNMENTS = {COMP401ASSIGNMENTS, COMP410ASSIGNMENTS, COMP411ASSIGNMENTS};
//	private static final String[] PROBLEMS = {"1", "2", "3", "4"};
	private Text regexText;
	private Text output;
	private Label lblLanguage;
	private Combo languageCombo;
	private Label lblProblem;
	private Label lblProblem1;

	public HelperView() {
	}

	public void setHelpListener(HelperListener l) {
		helperListener = l;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());

		Composite idComposite = new Composite(parent, SWT.NONE);
		GridData gd_idComposite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_idComposite.heightHint = 36;
		idComposite.setLayoutData(gd_idComposite);
		idComposite.setLayout(new GridLayout(4, false));

		Label lblIEmail = new Label(idComposite, SWT.NONE);
		GridData gd_lblIEmail = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblIEmail.widthHint = 40;
		lblIEmail.setLayoutData(gd_lblIEmail);
		lblIEmail.setText("Email:");
		lblIEmail.setAlignment(SWT.RIGHT);

		emailText = new Text(idComposite, SWT.BORDER);
		GridData gd_emailText = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_emailText.widthHint = 245;
		emailText.setLayoutData(gd_emailText);

		Label lblIPW = new Label(idComposite, SWT.NONE);
		lblIPW.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIPW.setText("Password:");

		pwText = new Text(idComposite, SWT.BORDER);
		GridData gd_pwText = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_pwText.widthHint = 203;
		pwText.setLayoutData(gd_pwText);
		pwText.setEchoChar('*');
//		Button loginButton = new Button(idComposite, SWT.NONE);
//		loginButton.setText("Log in");
//		loginButton.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
//		loginButton.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				for (HelperListener helperListener : helperListeners) {
//					helperListener.login(emailText.getText(), pwText.getText());
//				}
//			}
//		});

		Composite filterComposite = new Composite(parent, SWT.NONE);
		filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		filterComposite.setLayout(new GridLayout(14, false));

		Label lblFilter = new Label(filterComposite, SWT.NONE);
		GridData gd_lblFilter = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblFilter.widthHint = 40;
		lblFilter.setLayoutData(gd_lblFilter);
		lblFilter.setText("Filters");
		lblFilter.setAlignment(SWT.RIGHT);

		Label lblFTerm = new Label(filterComposite, SWT.NONE);
		lblFTerm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFTerm.setText("Term:");
		lblFTerm.setAlignment(SWT.RIGHT);

		termCombo = new Combo(filterComposite, SWT.READ_ONLY);
		termCombo.setItems(new String[] { "comp401", "comp410", "comp411" });
		termCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		termCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				populateCourseCombo();
			}
		});
		populateTermCombo();

		Label lblFCourse = new Label(filterComposite, SWT.NONE);
		GridData gd_lblFCourse = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblFCourse.widthHint = 55;
		lblFCourse.setLayoutData(gd_lblFCourse);
		lblFCourse.setText("Course:");
		lblFCourse.setAlignment(SWT.RIGHT);

		courseCombo = new Combo(filterComposite, SWT.READ_ONLY);
		courseCombo.setItems(new String[] { "comp401", "comp410", "comp411" });
		courseCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		courseCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				populateAssignCombo();
			}
		});

		Label lblFAssign = new Label(filterComposite, SWT.NONE);
		GridData gd_lblFAssign = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblFAssign.widthHint = 98;
		lblFAssign.setLayoutData(gd_lblFAssign);
		lblFAssign.setText("Assignment:");
		lblFAssign.setAlignment(SWT.RIGHT);

		assignCombo = new Combo(filterComposite, SWT.READ_ONLY);
		assignCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		assignCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				populateProblemCombo();
			}
		});

		Label lblFProblem = new Label(filterComposite, SWT.NONE);
		lblFProblem.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFProblem.setText("Problem:");
		lblFProblem.setAlignment(SWT.RIGHT);

		problemCombo = new Combo(filterComposite, SWT.READ_ONLY);
		problemCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblLanguage = new Label(filterComposite, SWT.NONE);
		lblLanguage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLanguage.setText("Language:");

		languageCombo = new Combo(filterComposite, SWT.READ_ONLY);
		languageCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		populateLanguageCombo();

		Label lblFRegex = new Label(filterComposite, SWT.NONE);
		lblFRegex.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFRegex.setText("Regex:");

		regexText = new Text(filterComposite, SWT.BORDER);
		regexText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		pullRequestsButton = new Button(filterComposite, SWT.NONE);
		pullRequestsButton.setText("Pull Requests");
		pullRequestsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				if (emailText.getText().equals("")) {
//					popupMessage("Error", "Please enter your email.");
//				} else
				if (pwText.getText().equals("")) {
					popupMessage("Error", "Please enter your password");
				} else {
//				else if (termCombo.getText().equals("")) {
//					popupMessage("Error", "Please select a term.");
//				} else if (courseCombo.getText().equals("")) {
//					popupMessage("Error", "Please select a course.");
//				} else if (assignCombo.getText().equals("")) {
//					popupMessage("Error", "Please select an assignment.");
//				} else if (problemCombo.getText().equals("")) {
//					popupMessage("Error", "Please select a problem.");
//				} else {
					try {
						if (helperListener != null) {
							helperListener.pull(termCombo.getText(), courseCombo.getText(), assignCombo.getText(),
									problemCombo.getText(), pwText.getText(), regexText.getText(),
									languageCombo.getText());
						}
					} catch (IOException e2) {
						popupMessage("Error", e2.getMessage());
					}
				}
			}
		});

		Composite requestComposite = new Composite(parent, SWT.NONE);
		requestComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		requestComposite.setLayout(new GridLayout(2, false));

		Label lblRequests = new Label(requestComposite, SWT.NONE);
		lblRequests.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRequests.setText("Requests:");

		requestCombo = new Combo(requestComposite, SWT.READ_ONLY);
		requestCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		requestCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				index = requestCombo.getSelectionIndex();
//				if (index == 0) {
//					btnPrev.setEnabled(false);
//				}
//				if (index == requestCombo.getItemCount()-1) {
//					btnNext.setEnabled(false);
//				}
				showRequestInfo(index);
			}
		});

		Composite studentIdComposite = new Composite(parent, SWT.NONE);
		studentIdComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		studentIdComposite.setLayout(new GridLayout(12, false));

		Label lblSEmail1 = new Label(studentIdComposite, SWT.NONE);
		lblSEmail1.setAlignment(SWT.RIGHT);
		GridData gd_lblSEmail1 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblSEmail1.widthHint = 40;
		lblSEmail1.setLayoutData(gd_lblSEmail1);
		lblSEmail1.setText("Email:");

		lblSEmail = new Label(studentIdComposite, SWT.NONE);
		GridData gd_lblSEmail = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblSEmail.widthHint = 250;
		lblSEmail.setLayoutData(gd_lblSEmail);
		lblSEmail.setText("New Label");

		Label lblSTerm1 = new Label(studentIdComposite, SWT.NONE);
		lblSTerm1.setText("Term:");

		lblSTerm = new Label(studentIdComposite, SWT.NONE);
		GridData gd_lblSTerm = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSTerm.widthHint = 90;
		lblSTerm.setLayoutData(gd_lblSTerm);
		lblSTerm.setText("New Label");

		Label lblSCourse1 = new Label(studentIdComposite, SWT.NONE);
		lblSCourse1.setAlignment(SWT.RIGHT);
		GridData gd_lblSCourse1 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblSCourse1.widthHint = 55;
		lblSCourse1.setLayoutData(gd_lblSCourse1);
		lblSCourse1.setText("Course:");

		lblSCourse = new Label(studentIdComposite, SWT.NONE);
		GridData gd_lblSCourse = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSCourse.widthHint = 93;
		lblSCourse.setLayoutData(gd_lblSCourse);
		lblSCourse.setText("New Label");

		Label lblSAssign1 = new Label(studentIdComposite, SWT.NONE);
		lblSAssign1.setAlignment(SWT.RIGHT);
		GridData gd_lblSAssign1 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblSAssign1.widthHint = 98;
		lblSAssign1.setLayoutData(gd_lblSAssign1);
		lblSAssign1.setText("Assignment:");

		lblSAssign = new Label(studentIdComposite, SWT.NONE);
		GridData gd_lblSAssign = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSAssign.widthHint = 137;
		lblSAssign.setLayoutData(gd_lblSAssign);
		lblSAssign.setText("New Label");

		lblProblem1 = new Label(studentIdComposite, SWT.NONE);
		lblProblem1.setText("Problem:");

		lblProblem = new Label(studentIdComposite, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.widthHint = 88;
		lblProblem.setLayoutData(gd_lblNewLabel);
		lblProblem.setText("New Label");

		Label lblSDifficulty1 = new Label(studentIdComposite, SWT.NONE);
		lblSDifficulty1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lblSDifficulty1.setText("Difficulty: ");

		lblSdifficulty = new Label(studentIdComposite, SWT.NONE);
		lblSdifficulty.setText("New Label");

		Composite ExceptionComposite = new Composite(parent, SWT.NONE);
		ExceptionComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		ExceptionComposite.setLayout(new GridLayout(4, false));

		Label lblSErrorType1 = new Label(ExceptionComposite, SWT.NONE);
		GridData gd_lblSErrorType1 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblSErrorType1.widthHint = 90;
		lblSErrorType1.setLayoutData(gd_lblSErrorType1);
		lblSErrorType1.setText("Error Type:");

		lblSErrorType = new Label(ExceptionComposite, SWT.NONE);
		GridData gd_lblSErrorType = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSErrorType.widthHint = 242;
		lblSErrorType.setLayoutData(gd_lblSErrorType);
		lblSErrorType.setText("New Label");

		Label lblSErrorMessage1 = new Label(ExceptionComposite, SWT.NONE);
		GridData gd_lblSErrorMessage1 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblSErrorMessage1.widthHint = 105;
		lblSErrorMessage1.setLayoutData(gd_lblSErrorMessage1);
		lblSErrorMessage1.setText("Error Message:");
		lblSErrorMessage1.setAlignment(SWT.RIGHT);

		lblSErrorMessage = new Label(ExceptionComposite, SWT.NONE);
		GridData gd_lblSErrorMessage = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_lblSErrorMessage.minimumWidth = 500;
		lblSErrorMessage.setLayoutData(gd_lblSErrorMessage);
		lblSErrorMessage.setText("New Label");
		String[] difficultyLabels = { "Trivial", "Easy", "Challenging", "Hard", "Impossible" };

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_composite = new GridLayout(3, true);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);

		output = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		output.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		GridData gd_output = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_output.heightHint = 132;
		output.setLayoutData(gd_output);
		output.setMessage("Student Console Output");

		message = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		message.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		GridData gd_message = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_message.heightHint = 132;
		message.setLayoutData(gd_message);
		message.setMessage("Student Message");

		reply = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		reply.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		GridData gd_reply = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_reply.widthHint = 360;
		gd_reply.heightHint = 132;
		reply.setLayoutData(gd_reply);
		reply.setMessage("Reply Here");

		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		buttonComposite.setLayout(new GridLayout(4, false));

		btnCreateProject = new Button(buttonComposite, SWT.NONE);
		btnCreateProject.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnCreateProject.setText("Create Project");
		btnCreateProject.setEnabled(true);
		btnCreateProject.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				helperListener.createProject(requests.get(requestCombo.getSelectionIndex()));
//				helperListener.createProject(null);
			}
		});

		replyButton = new Button(buttonComposite, SWT.PUSH);
		GridData gd_replyButton = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_replyButton.widthHint = 100;
		replyButton.setLayoutData(gd_replyButton);
		replyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (requestCombo.getText().equals("")) {
					popupMessage("Error", "Please select a request to reply.");
				} else if (reply.getText().equals("")) {
					popupMessage("Error", "Please enter your reply.");
				} else {
					try {
						if (helperListener != null) {
							helperListener.reply(reply.getText(), emailText.getText(), pwText.getText(),
									requests.get(index).getString("_id"));
						}
						popupMessage("Info", "Reply Sent");
					} catch (IOException e2) {
						popupMessage("Error", "Connection Failed");
					} catch (JSONException e1) {
						e1.printStackTrace();
					}

				}
			}
		});
		replyButton.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		replyButton.setText("Reply");

		btnPrev = new Button(buttonComposite, SWT.PUSH);
		GridData gd_btnPrev = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_btnPrev.widthHint = 100;
		btnPrev.setLayoutData(gd_btnPrev);
		btnPrev.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (requests != null) {
					if (index <= 0) {
						index = 0;
					} else {
						index--;
						btnNext.setEnabled(true);
						if (index == 0) {
							btnPrev.setEnabled(false);
						}
					}
					requestCombo.select(index);
					showRequestInfo(index);
				} else {
					index = 0;
				}
			}
		});
		btnPrev.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		btnPrev.setText("Previous");
		btnPrev.setEnabled(false);

		btnNext = new Button(buttonComposite, SWT.NONE);
		btnNext.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (requests != null) {
					if (index >= requests.size() - 1) {
						index = requests.size() - 1;
//						popupMessage("Warning", "This is the last reply.");
					} else {
						index++;
						btnPrev.setEnabled(true);
						if (index == requests.size() - 1) {
							btnNext.setEnabled(false);
						}
						requestCombo.select(index);
						showRequestInfo(index);
					}
				} else {
					index = 0;
				}
			}
		});
		GridData gd_btnNext = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnNext.widthHint = 100;
		btnNext.setLayoutData(gd_btnNext);
		btnNext.setText("Next");
		btnNext.setEnabled(false);

		new HelperViewController(this);
	}

	@Override
	public void setFocus() {
	}

	private void populateTermCombo() {
		termCombo.removeAll();
		for (String[] s : HelpPreferencePage.TERMS) {
			termCombo.add(s[0]);
		}
	}

	private void populateLanguageCombo() {
		languageCombo.removeAll();
		for (String[] s : HelpPreferencePage.LANGUAGES) {
			languageCombo.add(s[0]);
		}
	}

	private void populateCourseCombo() {
		courseCombo.removeAll();
		for (String[] s : HelpPreferencePage.COURSES) {
			courseCombo.add(s[0]);
		}
	}

	private void populateAssignCombo() {
		assignCombo.removeAll();
		for (String[] s : HelpPreferencePage.ASSIGNMENTS) {
			assignCombo.add(s[0]);
		}
	}

	public void populateRequestCombo(JSONObject response) {
		try {
//			System.out.println(response.toString(4));
			this.requests.clear();
			requestCombo.removeAll();
			JSONArray requests = response.getJSONArray("requests");
			for (int i = 0; i < requests.length(); i++) {
				this.requests.add(requests.getJSONObject(i));
				requestCombo.add(requests.getJSONObject(i).getString("_id"));
			}
			if (requestCombo.getItemCount() > 0) {
				btnNext.setEnabled(true);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void populateProblemCombo() {
		problemCombo.removeAll();
		for (String[] s : HelpPreferencePage.PROBLEMS) {
			problemCombo.add(s[0]);
		}
	}

	private void popupMessage(String title, String text) {
		JOptionPane optionPane = new JOptionPane(text, JOptionPane.WARNING_MESSAGE);
		JDialog dialog = optionPane.createDialog("Error");
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
	}

	private void showRequestInfo(int i) {
		if (i < 0) {
			return;
		}
		try {
			JSONObject request = requests.get(i);
			lblSEmail.setText(request.getString("email"));
			lblSTerm.setText(request.getString("term"));
			lblSCourse.setText(request.getString("course"));
			lblSAssign.setText(request.getString("assignment"));
			lblSErrorType.setText(request.getString("error-type"));
			lblSErrorMessage.setText(request.getString("error-message"));
			lblProblem.setText(request.getString("problem"));
			JSONArray comment = request.getJSONArray("comment");
			String commentString = "";
			for (int j = 0; j < comment.length(); j++) {
				commentString += comment.getString(j) + '\n';
			}
			message.setText(commentString);
//			message.setText(request.getString("comment"));
			JSONArray help = request.getJSONArray("help");
			String replyString = "";
			for (int j = 0; j < help.length(); j++) {
				replyString += help.getString(j) + '\n';
			}
			reply.setText(replyString);
			if (i > 0) {
				btnPrev.setEnabled(true);
			} else {
				btnPrev.setEnabled(false);
			}
			if (i < requestCombo.getItemCount() - 1) {
				btnNext.setEnabled(true);
			} else {
				btnNext.setEnabled(false);
			}
			if (request.getString("language").equalsIgnoreCase("java")) {
				btnCreateProject.setEnabled(true);
			} else {
				btnCreateProject.setEnabled(false);
			}
			if (request.getString("output") != null) {
				output.setText(request.getString("output"));
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}

//	public JSONObject getSelectedRequest() {
//		if (requests.size() == 0 || requestCombo.getSelectionIndex() == -1) {
//			return null;
//		}
//		return requests.get(requestCombo.getSelectionIndex());
//	}
}