package analyzer.tracker;

import weka.core.Instances;

public interface Filter {
	// not called
	Instances filter(Instances i) throws Exception;
	
}
