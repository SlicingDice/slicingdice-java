package com.slicingdice.jslicer;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class RunQueryTests {
    // Your demo api key, to get a valid demo API key you can use:
    // http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys-demo-key
    private static final String DEMO_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfX3NhbHQiOiJkZW1vNzAzM20iLCJwZXJtaXNzaW9uX2xldmVsIjozLCJwcm9qZWN0X2lkIjoyNzAzMywiY2xpZW50X2lkIjoxMH0.8lLB7vDAj8SecpHUsgCyZm4yRoizqggKPRm4Q9BfEu8";

    private static String getDemoKey() {
        String demoApiKey = System.getenv("SD_API_KEY");

        if (demoApiKey == null) {
            demoApiKey = DEMO_API_KEY;
        }

        return demoApiKey;
    }

    public static void main(final String[] args) throws ExecutionException, InterruptedException {
        // the query types to use on tests
        final ArrayList<String> queryTypes = new ArrayList<String>() {{
            add("sql");
            add("count_entity");
            add("count_event");
            add("top_values");
            add("aggregation");
            add("result");
            add("score");
            add("delete");
            add("update");
        }};

        // Testing class with demo API key
        final SlicingDiceTester sdTester = new SlicingDiceTester(getDemoKey(), true);

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                showResults(sdTester);
            }
        });

        // run tests for all query types
        for (final String queryType : queryTypes) {
            sdTester.runTests(queryType);
        }

        if (!showResults(sdTester)) {
            System.exit(1);
        }
        System.exit(0);
    }

    private static boolean showResults(final SlicingDiceTester sdTester) {
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
            return false;
        }

        System.out.println("SUCCESS: All tests passed");
        return true;
    }
}
