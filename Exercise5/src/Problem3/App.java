package Problem3;

import java.util.ArrayList;
import java.util.List;

public class App {

    public static void main(String[] args) throws Exception {
        SharedVariables sharedVariables = new SharedVariables();

        for (int i = 1; i < 100; i++) {
            new MyThread(sharedVariables, 5).start();
            new MyThread(sharedVariables, 6).start();
        }
    }

    static class MyThread extends Thread {
        private SharedVariables sharedVariable;

        private Integer request;
        private List<Integer> response;

        public MyThread(SharedVariables variables, Integer request) {
            this.sharedVariable = variables;
            this.request = request;
        }

        @Override
        public void run() {
            // Call the function that modifies the shared variable

            Integer number = request;

            // Acquire read lock to check cached values
            sharedVariable.readLock();
            try {
                if (number.equals(sharedVariable.getLastNumber())) {
                    response = sharedVariable.getLastFactors();

                    // Calculate product from all factors (should be number)
                    int result = response.stream().reduce(1, (a, b) -> a * b);
                    System.out.println(
                            "Given Number: " + number + " creates response " + response + " " + (result == number));
                    return;
                }
            } finally {
                sharedVariable.readUnlock();
            }

            List<Integer> factors = calculateFactors(number);

            // Acquire write lock to update shared values
            sharedVariable.writeLock();
            try {
                sharedVariable.setSharedValues(number, factors);
            } finally {
                sharedVariable.writeUnlock();
            }
            response = factors;
        }

        public static List<Integer> calculateFactors(int number) {
            List<Integer> factors = new ArrayList<>();

            for (int i = 2; i * i <= number; i++) {
                while (number % i == 0) {
                    factors.add(i);
                    number /= i;
                }
            }

            if (number > 1) {
                factors.add(number);
            }

            return factors;
        }
    }
}
