package dayton.ellwanger.helpbutton.preferences;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.editors.text.EditorsUI;

import dayton.ellwanger.hermes.preferences.Preferences;
import dayton.ellwanger.hermes.preferences.ui.PreferencePage;
import dayton.ellwanger.hermes.preferences.ui.SecureStringFieldEditor;
import dayton.ellwanger.hermes.xmpp.ConnectionManager;

public class HelpPreferencePage extends PreferencePage {

	private static final String DESCRIPTION = "Hermes Help Preferences";
	public static final String[][] TERMS = {{"",""},
											{"2018 Spring", "2018 Spring"},
											{"2018 Fall", "2018 Fall"},
											{"2019 Spring", "2019 Spring"},
											{"2019 Fall", "2019 Fall"},
											{"2020 Spring", "2020 Spring"},
											{"2020 Fall", "2020 Fall"},
											{"2021 Spring", "2021 Spring"},
											{"2021 Summer", "2021 Summer"},
											{"2021 Fall", "2021 Fall"},
											{"2022 Spring", "2022 Spring"}};
	public static final String[][] COURSES = {{"",""},
											  {"COMP301", "COMP301"},
			 								  {"COMP410", "COMP410"}, 
			 								  {"COMP411", "COMP411"},
			 								  {"COMP524", "COMP524"},
			 								  {"COMP533", "COMP533"}};
	public static final String[][] ASSIGNMENTS = {{"",""},
												  {"A0", "A0"},
												  {"A1", "A1"},
			   									  {"A2", "A2"}, 
			   									  {"A3", "A3"},
			   									  {"A4", "A4"},
			   									  {"A5", "A5"},
			   									  {"A6", "A6"},
			   									  {"A7", "A7"},
			   									  {"A8", "A8"},
			   									  {"A9", "A9"},
			   									  {"A10", "A10"}};
	public static final String[][] PROBLEMS = {{"",""},
											   {"1", "1"},
											   {"2", "2"}, 
											   {"3", "3"},
											   {"4", "4"},
											   {"5", "5"},
											   {"6", "6"},
											   {"7", "7"},
											   {"8", "8"},
											   {"9", "9"},
											   {"10", "10"}};
	public static final String[][] LANGUAGES = {{"",""},
												{"java", "java"},
												{"python", "python"},
												{"prolog", "prolog"},
												{"SML", "SML"}};
	
	public boolean performOk() {
		boolean result = super.performOk();
		checkPreferenceFile();
		HelpPreferences.getInstance().preferencesUpdated();
		return result;
	}
	
	private void checkPreferenceFile() {
		String preferenceFile = EditorsUI.getPreferenceStore().getString("PreferenceFile");
		if(preferenceFile.equals("")) {
			return;
		}
		EditorsUI.getPreferenceStore().setValue("PreferenceFile", "");
		try {
			Scanner scanner = new Scanner(new File(preferenceFile));
			while(scanner.hasNext()) {
				boolean secure = scanner.next().contains("secure");
				String type = scanner.next();
				String preference = scanner.next();
				String value = scanner.next();
				if(type.equalsIgnoreCase("boolean")) {
					boolean bValue = Boolean.parseBoolean(value);
					EditorsUI.getPreferenceStore().setValue(preference, bValue);
				} else if (type.equalsIgnoreCase("string")) {
					if(secure) {
						HelpPreferences.storePreference(preference, value);
					} else {
						EditorsUI.getPreferenceStore().setValue(preference, value);
					}
				} else if (type.equalsIgnoreCase("int")) {
					int iValue = Integer.parseInt(value);
					EditorsUI.getPreferenceStore().setValue(preference, iValue);
				}
			}
			scanner.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}

	@Override
	protected void createFieldEditors() {
		StringFieldEditor email = new StringFieldEditor(HelpPreferences.EMAIL, 
				"Email:", getFieldEditorParent());
		FileFieldEditor preferenceFile = new FileFieldEditor("PreferenceFile", "Preference File: ", getFieldEditorParent());
		ComboFieldEditor terms = new ComboFieldEditor(HelpPreferences.TERM, 
				"Term:", TERMS, getFieldEditorParent());
		ComboFieldEditor courses = new ComboFieldEditor(HelpPreferences.COURSE, "Course:", COURSES, getFieldEditorParent());
		ComboFieldEditor assigns = new ComboFieldEditor(HelpPreferences.ASSIGNMENT, 
				"Assignment:", ASSIGNMENTS, getFieldEditorParent());
		ComboFieldEditor problems = new ComboFieldEditor(HelpPreferences.PROBLEM, 
				"Problem:", PROBLEMS, getFieldEditorParent());
		ComboFieldEditor languages = new ComboFieldEditor(HelpPreferences.PROBLEM, 
				"Languages:", LANGUAGES, getFieldEditorParent());
		BooleanFieldEditor connectToServer = new BooleanFieldEditor(Preferences.CONNECT_TO_SERVER, "Connet to Server", getFieldEditorParent());
		BooleanFieldEditor showNotification = new BooleanFieldEditor(Preferences.SHOW_STATUS_NOTIFICATION, "Show Status Notification", getFieldEditorParent());
		BooleanFieldEditor keepNotificationLogs = new BooleanFieldEditor(Preferences.KEEP_NOTIFICATION_LOGS, "Keep Notification Logs", getFieldEditorParent());
		
		setPreferenceStore(EditorsUI.getPreferenceStore());
		addField(preferenceFile);
		addField(email);
		addField(terms);
		addField(courses);
		addField(assigns);
		addField(problems);
		addField(languages);
		addField(connectToServer);
		addField(showNotification);
		addField(keepNotificationLogs);
	}
}