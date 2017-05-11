package dayton.ellwanger.hermes.preferences.ui;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

import dayton.ellwanger.hermes.preferences.Preferences;

public class SecureStringFieldEditor extends StringFieldEditor {

	public SecureStringFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}
	
	@Override
	public void load() {
		doLoad();
	}

	@Override
	protected void doLoad() {
		setStringValue(Preferences.getPreference(getPreferenceName()));
	}
	
	@Override
	public void loadDefault() {
		doLoadDefault();
	}
	
	@Override
	protected void doLoadDefault() {
		setStringValue("");
	}
	
	@Override
	public void store() {
		doStore();
	}
	
	@Override
	protected void doStore() {
		Preferences.storePreference(getPreferenceName(), getStringValue());
	}
	
}