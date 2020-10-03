package dayton.ellwanger.helpbutton.preferences;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import dayton.ellwanger.hermes.preferences.PreferencesListener;


public class HelpPreferences {

	protected static final String STORE_NODE = "hermes";
	
	public static final String EMAIL = "email";
	public static final String TERM = "Term";
	public static final String COURSE = "course";
	public static final String ASSIGNMENT = "assignment";
	public static final String PROBLEM = "problem";
	public static final String LANGUAGE = "language";
	
	private static HelpPreferences instance;
	private List<PreferencesListener> listeners;
	
	private HelpPreferences() {
		listeners = new LinkedList<PreferencesListener>();
	}
	
	public static HelpPreferences getInstance() {
		if(instance == null) {
			instance = new HelpPreferences();
		}
		return instance;
	}
	
	public void addListener(PreferencesListener l) {
		listeners.add(l);
	}
	
	public void removeListener(PreferencesListener l) {
		listeners.remove(l);
	}
	
	public void preferencesUpdated() {
		for(PreferencesListener l : listeners) {
			l.preferencesUpdated();
		}
	}
	/*
	 * Andrew's code
	 */
	public static String readPreference(ISecurePreferences node, String name, String defaultValue) {
		try {
			Object pref = node.get(name, defaultValue);
			if(pref instanceof String) {
				return (String)pref;
			} else {
				return defaultValue;
			}
		} catch (StorageException e1) {
			e1.printStackTrace();
			return defaultValue;
		}
	}
	/*
	 * Using Andrew's code
	 */
//	public static String getOnyen() {
//		// Andrew onyen code
//		ISecurePreferences root = SecurePreferencesFactory.getDefault();
//		final ISecurePreferences node = root.node("/com/unc");
//
//		// gets onyen or null
//		return HelpPreferences.readPreference(node, "onyen", "");
//
//		//
//	}
	
	
	public static String getPreference(String preferenceName) {
		
		ISecurePreferences prefs = SecurePreferencesFactory.getDefault();
		if(prefs.nodeExists(HelpPreferences.STORE_NODE)) {
			ISecurePreferences node = prefs.node(HelpPreferences.STORE_NODE);
			try {
				String aRetVal = node.get(preferenceName, "");
				return aRetVal;
//				return node.get(preferenceName, "");
			} catch (StorageException ex) {
				ex.printStackTrace();
			}
		}
		return "";
	}
	
	public static void storePreference(String preferenceName, String value) {
		ISecurePreferences prefs = SecurePreferencesFactory.getDefault();
		ISecurePreferences node = prefs.node(HelpPreferences.STORE_NODE);
		try {
			node.put(preferenceName, value, true);
		} catch (StorageException ex) {}
	}
	
	/*
	 * Probably needs to be in some configuration file
	 */
	
//    protected static boolean uncSetup = true;
//	
//	public static boolean isUncSetup() {
//		return uncSetup;
//	}
//
//	public static void setUncSetup(boolean uncSetup) {
//		HelpPreferences.uncSetup = uncSetup;
//	}

	
}

