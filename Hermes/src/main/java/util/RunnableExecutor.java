package util;

public interface RunnableExecutor extends Runnable {
	public void asyncExecute(Runnable aRunnable) ;
	public void syncExecute(Runnable aRunnable) ;


}
