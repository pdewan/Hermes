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
//	/*
//	 * Andrew's code
//	 */
//	public static String readPreference(ISecurePreferences node, String name, String defaultValue) {
//		try {
//			Object pref = node.get(name, defaultValue);
//			if(pref instanceof String) {
//				return (String)pref;
//			} else {
//				return defaultValue;
//			}
//		} catch (StorageException e1) {
//			e1.printStackTrace();
//			return defaultValue;
//		}
//	}
	
//	public static String getOnyen() {
//		// Andrew onyen code
//		ISecurePreferences root = SecurePreferencesFactory.getDefault();
//		final ISecurePreferences node = root.node("/com/unc");
//
//		// gets onyen or null
//		return Preferences.readPreference(node, "onyen", "");
//
//		//
//	}
	
	

	@Override
	protected void createFieldEditors() {
		
//		String anOnyen =  getOnyen();
//		System.out.println("Saved onyen:" + getOnyen());
		
		SecureStringFieldEditor aGoogleId = new SecureStringFieldEditor(Preferences.GOOGLEID, 
				"GoogleId:", getFieldEditorParent());
//		aGoogleId.setStringValue(anOnyen);
//		aGoogleId.setEnabled(false, getFieldEditorParent());
		FileFieldEditor preferenceFile = new FileFieldEditor("PreferenceFile", "Preference File: ", getFieldEditorParent());
		SecureStringFieldEditor username = new SecureStringFieldEditor(Preferences.USERNAME, 
				"Username:", getFieldEditorParent());
		username.setEnabled(false, getFieldEditorParent());
		PasswordFieldEditor password = new PasswordFieldEditor(Preferences.PASSWORD, 
				"Password:", getFieldEditorParent());
		SecureStringFieldEditor domain = new SecureStringFieldEditor(Preferences.DOMAIN, "Domain:", getFieldEditorParent());
		SecureStringFieldEditor host = new SecureStringFieldEditor(Preferences.HOST, 
				"Host:", getFieldEditorParent());
		SecureStringFieldEditor instructor = new SecureStringFieldEditor(Preferences.MESSAGE_BUS, 
				"Message Bus Account:", getFieldEditorParent());
		BooleanFieldEditor security = new BooleanFieldEditor(Preferences.SECURITY, "Security", getFieldEditorParent());
		BooleanFieldEditor createAccount = new BooleanFieldEditor(Preferences.CREATE, "Create Account", getFieldEditorParent());
		BooleanFieldEditor autoLogin = new BooleanFieldEditor(Preferences.AUTOLOGIN, "Auto Login", getFieldEditorParent());
	
		setPreferenceStore(EditorsUI.getPreferenceStore());
		
		addField(preferenceFile);
		//PD
		addField(aGoogleId);
		//
		addField(username);
		addField(domain);
		addField(password);
		addField(host);
		addField(instructor);
		addField(security);
		addField(createAccount);
		addField(autoLogin);
		
		if (Preferences.isUncSetup()) {
			username.setEnabled(false, getFieldEditorParent());
			host.setEnabled(false, getFieldEditorParent());
			instructor.setEnabled(false, getFieldEditorParent());
			security.setEnabled(false, getFieldEditorParent());
			createAccount.setEnabled(false, getFieldEditorParent());			
		}
		
		
	}

}