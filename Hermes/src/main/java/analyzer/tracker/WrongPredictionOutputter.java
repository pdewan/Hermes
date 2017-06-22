package analyzer.tracker;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**Prints out all the wrongly predicted instances
 * 
 * @author wangk1
 *
 */
public class WrongPredictionOutputter extends AbstractOutputter implements PredictionOutputter{
	private BufferedWriter w;
	private boolean headerGened;

	public WrongPredictionOutputter(String fileName) {
		this.headerGened=false;

		try {
			w=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

	}

	@Override
	public void output(PredictedInstance i) {
		//generate first column so we know what each column is 
		if(!headerGened) {
			try {
				for(int j=0;j<i.getInstance().numAttributes()-1;j++) {
					w.write(i.getInstance().attribute(j).name()+",");
				}
				
				w.write(i.getInstance().attribute(i.getInstance().numAttributes()-1).name()+"\n");
				
			} catch (IOException e) {
				e.printStackTrace();
				
			}
			
			this.headerGened=true;

		}


		double predicted=i.getPrediction();
		double actual=i.getInstance().value(i.getInstance().numAttributes()-1);

		if(predicted != actual) {
			try {
				w.write(i.getInstance().toString()+"\n");
				w.flush();
			} catch (IOException e) {
				e.printStackTrace();

			}

		}



	}

	@Override
	public void closeResource() {
		try {
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}



}
