package Problem5;

import java.util.ArrayList;

public class Webservice {
    private int lastNumber;
    private int[] lastFactors;

    // A dedicated lock object to synchronize access to shared state.
    private final Object lock = new Object();

    public OwnResponse doWebserviceCall(int i) {
        OwnResponse resp = new OwnResponse(i);
        service(new OwnRequest(i), resp);
        return resp;
    }

    public void service(OwnRequest req, OwnResponse res) {
        int number = extractFromRequest(req);

        // Take a local snapshot of the shared state (lastNumber, lastFactors) while
        // holding the lock
        int localLastNumber;
        int[] localLastFactors;
        synchronized (lock) {
            localLastNumber = lastNumber;
            localLastFactors = lastFactors;
        }

        // Compare the request with the snapshot
        if (number == localLastNumber) {
            // Request is the same as the local snapshot, return snapshot factors
            System.out.println("Return cached factors from snapshot for number " + number);
            encodeIntoResponse(res, localLastFactors);
        } else {
            // Compute factors outside the lock
            int[] computedFactors = factor(number);

            // Reacquire the lock and update the cache
            synchronized (lock) {
                lastNumber = number;
                lastFactors = computedFactors;
                encodeIntoResponse(res, computedFactors);
            }
        }
    }

    private int[] factor(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Number must be positive.");
        }
        ArrayList<Integer> factorsList = new ArrayList<>();
        for (int i = 1; i <= number; i++) {
            if (number % i == 0) {
                factorsList.add(i);
            }
        }
        return factorsList.stream().mapToInt(i -> i).toArray();
    }

    private int extractFromRequest(OwnRequest req) {
        return req.i;
    }

    private void encodeIntoResponse(OwnResponse resp, int[] factors) {
        resp.factors = factors;
    }
}