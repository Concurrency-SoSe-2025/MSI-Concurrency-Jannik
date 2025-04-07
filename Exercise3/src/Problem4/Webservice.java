package Problem4;

import java.util.ArrayList;

public class Webservice {
    private int lastNumber;
    private int[] lastFactors;

    // A dedicated lock object to synchronize shared state.
    private final Object lock = new Object();

    public OwnResponse doWebserviceCall(int i) {
        OwnResponse resp = new OwnResponse(i);
        service(new OwnRequest(i), resp);
        return resp;
    }

    public void service(OwnRequest req, OwnResponse resp) {
        int i = extractFromRequest(req);

        // Acquire lock to check cached values
        synchronized (lock) {
            if (i == lastNumber) {
                // If the number matches the cached number, use cached factors
                encodeIntoResponse(resp, lastFactors);
                return;
            }
        }

        // If not found in the cache, factor the number outside the lock
        int[] factors = factor(i);

        // Acquire lock again to update the cache
        synchronized (lock) {
            lastNumber = i;
            lastFactors = factors;
        }

        // Finally, encode into the response
        encodeIntoResponse(resp, factors);
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
        return factorsList.stream().mapToInt(Integer::intValue).toArray();
    }

    private int extractFromRequest(OwnRequest req) {
        return req.i;
    }

    private void encodeIntoResponse(OwnResponse resp, int[] factors) {
        resp.factors = factors;
    }
}