package analyzer.extension.replayView;

import org.eclipse.core.resources.IProject;

public interface ReplayListener {
	public void forward(String numStep, String step);
	public void back(String numStep, String step);
	public void jumpTo(int index, String step);
	public void updateTimeSpent(String path, String time);
	public void zipCurrentProject(IProject path);
}
