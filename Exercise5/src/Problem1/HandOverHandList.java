package Problem1;

public class HandOverHandList {
    public static void main(String[] args) throws InterruptedException {
        OwnList list = new OwnList();

        // Thread that inserts values
        Thread inserter = new Thread(() -> {
            try {
                for (int i = 1;; i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    list.insert(i);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Re-set interrupt flag
            }
        });

        // Start inserter thread
        inserter.start();

        // After 10 seconds, start deleter thread
        Thread.sleep(10000); // Sleep for 10 seconds

        Thread deleter = new Thread(() -> {
            try {
                for (int i = 1;; i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    list.delete(i);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Re-set interrupt flag
            }
        });

        // Start deleter thread
        deleter.start();

        // Let the program run for 50 more seconds (total 60 seconds)
        Thread.sleep(50000); // Sleep for 50 seconds

        // Interrupt both threads
        inserter.interrupt();
        deleter.interrupt();

        // Wait for both threads to finish
        inserter.join();
        deleter.join();

        System.out.println("Program terminated");
    }
}
