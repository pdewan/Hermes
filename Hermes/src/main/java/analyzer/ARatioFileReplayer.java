package analyzer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import analyzer.ui.graphics.ARatioFileReader;
import analyzer.ui.graphics.DuriRatioFeaturesListener;
import analyzer.ui.graphics.RatioFileComponents;
import analyzer.ui.graphics.RatioFileReader;
import difficultyPrediction.AMediatorRegistrar;
import difficultyPrediction.predictionManagement.PredictionManagerStrategy;
import fluorite.commands.EHICommand;
import fluorite.commands.PredictionCommand;

public class ARatioFileReplayer extends AMediatorRegistrar implements RatioFilePlayer  {
//	List<List<ICommand>> nestedCommandsList;
	String ratioFileName;
	List<EHICommand> flattenedCommandsList;
	RatioFileReader ratioFileReader;
	TimeStampComputer timeStampComputer;
	long startTimeStamp;
	int nextStartCommandIndex = 0;
	int segmentNumber = 0;

	public ARatioFileReplayer() {
		super("");
		ratioFileReader = new ARatioFileReader();
//		timeStampComputer = new ATimeStampComputer();
		timeStampComputer = TimeStampComputerFactory.getSingleton();

		flattenedCommandsList = new ArrayList();
		ratioFileReader.addPropertyChangeListener(this);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see analyzer.RatioFilePlayer#setReplayedData(java.util.List, java.lang.String)
	 */
	@Override
	public void setReplayedData(List<List<EHICommand>> aNestedCommandsList, String aRatioFileName) {
//		nestedCommandsList = aNestedCommandsList;
		timeStampComputer.reset();
		flattenCommandsList(aNestedCommandsList);
		ratioFileName = aRatioFileName;
		nextStartCommandIndex = 0;
		segmentNumber = 0;
		System.out.println("New ratio file:" + aRatioFileName);
	
	}
	
	void flattenCommandsList(List<List<EHICommand>> aNestedCommandsList) {
		flattenedCommandsList.clear();
		for (int index = 0; index < aNestedCommandsList.size(); index++) {
			List<EHICommand> commands = aNestedCommandsList.get(index);
			for (int i = 0; i < commands.size(); i++) {
				EHICommand aCommand = commands.get(i);
				flattenedCommandsList.add(aCommand);				
				if ((aCommand.getTimestamp() == 0)
						&& (aCommand.getTimestamp2() > 0)) {
					// this is the starttimestamp
					startTimeStamp = commands.get(i).getTimestamp2();
				}
			}
		}
		System.out.println("Flattend command list size:" + flattenedCommandsList.size());
	}
	
	/* (non-Javadoc)
	 * @see analyzer.RatioFilePlayer#replay()
	 */
	@Override
	public void replay() {
		if (ratioFileReader == null || flattenedCommandsList == null)
			return;
		ratioFileReader.readFile(ratioFileName);
		
//		notifyStartCommand();
//		List<RatioFileComponents> ratioFeaturesList = ratioFileReader.getRatioFeaturesList();
//		for (int anIndex = 0; anIndex < ratioFeaturesList.size(); anIndex++) {
//			RatioFileComponents aRatioFileComponents = ratioFeaturesList.get(anIndex);
//			long aTimeStamp = aRatioFileComponents.getSavedTimeStamp();
//			fireCommands(aTimeStamp);
//			fireRatioFileComponents(aRatioFileComponents);
//		}		
//		notifyStopCommand();		
	}
	
//	int nextLastCommandIndex(long aTimeStamp) {
////		int retVal = flattenedCommandsList.size() - 1;
//		for (int anIndex = nextStartCommandIndex; anIndex < flattenedCommandsList.size(); anIndex++) {
//			if (flattenedCommandsList.get(anIndex).getTimestamp() > aTimeStamp || flattenedCommandsList.get(anIndex).getTimestamp2() > aTimeStamp))
//				return Math.min(0, anIndex - 1);
//		}
//		return flattenedCommandsList.size() - 1;
//	}
	
	void fireCommands(long aTimeStamp) {
		

		String aTimeStampString = ATimeStampComputer.toDateString(aTimeStamp);
		System.out.println("ratio time stamp" + aTimeStampString + " segment number:" + segmentNumber);
		segmentNumber++;
		for (int anIndex = nextStartCommandIndex; anIndex < flattenedCommandsList.size(); anIndex++) {
			EHICommand nextCommand = flattenedCommandsList.get(anIndex);
			long aComputedTimeStamp = timeStampComputer.computeTimestamp(nextCommand);
			String aComputedTimeStampString =  ATimeStampComputer.toDateString( aComputedTimeStamp);
			System.out.println(anIndex + " computed time stamp" + aComputedTimeStampString);

//			String aCommandTimeStampString =  AParticipantTimeLine.toDateString( nextCommand.getTimestamp());
//			String aCommandTimeStampString2 =  AParticipantTimeLine.toDateString( nextCommand.getTimestamp2());
//			System.out.println("command time stamp 1" + aCommandTimeStampString + " 2 " + aCommandTimeStampString2);

//			if (nextCommand.getTimestamp() <= aTimeStamp && 
//					nextCommand.getTimestamp2() <= aTimeStamp)
			if (aComputedTimeStamp <= aTimeStamp) {
				
				System.out.println ("Firing command at index:" + anIndex);
				if (nextCommand instanceof PredictionCommand) { //it seems we create a prediction command for each ratio command
					System.out.println ("prediction command");
				}
				notifyNewCommand(flattenedCommandsList.get(anIndex));
			
			} else {
				nextStartCommandIndex = anIndex;
				return;
			}
		}
//		return flattenedCommandsList.size() - 1;
	}
	public static String toStringStatus(int newStatus) {
		return newStatus == 0?
				PredictionManagerStrategy.PROGRESS_PREDICTION:
					PredictionManagerStrategy.DIFFICULTY_PREDICTION;
	}
	
	/* (non-Javadoc)
	 * @see analyzer.RatioFilePlayer#fireRatioFileComponents(analyzer.ui.graphics.RatioFileComponents)
	 */
	@Override
	public void fireRatioFileComponents(RatioFileComponents aRatioFileComponents) {
		notifyNewRatios(aRatioFileComponents);
//		String newStatus = aRatioFileComponents.getPredictedStatus() == 0?
//				PredictionManagerStrategy.PROGRESS_PREDICTION:
//					PredictionManagerStrategy.DIFFICULTY_PREDICTION;
		String newStatus = toStringStatus(aRatioFileComponents.getPredictedStatus());
		// this is not consistent with prediction commands,
		notifyNewStatus(newStatus);
		// we did not record incremental status
		// actuall we did, Ratios are claculated for incremental not batch
		// prediction commands are aggregated ones
		// ratios are accuulated and each aggregatede status associated with previous commands
		// by analuzer processor
		notifyNewAggregateStatus(newStatus);
		notifyNewWebLinks(aRatioFileComponents.getWebLinkList()); // will be erased at next agrregated status, must be before ag
		notifyNewBarrier(aRatioFileComponents.getDifficultyType());
		notifyNewManualStatus(AParticipantTimeLine.
				manualStatusIntToString(aRatioFileComponents.getActualStatus()));
	}
		

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(RatioFileReader.START_RATIOS)) {
			notifyStartCommand();
		} else if (evt.getPropertyName().equals(RatioFileReader.END_RATIOS)) {
			notifyStopCommand();
		} else if (evt.getPropertyName().equals(RatioFileReader.NEW_RATIO)) {
//			RatioFileComponents aRatioFileComponents = ratioFeaturesList.get(anIndex);
			RatioFileComponents aRatioFileComponents = (RatioFileComponents) evt.getNewValue();
			long aTimeStamp = aRatioFileComponents.getSavedTimeStamp();
			// commands and ratios may not be in sync, which means ratio file is not correctly saved or fireCommands does not play right
			// for every aggregated status there should be a prediction command
			fireCommands(aTimeStamp);
			fireRatioFileComponents(aRatioFileComponents);
		}
		
	}
	

}
