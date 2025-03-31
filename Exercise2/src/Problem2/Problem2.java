package Problem2;

import java.util.Random;

public class Problem2 {
    final static Webservice ws = new Webservice();

    public static void main(String[] args) {

        int[] numbers = { 60, 45, 30, 72, 90 };
        Thread[] threads = new Thread[numbers.length];

        for (int j = 0; j < numbers.length; j++) {
            final int number = numbers[j];
            threads[j] = new Thread(() -> {
                OwnResponse response = ws.doWebserviceCall(number);
                String factorString = arrayToString(response.factors);

                // After the call, compare ws.lastNumber & ws.lastFactors:
                boolean consistent = checkFactors(ws.lastNumber, ws.lastFactors);

                System.out.println(
                        "Run for number " + number
                                + " => factors: " + factorString
                                + " | Webservice State - Last Number: " + ws.lastNumber
                                + ", Last Factors: " + arrayToString(ws.lastFactors)
                                + (consistent ? " (consistent)" : " (RACE DETECTED!)"));
            });
        }

        // Start all threads
        for (Thread t : threads) {
            t.start();
            try {
                int min_sleep = 5;
                int max_sleep = 50;
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

    private static boolean checkFactors(int number, int[] supposedFactors) {
        int[] correctFactors = ws.factor(number);
        if (correctFactors.length != supposedFactors.length) {
            return false;
        }
        for (int i = 0; i < correctFactors.length; i++) {
            if (correctFactors[i] != supposedFactors[i]) {
                return false;
            }
        }
        return true;
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
