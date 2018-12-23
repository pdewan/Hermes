package analyzer.extension;

import java.util.Date;
import java.util.List;

import analyzer.AnAnalyzer;
import analyzer.AnalyzerListener;
import bus.uigen.ObjectEditor;
import difficultyPrediction.DifficultyPredictionSettings;
import fluorite.commands.DifficultyCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.PredictionCommand;
import fluorite.commands.PredictionType;
import fluorite.commands.Status;
import fluorite.commands.WebVisitCommand;
import programmatically.AnEclipseProgrammatticController;
import util.annotations.Visible;

public class AnEclipseReplayer extends AnAnalyzer implements AnalyzerListener {
	public static final String ECLIPSE_REPLAYER_DEFAULT_PARTICIPANT_DIRECTORY = "D:/dewan_backup/Java/Hermes/Hermes/data";
	protected String defaultParticipantDirectory() {
		return ECLIPSE_REPLAYER_DEFAULT_PARTICIPANT_DIRECTORY;
	}
	public AnEclipseReplayer() {
		this.addAnalyzerListener(this);
	}
	public AnEclipseProgrammatticController logReplayer() {
		return AnEclipseProgrammatticController.getInstance();
	}
	public static void createUI() {
		ObjectEditor.edit(new AnEclipseReplayer() );
	}
	
	@Visible(false)
	public void newBrowseLine(String aLine) {
		// TODO Auto-generated method stub
		
	}

	@Visible(false)
	public void newBrowseEntries(Date aDate, String aSearchString, String aURL) {
		// TODO Auto-generated method stub
		
	}

	@Visible(false)
	public void newBrowserCommands(List<WebVisitCommand> aCommands) {
		// TODO Auto-generated method stub
		
	}

	@Visible(false)
	public void finishedBrowserLines() {
		// TODO Auto-generated method stub
		
	}

	@Visible(false)
	public void newParticipant(String anId, String aFolder) {
		// TODO Auto-generated method stub
		
	}

	@Visible(false)
	public void startTimestamp(long aStartTimeStamp) {
		// TODO Auto-generated method stub
		
	}

	@Visible(false)
	public void experimentStartTimestamp(long aStartTimeStamp) {
		// TODO Auto-generated method stub
		
	}

	@Visible(false)
	public void replayFinished() {
		// TODO Auto-generated method stub
		
	}

	@Visible(false)
	public void replayStarted() {
		// TODO Auto-generated method stub
		
	}

	@Visible(false)
	public void finishParticipant(String anId, String aFolder) {
		// TODO Auto-generated method stub
		
	}

	@Visible(false)
	public void newStoredCommand(EHICommand aNewCommand, long aStartAbsoluteTime, long aDuration) {
		// TODO Auto-generated method stub
		
	}

	@Visible(false)
	public void newStoredInputCommand(EHICommand aNewCommand, long aStartAbsoluteTime, long aDuration) {
		// TODO Auto-generated method stub
		
	}

	@Visible(false)
	public void newCorrectStatus(DifficultyCommand newCommand, Status aStatus, long aStartAbsoluteTime,
			long aDuration) {
		// TODO Auto-generated method stub
		
	}

	@Visible(false)
	public void newPrediction(PredictionCommand newParam, PredictionType aPredictionType, long aStartAbsoluteTime,
			long aDuration) {
		// TODO Auto-generated method stub
		
	}

	@Visible(false)
	public void newWebVisit(WebVisitCommand aWebVisitCommand, long aStartAbsoluteTime, long aDuration) {
		// TODO Auto-generated method stub
		
	}
	
	public static  void main (String[] args) {
		DifficultyPredictionSettings.setReplayMode(true);

		AnEclipseReplayer anEclipseReplayer = new AnEclipseReplayer();
		ObjectEditor.edit(anEclipseReplayer);
	}

}
