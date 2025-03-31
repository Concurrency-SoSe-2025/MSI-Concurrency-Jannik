package Problem3;

import java.util.ArrayList;

public class Webservice {
    int lastNumber;
    int[] lastFactors;

    public OwnResponse doWebserviceCall(int i) {
        OwnResponse resp = new OwnResponse(i);
        service(new OwnRequest(i), resp);
        return resp;
    }

    public void service(OwnRequest req, OwnResponse res) {
        int i = extractFromRequest(req);
        if (i == lastNumber) {
            // Increase Chance of Threads to switch and produce check-then-act race
            // condition
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Return with cache for number " + i);
            encodeIntoResponse(res, lastFactors);
        } else {
            int[] factors = factor(i);
            lastNumber = i;
            lastFactors = factors;
            encodeIntoResponse(res, factors);
        }
    }

    private int[] factor(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Number must be positive.");
        }
        ArrayList<Integer> factors = new ArrayList<>();
        // Check for each number if it's a factor of the given number
        for (int i = 1; i <= number; i++) {
            if (number % i == 0) {
                factors.add(i);
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