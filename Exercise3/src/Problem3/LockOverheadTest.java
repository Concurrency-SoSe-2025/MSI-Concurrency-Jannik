package Problem3;

public class LockOverheadTest {

    private static final int ITERATIONS = 100000000;
    private static long sum;

    // Synchronized (locked) version.
    private static synchronized void lockedMethod() {
        sum++;
    }

    // Unsynchronized (unlocked) version.
    private static void unlockedMethod() {
        sum++;
    }

    public static void main(String[] args) {
        // Warm-up phase to allow JIT (just-in-time) compilation and reduce measurement
        // artifacts
        for (int i = 0; i < ITERATIONS / 10; i++) {
            lockedMethod();
            unlockedMethod();
        }

        // Reset sum for a clean measurement
        sum = 0;

        // Measure locked method
        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            lockedMethod();
        }
        long end = System.nanoTime();
        long lockedTimeNanos = end - start;
        System.out.println("Synchronized (locked) total time: "
                + (lockedTimeNanos / 1000000) + " ms");
        System.out.println("Final sum (locked): " + sum);

        // Reset sum before measuring unlocked method
        sum = 0;

        // Measure unlocked method
        start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            unlockedMethod();
        }
        end = System.nanoTime();
        long unlockedTimeNanos = end - start;
        System.out.println("Unsynchronized (unlocked) total time: "
                + (unlockedTimeNanos / 1000000) + " ms");
        System.out.println("Final sum (unlocked): " + sum);

        // compute the ratio of locked to unlocked time
        if (unlockedTimeNanos != 0) {
            double ratio = (double) lockedTimeNanos / unlockedTimeNanos;
            System.out.printf("Locked is %.2fx slower than unlocked.%n", ratio);
        }
    }
}
