package Problem2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionedPhilosopher implements Runnable {
    private final char id;
    private boolean eating;
    private ConditionedPhilosopher left, right;
    private final ReentrantLock table;
    private final Condition condition;

    public ConditionedPhilosopher(char id, ReentrantLock table) {
        this.id = id;
        this.eating = false;
        this.table = table;
        this.condition = table.newCondition();
    }

    public void setLeft(ConditionedPhilosopher left) {
        this.left = left;
    }

    public void setRight(ConditionedPhilosopher right){
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

    private void think() throws InterruptedException {
        table.lock();
        try {
            eating = false;
            left.condition.signal();
            right.condition.signal();
        } finally {
            table.unlock();
        }
        System.out.println("Philosopher " + id + " thinks for a while");
        TimeUnit.SECONDS.sleep(1);
    }

    private void eat() throws InterruptedException {
        table.lock();
        try {
            while (left.eating || right.eating)
                condition.await();
            eating = true;
        } finally {
            table.unlock();
        }
        System.out.println("Philosopher " + id + " eats for a while");
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

        System.out.println("All philosophers are finished thinking and eatingg.");
    }
}
