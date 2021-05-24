package dayton.ellwanger.helpbutton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.part.ViewPart;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dayton.ellwanger.helpbutton.exceptionMatcher.JavaExceptionMatcher;
import dayton.ellwanger.helpbutton.exceptionMatcher.PrologExceptionMatcher;
import dayton.ellwanger.helpbutton.exceptionMatcher.SMLExceptionMatcher;
import dayton.ellwanger.helpbutton.preferences.HelpPreferencePage;
import dayton.ellwanger.helpbutton.preferences.HelpPreferences;
import dayton.ellwanger.hermes.SubView;
import dayton.ellwanger.hermes.preferences.Preferences;
import dayton.ellwanger.hermes.preferences.PreferencesListener;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.wb.swt.SWTResourceManager;

public class HelpView extends ViewPart implements PreferencesListener {

	private HelpListener helpListener;
	private Text message;
	private Text reply;
	private Text emailText;
	private Combo courseCombo;
	private Combo assignCombo;
	private Label errorMessagelbl;
	private Combo errorCombo;
	private Combo problemCombo;
	private Combo termCombo;
	private Combo pendingCombo;
	private Combo repliedCombo;
	private Combo languageCombo;
	private Button btnNext;
	private Button btnPrev;
	private int difficultySelected;
	private String requestID;
	private List<String> exceptions = new ArrayList<>();
	private List<String> localcheckErrors = new ArrayList<>();
	private List<String> replies;
	private List<JSONObject> pendingRequests;
	private List<JSONObject> repliedRequests;
	private List<JSONObject> errorRequests;
	private boolean selected = false;
	private boolean updateExceptions = true;
//	private File requestFolder = new File(System.getProperty("user.home") + "\\hermesHelp");
	private File pendingFolder = new File(System.getProperty("user.home") + File.separator + "helper-config"
			+ File.separator + "Help" + File.separator + "Pending");
	private File repliedFolder = new File(System.getProperty("user.home") + File.separator + "helper-config"
			+ File.separator + "Help" + File.separator + "Replied");
	private int index;
	private static final String getParamURL = "https://us-south.functions.appdomain.cloud/api/v1/web/ORG-UNC-dist-seed-james_dev/V2/get-available-parameters";
//	private static final String[] TERMS = {"2018 Spring", "2018 Fall", "2019 Spring", "2019 Fall", "2020 Spring", "2020 Fall"};
//	private static final String[] COURSES = {"comp401", "comp410", "comp411"};
//	private static final String[] COMP401ASSIGNMENTS = {"Assignment1", "Assignment2", "Assignment3"};
//	private static final String[] COMP410ASSIGNMENTS = {"Assignmnet1", "Assignment2"};
//	private static final String[] COMP411ASSIGNMENTS = {"Assignment1", "Assignment2", "Assignment3", "Assignment4"};
//	private static final String[][] ASSIGNMENTS = {COMP401ASSIGNMENTS, COMP410ASSIGNMENTS, COMP411ASSIGNMENTS};
//	private static final String[] PROBLEMS = {"1", "2", "3", "4"};
	private Text output;
	private static final String NOT_CONNECTED = "Not connected to server.\nTo connect to server, go to Preference->Hermes->Hermes Help->Check Connect to Server.";
//	HttpURLConnection conn;
//	private final int timeout = 5000;

	public HelpView() {
	}

	public void setHelpListener(HelpListener l) {
		helpListener = l;
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout gl_parent = new GridLayout();
		gl_parent.verticalSpacing = 0;
		gl_parent.marginWidth = 0;
		gl_parent.marginHeight = 0;
		gl_parent.horizontalSpacing = 0;
		parent.setLayout(gl_parent);

		Composite idComposite = new Composite(parent, SWT.NONE);
		idComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		idComposite.setLayout(new GridLayout(12, false));

		Label lblEmail = new Label(idComposite, SWT.NONE);
		lblEmail.setAlignment(SWT.RIGHT);
		GridData gd_lblEmail = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblEmail.widthHint = 40;
		lblEmail.setLayoutData(gd_lblEmail);
		lblEmail.setText("Email:");

		emailText = new Text(idComposite, SWT.BORDER);
		GridData gd_emailText = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_emailText.widthHint = 257;
		emailText.setLayoutData(gd_emailText);
		emailText.setText(EditorsUI.getPreferenceStore().getString(HelpPreferences.EMAIL));
		emailText.addModifyListener((ModifyEvent e) -> {
			EditorsUI.getPreferenceStore().setValue(HelpPreferences.EMAIL, emailText.getText());
		});

		Label lblTerm = new Label(idComposite, SWT.NONE);
		lblTerm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTerm.setText("Term:");
		lblTerm.setAlignment(SWT.RIGHT);

		termCombo = new Combo(idComposite, SWT.READ_ONLY);
		termCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		termCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				populateCourseCombo();
				if (termCombo.getSelectionIndex() < 0) {
					return;
				}
				EditorsUI.getPreferenceStore().setValue(HelpPreferences.TERM, termCombo.getText());
				filterRequests();
			}
		});
//		populateTermCombo();

		Label lblCourse = new Label(idComposite, SWT.NONE);
		lblCourse.setAlignment(SWT.RIGHT);
		GridData gd_lblCourse = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblCourse.widthHint = 55;
		lblCourse.setLayoutData(gd_lblCourse);
		lblCourse.setText("Course:");

		courseCombo = new Combo(idComposite, SWT.READ_ONLY);
		GridData gd_courseCombo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_courseCombo.widthHint = 90;
		courseCombo.setLayoutData(gd_courseCombo);
		courseCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// populateAssignCombo();
				if (courseCombo.getSelectionIndex() < 0) {
					return;
				}
				EditorsUI.getPreferenceStore().setValue(HelpPreferences.COURSE, courseCombo.getText());
				filterRequests();
			}
		});

		Label lblAssign = new Label(idComposite, SWT.NONE);
		lblAssign.setAlignment(SWT.RIGHT);
		GridData gd_lblAssign = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblAssign.widthHint = 98;
		lblAssign.setLayoutData(gd_lblAssign);
		lblAssign.setText("Assignment:");

		assignCombo = new Combo(idComposite, SWT.READ_ONLY);
		GridData gd_assignCombo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_assignCombo.widthHint = 62;
		assignCombo.setLayoutData(gd_assignCombo);
		assignCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// populateProblemCombo();
				if (assignCombo.getSelectionIndex() < 0) {
					return;
				}
				EditorsUI.getPreferenceStore().setValue(HelpPreferences.ASSIGNMENT, assignCombo.getText());
				filterRequests();
			}
		});

		Label lblProblem = new Label(idComposite, SWT.NONE);
		lblProblem.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblProblem.setText("Problem:");
		lblProblem.setAlignment(SWT.RIGHT);

		problemCombo = new Combo(idComposite, SWT.READ_ONLY);
		GridData gd_problemCombo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_problemCombo.widthHint = 64;
		problemCombo.setLayoutData(gd_problemCombo);
		problemCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (problemCombo.getSelectionIndex() < 0) {
					return;
				}
				EditorsUI.getPreferenceStore().setValue(HelpPreferences.PROBLEM, problemCombo.getText());
				filterRequests();
				
			}
		});
		
		Label lblLanguage = new Label(idComposite, SWT.NONE);
		lblLanguage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLanguage.setText("Language:");
		
		languageCombo = new Combo(idComposite, SWT.READ_ONLY);
		GridData gd_languageCombo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_languageCombo.widthHint = 86;
		languageCombo.setLayoutData(gd_languageCombo);
		languageCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// populateProblemCombo();
				if (languageCombo.getSelectionIndex() < 0) {
					return;
				}
				helpListener.setExceptionMatcher(languageCombo.getText());
				helpListener.consoleOutput(output.getText());
				EditorsUI.getPreferenceStore().setValue(HelpPreferences.LANGUAGE, languageCombo.getText());
				filterRequests();
			}
		});
		

		Composite ExceptionComposite = new Composite(parent, SWT.NONE);
		ExceptionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		ExceptionComposite.setLayout(new GridLayout(4, false));

		Label lblErrorType = new Label(ExceptionComposite, SWT.NONE);
		GridData gd_lblErrorType = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblErrorType.widthHint = 78;
		lblErrorType.setLayoutData(gd_lblErrorType);
		lblErrorType.setText("Error Type:");

		errorCombo = new Combo(ExceptionComposite, SWT.READ_ONLY);
		GridData gd_errorCombo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_errorCombo.widthHint = 334;
		errorCombo.setLayoutData(gd_errorCombo);
		errorCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (errorCombo.getSelectionIndex() < 0) {
					return;
				}
				if (selected) {
					if (errorCombo.getSelectionIndex() == 0) {
						return;
					}
					errorCombo.remove(0);
					index--;
					selected = false;
				}
				int index = errorCombo.getSelectionIndex();
				if (errorRequests.get(index) != null) {
					try {
						requestID = errorRequests.get(index).getString("request-id");
						output.setText(errorRequests.get(index).getString("output"));
						message.setText(errorRequests.get(index).getString("comment"));
//						List<String> replies = new ArrayList<>();
//						JSONArray help = response.getJSONArray("help");
//						String id = response.getJSONObject("input").getJSONObject("body").getString("request-id");
////						JSONArray key = help.names();
//						for (int i = 0; i < help.length(); i++) {
//							replies.add(help.getString(i));
//						}
//						reply.setText(errorRequests.get(index).getJSONArray("help"));
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				} else {
					message.setText("");
					if (replies != null) {
						replies.clear();
					}
					reply.setText("");
					btnPrev.setEnabled(false);
					btnNext.setEnabled(false);
					requestID = "";
				}
				if (errorCombo.getText().equals("Other")) {
					errorMessagelbl.setText("Other");
				} else if (index < 0) {
					errorMessagelbl.setText("");
				} else if (index < exceptions.size()) {
					String ex = exceptions.get(index);
					if (ex.indexOf("\n") > 0) {
						switch (languageCombo.getText()) {
						case "java":
							errorMessagelbl.setText(JavaExceptionMatcher.getInstance().getErrorMsg(ex));
							break;
						case "prolog":
							errorMessagelbl.setText(PrologExceptionMatcher.getInstance().getErrorMsg(ex));
							break;
						case "SML":
							errorMessagelbl.setText(SMLExceptionMatcher.getInstance().getErrorMsg(ex));
							break;
						default:
							break;
						} 
					} else {
						errorMessagelbl.setText("");
					}
				} else {
					errorMessagelbl.setText(localcheckErrors.get(index - exceptions.size()));
				}
				pendingCombo.deselectAll();
				repliedCombo.deselectAll();
//				requestID = "";
			}
		});

		Label lblErrorMessage = new Label(ExceptionComposite, SWT.NONE);
		GridData gd_lblErrorMessage = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblErrorMessage.widthHint = 79;
		lblErrorMessage.setLayoutData(gd_lblErrorMessage);
		lblErrorMessage.setText("Message:");
		lblErrorMessage.setAlignment(SWT.RIGHT);

		errorMessagelbl = new Label(ExceptionComposite, SWT.READ_ONLY);
		errorMessagelbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite difficultyComposite = new Composite(parent, SWT.NONE);
		difficultyComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		difficultyComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		RowLayout difficultyLayout = new RowLayout();
		difficultyLayout.marginHeight = 8;
		difficultyLayout.marginTop = 0;
		difficultyLayout.marginRight = 5;
		difficultyLayout.marginLeft = 5;
		difficultyLayout.marginBottom = 0;
		difficultyLayout.type = SWT.HORIZONTAL;
		difficultyLayout.pack = true;
		difficultyComposite.setLayout(difficultyLayout);

		Label difficultyLabel = new Label(difficultyComposite, SWT.NONE);
		difficultyLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		difficultyLabel.setText("Difficulty: ");
		String[] difficultyLabels = { "Trivial", "Easy", "Challenging", "Hard", "Impossible" };
		for (int i = 0; i < difficultyLabels.length; i++) {
			Button button = new Button(difficultyComposite, SWT.RADIO);
			button.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			button.setText(difficultyLabels[i]);
			button.addSelectionListener(new DifficultyButtonHandler(i));
		}

		Composite comboComposite = new Composite(parent, SWT.NONE);
		comboComposite.setLayoutData(new RowData(1430, SWT.DEFAULT));
		GridLayout gl_comboComposite = new GridLayout(12, true);
		gl_comboComposite.horizontalSpacing = 0;
		comboComposite.setLayout(gl_comboComposite);
		GridData gd_comboComposite = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd_comboComposite.heightHint = 38;
		comboComposite.setLayoutData(gd_comboComposite);

		Label lblPending = new Label(comboComposite, SWT.NONE);
		lblPending.setText("Pending Requests:");
		lblPending.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		pendingCombo = new Combo(comboComposite, SWT.READ_ONLY);
		pendingCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		pendingCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					if (pendingCombo.getSelectionIndex() < 0) {
						return;
					}
					JSONObject request = pendingRequests.get(pendingCombo.getSelectionIndex());
					termCombo.setText(request.getString("term"));
					courseCombo.setText(request.getString("course"));
					assignCombo.setText(request.getString("assignment"));
					problemCombo.setText(request.getString("problem"));
//					if (request.has("comment")) {
//						message.setText(request.getString("comment"));
//					}
//					errorCombo.select(-1);
					if (selected) {
						errorCombo.remove(0);
					}
					errorCombo.add(request.getString("error-type"), 0);
					errorCombo.select(0);
					selected = true;
//					errorCombo.setText(request.getString("error-type"));
					errorMessagelbl.setText(request.getString("error-message")
							.substring(request.getString("error-message").indexOf("\n\t") + 2).replace("\r\n", ""));
					requestID = request.getString("request-id");
					if (request.getString("output") != null) {
						updateExceptions = false;
						output.setText(request.getString("output"));
						updateExceptions = true;
					} else {
						output.setText("");
					}
					if (request.getString("comment") != null) {
						message.setText(request.getString("comment"));
					} else {
						message.setText("");
					}
					if (replies != null) {
						replies.clear();
					}
					if (request.has("comment")) {
						message.setText(request.getString("comment"));
					} else {
						message.setText("");
					}
					reply.setText("");
					repliedCombo.deselectAll();
				} catch (JSONException e2) {
				}
			}
		});

		Label lblReplied = new Label(comboComposite, SWT.NONE);
		lblReplied.setText(" Replied Requests:");
		lblReplied.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		repliedCombo = new Combo(comboComposite, SWT.READ_ONLY);
		repliedCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		repliedCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					if (repliedCombo.getSelectionIndex() < 0) {
						return;
					}
					JSONObject request = repliedRequests.get(repliedCombo.getSelectionIndex());
					termCombo.setText(request.getString("term"));
					courseCombo.setText(request.getString("course"));
					assignCombo.setText(request.getString("assignment"));
					problemCombo.setText(request.getString("problem"));
					if (selected) {
						errorCombo.remove(0);
					}
					errorCombo.add(request.getString("error-type"), 0);
					errorCombo.select(0);
					selected = true;
					String em = request.getString("error-message");
					if (em.equals("Other")) {
						errorMessagelbl.setText(em);
					} else {
						errorMessagelbl.setText(request.getString("error-message")
								.substring(request.getString("error-message").indexOf("\n\t") + 2).replace("\r\n", ""));
					}
					requestID = request.getString("request-id");
					pendingCombo.deselectAll();
					if (request.getString("output") != null) {
						updateExceptions = false;
						output.setText(request.getString("output"));
						updateExceptions = true;
					}
					if (request.has("comment")) {
						message.setText(request.getString("comment"));
					} else {
						message.setText("");
					}
					if (request.has("help")) {
						replies = new ArrayList<>();
						for (int i = 0; i < request.getJSONArray("help").length(); i++) {
							replies.add(request.getJSONArray("help").getString(i));
						}
						updateReplies(replies);
					} else {
						if (replies != null) {
							replies.clear();
						}
						reply.setText("");
						btnNext.setEnabled(false);
						btnPrev.setEnabled(false);
					}
				} catch (JSONException e2) {}
			}
		});

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gl_composite = new GridLayout(3, true);
		gl_composite.marginRight = 5;
		gl_composite.marginLeft = 5;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		output = new Text(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		output.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		output.setMessage("Console Output");
		output.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		output.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (updateExceptions) {
					helpListener.exceptionEvent(output.getText());
				}
			}
		});

		message = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		message.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		message.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		message.setMessage("What are you having trouble with?");

		reply = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		reply.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		reply.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		reply.setMessage("Pull to see instructor reply.");

		Composite buttonComposite = new Composite(parent, SWT.NONE);
		GridLayout gl_buttonComposite = new GridLayout(4, false);
		buttonComposite.setLayout(gl_buttonComposite);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Button getHelpButton = new Button(buttonComposite, SWT.NONE);
		getHelpButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!EditorsUI.getPreferenceStore().getBoolean(Preferences.CONNECT_TO_SERVER)) {
					popupMessage("Error", NOT_CONNECTED);
					return;
				}
				if (termCombo.getText().equals("")) {
					popupMessage("Error", "Please select a term.");
				} else if (courseCombo.getText().equals("")) {
					popupMessage("Error", "Please select a course.");
				} else if (assignCombo.getText().equals("")) {
					popupMessage("Error", "Please select an assignment.");
				} else if (problemCombo.getText().equals("")) {
					popupMessage("Error", "Please select a problem.");
				} else if (errorCombo.getText().equals("")) {
					popupMessage("Error", "Please select an error.");
				} else {
					try {
						if (helpListener != null) {
							int index = errorCombo.getSelectionIndex();
							JSONObject request = null;
//							if (index < errorRequests.size() && errorRequests.get(index) != null) {
//								if (selected) {
//									if (index > 0) {
//										requestID = errorRequests.get(index-1).getString("request-id");
//									}
//								} else {
//									requestID = errorRequests.get(index).getString("request-id");
//								}
//							}
							if (index < exceptions.size()) {
								request = helpListener.getHelp(emailText.getText(), courseCombo.getText(), assignCombo.getText(),
										errorCombo.getText(), exceptions.get(index), problemCombo.getText(),
										termCombo.getText(), requestID, output.getText(), languageCombo.getText());
//								if (request != null && errorRequests.get(index) != null) {
//									errorRequests.set(index, request);
//								}
							} else {
								request = helpListener.getHelp(emailText.getText(), courseCombo.getText(), assignCombo.getText(),
										errorCombo.getText(), errorMessagelbl.getText(), problemCombo.getText(),
										termCombo.getText(), requestID, output.getText(), languageCombo.getText());
							}
							
						}
					} catch (IOException e1) {
						popupMessage("Error", "Connection Failed");
					}
				}
			}
		});
		GridData gd_getHelpButton = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_getHelpButton.widthHint = 100;
		getHelpButton.setLayoutData(gd_getHelpButton);
		getHelpButton.setText("Get Help");
		Button requestHelpButton = new Button(buttonComposite, SWT.PUSH);
		GridData gd_requestHelpButton = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_requestHelpButton.widthHint = 100;
		requestHelpButton.setLayoutData(gd_requestHelpButton);
		requestHelpButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				MessageConsoleStream out = findConsole("debugRequestHelp").newMessageStream();
//				out.println("Request Help Pressed");
				if (!EditorsUI.getPreferenceStore().getBoolean(Preferences.CONNECT_TO_SERVER)) {
					popupMessage("Error", NOT_CONNECTED);
					return;
				}
				if (termCombo.getText().equals("")) {
					popupMessage("Error", "Please select a term.");
				} else if (courseCombo.getText().equals("")) {
					popupMessage("Error", "Please select a course.");
				} else if (assignCombo.getText().equals("")) {
					popupMessage("Error", "Please select an assignment.");
				} else if (problemCombo.getText().equals("")) {
					popupMessage("Error", "Please select a problem.");
				} else if (errorCombo.getText().equals("")) {
					popupMessage("Error", "Please select an error.");
				} else if (message.getText().equals("")) {
					popupMessage("Error", "Please describe the difficulty you are facing.");
				} else {
					try {
//						out.println("all checks are good");
						if (helpListener != null) {
//							out.println("help listener is not null");
							int index = errorCombo.getSelectionIndex();
							JSONObject request = null;
							if (index < exceptions.size()) {
								request = helpListener.requestHelp(emailText.getText(), courseCombo.getText(),
										assignCombo.getText(), errorCombo.getText(), exceptions.get(index),
										problemCombo.getText(), termCombo.getText(), difficultySelected,
										message.getText(), requestID, output.getText(), languageCombo.getText());
								if (request != null && errorRequests.get(index) == null) {
									errorRequests.set(index, request);
									requestID = request.getString("request-id");
								}
							} else {
								request = helpListener.requestHelp(emailText.getText(), courseCombo.getText(),
										assignCombo.getText(), errorCombo.getText(), errorMessagelbl.getText(),
										problemCombo.getText(), termCombo.getText(), difficultySelected,
										message.getText(), requestID, output.getText(), languageCombo.getText());
								if (errorCombo.getText().equals("Other") && request != null) {
									errorRequests.set(index, request);
									requestID = request.getString("request-id");
								}
							}
							popupMessage("Info", "Request Sent");
						}
					} catch (IOException e1) {
						popupMessage("Error", "Connection Failed");
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		requestHelpButton.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		requestHelpButton.setText("Request Help");

		btnPrev = new Button(buttonComposite, SWT.PUSH);
		GridData gd_btnPrev = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_btnPrev.widthHint = 100;
		btnPrev.setLayoutData(gd_btnPrev);
		btnPrev.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (replies != null) {
					if (index <= 0) {
						index = 0;
					} else {
						index--;
						btnNext.setEnabled(true);
						if (index == 0) {
							btnPrev.setEnabled(false);
						}
					}
					reply.setText(replies.get(index));
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
				if (replies != null) {
					if (index >= replies.size() - 1) {
						index = replies.size() - 1;
					} else {
						index++;
						btnPrev.setEnabled(true);
						if (index == replies.size() - 1) {
							btnNext.setEnabled(false);
						}
					}
					reply.setText(replies.get(index));
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

		new HelpViewController(this);
		pendingRequests = new ArrayList<>();
		repliedRequests = new ArrayList<>();
		errorRequests = new ArrayList<>();
		if (!pendingFolder.exists()) {
			pendingFolder.mkdirs();
		}
		if (!repliedFolder.exists()) {
			repliedFolder.mkdirs();
		}
		populateParams();
		readRequests();
		HelpPreferences.getInstance().addListener(this);
	}

	@Override
	public void setFocus() {
	}

	public void populateParams() {
//		try {
//			JSONObject response = HTTPRequest.post(new JSONObject().put("parameter", ""), getParamURL);
//			JSONArray param = response.getJSONArray("parameter");
//			System.out.println(response.toString(4));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
		populateTermCombo();
		populateCourseCombo();
		populateAssignCombo();
		populateProblemCombo();
		populateLanguageCombo();
	}

	private void readRequests() {
		try {
			for (File file : pendingFolder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".json");
				}
			})) {
				StringBuilder sb = new StringBuilder();
				String line;
				BufferedReader reader = new BufferedReader(new FileReader(file));

				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				reader.close();
				JSONObject request = new JSONObject(sb.toString());
				pendingRequests.add(request);
				pendingCombo.add(request.getString("request-id"));
			}
			for (File file : repliedFolder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".json");
				}
			})) {
				StringBuilder sb = new StringBuilder();
				String line;
				BufferedReader reader = new BufferedReader(new FileReader(file));

				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				reader.close();
				JSONObject request = new JSONObject(sb.toString());
				repliedRequests.add(request);
				repliedCombo.add(request.getString("request-id"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void updateReplies(List<String> list) {
		replies = list;
		index = 0;
		if (list.size() == 0) {
			btnPrev.setEnabled(false);
			btnNext.setEnabled(false);
			reply.setText("No help available");
			return;
		}
		reply.setText(list.get(0));
		btnPrev.setEnabled(false);
		if (replies.size() > 1) {
			btnNext.setEnabled(true);
		} else {
			btnNext.setEnabled(false);
		}
	}

	private void populateTermCombo() {
//		termCombo.setItems(TERMS);
		for (int i = 0; i < HelpPreferencePage.TERMS.length; i++) {
			termCombo.add(HelpPreferencePage.TERMS[i][0]);
			if (HelpPreferencePage.TERMS[i][0].equals(EditorsUI.getPreferenceStore().getString(HelpPreferences.TERM))) {
				termCombo.select(i);
			}
		}
	}
	
	private void populateLanguageCombo() {
//		termCombo.setItems(TERMS);
		for (int i = 0; i < HelpPreferencePage.LANGUAGES.length; i++) {
			languageCombo.add(HelpPreferencePage.LANGUAGES[i][0]);
			if (HelpPreferencePage.LANGUAGES[i][0].equals(EditorsUI.getPreferenceStore().getString(HelpPreferences.LANGUAGE))) {
				languageCombo.select(i);
				helpListener.setExceptionMatcher(languageCombo.getText());
			}
		}
	}

	private void populateCourseCombo() {
//		courseCombo.setItems(COURSES);
		for (int i = 0; i < HelpPreferencePage.COURSES.length; i++) {
			courseCombo.add(HelpPreferencePage.COURSES[i][0]);
			if (HelpPreferencePage.COURSES[i][0]
					.equals(EditorsUI.getPreferenceStore().getString(HelpPreferences.COURSE))) {
				courseCombo.select(i);
			}
		}
	}

	private void populateAssignCombo() {
//		assignCombo.setItems(ASSIGNMENTS[courseCombo.getSelectionIndex()]);
		for (int i = 0; i < HelpPreferencePage.ASSIGNMENTS.length; i++) {
			assignCombo.add(HelpPreferencePage.ASSIGNMENTS[i][0]);
			if (HelpPreferencePage.ASSIGNMENTS[i][0]
					.equals(EditorsUI.getPreferenceStore().getString(HelpPreferences.ASSIGNMENT))) {
				assignCombo.select(i);
			}
		}
	}

	private void filterRequests() {
		filterPendingCombo(termCombo.getText(), courseCombo.getText(), assignCombo.getText(),
				problemCombo.getText(), languageCombo.getText());
		filterRepliedCombo(termCombo.getText(), courseCombo.getText(), assignCombo.getText(),
				problemCombo.getText(), languageCombo.getText());
	}
	
	private void filterPendingCombo(String term, String course, String assign, String problem, String language) {
		pendingCombo.removeAll();
		for (JSONObject request : filter(pendingRequests, term, course, assign, problem, language)) {
			try {
				pendingCombo.add(request.getString("request-id"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private List<JSONObject> filter(List<JSONObject> requests, String term, String course, String assign, String problem, String language) {
		List<JSONObject> retVal = new ArrayList<>();
		try {
			for (int i = 0; i < requests.size(); i++) {
				JSONObject request = requests.get(i);
				if ((term.isEmpty() || request.getString("term").equalsIgnoreCase(term)) 
						&& (course.isEmpty() || request.getString("course").equalsIgnoreCase(course))
						&& (assign.isEmpty() || request.getString("assignment").equalsIgnoreCase(assign))
						&& (problem.isEmpty() || request.getString("problem").equalsIgnoreCase(problem))
						&& (language.isEmpty() || request.getString("language").equalsIgnoreCase(language))) {
					retVal.add(request);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retVal;
	}

	private void filterRepliedCombo(String term, String course, String assign, String problem, String language) {
		repliedCombo.removeAll();
		for (JSONObject request : filter(repliedRequests, term, course, assign, problem, language)) {
			try {
				repliedCombo.add(request.getString("request-id"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void populateProblemCombo() {
//		problemCombo.setItems(PROBLEMS);
		for (int i = 0; i < HelpPreferencePage.PROBLEMS.length; i++) {
			problemCombo.add(HelpPreferencePage.PROBLEMS[i][0]);
			if (HelpPreferencePage.PROBLEMS[i][0]
					.equals(EditorsUI.getPreferenceStore().getString(HelpPreferences.PROBLEM))) {
				problemCombo.select(i);
			}
		}
	}

	public void populateErrorCombo(List<String> exceptions) {
		this.exceptions = exceptions;
		if (selected) {
			String ex = errorCombo.getText();
			errorCombo.removeAll();
			errorCombo.add(ex);
			errorCombo.select(0);
		} else {
			errorCombo.removeAll();
		}
		errorRequests.clear();
		
		for (String exception : this.exceptions) {
			switch (languageCombo.getText()) {
			case "java":
				errorCombo.add(JavaExceptionMatcher.getInstance().getException(exception));
				break;
			case "prolog":
				errorCombo.add(PrologExceptionMatcher.getInstance().getException(exception));
				break;
			case "SML":
				errorCombo.add(SMLExceptionMatcher.getInstance().getException(exception));
				break;
			default:
				break;
			}
			errorRequests.add(null);
		}
		errorRequests.add(null);
		for (String localcheck : localcheckErrors) {
			errorCombo.add(localcheck);
		}
		errorCombo.add("Other");
	}

	private void popupMessage(String title, String text) {
		JOptionPane optionPane = new JOptionPane(text, JOptionPane.WARNING_MESSAGE);
		JDialog dialog = optionPane.createDialog(title);
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
	}

	public File getPendingFolder() {
		return pendingFolder;
	}

	public File getRepliedFolder() {
		return repliedFolder;
	}

	public void addPendingRequest(JSONObject request) {
		try {
			for (int i = 0; i < pendingRequests.size(); i++) {
				if (pendingRequests.get(i).getString("request-id").equals(request.getString("request-id"))) {
					pendingRequests.remove(i);
					pendingCombo.remove(i);
					break;
				}
			}
			pendingRequests.add(request);
//			pendingCombo.add(request.getString("request-id")+":"+request.getString("error-type"));
			pendingCombo.add(request.getString("request-id"));
			pendingCombo.setText(request.getString("request-id"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void addRepliedRequest(JSONObject request) {
		try {
			for (int i = 0; i < repliedRequests.size(); i++) {
				if (repliedRequests.get(i).getString("request-id").equals(request.getString("request-id"))) {
					repliedRequests.remove(i);
					repliedCombo.remove(i);
					break;
				}
			}
			repliedRequests.add(request);
//			repliedCombo.add(request.getString("request-id")+":"+request.getString("error-type"));
			repliedCombo.add(request.getString("request-id"));
			repliedCombo.setText(request.getString("request-id"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void removePendingRequest(String id) {
		try {
			for (int i = 0; i < pendingRequests.size(); i++) {
				if (pendingRequests.get(i).getString("request-id").equals(id)) {
					pendingRequests.remove(i);
					pendingCombo.remove(i);
					pendingCombo.deselectAll();
					return;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void removeRepliedRequest(String id) {
		try {
			for (int i = 0; i < repliedRequests.size(); i++) {
				if (repliedRequests.get(i).getString("request-id").equals(id)) {
					repliedRequests.remove(i);
					repliedCombo.remove(i);
					repliedCombo.deselectAll();
					return;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void updateConsoleOutput(String output) {
		this.output.setText(output);
	}

	class DifficultyButtonHandler extends SelectionAdapter {

		int difficulty;

		public DifficultyButtonHandler(int difficulty) {
			this.difficulty = difficulty;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			Button b = (Button) e.getSource();
			if (b.getSelection()) {
				difficultySelected = difficulty;
			}
		}
	}

	public void preferencesUpdated() {
		populateParams();
	}


	public static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		//no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[]{myConsole});
		return myConsole;
	}
}