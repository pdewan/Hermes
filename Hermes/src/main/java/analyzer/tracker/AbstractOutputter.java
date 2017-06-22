package analyzer.tracker;

import java.util.ArrayList;
import java.util.List;


/**Abstract class representing a base implementation of prediction outputter<br>
 * 
 * Prediction ended is called in preorder sequence
 * @author wangk1
 *
 */
public abstract class AbstractOutputter implements PredictionOutputter{
	private List<PredictionOutputter> children;
	private boolean outputtingEnded;
	
	public AbstractOutputter() {
		this.children=new ArrayList<>();
		this.outputtingEnded=false;
		
	}
	
	public AbstractOutputter(List<PredictionOutputter> l) {
		this.children=l;
		
	}

	@Override
	public abstract void output(PredictedInstance i);

	//Output Finished Section
	
	
	@Override
	public final void predictionOutputtingEnded() {
		this.outputtingEnded=true;
		this.outputtingComplete();
		
		for(PredictionOutputter c:this.children) {
			c.predictionOutputtingEnded();
			
		}
		
		
	}
	
	//override this to provide specific subclass behavior
	@Override
	public void outputtingComplete() {
		
	}

	
	//Close resource and streams section
	//final so that it cannot be overriden and must be called before the close resource method is called
	
	@Override
	public final void closeStream() {
		if(!this.outputtingEnded) {
			this.predictionOutputtingEnded();
			
		}
		
		this.closeResource();
		
		for(PredictionOutputter c:this.children) {
			c.closeStream();
			
		}
		
		
	}

	@Override
	public void closeResource() {
	}
	
	
	//Notify/add children outputters
	
	// not called (PD)
	public final void notifyChildren(PredictedInstance i) {
		for(PredictionOutputter c:this.children) {
			c.output(i);
			
		}
		
	}
	// not called (PD)
	public final void addChildren(PredictionOutputter c) {
		this.children.add(c);
		
	}
	
	public final List<PredictionOutputter> getChildren() {
		return this.children;
		
	}
}
