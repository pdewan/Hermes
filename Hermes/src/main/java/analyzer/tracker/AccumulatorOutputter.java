package analyzer.tracker;

import java.util.ArrayList;
import java.util.List;

/**Accumulate results, can be used with getters
 * 
 * @author wangk1
 *
 */
public class AccumulatorOutputter extends AbstractOutputter{
	List<PredictedInstance> list;
	
	public AccumulatorOutputter() {
		this.list=new ArrayList<>();
		
	}

	@Override
	public void output(PredictedInstance i) {
		list.add(i);
		
	}

	public List<PredictedInstance> getOutputList() {
		return this.list;
		
	}
}
