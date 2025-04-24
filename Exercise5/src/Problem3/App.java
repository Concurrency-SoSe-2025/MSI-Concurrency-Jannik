package Problem3;

import java.util.*;
// import java.util.concurrent.Semaphore;

public class App {

    // private static final Semaphore semaphore = new Semaphore(1);

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

            synchronized (sharedVariable) {
                if (number == sharedVariable.getLastNumber()) {
                    response = sharedVariable.getLastFactors();

                    int result = response.stream().reduce(1, (a, b) -> a * b);
                    System.out.println("Given Number: " + number + " creates response " + response + " " + (result == number));
                    return;
                }
            }

            List<Integer> factors = calculateFactors(number);

            synchronized (sharedVariable) {
                sharedVariable.setLastNumber(number);
                sharedVariable.setLastFactors(factors);
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

        private void sleep() {
            try {
                Thread.sleep(1000);
                // System.out.println(sharedAccount.getBalance());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
