package com.slicingdice.jslicer;

import junit.framework.Test;

import java.util.ArrayList;

public class RunQueryTests {

    private static Thread mainThread;
    public static void main(String[] args) {
        mainThread = Thread.currentThread();


        ArrayList<String> queryTypes = new ArrayList<String>() {{
            add("count_entity");
            add("count_event");
            add("top_values");
            add("aggregation");
            add("result");
            add("score");
        }};

        // Testing class with demo API key
        // http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys-demo-key
        final SlicingDiceTester sdTester = new SlicingDiceTester("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfX3NhbHQiOiJkZW1vMG0iLCJwZXJtaXNzaW9uX2xldmVsIjozLCJwcm9qZWN0X2lkIjoxNjEsImNsaWVudF9pZCI6MTB9.vt5eGeQb0AUKu2o075vEzaC5m-XgD4ohgJkDZYBmFu8");

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                showResults(sdTester);
                mainThread.interrupt();
            }
        });

        for (String queryType : queryTypes) {
            sdTester.runTests(queryType);
        }
        showResults(sdTester);
    }

    private static void showResults(SlicingDiceTester sdTester){
        System.out.println();
        System.out.println("Results:");
        System.out.println(String.format("  Successes: %s", sdTester._numSuccesses));
        System.out.println(String.format("  Fails: %s", sdTester._numFails));

        for(Object testFailed : sdTester._failedTests){
            System.out.println(String.format("    - %s", testFailed));
        }

        System.out.println();

        if (sdTester._numFails > 0){
            boolean isSingular = sdTester._numFails == 1;
            String testsOrTest = null;
            if(isSingular){
                testsOrTest = "test has";
            } else {
                testsOrTest = "tests have";
            }

            System.out.println(String.format("FAIL: %1$s %2$s failed", sdTester._numFails, testsOrTest));
            System.exit(1);
        }
        else {
            System.out.println("SUCCESS: All tests passed");
        }
    }
}
