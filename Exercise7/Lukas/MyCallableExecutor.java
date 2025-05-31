package Exercise7.Lukas;
import Exercise7.Lukas.Factorize.FactorizerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class MyCallableExecutor {
    private final Thread[] Workers;
    private final Queue<Runnable> ExecutionQueue;

    public MyCallableExecutor(int WorkerPoolSize) {
        this.ExecutionQueue = new ConcurrentLinkedQueue<>();
        this.Workers = new Thread[WorkerPoolSize];

        for (int i = 0; i < WorkerPoolSize; i++) {
            Workers[i] = new Thread(new Worker());
            Workers[i].start();
        }
    }

    public <V> Future<V> execute(Callable<V> task) {
        FutureTask<V> futureTask = new FutureTask<>(task);
        this.ExecutionQueue.offer(futureTask);
        return futureTask;
    }

    public void execute(Runnable task) {
        this.ExecutionQueue.offer(task);
    }

    public void stop() {
        for (Thread worker : this.Workers) {
            worker.interrupt();
        }
    }

    private class Worker implements Runnable {
        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                Runnable task = ExecutionQueue.poll();
                if (task != null){
                    task.run();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int numberExecutions = 25;

        MyCallableExecutor executor = new MyCallableExecutor(6);
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