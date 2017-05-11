package dayton.ellwanger.hermes.preferences.ui;

import java.io.File;
import java.util.Scanner;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.eclipse.ui.editors.text.EditorsUI;

import dayton.ellwanger.hermes.preferences.Preferences;
import dayton.ellwanger.hermes.xmpp.ConnectionManager;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final String DESCRIPTION = "Hermes Preferences";
	
	public PreferencePage() {
		super(FieldEditorPreferencePage.GRID);
	}
	
	@Override
	public boolean isValid() {
		return true;
	}
	
	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		checkPreferenceFile();
		Preferences.getInstance().preferencesUpdated();
		if(EditorsUI.getPreferenceStore().getBoolean(Preferences.AUTOLOGIN)) {
			ConnectionManager.getInstance().doConnect();
		}
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
						Preferences.getInstance().storePreference(preference, value);
					} else {
						EditorsUI.getPreferenceStore().setValue(preference, value);
					}
				} else if (type.equalsIgnoreCase("int")) {
					int iValue = Integer.parseInt(value);
					EditorsUI.getPreferenceStore().setValue(preference, iValue);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void init(IWorkbench workbench) {
		setDescription(DESCRIPTION);
	}

	@Override
	protected void createFieldEditors() {	
		FileFieldEditor preferenceFile = new FileFieldEditor("PreferenceFile", "Preference File: ", getFieldEditorParent());
		SecureStringFieldEditor username = new SecureStringFieldEditor(Preferences.USERNAME, 
				"Username:", getFieldEditorParent());
		PasswordFieldEditor password = new PasswordFieldEditor(Preferences.PASSWORD, 
				"Password:", getFieldEditorParent());
		SecureStringFieldEditor domain = new SecureStringFieldEditor(Preferences.DOMAIN, "Domain:", getFieldEditorParent());
		SecureStringFieldEditor host = new SecureStringFieldEditor(Preferences.HOST, 
				"Host:", getFieldEditorParent());
		SecureStringFieldEditor instructor = new SecureStringFieldEditor(Preferences.INSTRUCTOR, 
				"Instructor:", getFieldEditorParent());
		BooleanFieldEditor security = new BooleanFieldEditor(Preferences.SECURITY, "Security", getFieldEditorParent());
		BooleanFieldEditor createAccount = new BooleanFieldEditor(Preferences.CREATE, "Create Account", getFieldEditorParent());
		BooleanFieldEditor autoLogin = new BooleanFieldEditor(Preferences.AUTOLOGIN, "Auto Login", getFieldEditorParent());
	
		setPreferenceStore(EditorsUI.getPreferenceStore());
		
		addField(preferenceFile);
		addField(username);
		addField(domain);
		addField(password);
		addField(host);
		addField(instructor);
		addField(security);
		addField(createAccount);
		addField(autoLogin);
	}

}