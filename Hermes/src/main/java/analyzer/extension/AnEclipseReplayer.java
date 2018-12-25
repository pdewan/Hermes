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
import programmatically.AnEclipseProgrammaticController;
import util.annotations.Visible;

public class AnEclipseReplayer extends AnAnalyzer implements AnalyzerListener {
	// edit this to set your folder
	public static final String ECLIPSE_REPLAYER_DEFAULT_PARTICIPANT_DIRECTORY = "D:/dewan_backup/Java/Hermes/Hermes/data";
	protected long startTimestamp;

	protected String defaultParticipantDirectory() {
		return ECLIPSE_REPLAYER_DEFAULT_PARTICIPANT_DIRECTORY;
	}
	public AnEclipseReplayer() {
		this.addAnalyzerListener(this);
	}
	public AnEclipseProgrammaticController programmaticController() {
		return AnEclipseProgrammaticController.getInstance();
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
		resetLogger(anId, startTimestamp);
		
	}
	
	

	@Visible(false)
	public void startTimestamp(long aStartTimestamp) {
		// TODO Auto-generated method stub
		startTimestamp = aStartTimestamp;
		
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
		removeLogHandlers(anId);
		
	}

	@Visible(false)
	public void newStoredCommand(EHICommand aNewCommand, long aStartAbsoluteTime, long aDuration) {
		// TODO Auto-generated method stub
		
	}

	@Visible(false)
	public void newStoredInputCommand(EHICommand aNewCommand, long aStartAbsoluteTime, long aDuration) {
		recordCommand(aNewCommand);
		
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
