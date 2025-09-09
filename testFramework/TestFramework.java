package testFramework;

import java.util.ArrayList;
import java.util.List;

import static testFramework.TerminalStyle.*;

public class TestFramework {
    private static final int maxFailuresToPrint = 3;

    public static void runTest(String testName, Test test) {
        TestFramework.runTest(testName, 1, test);
    }

    public static void runTest(String testName, int numRuns, Test test) {
        List<TestResult.Failure> failures = new ArrayList<>();

        long startTimeNanos = System.nanoTime();

        for (int i = 0; i < numRuns; i++) {
            TestResult result = test.run();

            if (!result.didPass()) {
                failures.add(result.asFailure());
            }
        }

        long endTimeNanos = System.nanoTime();
        double totalRuntimeSeconds = (endTimeNanos - startTimeNanos) / 1_000_000_000.0;

        String runTimeInfo = MAGENTA.format("%.3fs", totalRuntimeSeconds);

        if (numRuns > 1) {
            runTimeInfo += String.format(
                " (%s per run * %s runs)", MAGENTA.format("%.3fs", totalRuntimeSeconds / numRuns),
                MAGENTA.format("%d", numRuns)
            );
        }

        if (failures.isEmpty()) {
            System.out.printf("%s %s in %s%n", GREEN.format("Passed test"), testName, runTimeInfo);
        }
        else {
            System.out.printf(
                "%s %s in %s:%n",
                numRuns == 1 ? RED.format("Failed test") : RED.format(
                    "Failed %d/%d runs for test",
                    failures.size(), numRuns
                ), testName, runTimeInfo
            );

            // Only print up to a max threshold of errors
            int numFailuresPrinted = 0;
            for (TestResult.Failure testResult : failures) {
                String input = testResult.input();

                System.out.printf(
                    "    %s%s%n",
                    input == null ? "" : RED.format("With input %s: ", CYAN.format(input)),
                    RED.format(testResult.message())
                );

                numFailuresPrinted++;

                if (numFailuresPrinted == maxFailuresToPrint) {
                    System.out.println(RED.format("    etc."));
                    break;
                }
            }
        }
    }
}
