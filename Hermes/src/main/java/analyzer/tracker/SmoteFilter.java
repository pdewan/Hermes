package analyzer.tracker;

import weka.core.Instances;
import weka.filters.supervised.instance.SMOTE;

public class SmoteFilter implements Filter{
	private SMOTE s;
	
	public SmoteFilter() {
		s=new SMOTE();
		
	}
	
	  /**
	   * Parses a given list of options.
	   * 
	   * <!-- options-start --> Valid options are:
	   * <p/>
	   * 
	   * <pre>
	   * -S &lt;num&gt;
	   *  Specifies the random number seed
	   *  (default 1)
	   * </pre>
	   * 
	   * <pre>
	   * -P &lt;percentage&gt;
	   *  Specifies percentage of SMOTE instances to create.
	   *  (default 100.0)
	   * </pre>
	   * 
	   * <pre>
	   * -K &lt;nearest-neighbors&gt;
	   *  Specifies the number of nearest neighbors to use.
	   *  (default 5)
	   * </pre>
	   * 
	   * <pre>
	   * -C &lt;value-index&gt;
	   *  Specifies the index of the nominal class value to SMOTE
	   *  (default 0: auto-detect non-empty minority class))
	   * </pre>
	   * 
	   * <!-- options-end -->
	   * 
	   * @param options the list of options as an array of strings
	   * @throws Exception if an option is not supported
	   */
	public void setOptions(String[] options) throws Exception {
		s.setOptions(options);
		
	}

	public void setPercentage(double d) {
		s.setPercentage(d);
		
	}
	
	@Override
	public Instances filter(Instances i) throws Exception {
		if(this.s.isOutputFormatDefined())
			s.setInputFormat(i);
		
		return weka.filters.Filter.useFilter(i, this.s);
	}

}
