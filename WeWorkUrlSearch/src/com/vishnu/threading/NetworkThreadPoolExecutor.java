package com.vishnu.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;

/**
 * Custom thread pool executor. Pool size is fixed to 20 worker threads.
 * @author vishnu
 */

public class NetworkThreadPoolExecutor {

    // this is pool size for worker thread. Taking as 10 because we want only 20 running threads
    // at a time. Each thread will call the runnable(10*2 = 20 threads)
    private static final int POOL_SIZE = 10;
    private static LinkedBlockingQueue<RunnableFuture> tasksQueue = new LinkedBlockingQueue<>();

    /*
    Actual thread that take the task and run the tasks from the queue
     */
    private static final class WorkerThread extends Thread {

        private boolean shouldStop;

        public WorkerThread() {
            System.out.println("Initializing worker " + this.getName() +" ...");
            this.shouldStop = false;
        }

        @Override
        public void run() {
            // keep running until this state variable is set to true
            while (!shouldStop) {
                if (!tasksQueue.isEmpty()) {
                    try {
                        //System.out.println("Thread [" + this.getName()+ "]");
                        tasksQueue.take().run();
                    } catch (InterruptedException e) {
                        System.out.println("Exception while handling the task");
                    }
                }
            }
        }

        public void stopExecuting() {
            shouldStop = true;
        }
    }

    private final List<WorkerThread> threadPool;

    public NetworkThreadPoolExecutor() {
        this.threadPool = new ArrayList<>(POOL_SIZE);
        for (int i = 0; i < POOL_SIZE; i++) {
            WorkerThread thread = new WorkerThread();
            threadPool.add(thread);
            thread.start();
        }
    }

    /**
     * Adds the runnable into the task queue so that next free worker thread can go and
     * finish the work
     */
    public Future<String> submit(NetworkCallable networkCallable) {
        RunnableFuture<String> futureTask = new FutureTask<>(networkCallable);
        tasksQueue.add(futureTask);
        return futureTask;
    }

    /**
     * Will halt the worker thread but if there is a running thread, that will complete
     * the task first before shutting down.
     */
    public void stop() {
        for (int i = 0; i < POOL_SIZE; i++) {
            threadPool.get(i).stopExecuting();
        }
        tasksQueue.clear();
    }
}
