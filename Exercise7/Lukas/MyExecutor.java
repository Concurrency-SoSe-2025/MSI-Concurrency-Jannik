package Exercise7.Lukas;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MyExecutor {
    private final Thread[] Workers;
    private final Queue<Runnable> ExecutionQueue;

    public MyExecutor(int WorkerPoolSize) {
        this.ExecutionQueue = new ConcurrentLinkedQueue<>();
        this.Workers = new Thread[WorkerPoolSize];

        for (int i = 0; i < WorkerPoolSize; i++) {
            Workers[i] = new Thread(new Worker());
            Workers[i].start();
        }
    }

    public void execute(Runnable task) {
        this.ExecutionQueue.offer(task);
    }

    public void stop() {
        for (Thread thread : this.Workers) {
            thread.interrupt();
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

    public static void main(String[] args) throws InterruptedException {
        MyExecutor executor = new MyExecutor(6);
        FactorizerFactory factorizerFactory = new FactorizerFactory();

        for (int i = 0; i < 25; i++) {
            executor.execute(factorizerFactory.getFactorizerService());
        }

        Thread.sleep(100);
        executor.stop();
    }
}