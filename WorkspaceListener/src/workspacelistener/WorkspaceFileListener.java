package workspacelistener;

public interface WorkspaceFileListener {

	public void newFileContents(NewFileContents newFileContents);
	public void fileDelta(Delta fileDelta);
	
}