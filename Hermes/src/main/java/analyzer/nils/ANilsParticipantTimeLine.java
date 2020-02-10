/**
 * @author Nils Persson
 * @date 2018-Oct-24 6:17:05 PM 
 */
package analyzer.nils;

import java.util.ArrayList;
import java.util.List;

import analyzer.AParticipantTimeLine;
import analyzer.WebLink;

/**
 * 
 */
public class ANilsParticipantTimeLine extends AParticipantTimeLine implements NilsParticipantTimeLine{

	protected List<Double> pasteList;
	protected List<Double> copyList;
	protected List<Double> longestDeleteList;
	protected List<Double> longestInsertList;
	protected List<Double> compileList;
	
	protected List<Integer> predictionIndexes; 
	
	public ANilsParticipantTimeLine(List<Double> editList,
			List<Double> insertionList,
			List<Double> deletionList, List<Double> debugList,
			List<Double> navigationList, List<Double> focusList,
			List<Double> removeList, List<Long> timeStampList,
			List<Integer> predictionList, List<Integer> predictionCorrection,
			List<List<WebLink>> webLinks) {
		super();
		setEditList(editList);
		setInsertionList(insertionList);
		setDeletionList(deletionList);
		setDebugList(debugList);
		setNavigationList(navigationList);
		setFocusList(focusList);
		setRemoveList(removeList);
		setTimeStampList(timeStampList);
		this.predictionList = predictionList;
		this.predictionCorrections = predictionCorrection;
		this.webLinks = webLinks;
		setStuckInterval(new ArrayList<>());
		setStuckPoint(new ArrayList<>());
		
		// maybe add these to the constructor parameters when I have time 
		setPasteList(new ArrayList<>());
		setCopyList(new ArrayList<>());
		setLongestDeleteList(new ArrayList<>());
		setLongestInsertList(new ArrayList<>());
		setCompileList(new ArrayList<>());
		setPredictionIndexesList(new ArrayList<>());
	}
	
	public ANilsParticipantTimeLine() {
		this(new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), 
				new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), 
				new ArrayList(), new ArrayList(), new ArrayList());
		
	}
	
	public void setPasteList(List<Double> pasteList) {
		this.pasteList = pasteList;
	}
	
	public void setCopyList(List<Double> copyList) {
		this.copyList = copyList;
	}
	
	public List<Double> getPasteList(){
		return pasteList;
	}
	
	public List<Double> getCopyList(){
		return copyList;
	}

	public List<Double> getLongestDeleteList() {
		return longestDeleteList;
	}

	public void setLongestDeleteList(List<Double> longestDeleteList) {
		this.longestDeleteList = longestDeleteList;
	}
	
	public List<Double> getLongestInsertList() {
		return longestInsertList;
	}

	public void setLongestInsertList(List<Double> longestInsertList) {
		this.longestInsertList = longestInsertList;
	}
	
	public List<Double> getCompileList(){
		return compileList;
	}
	
	public void setCompileList(List<Double> compileList){
		this.compileList = compileList;
	}
	
	public void setPredictionIndexesList(List<Integer> predList){
		this.predictionIndexes = predList;
	}
	
	public List<Integer> getPredictionIndexesList(){
		return predictionIndexes;
	}
}
