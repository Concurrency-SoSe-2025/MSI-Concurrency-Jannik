package Problem1;

public class Problem1 {
    public static void main(String[] args) throws InterruptedException {
        Account account = new Account();
        int numberOfThreads = 10;
        int amount = 1;
        Thread[] threads = new Thread[numberOfThreads * 2];

        // Create deposit threads
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    account.deposit(amount);
                }
            });
        }

        // Create withdraw threads
        for (int i = 0; i < numberOfThreads; i++) {
            threads[numberOfThreads + i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    account.withdraw(amount);
                }
            });
        }

        // Start all threads
        for (Thread t : threads) {
            t.start();
        }

        // Wait for all threads to complete
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("Final balance should be 0, because the same amount (10.000) got deposited and withdrawn");
        System.out.println("Final balance: " + account.balance);
    }
}

class Account {
    double balance;

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) {
        balance -= amount;
    }
}
