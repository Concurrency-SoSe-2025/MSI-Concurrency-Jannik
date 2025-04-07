package Problem5;

import java.util.Random;

public class Problem5 {
    public static void main(String[] args) {
        Webservice ws = new Webservice();

        int[] numbers = { 60, 60, 60, 60, 60, 120, 120, 60, 150, 80, 80, 80 };
        Thread[] threads = new Thread[numbers.length];

        for (int j = 0; j < numbers.length; j++) {
            final int number = numbers[j];
            threads[j] = new Thread(() -> {
                OwnResponse response = ws.doWebserviceCall(number);
                String factorString = arrayToString(response.factors);
                boolean consistent = response.factors[response.factors.length - 1] == number;
                System.out.println("Number " + number + " with factors: " + factorString + " "
                        + (consistent ? " (consistent)" : " (RACE DETECTED!)"));
                System.out.println();
            });
        }

        // Start all threads
        for (Thread t : threads) {
            t.start();
            // Start next Thread a little late so chances for precalculated factors are
            // higher
            try {
                int min_sleep = 10;
                int max_sleep = 600;
                Thread.sleep(new Random().nextInt(max_sleep - min_sleep + 1) + min_sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Join all threads to ensure completion
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Utility method to print arrays as a string
    private static String arrayToString(int[] array) {
        if (array == null)
            return "null";
        StringBuilder sb = new StringBuilder();
        for (int item : array) {
            sb.append(item).append(" ");
        }
        return sb.toString().trim();
    }
}