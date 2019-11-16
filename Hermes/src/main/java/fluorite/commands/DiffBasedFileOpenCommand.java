package fluorite.commands;

import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;

import fluorite.commands.FileOpenCommand;
import fluorite.util.EHUtilities;
import hermes.proxy.Diff_Match_Patch_Proxy;
import fluorite.model.DiffBasedFileSnapshotManager;

public class DiffBasedFileOpenCommand extends FileOpenCommand {
	
	public DiffBasedFileOpenCommand(){}
	
	public DiffBasedFileOpenCommand(IEditorPart editor) {
		super(editor);
		addDiff(editor);
	}
	
	private String diff;

	private void addDiff(IEditorPart editor) {
		if (editor == null) {
			return;
		}
		String filePath = getFilePath();
		IDocument aDocument = EHUtilities.getDocument(editor);
		if (aDocument == null) {
			return;
		}
//		String content = EHUtilities.getDocument(editor).get();
		String content = aDocument.get();

//		calcNumericalValues(content);
		String oldContent = DiffBasedFileSnapshotManager.getInstance().getSnapshot(filePath);
		if (oldContent == null) {
			diff = "null";
		} else {
			diff = Diff_Match_Patch_Proxy.diff(oldContent, content);
		}
		DiffBasedFileSnapshotManager.getInstance().updateSnapshot(filePath, content);
		
	}
	
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = super.getDataMap();
		if (diff != null) {
			dataMap.put("diff", diff);
		} else {
			dataMap.put("diff", "null");
		}
		
		return dataMap;			
	}
	
	public String getDiff(){
		return diff;
	}
	
	public String getCommandType(){
		return "DiffBasedFileOpenCommand";
	}
}
