package Exercise7.Lukas;
import Exercise7.Lukas.Factorize.FactorizerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class MyExecutor_Callable {
    private final Thread[] workers;
    private final Queue<Runnable> executionQueue;

    public MyExecutor_Callable(int workerPoolSize) {
        this.executionQueue = new ConcurrentLinkedQueue<>();
        this.workers = new Thread[workerPoolSize];

        for (int i = 0; i < workerPoolSize; i++) {
            workers[i] = new Thread(new Worker());
            workers[i].start();
        }
    }

    public <V> Future<V> execute(Callable<V> task) {
        FutureTask<V> futureTask = new FutureTask<>(task);
        this.executionQueue.offer(futureTask);
        return futureTask;
    }

    public void execute(Runnable task) {
        this.executionQueue.offer(task);
    }

    public void stop() {
        for (Thread worker : this.workers) {
            worker.interrupt();
        }
    }

    private class Worker implements Runnable {
        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                Runnable task = executionQueue.poll();
                if (task != null){
                    task.run();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int numberExecutions = 25;

        MyExecutor_Callable executor = new MyExecutor_Callable(6);
        FactorizerFactory factorizerFactory = new FactorizerFactory();
        List<Future<String>> futures = new ArrayList<>(numberExecutions);

        for (int i = 0; i < numberExecutions; i++) {
            executor.execute(factorizerFactory.getFactorizerService());

            Future<String> futureResult = executor.execute(factorizerFactory.getCallableFactorizerService());
            futures.add(futureResult);
        }

        Thread.sleep(100);
        executor.stop();

        System.out.println("\nResolving Futures");
        for (Future<String> future: futures) {
            System.out.println(future.get());
        }
    }
}