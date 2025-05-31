package Exercise7.Lukas.Factorize;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FactorizerFactory {
    private final SharedCache cache;
    private final Random randomInt;
    private final ReentrantReadWriteLock lock;
    private final int[] compoundNumbers;

    public FactorizerFactory() {
        this.randomInt = new Random();
        this.cache = new SharedCache();
        this.lock = new ReentrantReadWriteLock();
        this.compoundNumbers = getCompoundNumbers(10);
    }

    public FactorizerService getFactorizerService() {
        return new FactorizerService(
                this.compoundNumbers[this.randomInt.nextInt(this.compoundNumbers.length)],
                this.cache,
                this.lock
        );
    }

    public FactorizerService_Callable getCallableFactorizerService() {
        return new FactorizerService_Callable(
                this.compoundNumbers[this.randomInt.nextInt(this.compoundNumbers.length)],
                this.cache,
                this.lock
        );
    }

    private static int[] getCompoundNumbers(int len) {
        int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97};

        Random rand = new Random();
        int[] compounds = new int[len];
        Arrays.fill(compounds, 1);

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < rand.nextInt(5) + 2; j++) {
                compounds[i] *= primes[rand.nextInt(primes.length)];
            }
        }

        return compounds;
    }
}
