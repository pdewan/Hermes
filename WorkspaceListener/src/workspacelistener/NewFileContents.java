package workspacelistener;

import org.eclipse.core.runtime.IPath;

public class NewFileContents extends FileUpdate {

	private String contents;
	
	public NewFileContents(IPath filePath, String contents) {
		super(filePath);
		this.contents = contents;
	}
	
	public String getContents() {
		return contents;
	}
	
}