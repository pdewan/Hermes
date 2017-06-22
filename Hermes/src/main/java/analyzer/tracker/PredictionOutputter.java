package analyzer.tracker;


public interface PredictionOutputter {

	/**Output the predicted instances in predetermined format
	 * 
	 * @param i
	 */
	void output(PredictedInstance i);
	
	
	/**Cue to the outputter the prediction process has ended. This is final so that all children outputter's
	 * predictionOutputtingEnded() method is called
	 * 
	 */
	void predictionOutputtingEnded();

	/**Called by predictionOutputtingEnded(), can be overridden to have specific behavior in subclasses
	 * 
	 */
	void outputtingComplete();
	
	/**Must be called to close the outputter. Note that this class is final and is only implemented by abstractoutputter.<br>
	 * However, outputter can implement closeResource instead, which is called by closeStream after operation completes, to close any resources.
	 * 
	 */
	void closeStream();
	
	
	/**Called by abstractoutputter to close any unneccessary resources
	 * 
	 */
	void closeResource();
	
	
	/**Link as a children of this prediction outputter
	 * @param c
	 */
	void addChildren(PredictionOutputter c);
}
