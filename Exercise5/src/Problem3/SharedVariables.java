package Problem3;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SharedVariables {
    private Integer lastNumber;
    private List<Integer> lastFactors;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public void readLock() {
        readLock.lock();
    }

    public void readUnlock() {
        readLock.unlock();
    }

    public void writeLock() {
        writeLock.lock();
    }

    public void writeUnlock() {
        writeLock.unlock();
    }

    public Integer getLastNumber() {
        if (this.lastNumber != null) {
            return this.lastNumber;
        } else {
            return -1;
        }
    }

    public List<Integer> getLastFactors() {
        return lastFactors;
    }

    public void setSharedValues(Integer lastNumber, List<Integer> lastFactors) {
        this.lastNumber = lastNumber;
        this.lastFactors = lastFactors;
        if (!checkFactors(this.lastNumber, this.lastFactors)) {
            System.out.println(
                    "Factors of " + lastNumber + " are " + lastFactors + ", but " + lastNumber + " and " + lastFactors);
        }
    }

    public static boolean checkFactors(int original, List<Integer> factors) {
        int product = 1;
        for (int factor : factors) {
            product *= factor;
        }
        return product == original;
    }
}
