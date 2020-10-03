package analyzer.extension.replayView;

import java.util.ArrayList;
import analyzer.AnAnalyzer;
import analyzer.Analyzer;
import difficultyPrediction.DifficultyPredictionSettings;
import fluorite.commands.EHICommand;


public class ReplayViewController implements ReplayListener{
	private Analyzer analyzer;
	private ReplayView replayView; 
	private AReplayer replayer;
	
	public ReplayViewController(ReplayView view) {
		replayView = view;
//		view.addReplayListener(this);
		if (replayer == null || analyzer == null) {
			analyzer = new AnAnalyzer();
			replayer = new AReplayer(analyzer);
			analyzer.addAnalyzerListener(replayer);
		}
	}

	@Override
	public void forward(String numStep, String step) {
		// TODO Auto-generated method stub
		replayer.setup();
		ArrayList<EHICommand> commands = replayer.forward(numStep, step);
		if (commands == null) {
			return;
		}
		replayView.createForwardCommandList(commands);
		replayView.updateTimeSpent(replayer.getTotalTimeSpent(), replayer.getCurrentTimeSpent());
		replayView.updateTimeline(replayer.timelineIndex());
		replayView.updateNumOfExceptions(replayer.getCurrentExceptions(), replayer.getTotalExceptions());
		replayView.updateReplayedFile(replayer.getReplayedFile());
		replayView.updateMetrics(replayer.getMetrics());
	}

	@Override
	public void back(String numStep, String step) {
		// TODO Auto-generated method stub
		replayer.setup();
		ArrayList<EHICommand> commands = replayer.back(numStep, step);
		if (commands == null) {
			return;
		}
		replayView.createBackCommandList(commands);
		replayView.updateTimeSpent(replayer.getTotalTimeSpent(), replayer.getCurrentTimeSpent());
		replayView.updateTimeline(replayer.timelineIndex());
		replayView.updateNumOfExceptions(replayer.getCurrentExceptions(), replayer.getTotalExceptions());
		replayView.updateReplayedFile(replayer.getReplayedFile());
		replayView.updateMetrics(replayer.getMetrics());
	}
	
	public void jumpTo(int index, String step) {
		replayer.setup();
		if (index >= 990) {
			replayView.createForwardCommandList(replayer.jumpTo(index, step));
		} else {
			replayView.createBackCommandList(replayer.jumpTo(index, step));
		}
		replayView.updateTimeSpent(replayer.getTotalTimeSpent(), replayer.getCurrentTimeSpent());
		replayView.updateNumOfExceptions(replayer.getCurrentExceptions(), replayer.getTotalExceptions());
		replayView.updateReplayedFile(replayer.getReplayedFile());
		replayView.updateMetrics(replayer.getMetrics());
	}
}
