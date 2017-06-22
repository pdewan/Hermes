package analyzer.tracker;

import java.util.ArrayList;
import java.util.Arrays;

/**Can contain many outputters that it outputs to all at once
 * 
 * @author wangk1
 *
 */
public class CombinedOutputter extends AbstractOutputter implements PredictionOutputter{
	
	public CombinedOutputter(final PredictionOutputter...outputters) {
		super(new ArrayList<>(Arrays.asList(outputters)));
		
	}
		
	@Override
	public void output(PredictedInstance i) {
		for(PredictionOutputter o:this.getChildren()) {
			o.output(i);
			
		}
		
	}

}
