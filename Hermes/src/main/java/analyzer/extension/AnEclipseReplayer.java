package analyzer.extension;

import java.util.Date;
import java.util.List;

import analyzer.AnAnalyzer;
import analyzer.AnalyzerListener;
import analyzer.extension.replay.CommandProcessor;
import analyzer.extension.replay.DocumentChangeProcessor;
import analyzer.extension.replay.EclipseCommandProcessor;
import bus.uigen.ObjectEditor;
import difficultyPrediction.DifficultyPredictionSettings;
import fluorite.commands.BaseDocumentChangeEvent;
import fluorite.commands.DifficultyCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.EclipseCommand;
import fluorite.commands.PredictionCommand;
import fluorite.commands.PredictionType;
import fluorite.commands.Status;
import fluorite.commands.WebVisitCommand;
import programmatically.AnEclipseProgrammaticController;
import util.annotations.Visible;
import util.trace.Tracer;

public class AnEclipseReplayer extends AnAnalyzer implements AnalyzerListener {
	// edit this to set your folder
	public static final String ECLIPSE_REPLAYER_DEFAULT_PARTICIPANT_DIRECTORY = "C:/Users/avitk/git/Hermes/Hermes/data";//D:/dewan_backup/Java/Hermes/Hermes/data";
	protected long startTimestamp;
	
	private final AnEclipseProgrammaticController PROGRAMATIC_CONTROLLER;
	private final CommandProcessor COMMAND_PROCESSOR;
	private final DocumentChangeProcessor DOCUMENT_CHANGE_PROCESSOR;
	private final EclipseCommandProcessor ECLIPSE_COMMAND_PROCESSOR;
	
	@Override
	protected String defaultParticipantDirectory() {
		return ECLIPSE_REPLAYER_DEFAULT_PARTICIPANT_DIRECTORY;
	}
	
	public AnEclipseReplayer() {
		this.addAnalyzerListener(this);
		PROGRAMATIC_CONTROLLER = AnEclipseProgrammaticController.getInstance();
		COMMAND_PROCESSOR = new CommandProcessor(PROGRAMATIC_CONTROLLER);
		DOCUMENT_CHANGE_PROCESSOR = new DocumentChangeProcessor(PROGRAMATIC_CONTROLLER);
		ECLIPSE_COMMAND_PROCESSOR = new EclipseCommandProcessor(PROGRAMATIC_CONTROLLER);
//		PROGRAMATIC_CONTROLLER.createLaunchConfiguration();
//		PROGRAMATIC_CONTROLLER.getOrCreatePredefinedProject();
	}
	
	public static void createUI() {
		Tracer.setMaxTraces(Integer.MAX_VALUE);
		ObjectEditor.edit(new AnEclipseReplayer() );
	}
	
	@Override
	@Visible(false)
	public void newBrowseLine(String aLine) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Visible(false)
	public void newBrowseEntries(Date aDate, String aSearchString, String aURL) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Visible(false)
	public void newBrowserCommands(List<WebVisitCommand> aCommands) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Visible(false)
	public void finishedBrowserLines() {
		// TODO Auto-generated method stub
		
	}
	@Override
	@Visible(false)
	public void newParticipant(String anId, String aFolder) {
//		System.out.println("Folder: " + getOrCreateEmptyProjectLocation(anId));
		resetLogger(anId, startTimestamp);
		resetProject(anId, startTimestamp);
		PROGRAMATIC_CONTROLLER.getOrCreateProject("Project_" + anId, getOrCreateEmptyProjectLocation(anId).getParent());
	}
	
	

	@Override
	@Visible(false)
	public void startTimestamp(long aStartTimestamp) {
		// TODO Auto-generated method stub
		startTimestamp = aStartTimestamp;
		
	}

	@Override
	@Visible(false)
	public void experimentStartTimestamp(long aStartTimeStamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Visible(false)
	public void replayFinished() {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Visible(false)
	public void replayStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Visible(false)
	public void finishParticipant(String anId, String aFolder) {
		removeLogHandlers(anId);
		
	}

	@Override
	@Visible(false)
	public void newStoredCommand(EHICommand aNewCommand, long aStartAbsoluteTime, long aDuration) {
		// TODO Auto-generated method stub
		
	}
// override this to terminate earlier, by returning false
	@Override
	protected boolean terminateReplayNow() {
		return super.terminateReplayNow();
	}
	
	@Override
	@Visible(false)
	public void newStoredInputCommand(EHICommand aNewCommand, long aStartAbsoluteTime, long aDuration) {
//		recordCommand(aNewCommand);
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println(aNewCommand.getCommandType());
		if (aNewCommand instanceof EclipseCommand) {
			EclipseCommand eCmd = (EclipseCommand)aNewCommand;
//			System.out.println(eCmd.getCommandType() + " " + eCmd.getCommandID() + " " + eCmd.getClass());
			ECLIPSE_COMMAND_PROCESSOR.processEclipseCommand(eCmd);
		} else if (aNewCommand instanceof BaseDocumentChangeEvent) {
			BaseDocumentChangeEvent docChangeCmd = (BaseDocumentChangeEvent)aNewCommand;
//			System.out.println(docChangeCmd.getCommandType() + " " + docChangeCmd.getClass());
			DOCUMENT_CHANGE_PROCESSOR.processDocumentChange(docChangeCmd);
		}
		else {
//			System.out.println(aNewCommand.getCommandType() + " " + aNewCommand.getClass());
			COMMAND_PROCESSOR.processCommand(aNewCommand);
		}
	}

	@Override
	@Visible(false)
	public void newCorrectStatus(DifficultyCommand newCommand, Status aStatus, long aStartAbsoluteTime,
			long aDuration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Visible(false)
	public void newPrediction(PredictionCommand newParam, PredictionType aPredictionType, long aStartAbsoluteTime,
			long aDuration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Visible(false)
	public void newWebVisit(WebVisitCommand aWebVisitCommand, long aStartAbsoluteTime, long aDuration) {
		// TODO Auto-generated method stub
		
	}
	
	public static  void main (String[] args) {
		Tracer.setMaxTraces(Integer.MAX_VALUE);
		DifficultyPredictionSettings.setReplayMode(true);

		AnEclipseReplayer anEclipseReplayer = new AnEclipseReplayer();
		ObjectEditor.edit(anEclipseReplayer);
		Tracer.setMaxTraces(Integer.MAX_VALUE);
	}

}
