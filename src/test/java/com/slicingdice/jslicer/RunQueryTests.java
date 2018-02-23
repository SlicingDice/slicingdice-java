package com.slicingdice.jslicer;

import java.util.ArrayList;

public class RunQueryTests {
    // Your demo api key, to get a valid demo API key you can use:
    // http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys-demo-key
    private static final String DEMO_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfX3NhbHQiOiIxN" +
            "TE4NjA3ODQ0NDAzIiwicGVybWlzc2lvbl9sZXZlbCI6MywicHJvamVjdF9pZCI6NDY5NjYsImNsaWVudF9pZ" +
            "CI6OTUxfQ.S6LCWQDcLS1DEFy3lsqk2jTGIe5rJ5fsQIvWuuFBdkw";

    public static void main(final String[] args) {
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
        final SlicingDiceTester sdTester = new SlicingDiceTester(DEMO_API_KEY, true);

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
