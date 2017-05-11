package dayton.ellwanger.hermes.preferences.ui;

import org.eclipse.swt.widgets.Composite;

public class PasswordFieldEditor extends SecureStringFieldEditor {
	
	private static final char PASSWORD_CHARACTER = '•';

	public PasswordFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}
	
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns);
		getTextControl().setEchoChar(PasswordFieldEditor.PASSWORD_CHARACTER);
	}
	
}