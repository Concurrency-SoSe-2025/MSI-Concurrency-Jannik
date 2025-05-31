package Exercise7.Lukas;

import Exercise7.Lukas.Factorize.FactorizerFactory;

import java.util.concurrent.*;

public class MyCompletionService<V> {
    private final MyExecutor_Callable executor;
    private final ConcurrentLinkedQueue<Future<V>> completionQueue;

    public MyCompletionService(int workerPoolSize) {
        this.executor = new MyExecutor_Callable(workerPoolSize);
        this.completionQueue = new ConcurrentLinkedQueue<>();
    }

    public Future<V> execute(final Callable<V> task) {
        FutureTask<V> futureTask = new FutureTask<V>(task) {
            @Override
            protected void done() {
                completionQueue.offer(this);
            }
        };
        executor.execute(futureTask);
        return futureTask;
    }

    public Future<V> poll() {
        return completionQueue.poll();
    }

    public void stop() {
        this.executor.stop();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int numberExecutions = 25;

        MyCompletionService completionService = new MyCompletionService(6);
        FactorizerFactory factorizerFactory = new FactorizerFactory();

        for (int i = 0; i < numberExecutions; i++) {
            completionService.execute(factorizerFactory.getCallableFactorizerService());
        }

        int waitTime = 100;
        long deadline = System.currentTimeMillis() + waitTime;

        while (System.currentTimeMillis() < deadline) {
            Future future = completionService.poll();
            if (future != null) {
                System.out.println(future.get());
            }
        }

        completionService.stop();
    }
}
