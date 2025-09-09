package example;

import testFramework.TestCaseBuilder;
import testFramework.TestFramework;
import testFramework.TestResult;

import java.util.Arrays;

public class VectorTester {
    public static void main(String[] args) {
        // A raw test case, returning test results explicitly
        TestFramework.runTest(
            // Name of test
            "Vector.zero(3)",
            // Zero-argument lambda returning a TestResult
            () -> {
                Vector v = Vector.zero(3);
                if (v.dim() != 3) {
                    return TestResult
                        // We can only construct TestResults through TestResult.failure() or
                        // TestResult.success()
                        .failure()
                        // Method chaining lets us optionally add information to tests
                        .withMessage("Expected Vector.zero(3) to have length 3");
                }
                for (int i = 0; i < 3; i++) {
                    if (v.get(i) != 0) {
                        return TestResult
                            .failure()
                            .withMessage(String.format("Expected Vector.zero(3)[%d] to be 0", i));
                    }
                }
                return TestResult.success();
            }
        );
        // We can run tests multiple times by adding a parameter in the middle
        TestFramework.runTest(
            "new Vector(double[]) with random inputs", 50, () -> {
                int dim = 1 + (int) (10 * Math.random());
                double[] arr = new double[dim];
                for (int i = 0; i < dim; i++) {
                    arr[i] = (int) (Math.random() * 2);
                }
                Vector v = new Vector(arr);
                if (v.dim() != dim) {
                    return TestResult
                        .failure()
                        .withMessage("Expected Vector constructor to preserve dimension")
                        // We can also add an input to
                        .withInput(Arrays.toString(arr));
                }
                for (int i = 0; i < v.dim(); i++) {
                    if (v.get(i) != arr[i]) {
                        return TestResult
                            .failure()
                            .withMessage("Expected Vector constructor to preserve values")
                            .withInput(Arrays.toString(arr));
                    }
                }
                return TestResult.success();
            }
        );
        // Alternatively, we can use TestCaseBuilder to create test cases with less boilerplate, but
        // also less control
        TestFramework.runTest(
            "Vector.dot for (2, 0) and (2, 0)",
            () -> new TestCaseBuilder<Double>()
                // Optionally, specify the input string to be displayed when the test cases fail
                .withInputString("new Vector(2, 0), new Vector(2, 0)")
                .expect(() -> new Vector(2, 0).dot(new Vector(2, 0)))
                .toEqual(4.0)
        );
        TestFramework.runTest(
            "Vector.dot for (1, 0) and (0, 1)",
            () -> new TestCaseBuilder<Double>()
                .withInputString("new Vector(1, 0), new Vector(0, 1)")
                .expect(() -> new Vector(1, 0).dot(new Vector(0, 1)))
                .toEqual(0.0)
        );

        // Partial results of method chaining TestCaseBuilders are reusable!
        TestCaseBuilder<Vector> vectorTester = new TestCaseBuilder<Vector>()
            // We can specify a prettier output format in the "Expected x, instead got y" part
            .withFormatter(v -> Arrays.toString(v.toArray()));
        TestFramework.runTest(
            "Vector.add for (1, 1) and (0, 1)",
            () -> vectorTester
                .withInputString("new Vector(1, 1), new Vector(0, 1)")
                .expect(() -> new Vector(1, 1).plus(new Vector(0, 1)))
                // We can specify a method for testing equality by giving a second argument
                .toEqual(new Vector(1, 2), Vector::equals)
        );

        // If you are using a java version which supports var, that could make this declaration
        // much shorter and thus much more nice to use
        TestCaseBuilder.TestCaseBuilder2<Vector, Vector, Vector> vectorAddTester = vectorTester
            // We can even pre-specify the method to test, if we are testing the same method many
            // times
            .testing(Vector::plus)
            // When we know the method to test, we can also specify input formats automatically
            .withInputFormatter(
                v -> Arrays.toString(v.toArray()),
                v -> Arrays.toString(v.toArray())
            )
            // Remember that we specified the outputFormat in vectorTester's definition. If we
            // wanted to do it here, we need to use withOutputFormatter() instead of withFormatter()
            // (which is not defined on TestCaseBuilder2 or TestCaseBuilder1)
            ;
        TestFramework.runTest(
            "Vector.add for (1, 1, 1) and (0, 1, 2)",
            () -> vectorAddTester
                .expectCall(new Vector(1, 1), new Vector(0, 1))
                .toEqual(new Vector(1, 2), Vector::equals)
        );
        TestFramework.runTest(
            "Vector.add for (1, 2, 3) and (4, 5, 6)",
            () -> vectorAddTester
                .expectCall(new Vector(1, 2, 3), new Vector(4, 5, 6))
                .toEqual(new Vector(5, 7, 9), Vector::equals)
        );
    }
}
