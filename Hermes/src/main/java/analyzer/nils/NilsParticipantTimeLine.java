/**
 * @author Nils Persson
 * @date 2018-Oct-24 6:21:33 PM 
 */
package analyzer.nils;

import java.util.List;

import analyzer.ParticipantTimeLine;

/**
 * 
 */
public interface NilsParticipantTimeLine extends ParticipantTimeLine{
	public abstract List<Double> getPasteList();

	public abstract void setPasteList(List<Double> pasteList);
	
	public abstract List<Double> getCopyList();

	public abstract void setCopyList(List<Double> copyList);
	
	public abstract List<Double> getLongestDeleteList();
	
	public abstract void setLongestDeleteList(List<Double> longestDeleteList);
	
	public abstract List<Double> getLongestInsertList();
	
	public abstract void setLongestInsertList(List<Double> longestInsertList);
	
	public List<Double> getCompileList();
	
	public void setCompileList(List<Double> compileList);
	
	public List<Integer> getPredictionIndexesList();
	
	public void setPredictionIndexesList(List<Integer> compileList);
}
