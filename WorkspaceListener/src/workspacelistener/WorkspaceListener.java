package workspacelistener;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class WorkspaceListener implements WorkspaceFileListener {
	
	private List<WorkspaceFileListener> listeners;

	public WorkspaceListener() {
		listeners = new LinkedList<WorkspaceFileListener>();
		PlatformUI.getWorkbench().getDisplay().asyncExec(new AddListener(this));
	}
	
	public void addWorkspaceFileListener(WorkspaceFileListener listener) {
		listeners.add(listener);
	}
	
	public void removeWorkspaceFileListener(WorkspaceFileListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void newFileContents(NewFileContents newFileContents) {
		for(WorkspaceFileListener l : listeners) {
			l.newFileContents(newFileContents);
		}
	}

	@Override
	public void fileDelta(Delta fileDelta) {
		for(WorkspaceFileListener l : listeners) {
			l.fileDelta(fileDelta);
		}
	}
	
	class AddListener implements Runnable {
		
		WorkspaceListener wsl;
		
		public AddListener(WorkspaceListener wsl) {
			this.wsl = wsl;
		}
		
		public void run() {
			for(IWorkbenchPage p : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()) {
				p.addPartListener(new EditorListener(wsl, p.getActivePart()));
			}
		}
	}
}
