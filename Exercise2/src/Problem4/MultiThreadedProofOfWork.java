package Problem4;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class MultiThreadedProofOfWork {
    private static int NUM_THREADS = 12; // Adjust for CPU cores
    private static final AtomicLong winnerNonce = new AtomicLong(-1);

    public static void main(String[] args) {
        // Utilize available processors
        NUM_THREADS = Runtime.getRuntime().availableProcessors();
        System.out.println("Available Processor: " + NUM_THREADS);

        String block = "new block";
        BigInteger target = new BigInteger("00000fffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);

        long startTime = System.currentTimeMillis();
        long foundNonce = proveWork(block, target);
        long endTime = System.currentTimeMillis();

        System.out.println("Target: " + target);
        System.out.println("Winner nonce: " + foundNonce);
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
    }

    public static long proveWork(String block, BigInteger target) {
        // Create a thread pool to run the proof of work
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        // Submit threads to the thread pool
        for (int i = 0; i < NUM_THREADS; i++) {
            final long startNonce = i; // Each thread starts at a different nonce
            executor.submit(() -> {
                checkHash(block, startNonce, target);
            });
        }
        // Shutdown the executor and wait for all tasks to finish
        executor.shutdown();
        while (!executor.isTerminated() && winnerNonce.get() == -1) {
        }
        executor.shutdownNow();
        return winnerNonce.get();
    }

    private static void checkHash(String block, long startNonce, BigInteger target) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            long nonce = startNonce;

            while (winnerNonce.get() == -1) { // Continue until a thread finds a valid nonce
                String input = block + nonce;
                byte[] hash = computeSHA256(computeSHA256(input.getBytes(), digest), digest);
                // Convert hash to BigInteger for comparison
                BigInteger hashInt = new BigInteger(1, hash);

                if (hashInt.compareTo(target) < 0) {
                    winnerNonce.compareAndSet(-1, nonce); // Set the winner nonce if not already found
                    break;
                }
                nonce += NUM_THREADS; // Each thread skips ahead by NUM_THREADS to avoid redundant checks
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static byte[] computeSHA256(byte[] input, MessageDigest digest) {
        // Compute the SHA-256 hash of the input
        return digest.digest(input); // and return the result
    }
}
