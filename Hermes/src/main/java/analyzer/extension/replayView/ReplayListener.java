package analyzer.extension.replayView;

public interface ReplayListener {
	public void forward(String numStep, String step);
	public void back(String numStep, String step);
	public void jumpTo(int index, String step);
}
