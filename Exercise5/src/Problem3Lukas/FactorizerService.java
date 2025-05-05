package Problem3Lukas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FactorizerService extends Thread{
    private final int number;
    private final SharedCache cache;
    private final Lock readLock;
    private final Lock writeLock;

    public static void main(String[] args) throws InterruptedException {
        SharedCache sharedCache = new SharedCache();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        List<Thread> threads = new ArrayList<>();
        int maxNumber = 1000;

        for (int i = 300; i <= maxNumber; i++) {
            FactorizerService service1 = new FactorizerService(i, sharedCache, lock);
            service1.start();
            threads.add(service1);

            FactorizerService service2 = new FactorizerService(i, sharedCache, lock);
            service2.start();
            threads.add(service2);
        }

        for (Thread t : threads) {
            t.join();
        }
    }

    public FactorizerService(int number, SharedCache cache, ReentrantReadWriteLock lock){
        this.number = number;
        this.cache = cache;
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    @Override
    public void run() {
        int[] factors = new int[0];
        boolean hit = false;

        readLock.lock();
        try {
            if (number == cache.lastNumber) {
                factors = cache.lastFactors.clone();
                hit = true;
            }
        } finally {
            readLock.unlock();
        }

        if (!hit) {
            factors = factorize(number);

            writeLock.lock();
            try {
                if (cache.lastNumber != number) {
                    cache.lastNumber  = number;
                    cache.lastFactors = factors.clone();
                } else {
                    factors = cache.lastFactors.clone();
                    hit = true;
                }
            } finally {
                writeLock.unlock();
            }
        }

        printResult(number, factors, hit);
    }

    public static void printResult(long number, int[] factors, boolean hit) {
        String out = String.format("Cache %s on %d: ", hit ? "Hit" : "Miss", number);

        out += IntStream.of(factors)
                .takeWhile(f -> f != 0)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(" * "));

        System.out.println(out);
    }

    static int[] factorize(int number) {

        List<Integer> factors = new ArrayList<>();

        int sqrt = (int) Math.sqrt(number);
        for (int i = 2; i < sqrt; i += 1) {
            while (number % i == 0) {
                factors.add(i);
                number /= i;
            }
        }

        if (number > 1) {
            factors.add(number);
        }

        Collections.sort(factors);
        int[] factors_out = new int[factors.size()];
        for (int i = 0; i < factors.size(); i++) {
            factors_out[i] = factors.get(i);
        }

        return factors_out;
    }
}
