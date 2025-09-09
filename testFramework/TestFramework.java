package testFramework;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static testFramework.TerminalStyle.*;

public class TestFramework {
    private static final int maxFailuresToPrint = 3;

    public static <T> TestResult assertEquals(Supplier<T> actual, T expected) {
        return assertEquals(actual, expected, null);
    }

    public static <T> TestResult assertEquals(Supplier<T> actual, T expected, String input) {
        return assertHelper(
            actual, expected::equals,
            x -> RED.format(
                "expected %s, instead got " + "%s", CYAN.format(expected), CYAN.format(x)), input
        );
    }

    public static TestResult assertInInterval(Supplier<Integer> actual, int expectedMinInclusive,
                                              int expectedMaxInclusive) {
        return assertInInterval(actual, expectedMinInclusive, expectedMaxInclusive, null);
    }

    public static TestResult assertInInterval(Supplier<Integer> actual, int expectedMinInclusive,
                                              int expectedMaxInclusive, String input) {
        return assertHelper(
            actual, x -> x >= expectedMinInclusive && x <= expectedMaxInclusive, x -> String.format(
                "expected %s to be in range [%d, %d]", x, expectedMinInclusive,
                expectedMaxInclusive
            ), input
        );
    }

    private static <T> TestResult assertHelper(Supplier<T> result, Predicate<T> didPass,
                                               Function<T, String> assertErrorMessage,
                                               String input) {
        TestResult testResult;

        try {
            T actualValue = result.get();
            if (didPass.test(actualValue)) {
                testResult = TestResult.success();
            }
            else {
                String message = assertErrorMessage.apply(actualValue);
                throw new AssertionError(message);
            }
        }
        catch (Exception | Error e) {
            String errorMessage = e.getMessage();

            testResult = TestResult.failure().withMessage(errorMessage).withInput(input);
        }

        return testResult;
    }

    public static <R> void runBasicEqualityTest(Supplier<R> func, R expected, String testName) {
        TestFramework.runTest(() -> assertEquals(func, expected), testName);
    }

    public static <T, R> void runBasicEqualityTest(Function<T, R> func, T arg, R expected,
                                                   String testName) {
        TestFramework.runTest(() -> assertEquals(() -> func.apply(arg), expected), testName);
    }

    public static <T1, T2, R> void runBasicEqualityTest(BiFunction<T1, T2, R> func, T1 arg1,
                                                        T2 arg2, R expected, String testName) {
        TestFramework.runTest(() -> assertEquals(() -> func.apply(arg1, arg2), expected), testName);
    }

    public static void runTest(Test test, String testName) {
        TestFramework.runTest(test, testName, 1);
    }

    public static void runTest(Test test, String testName, int numRuns) {
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
