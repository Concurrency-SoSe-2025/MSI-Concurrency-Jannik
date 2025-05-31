package Exercise7.Lukas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FactorizerService implements Runnable{
    private final int number;
    private final SharedCache cache;
    private final Lock readLock;
    private final Lock writeLock;

    public FactorizerService(int number, SharedCache cache, ReentrantReadWriteLock lock){
        this.number = number;
        this.cache = cache;
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    @Override
    public void run() {
        int[] factors;
        boolean hit = false;

        try {
            readLock.lock();
            factors = cache.sharedCache.get(this.number);
            if (factors != null) {
                hit = true;
            }
        } finally {
            readLock.unlock();
        }

        if (!hit) {
            factors = factorize(number);

            try {
                writeLock.lock();
                if (cache.sharedCache.get(this.number) == null) {
                    cache.sharedCache.put(number, factors.clone());
                } else {
                    factors = cache.sharedCache.get(this.number);
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
