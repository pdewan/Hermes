package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fluorite.model.EHEventRecorder;
import fluorite.model.FileSnapshotManager;
import fluorite.util.CurrentProjectHolder;
import fluorite.util.EHUtilities;

public class FileOpenCommand extends 
BaseDocumentChangeEvent 
/*
 * Cannot extend FileOpenCommand as it is not extension of EHBaseDocumentChangeEvent
 */
//FileOpenCommand
implements EHICommand {
	
	protected static final String[] EXTENSIONS = {".java", ".pl", ".py", ".sml"};
	
	public FileOpenCommand() {
	}

	public FileOpenCommand(IEditorPart editor) {
//		super(editor);
		initialize(editor);
	}

	private void initialize(IEditorPart editor) {
		IEditorInput input = editor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			try {
				IFileEditorInput fileInput = (IFileEditorInput) input;
				IFile file = fileInput.getFile();
				IProject project = file.getProject();
				mProjectName = project.getName();
				CurrentProjectHolder.setProject(project);
				mFilePath = fileInput.getFile().getLocation().toOSString();

				String content = EHUtilities.getDocument(editor).get();
				if (mFilePath.endsWith(".java")) {
					calcNumericalValues(content);
				} else if (record()) {
					Map<String, Integer> numericalValues = new HashMap<>();
					numericalValues.put("docLength", content.length());
					setNumericalValues(numericalValues);
				}

				// Snapshot
				if (record() && !FileSnapshotManager.getInstance().isSame(mFilePath,
						content)) {
					mSnapshot = content;
					FileSnapshotManager.getInstance().updateSnapshot(mFilePath,
							content);
				} else {
					mSnapshot = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected boolean record() {
		if (mFilePath == null) {
			return false;
		}
		for (String ext : EXTENSIONS) {
			if (mFilePath.endsWith(ext)) {
				return true;
			}
		}
		return false;
	}

	private String mFilePath;
	private String mProjectName;
	private String mSnapshot;

	public boolean execute(IEditorPart target) {
		// Not supported yet

		// IWorkbenchWindow window =
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		// if (window == null) { return false; }
		//
		// IWorkbenchPage page = window.getActivePage();
		// IEditorReference[] editorReferences = page.getEditorReferences();
		return false;
	}

	public void dump() {
		// TODO Auto-generated method stub

	}

	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("projectName", mProjectName == null ? "null" : mProjectName);

		Map<String, Integer> numericalValues = getNumericalValues();
		if (numericalValues != null) {
			for (Map.Entry<String, Integer> pair : numericalValues.entrySet()) {
				attrMap.put(pair.getKey(), Integer.toString(pair.getValue()));
			}
		}

		return attrMap;
	}

	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("filePath", mFilePath == null ? "null" : mFilePath);
		dataMap.put("snapshot", mSnapshot == null ? "null" : mSnapshot);
//		if (mSnapshot != null) {
//			dataMap.put("snapshot", mSnapshot);
//		}

		return dataMap;
	}

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr attr = null;
		String value = null;
		NodeList nodeList = null;
		
		if ((attr = commandElement.getAttributeNode("projectName")) != null) {
			value = attr.getValue();
			mProjectName = value.equals("null") ? null : value;
		}
		else {
			mProjectName = null;
		}
		
		if ((nodeList = commandElement.getElementsByTagName("filePath")).getLength() > 0) {
			Node textNode = nodeList.item(0);
			value = textNode.getTextContent();
			mFilePath = value.equals("null") ? null : value;
		}
		else {
			mFilePath = null;
		}
		
		if ((nodeList = commandElement.getElementsByTagName("snapshot")).getLength() > 0) {
			Node textNode = nodeList.item(0);
			value = textNode.getTextContent();
//			if (getTimestamp() == 3134229) {
//				System.out.println("*****found offending time stamp");
//			}
			Map<String, Integer> aNumericalValues = getNumericalValues();
			if (aNumericalValues == null) {
//				System.out.println("aNumercial Values== null " + getTimestamp());
				mSnapshot = null;
			} else {
				Integer aDocLengthValue = aNumericalValues.get("docLength");
				if (aDocLengthValue == null) {
//					System.out.println("aDocLengthValue == null " + getTimestamp());
					mSnapshot = null;
				} else {
					mSnapshot = normalizeText(value, aDocLengthValue);
					
				}
			}	
			
//			mSnapshot = normalizeText(value, getNumericalValues().get("docLength"));
		}
		else {
			mSnapshot = null;
		}
	}

	public String getCommandType() {
		return "FileOpenCommand";
	}

	public String getName() {
		return "File Open: \"" + mFilePath + "\"";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCategory() {
		return EHEventRecorder.MacroCommandCategory;
	}

	public String getCategoryID() {
		return EHEventRecorder.MacroCommandCategoryID;
	}
	
	public String getSnapshot() {
		return mSnapshot;
	}
	
	public String getFilePath() {
		return mFilePath;
	}
	
	public String getProjectName() {
		return mProjectName;
	}

	public boolean combine(EHICommand anotherCommand) {
		return false;
	}

	@Override
	public void applyToDocument(IDocument doc) {
		if (getSnapshot() != null) {
			doc.set(getSnapshot());
		}
	}
	
	public String toString() {
		return getName();
	}
}
