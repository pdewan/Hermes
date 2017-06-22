package analyzer.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import analyzer.AnAnalyzer;
import analyzer.Analyzer;

/**Factory for generation multiple arff files at once, each <s>on a different thread</s>  with its individual arfffile generator 
 * <br>
 * 
 * @author wangk1
 *
 */
public class ArffFileGeneratorFactory {
	private String outputSubDirectory;
	private ExecutorService threadPool;
	private List<String> listOfCommands;
	
	/**ouput subdirectory is the subdirectory inside of output folder to put generated arff*/
	public ArffFileGeneratorFactory(String outputSubDirectory) {
		this(outputSubDirectory,new ArrayList<String>());
		
	}
	
	public ArffFileGeneratorFactory(String outputSubDirectory, List<String> commands) {
		this.outputSubDirectory=outputSubDirectory;
		this.threadPool=Executors.newSingleThreadExecutor();
		this.listOfCommands=Collections.synchronizedList(new ArrayList<String>());
		
	}
	
	/**Insert command to arff generator
	 * 
	 * @param command
	 */
	public void insertCommand(String command) {
		listOfCommands.add(command);
		
	}
	
	public void insertCommand(List<String> command) {
		this.listOfCommands.addAll(command);
		
	}
	
	/**Execute each participant command
	 * 
	 */
	public void createArffs() {
		for(String c:this.listOfCommands) {
			//difficulty prediction does not work for multiple threads
			new ArffFileGeneratorThread(this.outputSubDirectory,c).run();
			
			
		}
		
		this.listOfCommands.clear();
	}
	
	/**Must call to ensure that all tasks are completed properly.
	 * <br>
	 * Unless the difficulty predictor supports multiple threads, this is not going to be used
	 * 
	 * @throws InterruptedException
	 */
	@Deprecated
	public void join() throws InterruptedException {
		//hopefully takes shorter than this
		threadPool.awaitTermination(1, TimeUnit.DAYS);
		
	}
	
	public void setOutputSubDirectory(String f) {
		this.outputSubDirectory=f;
		
	}
	
	/**Helper thread that each starts a separate AnarffGenerator instance to execute command
	 * 
	 * @author wangk1
	 *
	 */
	private class ArffFileGeneratorThread implements Runnable{
		private final ArffGenerator arffGenerator;
		private final Analyzer analyzer;
		private final String command;
		
		private ArffFileGeneratorThread(String outputSubDirectory,String command) {
			this.analyzer = new AnAnalyzer();
			this.analyzer.setOutputSubDirectory(outputSubDirectory);
			this.arffGenerator = new AnArffGenerator(analyzer);
			analyzer.addAnalyzerListener(arffGenerator);
			this.command=command;
			
		}
		
		@Override
		public void run() {
			this.analyzer.loadDirectory();
			this.analyzer.getParameterSelector().getParticipants().setValue(command);
			
			//load logs without creating another thread
			this.analyzer.loadLogs(false);
		}
		

		
	}
	
	public static void main(String[] args) throws InterruptedException {
		ArffFileGeneratorFactory f=new ArffFileGeneratorFactory("");
//		for(int i=1;i<18;i++) {
//			f.insertCommand("All ignore "+i);
//			
//		}
		f.insertCommand("All");
		f.createArffs();
	
		
	}
	
}
