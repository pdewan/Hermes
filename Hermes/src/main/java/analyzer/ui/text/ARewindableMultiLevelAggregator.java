package analyzer.ui.text;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import util.annotations.Visible;
import analyzer.WebLink;
import analyzer.extension.AnAnalyzerProcessor;
import analyzer.ui.GeneralizedPlayAndRewindCounter;
import analyzer.ui.SessionPlayerFactory;
import analyzer.ui.graphics.PlayAndRewindCounter;
//import bus.uigen.hermes.HermesObjectEditorProxy;
import bus.uigen.OEFrame;
import difficultyPrediction.featureExtraction.RatioFeatures;
import fluorite.commands.EHICommand;
import fluorite.commands.EHPredictionCommand.PredictionType;
import fluorite.model.StatusConsts;

public class ARewindableMultiLevelAggregator extends AMultiLevelAggregator implements PropertyChangeListener {
	protected List<List<EHICommand>> allCommands = new ArrayList();
//	protected List<StringBuffer> allCommandBuffers = new ArrayList();
//	protected List<ICommand> allCommands = new ArrayList();

	protected List<RatioFeatures> allFeatures = new ArrayList();
	protected List<String> allPredictions = new ArrayList();
	protected List<String> allAggregatedStatuses = new ArrayList();
	// this stuff can be taken from participant time line
	protected List<String> allManualStatuses = new ArrayList();
	protected List<String> allBarriers = new ArrayList();
	protected List<List<WebLink>> allWebLinks = new ArrayList();
	protected List<Integer> allCorrectStatuses = new ArrayList();


	protected int nextFeatureIndex; // does not change during replay
//	protected int previousAggregatedIndex;
//	protected int currentAggregatedStatus;
//	protected int previousFeatureIndex;
	protected String currentAggregateStatus = StatusConsts.INDETERMINATE;
	protected int currentFeatureIndex; // changes during replay
	private boolean playBack;
	protected GeneralizedPlayAndRewindCounter player;
	
//	public ARewindableMultiLevelAggregator() {
//		this (new AGeneralizedPlayAndRewindCounter());
////		addRatioBasedSlots();
//	}
	public ARewindableMultiLevelAggregator() {
		addRatioBasedSlots();
		player = SessionPlayerFactory.getSingleton();
		player.addPropertyChangeListener(this);
	}
	public void reset() {
		super.reset();
		currentAggregateStatus = StatusConsts.INDETERMINATE;
		currentFeatureIndex = 0;
		nextFeatureIndex = 0;
		playBack = false;
		// this should really be done directly
		player.live();
		player.setNextFeatureIndex(nextFeatureIndex);
//		player.setSize(size);
		propertyChangeSupport.firePropertyChange("this", "", this);
	}
	
	
//	@Row(0)
	@Visible(false)
	public PlayAndRewindCounter getPlayer() {
		return player;
	}

	
	@Override
	@Visible(false)
	public void newCommand(EHICommand newCommand) {
//		if (!isPlayBack()) {
		allCommands.get(nextFeatureIndex).add(newCommand);
//			allCommands.add(newCommand);
//		} else {
		if (!isPlayBack()) {
		 super.newCommand(newCommand);
		}
		
	}
	@Override
	@Visible(false)
	public void newStatus(String aStatus) {
//		if (!isPlayBack()) {
		   allPredictions.set(nextFeatureIndex - 1, aStatus); // next feature index was bumpted by new feature
//		   allAggregatedStatuses.add(null);
//		   if (lastFeatureIndex == 0)
//			  allAggregatedStatuses.add(StatusConsts.INDETERMINATE);
//		   else
//			allAggregatedStatuses.add(allAggregatedStatuses.get(lastFeatureIndex - 1));
//		} else {
		if (!isPlayBack()) {
			super.newStatus(aStatus); 
//			propagatePre(); // to reset back and forward
		}
		propagatePre(); // to reset back and forward

				
	}
	@Override
	@Visible(false)
	public void newManualStatus(String aStatus) {
//		if (!isPlayBack()) {
		   allManualStatuses.set(nextFeatureIndex - 1, aStatus); // next feature index was bumpted by new feature
//		   allAggregatedStatuses.add(null);
//		   if (lastFeatureIndex == 0)
//			  allAggregatedStatuses.add(StatusConsts.INDETERMINATE);
//		   else
//			allAggregatedStatuses.add(allAggregatedStatuses.get(lastFeatureIndex - 1));
//		} else {
		if (!isPlayBack()) {
			super.newManualStatus(aStatus); 
//			propagatePre(); // to reset back and forward
		}
		propagatePre(); // to reset back and forward				
	}
	@Override
	protected void setCorrectStatus(int aStatus) {
//		if (!isPlayBack()) {
		   if (nextFeatureIndex == 0)
			   return; // before a ratio is receiced, we may have a notification
		   allCorrectStatuses.set(nextFeatureIndex - 1, aStatus); // next feature index was bumpted by new feature
//		   allAggregatedStatuses.add(null);
//		   if (lastFeatureIndex == 0)
//			  allAggregatedStatuses.add(StatusConsts.INDETERMINATE);
//		   else
//			allAggregatedStatuses.add(allAggregatedStatuses.get(lastFeatureIndex - 1));
//		} else {
		if (!isPlayBack()) {
			super.setCorrectStatus(aStatus); 
//			propagatePre(); // to reset back and forward
		}
		propagatePre(); // to reset back and forward				
	}
	@Override
	@Visible(false)
	public void newBarrier(String aStatus) {
		   allBarriers.set(nextFeatureIndex - 1, aStatus); // next feature index was bumpted by new feature

		if (!isPlayBack()) {
			super.newBarrier(aStatus); 
		}
		propagatePre(); // to reset back and forward				
	}
	
	@Override
	@Visible(false)
	public void newWebLinks(List<WebLink> aWebLinks) {
		if (aWebLinks == null | aWebLinks.isEmpty())
			return;
		   allWebLinks.set(nextFeatureIndex - 1, aWebLinks); // next feature index was bumpted by new feature

		if (!isPlayBack()) {
			super.newWebLinks(aWebLinks); 
		}
		propagatePre(); // to reset back and forward				
	}
	
    void propagatePre() {
		propertyChangeSupport.firePropertyChange("this", null, this);
    }
	void addRatioBasedSlots() {	
		allFeatures.add(null);
		allCommands.add(new ArrayList());
		allPredictions.add(null);
		allAggregatedStatuses.add(null);
		allManualStatuses.add(null);
		allBarriers.add(null);
		allWebLinks.add(null);
		allCorrectStatuses.add(correctStatusInt); // asume same as last segment as we only get chnages
	}
	
	@Override
	@Visible(false)
	public void newRatios(RatioFeatures newVal) {
		// buffer commands
		allFeatures.set(nextFeatureIndex, newVal);
//		player.setCurrentTime(currentFeatureIndex);
		addRatioBasedSlots();
		nextFeatureIndex++;
		player.setNextFeatureIndex(nextFeatureIndex);
		if (!isPlayBack()) {

		currentFeatureIndex = nextFeatureIndex -1;

//		player.setNextFeatureIndex(nextFeatureIndex);


//		} else {
//		if (!playBack) { 
			super.newRatios(newVal);	
		}
			
//		}
	}
	@Visible(false)
	public void newAggregatedStatus(String aStatus) {
//		if (!isPlayBack()) {
		  allAggregatedStatuses.set(nextFeatureIndex - 1, aStatus); // index was bumped
//		} else {
		if (!isPlayBack()) {
		super.newAggregatedStatus(aStatus);
		}		
	}
	
	
	
	void setNewWindow(int newVal) {
//		setPlayBack(true);
		currentFeatureIndex = newVal;
		newWindow();
	}
	
	int previousAggregateIndex() {	
		int retVal = currentFeatureIndex - 1;
		while (retVal >= 0) {
			if (allAggregatedStatuses.get(retVal) != null) return retVal;
			retVal--;
		}
		return Math.max(retVal, 0);
	}
	
//	void setAggregateStatus () {
//		currentAggregateStatus = StatusConsts.INDETERMINATE;
//		for (int aFeatureIndex = currentFeatureIndex; aFeatureIndex >= 0; aFeatureIndex-- ) {
//			if (allAggregatedStatuses.get(aFeatureIndex) != null) {
//				currentAggregateStatus = allAggregatedStatuses.get(aFeatureIndex);
//				return;
//			}
//		}
//	}
	
	
	@Visible(false)
	public void resetWindowData() {
//		features.clear();
//		predictions.clear();
//		commandsBuffer.setLength(0);
		super.reset();
	}	
//	public void computeWindowBounds() {
//		previousAggregatedIndex = previousAggregateIndex();
//	}
	
	public void suppressNotifications() {
//		HermesObjectEditorProxy.suppressNotifications(propertyChangeSupport);
		propertyChangeSupport.firePropertyChange(OEFrame.SUPPRESS_NOTIFICATION_PROCESSING, false, true);
	}
	public void unsuppressNotifications() {
//		HermesObjectEditorProxy.unsuppressNotifications(propertyChangeSupport);
		propertyChangeSupport.firePropertyChange(OEFrame.SUPPRESS_NOTIFICATION_PROCESSING, true, false);
	}
	
    @Visible(false)
	public void fireWindowEvents() {
		String anAggregatedStatus = StatusConsts.INDETERMINATE;
		suppressNotifications();
		for (int featureIndex = 0; featureIndex <= currentFeatureIndex; featureIndex++) {
			for (EHICommand aCommand:allCommands.get(featureIndex)) {
				super.newCommand(aCommand);				
			}
			super.newRatios(allFeatures.get(featureIndex));
			super.newStatus(allPredictions.get(featureIndex));
			if (allAggregatedStatuses.get(featureIndex) != null)
				super.newAggregatedStatus (allAggregatedStatuses.get(featureIndex));
			super.newManualStatus(allManualStatuses.get(featureIndex));
			super.newBarrier(allBarriers.get(featureIndex));
			super.newWebLinks(allWebLinks.get(featureIndex));
			super.setCorrectStatus(allCorrectStatuses.get(featureIndex));
		}
		unsuppressNotifications();
	}
	@Visible(false)
	public void newWindow() {
//		setAggregateStatus();
		resetWindowData();		
//		computeWindowBounds();
		fireWindowEvents();	
		propagatePre();
	}
	
	
	public boolean preLive() {
		return isPlayBack();
	}
//	@Row(0)
//	@Column(0)
//	public void live() {		
//		end(); //play back all past events
//		setPlayBack(false);
//	}
//	@Row(0)
//	@Column(1)
	public void start() {
		setNewWindow (0);
	}
//	@Row(0)
//	@Column(2)
	public void end() {
		setNewWindow (nextFeatureIndex - 1);
	}
	public boolean preBack() {
		return currentFeatureIndex > 0;
	}
//	@Row(0)
//	@Column(3)
	public void back() {
		if (!preBack()) return;
		setNewWindow(  currentFeatureIndex - 1);
	}
	public boolean preForward() {
		return currentFeatureIndex < nextFeatureIndex - 1;
	}
//	@Row(0)
//	@Column(4)
	public void forward() {
		if (!preForward()) return;
		setNewWindow (currentFeatureIndex + 1);
	}
//	@Row(0)
//	@Column(1)
	@Visible(false)
	public int getCurrentFeature() {
		return currentFeatureIndex;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (player.isPlayBack() && evt.getPropertyName().equalsIgnoreCase("currentTime")) {
			setNewWindow((Integer) evt.getNewValue());			
		}
//		if (evt.getPropertyName().equalsIgnoreCase("newRatioFeatures")) {
////			newRatios((RatioFeatures) evt.getNewValue());
////			repaint();
//		} else if (evt.getPropertyName().equalsIgnoreCase("start") || evt.getPropertyName().equalsIgnoreCase("size") || evt.getPropertyName().equalsIgnoreCase("currentTime"))
//			repaint();
	}
	public static void main (String[] args) {
//		ObjectEditor.edit(AMultiLevelAggregator.getInstance());
		createUI();
	}
	@Visible(false)
	public boolean isPlayBack() {
//		return playBack || player.isPlayBack();
		return player.isPlayBack();

	}
//	public void setPlayBack(boolean playBack) {
//		this.playBack = playBack;
//	}
}
