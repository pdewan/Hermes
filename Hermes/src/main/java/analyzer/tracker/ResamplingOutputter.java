package analyzer.tracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import weka.core.Instance;

/**Resample output with random number generator and with replacement
 * 
 * @author wangk1
 *
 */
public class ResamplingOutputter extends AbstractOutputter{
	private AccumulatorOutputter accumulator;
	private double percentage;

	public ResamplingOutputter(double percentageOfOriginal) {
		this(percentageOfOriginal,1,new PredictionOutputter[0]);

	}
	
	public ResamplingOutputter(double percentageOfOriginal,double numberOfSamples,PredictionOutputter...children) {
		super(Arrays.asList(children));
		this.accumulator=new AccumulatorOutputter();
		this.percentage=percentageOfOriginal;

	}

	@Override
	public void output(PredictedInstance i) {
		this.accumulator.output(i);

	}

	public List<PredictedInstance> resample() {
		if(this.accumulator.getOutputList().size()<1)
			return new ArrayList<PredictedInstance>();

		int numTimes=(int) (percentage/100*this.accumulator.getOutputList().size());

		List<PredictedInstance> inst=this.accumulator.getOutputList();
		Random r=new Random();
		for(int i=0;i<numTimes;i++) {
			int index=r.nextInt(inst.size());
			inst.add(inst.get(index));
		}

		return inst;
	}
	
	public void sum(List<PredictedInstance> outputs) {
		
		
	}

	@Override
	public void outputtingComplete() {
		//resample
		List<PredictedInstance> outputs=resample();

		for(PredictedInstance p:outputs) {
			for(PredictionOutputter o:this.getChildren()) {

				o.output(p);

			}
		}
	}

}
