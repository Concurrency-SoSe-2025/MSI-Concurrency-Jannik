package Exercise5.src.Problem2;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class IntrinsicPhilosopher implements Runnable {

    private final int id;
    private volatile boolean eating;
    private final Object table;
    private IntrinsicPhilosopher left;
    private IntrinsicPhilosopher right;

    public IntrinsicPhilosopher(int id, Object table) {
        this.eating = false;
        this.id = id;
        this.table = table;
    }

    public void setLeft(IntrinsicPhilosopher left) {
        this.left = left;
    }

    public void setRight(IntrinsicPhilosopher right){
        this.right = right;
    }

    @Override
    public void run() {
        try {
            while (true) {
                think();
                eat();
            }
        } catch (InterruptedException ex) {
        }
    }

    private void eat() throws InterruptedException {
        synchronized (table) {
            while (left.eating || right.eating) {
                table.wait();
            }
            eating = true;
        }
        System.out.println("Philosopher " + id + " eats for a while");
        TimeUnit.SECONDS.sleep(1);
    }

    private void think() throws InterruptedException {
        synchronized (table) {
            while (left.eating || right.eating) {
                table.notifyAll();
            }
            eating = false;
        }
        System.out.println("Philosopher " + id + " thinks for a while");
        TimeUnit.SECONDS.sleep(1);
    }


    public static void main(String[] args) {
        int numberPhilosophers = 5;
        ReentrantLock table = new ReentrantLock();
        ConditionedPhilosopher[] philosophers = new ConditionedPhilosopher[numberPhilosophers];
        Thread[] threads = new Thread[numberPhilosophers];

        for (int i = 0; i < numberPhilosophers; i++) {
            philosophers[i] = new ConditionedPhilosopher((char)('A' + i), table);
        }

        for (int i = 0; i < numberPhilosophers; i++) {
            ConditionedPhilosopher left = philosophers[(i + numberPhilosophers - 1) % numberPhilosophers];
            ConditionedPhilosopher right = philosophers[(i + 1) % numberPhilosophers];
            philosophers[i].setLeft(left);
            philosophers[i].setRight(right);
        }

        for (int i = 0; i < numberPhilosophers; i++) {
            threads[i] = new Thread(philosophers[i]);
            threads[i].start();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) { }

        for (Thread t : threads) {
            t.interrupt();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) { }
        }

        System.out.println("All philosophers are finished thinking and eating.");
    }
}
