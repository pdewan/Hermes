package workspacelistener.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.editors.text.EditorsUI;

import dayton.ellwanger.hermes.SubView;

public class PrivacyView implements SubView {
	
	public static String PRIVACY_PREFERENCE = "fileprivacy";

	@Override
	public void createPartControl(Composite parent) {
		EditorsUI.getPreferenceStore().setDefault(PRIVACY_PREFERENCE, 1);
		int privacySetting = EditorsUI.getPreferenceStore().getInt(PRIVACY_PREFERENCE);
		
		RowLayout parentLayout = new RowLayout();
		parentLayout.type = SWT.HORIZONTAL;
		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		parentLayout.pack = true;
		parent.setLayout(parentLayout);
		
		Label privacyLabel = new Label(parent, SWT.NONE);
		privacyLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		privacyLabel.setText("File Privacy: ");
		String[] privacyLabels = {"Private", "Instructor", "Public"};
		for(int i = 0; i < privacyLabels.length; i++) {
			Button button = new Button(parent, SWT.RADIO);
			button.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			button.setText(privacyLabels[i]);
			if(i == privacySetting) {
				button.setSelection(true);
			}
			button.addSelectionListener(new PrivacyButtonHandler(i));
		}
	}

	class PrivacyButtonHandler extends SelectionAdapter {

		int privacy;

		public PrivacyButtonHandler(int privacy) {
			this.privacy = privacy;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			Button b = (Button) e.getSource();
			if(b.getSelection()) {
				EditorsUI.getPreferenceStore().setValue(PRIVACY_PREFERENCE, privacy);
			}
		}
	}
	
}
