package workspaceConnectionManager;

import java.util.HashMap;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.editors.text.EditorsUI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import workspacelistener.Delta;
import workspacelistener.NewFileContents;
import workspacelistener.WorkspaceFileListener;
import workspacelistener.WorkspaceListener;
import workspacelistener.ui.PrivacyView;
import dayton.ellwanger.hermes.xmpp.ConnectionManager;
import hermes.tags.Tags;
import util.trace.hermes.workspacelistener.FileForwardedToConnectionManager;
//import util.trace.messagebus.clients.JSONObjectForwardedToConnectionManager;
import util.trace.hermes.workspacelistener.WorkspaceListenerTraceUtility;

public class WorkspaceConnectionManager implements WorkspaceFileListener {
	static String activeDocumentName = "";

	private String workspaceString;
	protected String workspacePath;
	private WorkspaceListener workspaceListener;
	// do not think we need the pending variables
	private HashMap<String, DeltaContentSender> pendingContentChanges;
	protected HashMap<String, JSONContentSender> pendingJSONChanges;

	

	public WorkspaceConnectionManager() {
		EditorsUI.getPreferenceStore().setDefault(PrivacyView.PRIVACY_PREFERENCE, 1);
		pendingContentChanges = new HashMap<String, DeltaContentSender>();
		pendingJSONChanges = new HashMap<String, JSONContentSender>();
		workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
//		workspaceString = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString().replace("/", ".");
		workspaceString = workspacePath.replace("/", ".");

		workspaceListener = new WorkspaceListener();
		workspaceListener.addWorkspaceFileListener(this);
		// WorkspaceListenerTraceUtility.setTracing();
	}
	public JSONObject toJSONContent(NewFileContents newFileContents) {

		int privacySetting = EditorsUI.getPreferenceStore().getInt(PrivacyView.PRIVACY_PREFERENCE);

		if (ConnectionManager.getInstance() != null) {
			JSONObject messageData = new JSONObject();
			try {
				messageData.put("type", "editorContents");
//				messageData.put("filename", workspaceString + newFileContents.getFilePath());
				messageData.put(Tags.FILE_NAME, workspaceString + newFileContents.getFilePath());
				messageData.put(Tags.ABSOLUTE_FILE_NAME, workspacePath + newFileContents.getFilePath());
				messageData.put(Tags.RELATIVE_FILE_NAME, newFileContents.getFilePath());
				messageData.put("contents", newFileContents.getContents().replace('\r', ' '));
				messageData.put("isPublic", (privacySetting == 2));
				JSONArray tags = new JSONArray();
//				tags.put("EDITOR_CONTENTS");
				tags.put(Tags.EDITOR_CONTENTS);
				messageData.put(Tags.TAGS_FIELD, tags);
				return messageData;
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		return null;
	}
	public JSONObject toJSONDocumentChange(Delta fileDelta) {


		if (ConnectionManager.getInstance() != null) {
			JSONObject messageData = new JSONObject();
			try {
				messageData.put("type", "editorDoc");
				messageData.put(Tags.RELATIVE_FILE_NAME, fileDelta.getFilePath());
				messageData.put(Tags.DOCUMENT_CHANGE, fileDelta.getChanges());
				JSONArray tags = new JSONArray();
//				tags.put("EDITOR_CONTENTS");
				tags.put(Tags.DOCUMENT_CHANGE);
				messageData.put(Tags.TAGS_FIELD, tags);
				return messageData;
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		return null;
	}

	@Override
	public void newFileContents(NewFileContents newFileContents) {
		int privacySetting = EditorsUI.getPreferenceStore().getInt(PrivacyView.PRIVACY_PREFERENCE);
		if (privacySetting == 0) {
			return;
		}
		if (ConnectionManager.getInstance() != null) {
			JSONObject messageData = new JSONObject();
			try {
				messageData.put("type", "editorContents");
				messageData.put("filename", workspaceString + newFileContents.getFilePath());
				messageData.put("contents", newFileContents.getContents().replace('\r', ' '));
				messageData.put("isPublic", (privacySetting == 2));
				JSONArray tags = new JSONArray();
				// tags.put("EDITOR_CONTENTS");
				tags.put(Tags.EDITOR_CONTENTS);
				messageData.put("tags", tags);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			FileForwardedToConnectionManager.newCase(this, messageData.toString());
			// JSONObjectForwardedToConnectionManager.newCase(this,
			// messageData.toString());

			ConnectionManager.getInstance().sendMessage(messageData);
		}
	}

	public void newJSONContent(JSONObject aJSONObject) {
		int privacySetting = EditorsUI.getPreferenceStore().getInt(PrivacyView.PRIVACY_PREFERENCE);
		if (privacySetting == 0 || aJSONObject == null) {
			return;
		}
		if (ConnectionManager.getInstance() != null) {

			FileForwardedToConnectionManager.newCase(this, aJSONObject.toString());
			// JSONObjectForwardedToConnectionManager.newCase(this,
			// messageData.toString());

			ConnectionManager.getInstance().sendMessage(aJSONObject);
		}
	}

	//
	// @Override
	// public void fileDelta(Delta fileDelta) {
	// String aFileName = workspaceString + fileDelta.getFilePath();
	// setActiveDocumentName(aFileName);
	// if(ConnectionManager.getInstance() != null) {
	// DeltaContentSender cs =
	// pendingContentChanges.get(fileDelta.getFilePath());
	// if(cs == null) {
	// cs = new DeltaContentSender(fileDelta);
	// pendingContentChanges.put(fileDelta.getFilePath(), cs);
	// (new Thread(cs)).start();
	// } else {
	// cs.send = false;
	// cs.fileDelta = fileDelta;
	// }
	// }
	// }
	@Override
	public void fileDelta(Delta fileDelta) {
		String aFileName = workspaceString + fileDelta.getFilePath();
		setActiveDocumentName(aFileName);
		if (ConnectionManager.getInstance() != null) {
			// DeltaContentSender cs =
			// pendingContentChanges.get(fileDelta.getFilePath());
			JSONContentSender aJSONContentSender = pendingJSONChanges.get(aFileName);
			NewFileContents aNewFileContents = new NewFileContents(fileDelta.getIFilePath(),
					fileDelta.getChanges().getDocument().get());
			JSONObject aJSONContent = toJSONContent(aNewFileContents);
			ConnectionManager.getInstance().notifyNewJSONMessage(aJSONContent);
			JSONObject aJSONFileDelta = toJSONDocumentChange(fileDelta);
			ConnectionManager.getInstance().notifyNewJSONMessage(aJSONFileDelta);
			if (aJSONContentSender == null) {

				aJSONContentSender = new JSONContentSender(aJSONContent);
				// pendingContentChanges.put(fileDelta.getFilePath(), cs);
				pendingJSONChanges.put(aFileName, aJSONContentSender);

				(new Thread(aJSONContentSender)).start();
			} else {
				// update existing runnable in caseprevious change has not gone
				aJSONContentSender.send = false;
				aJSONContentSender.jsonObject = aJSONContent;
			}
		}
	}

	class DeltaContentSender implements Runnable {

		boolean send;
		int sleepDelay = 500;
		Delta fileDelta;

		public DeltaContentSender(Delta fileDelta) {
			send = false;
			this.fileDelta = fileDelta;
		}

		public void run() {
			while (!send) {
				send = true;
				try {
					Thread.sleep(sleepDelay);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			pendingContentChanges.remove(fileDelta.getFilePath());
			newFileContents(new NewFileContents(fileDelta.getIFilePath(), fileDelta.getChanges().getDocument().get()));
		}

	}

	class JSONContentSender implements Runnable {

		boolean send;
		int sleepDelay = 500;
		JSONObject jsonObject;

		public JSONContentSender(JSONObject aJSONObject) {
			send = false;
			jsonObject = aJSONObject;
		}

		// This loop should really be in connection manager. It should bufffe
		// changes and send periodically.
		// Or maybe here as some changes should be sent immediately
		// do we really need the pending changes?
		public void run() {
			while (!send) {
				send = true;
				try {
					Thread.sleep(sleepDelay);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			try {
				pendingJSONChanges.remove(jsonObject.get("filename"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newJSONContent(jsonObject); // want to send the current one
			// newFileContents(new NewFileContents(fileDelta.getIFilePath(),
			// fileDelta.getChanges().getDocument().get()));
		}

	}

	public static String getActiveDocumentName() {
		return activeDocumentName;
	}

	public static void setActiveDocumentName(String activeDocumentName) {
		WorkspaceConnectionManager.activeDocumentName = activeDocumentName;
	}

}