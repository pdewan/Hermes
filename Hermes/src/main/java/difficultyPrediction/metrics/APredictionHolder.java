package difficultyPrediction.metrics;

import java.util.ArrayList;
import java.util.List;

public class APredictionHolder {
	public int numberOfYes;
	public int numberOfNo;
	public List<String> predictions;
	
	public APredictionHolder() {
		this.predictions = new ArrayList<String>();
	}

}
