package util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import fluorite.commands.EHICommand;

public class AnAsyncExecutor implements RunnableExecutor {
	public static final int NUM_PENDING_RUNNABLES = 20024;
	BlockingQueue<Runnable> pendingRunnables = new LinkedBlockingQueue(
			NUM_PENDING_RUNNABLES);

	public void asyncExecute(Runnable aRunnable) {
		pendingRunnables.add(aRunnable);
		System.out.println("Add: Pending runnables queue size:" + pendingRunnables.size());

	}
	
	public void syncExecute(Runnable aRunnable) {
		try {

			
			aRunnable.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {

				Runnable aRunnable = pendingRunnables.take();
				syncExecute(aRunnable);
				System.out.println("Take: Pending runnables queue size:" + pendingRunnables.size());

				aRunnable.run();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
