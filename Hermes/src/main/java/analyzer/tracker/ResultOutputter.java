package analyzer.tracker;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.stream.IntStream;

/**Outputs classification result analysis.<br>
 * 1. Confusion Matrix<br>
 * 2. Total Percent<br>
 * 3. Respective true postive and true negative %<br>
 * 
 * @author wangk1
 *
 */
public class ResultOutputter extends AbstractOutputter{
	private int[] confusionMatrix;
	private String truePos;
	private String trueNeg;
	private BufferedWriter w;
	
	public static final int TRUE_POSITIVE=0;
	public static final int FALSE_POSITIVE=1;
	public static final int FALSE_NEGATIVE=2;
	public static final int TRUE_NEGATIVE=3;

	public ResultOutputter(String truePos, String trueNeg) {
		this(truePos,trueNeg, null);
		
	}
	
	public ResultOutputter(String truePos, String trueNeg,BufferedWriter w) {
		this.confusionMatrix=new int[4];
		this.truePos=truePos;
		this.trueNeg=trueNeg;
		this.w=w;
		
	}
	
	@Override
	public void output(PredictedInstance i) {
		double trueClass=i.getInstance().classValue();
		
		int index=0;
		
		if(i.getInstance().classAttribute().value((int)i.getInstance().classValue()).equalsIgnoreCase(this.trueNeg)) {
			index=i.getPrediction()==TRUE_POSITIVE?FALSE_POSITIVE:TRUE_NEGATIVE;
			
		} else {
			index=i.getPrediction()==TRUE_POSITIVE?TRUE_POSITIVE:FALSE_NEGATIVE;
		
		}
		
		this.confusionMatrix[index]++;
		
	}
	
	@Override
	public void outputtingComplete() {
		String accuracy=String.format("True Positive: %d | "+
				"False Negative: %d\n"+
				"False Positive: %d |"+
				"True Negative: %d\n", this.confusionMatrix[TRUE_POSITIVE]
				,this.confusionMatrix[FALSE_NEGATIVE]
				,this.confusionMatrix[FALSE_POSITIVE]
				,this.confusionMatrix[TRUE_NEGATIVE]);
		String totalAcc="Total Accuracy: "+((double)(this.confusionMatrix[TRUE_POSITIVE]+this.confusionMatrix[TRUE_NEGATIVE])/sum()*100);
		String truePos="True Positive %: "+(((double)this.confusionMatrix[TRUE_POSITIVE])/(this.confusionMatrix[TRUE_POSITIVE]+this.confusionMatrix[FALSE_NEGATIVE])*100);
		String trueNeg="True Negative %: "+((double)(this.confusionMatrix[TRUE_NEGATIVE])/(this.confusionMatrix[FALSE_POSITIVE]+this.confusionMatrix[TRUE_NEGATIVE])*100);
		
		System.out.printf(accuracy);
		System.out.println(totalAcc);
		System.out.println(truePos);
		System.out.println(trueNeg);
		
		if(w != null) {
			dumpToFile(w,accuracy+totalAcc+"\n"+truePos+"\n"+trueNeg+"\n");
			
		}
		
	}
	
	public void dumpToFile(BufferedWriter w,String out) {
		try {
			w.write(out);
			w.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public int sum() {
		return this.confusionMatrix[TRUE_POSITIVE]
				+this.confusionMatrix[FALSE_NEGATIVE]
				+this.confusionMatrix[FALSE_POSITIVE]
				+this.confusionMatrix[TRUE_NEGATIVE];
		
	}
	
	public int[] getMatrix() {
		return this.confusionMatrix;
		
	}
	
	
}
