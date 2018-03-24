package analyzer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import analyzer.ui.APredictionController;
import bus.uigen.ObjectEditor;
//import bus.uigsen..ADynamicEnum;
//import bus.uigen..DynamicEnum;
import difficultyPrediction.APredictionParameters;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.PredictionParameters;
import util.annotations.Column;
import util.annotations.ComponentHeight;
import util.annotations.ComponentWidth;
import util.annotations.Explanation;
import util.annotations.Row;
import util.annotations.StructurePattern;
import util.annotations.StructurePatternNames;
import util.annotations.Visible;
import util.misc.Common;
import util.models.ADynamicEnum;
import util.models.DynamicEnum;
@StructurePattern(StructurePatternNames.BEAN_PATTERN)
// there should really be a has-a relationship between the two
public class AnAnalyzerParameters implements AnalyzerParameters  {
	PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	static PredictionParameters instance;

	DynamicEnum<String> participants;
	Analyzer analyzer;
	boolean visualizePrediction;
	String currentParticipant = "";
//	PredictionParameters predictionParameters;
	
	
	
	public AnAnalyzerParameters(List<String> aParticipants) {
		participants = new ADynamicEnum(aParticipants);
//		propertyChangeSupport = new PropertyChangeSupport(this);
		
	}
	public AnAnalyzerParameters(String[] aParticipants) {
		participants = new ADynamicEnum(Common.arrayToArrayList(aParticipants));
	}
	public AnAnalyzerParameters(Analyzer anAnalyzer) {
		participants = new ADynamicEnum();
		analyzer = anAnalyzer;
	}
	/* (non-Javadoc)
	 * @see analyzer.AnalyzerParameters#getPredictionParameters()
	 */
	@Override
	@Row(0)
	public PredictionParameters getPredictionParameters() {
		return APredictionParameters.getInstance();
	}
//	public boolean preGetParticipants() {
//		return preLoadLogs();
//	}
	/* (non-Javadoc)
	 * @see analyzer.AnalyzerParameters#getParticipants()
	 */
	@Override
	@Row(2)
//	@Row(1)
	@Column(1)
	@ComponentWidth(75)
	@ComponentHeight(25)
//	@Label("")
	@Explanation("Participants whose logs you can replay")
	public DynamicEnum<String> getParticipants() {
		return participants;
	}

	/* (non-Javadoc)
	 * @see analyzer.AnalyzerParameters#setParticipants(util.models.DynamicEnum)
	 */
	@Override
	public void setParticipants(DynamicEnum<String> aNewVal) {
		participants = aNewVal;
	}
	
//	@Row(1)
	/* (non-Javadoc)
	 * @see analyzer.AnalyzerParameters#loadDirectory()
	 */
	@Override
	@Row(2)
	@Column(0)
	@Explanation("Load the names of the participants in the selected folder")
	@ComponentWidth(100)
	@ComponentHeight(25)
	public void loadDirectory() {
		if (analyzer != null)
			analyzer.loadDirectory();
	}
	/* (non-Javadoc)
	 * @see analyzer.AnalyzerParameters#preLoadLogs()
	 */
	@Override
	public boolean preReplayLogs() {
		if (analyzer != null)
		return analyzer.preLoadLogs();
		else return false;
	}
	/* (non-Javadoc)
	 * @see analyzer.AnalyzerParameters#preLoadDirectory()
	 */
	@Override
	@ComponentWidth(100)
	@ComponentHeight(25)
	public boolean preLoadDirectory() {
		if (analyzer != null)
		return analyzer.preLoadDirectory();
		else return false;
	}
//	@Row(1)
	/* (non-Javadoc)
	 * @see analyzer.AnalyzerParameters#loadLogs()
	 */
	@Override
	@Row(2)
	@Column(2)
	@Explanation("Loads the logs of a specific participant or all based on the participant selection")
	@ComponentWidth(100)
	@ComponentHeight(25)
    public void replayLogs() {
		if (analyzer != null)
			analyzer.loadLogs(true);
		
	}
	/* (non-Javadoc)
	 * @see analyzer.AnalyzerParameters#isNewOutputFiles()
	 */
	@Override
	@Row(1)
	@Column(0)
//	@Row(2)
//	@Column(2)
	public boolean isNewOutputFiles() {
		
		
		return DifficultyPredictionSettings.isNewRatioFiles();
	}
	
	/* (non-Javadoc)
	 * @see analyzer.AnalyzerParameters#setNewOutputFiles(boolean)
	 */
	@Override
	public void setNewOutputFiles(boolean newRatioFiles) {
//		if (analyzer != null)
//			 analyzer.setNewRatioFiles(newRatioFiles);
		DifficultyPredictionSettings.setNewRatioFiles(newRatioFiles);
	}
	
	/* (non-Javadoc)
	 * @see analyzer.AnalyzerParameters#preIsReplayOutputFiles()
	 */
	@Override
	public boolean preIsReplayOutputFiles() {
		return !isNewOutputFiles();
	}
	/* (non-Javadoc)
	 * @see analyzer.AnalyzerParameters#isReplayOutputFiles()
	 */
	@Override
	@Row(1)
	@Column(1)
//	@Row(2)
//	@Column(3)
	@Explanation("Replay ratio files rather than generating them from experimental data")
	public boolean isReplayOutputFiles() {
		
		
		return DifficultyPredictionSettings.isReplayRatioFiles();
	}
	
	/* (non-Javadoc)
	 * @see analyzer.AnalyzerParameters#setReplayOutputFiles(boolean)
	 */
	@Override
	public void setReplayOutputFiles(boolean newVal) {
//		if (analyzer != null)
//			 analyzer.setNewRatioFiles(newRatioFiles);
		DifficultyPredictionSettings.setReplayRatioFiles(newVal);
//		if (newVal) {
//			AnalyzerProcessorFactory.setSingleton(new AFileReplayAnalyzerProcessor());
//		} else {
//			AnalyzerProcessorFactory.createSingleton(); // just get the default one
//		}
			
	}
	/* (non-Javadoc)
	 * @see analyzer.AnalyzerParameters#isVisualizePrediction()
	 */
	
	protected boolean preVisualizePredictions() {
		return !isVisualizePrediction();
	}
	@Row(1)
	@Column(2)
	@Override
	public void visualizePredictions() {
//		PredictorConfigurer.visualizePrediction();
		APredictionController.createUI();
		setVisualizePrediction(true);
		
	}
	@Override
	@Row(1)
	@Column(2)
	@Visible(false)
//	@Column(1)
	public boolean isVisualizePrediction() {
		return visualizePrediction;
	}
	/* (non-Javadoc)
	 * @see analyzer.AnalyzerParameters#setVisualizePrediction(boolean)
	 */
	@Override
	public void setVisualizePrediction(boolean newVal) {
//		if (newVal) {
//			PredictorConfigurer.visualizePrediction();
//		}
		this.visualizePrediction = newVal;
	}
//	@Row(2)
//	@Column(3)
//	@ComponentWidth(50)
//	@ComponentHeight(25)
//	@Override
//	public String getCurrentParticipant() {
//		return currentParticipant;
//	}
//	@Override
//	public void setCurrentParticipant(String currentParticipant) {
//		this.currentParticipant = currentParticipant;
//		propertyChangeSupport.firePropertyChange("CurrentParticipant", "", currentParticipant);
//	}
	int currentParticipantIndex() {
		return participants.getChoices().indexOf(participants.getValue());
	}
	public boolean preNext() {
		return currentParticipantIndex() < (participants.getChoices().size() -1);
	}
	@Row(2)
	@Column(3)
	@ComponentWidth(50)
	@ComponentHeight(25)
	public void next() {
		if (!preNext())
			return;
		final Runnable aRunnable = 
				new Runnable() {
			public void run() {
				analyzer.processParticipant(participants.getChoices().get(1 + currentParticipantIndex()), 
						AnAnalyzer.PARTICIPANT_DIRECTORY + AnAnalyzer.OUTPUT_DATA, 
						AnAnalyzer.PARTICIPANT_DIRECTORY + AnAnalyzer.EXPERIMENTAL_DATA, true);

			}};
			Thread aThread = (new Thread(aRunnable));
			aThread.start();
//		analyzer.processParticipant(participants.getChoices().get(1 + currentParticipantIndex()));
		
	}
	
//	public static AnAnalyzerParametersSelector getInstance() {
//		if (instance == null) {
//			instance = new AnAnalyzerParametersSelector();
//		}
//		return instance;
//	}
	
//	@Column(1)
//	@Explanation("The set of problems in the selected module for which folders have been downloaded. Changing the problem automatically selects the corresponding download folder provided a valid folder has been slected for one of the problems in the module once.")
//
//	public DynamicEnum<String> getProblem() {
//		return problem;
//	}
//
//	public void setProblem(DynamicEnum<String> problem) {
//		this.problem = problem;
//	}
//	
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener arg0) {
		propertyChangeSupport.addPropertyChangeListener(arg0);
		
	}
	public static void main (String[] args) {
//		System.out.println(Common.intSuffix("Assignment2"));
//		System.out.println(Common.intSuffix("Assignment"));
		String[] participants = {"1", "2", "3"};
//		
//
//		List<String> modules = Common.arrayToArrayList(new String[] {"Comp110", "Comp401"});
//		List<String> problems = Common.arrayToArrayList(new String[] {"1", "2"});
		AnalyzerParameters selector = new AnAnalyzerParameters(participants);
		ObjectEditor.edit(selector);
		

	}

}
