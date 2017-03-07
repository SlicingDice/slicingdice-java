package com.slicingdice.jslicer;

import java.util.ArrayList;

public class RunQueryTests {
    // Your demo api key, to get a valid demo API key you can use:
    // http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys-demo-key
    private static final String DEMO_API_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfX3NhbHQiOiJkZW1vMG0iLCJwZXJtaXNzaW9uX2xldmVsIjozLCJwcm9qZWN0X2lkIjoxNjEsImNsaWVudF9pZCI6MTB9.vt5eGeQb0AUKu2o075vEzaC5m-XgD4ohgJkDZYBmFu8";

    private static Thread mainThread;

    public static void main(final String[] args) {
        mainThread = Thread.currentThread();

        // the query types to use on tests
        final ArrayList<String> queryTypes = new ArrayList<String>() {{
            add("count_entity");
            add("count_event");
            add("top_values");
            add("aggregation");
            add("result");
            add("score");
        }};

        // Testing class with demo API key
        final SlicingDiceTester sdTester = new SlicingDiceTester(DEMO_API_KEY);

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                showResults(sdTester);
                mainThread.interrupt();
            }
        });

        // run tests for all query types
        for (final String queryType : queryTypes) {
            sdTester.runTests(queryType);
        }

        showResults(sdTester);
    }

    private static void showResults(SlicingDiceTester sdTester){
        System.out.println();
        System.out.println("Results:");
        System.out.println(String.format("\tSuccesses: %s", sdTester.numberOfSuccesses));
        System.out.println(String.format("\tFails: %s", sdTester.numberOfFails));

        for(final Object testFailed : sdTester.failedTests){
            System.out.println(String.format("\t\t- %s", testFailed));
        }

        System.out.println();

        if (sdTester.numberOfFails > 0){
            final boolean isSingular = sdTester.numberOfFails == 1;
            String testsOrTest;
            if(isSingular){
                testsOrTest = "test has";
            } else {
                testsOrTest = "tests have";
            }

            System.out.println(String.format("FAIL: %1$s %2$s failed", sdTester.numberOfFails,
                    testsOrTest));
            // exit with error code to indicate failure
            System.exit(1);
        }
        else {
            System.out.println("SUCCESS: All tests passed");
        }
    }
}
