package Problem3;

import java.util.List;

public class SharedVariables {
    private Integer lastNumber;
    private List<Integer> lastFactors;

    // public void setLastNumber(Integer lastNumber) {
    //     this.lastNumber = lastNumber;
    // }

    public Integer getLastNumber() {
        // System.out.println(this.lastNumber);
        if (this.lastNumber != null) {
            return this.lastNumber;
        } else {
            return -1;
        }
    }

    public void setLastNumber(Integer lastNumber) {
        this.lastNumber = lastNumber;

        if (lastFactors != null && checkFactors(this.lastNumber, this.lastFactors)) {
            System.out.println(lastNumber + ": setNumber: Factors of " + lastNumber + " are " + lastFactors);
        }
    }

    public void setLastFactors(List<Integer> lastFactors) {
        this.lastFactors = lastFactors;

        // if (!checkFactors(this.lastNumber, this.lastFactors)) {
        //     System.out.println(lastNumber + ": setFactors: Factors of " + lastNumber + " are " + lastFactors);
        // }
    }

    public void setSharedValues(Integer lastNumber, List<Integer> lastFactors) {
        this.lastNumber = lastNumber;
        this.lastFactors = lastFactors;
        // System.out.println(checkFactors(lastNumber, lastFactors) + ": Factors of " + lastNumber + " are " + lastFactors);
        if (!checkFactors(this.lastNumber, this.lastFactors)) {
            System.out.println("Factors of " + lastNumber + " are " + lastFactors + ", but " + lastNumber + " and " + lastFactors);
        }
    }

    public List<Integer> getLastFactors() {
        return lastFactors;
    }

    public static boolean checkFactors(int original, List<Integer> factors) {
        int product = 1;
        for (int factor : factors) {
            product *= factor;
        }
        return product == original;
    }
}
