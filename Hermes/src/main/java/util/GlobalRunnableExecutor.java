package util;

public class GlobalRunnableExecutor {
	public static final String ASYNC_EXECUTOR_NAME = "Async Executor";
	static RunnableExecutor singleton;
	static Thread globalThread;
	public static RunnableExecutor getSingleton() {
		if (singleton == null) {
			singleton = new AnAsyncExecutor();
			globalThread = new Thread(singleton);
			globalThread.setName(ASYNC_EXECUTOR_NAME);
			globalThread.start();
		}
		return singleton;		
	}

}
