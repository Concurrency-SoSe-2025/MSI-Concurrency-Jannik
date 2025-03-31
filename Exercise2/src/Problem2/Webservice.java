package Problem2;

import java.util.ArrayList;

class Webservice {
    int lastNumber;
    int[] lastFactors;

    public OwnResponse doWebserviceCall(int i) {
        OwnResponse resp = new OwnResponse(i);
        service(new OwnRequest(i), resp);
        return resp;
    }

    public void service(OwnRequest req, OwnResponse res) {
        int i = extractFromRequest(req);
        int[] factors = factor(i);
        lastNumber = i;
        // Delay here to increase possibility for threads to switch which would create a
        // multi-attribute state race condition
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lastFactors = factors;
        encodeIntoResponse(res, factors);
    }

    public int[] factor(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Number must be positive.");
        }
        ArrayList<Integer> factors = new ArrayList<>();
        // Check for each number if it's a factor of the given number
        for (int i = 1; i <= number; i++) {
            if (number % i == 0) {
                factors.add(i);
                // Add a small delay in each iteration for threads to spend more time
                // try {
                // Thread.sleep(10);
                // } catch (InterruptedException e) {
                // e.printStackTrace();
                // }
            }
        }
        int[] arr = factors.stream().mapToInt(i -> i).toArray();
        return arr;
    }

    private int extractFromRequest(OwnRequest req) {
        return req.i;
    }

    private void encodeIntoResponse(OwnResponse resp, int[] factors) {
        resp.factors = factors;
    }
}