package workspacelistener;

import org.eclipse.core.runtime.IPath;

public abstract class FileUpdate {

	protected String filePath;
	protected IPath iFilePath;
	
	public FileUpdate(IPath iFilePath) {
		this.iFilePath = iFilePath;
		this.filePath = iFilePath.toString();
	}
	
	public IPath getIFilePath() {
		return iFilePath;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
}