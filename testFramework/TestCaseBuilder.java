package testFramework;

import java.util.function.*;

import static testFramework.TerminalStyle.CYAN;
import static testFramework.TerminalStyle.RED;

public class TestCaseBuilder<R> {
    private final Supplier<R> supplier;
    private final Function<R, String> formatter;
    private final Supplier<String> input;

    public TestCaseBuilder() {
        this(null, Object::toString, null);
    }

    private TestCaseBuilder(Supplier<R> supplier, Function<R, String> formatter,
                            Supplier<String> input) {
        this.supplier = supplier;
        this.formatter = formatter;
        this.input = input;
    }

    public TestCaseBuilder<R> expect(Supplier<R> supplier) {
        return new TestCaseBuilder<>(supplier, formatter, input);
    }

    public TestCaseBuilder<R> expect(R value) {
        return expect(() -> value);
    }

    public TestCaseBuilder<R> withFormatter(Function<R, String> formatter) {
        return new TestCaseBuilder<>(supplier, formatter, input);
    }

    public TestCaseBuilder<R> withInputString(Supplier<String> input) {
        return new TestCaseBuilder<>(supplier, formatter, input);
    }

    public TestCaseBuilder<R> withInputString(String input) {
        return withInputString(() -> input);
    }

    public TestResult toEqual(R expected) {
        return toEqual(expected, Object::equals);
    }

    public TestResult toEqual(R expected, BiPredicate<R, R> equals) {
        return toSatisfy(
            actual -> equals.test(actual, expected),
            actual -> RED.format(
                "Expected %s, instead got %s",
                CYAN.format(formatter.apply(expected)),
                CYAN.format(formatter.apply(actual))
            )
        );
    }

    public TestResult toBeTrue() {
        return toSatisfy(
            actual -> ((Boolean) true).equals(actual),
            x -> RED.format("Expected true")
        );
    }

    public TestResult toBeFalse() {
        return toSatisfy(
            actual -> ((Boolean) true).equals(actual),
            x -> RED.format("Expected false")
        );
    }

    public TestResult toSatisfy(Predicate<R> predicate, Function<R, String> message) {
        try {
            R actualValue = supplier.get();
            if (predicate.test(actualValue)) {
                return TestResult.success();
            }
            else {
                throw new AssertionError(message.apply(actualValue));
            }
        }
        catch (Throwable e) {
            String errorMessage = e.getMessage();

            return TestResult
                .failure()
                .withMessage(errorMessage)
                .withInput(input == null ? null : input.get());
        }
    }

    public TestResult toThrow(Class<Throwable> throwableClass) {
        try {
            R actualValue = supplier.get();
            return TestResult.failure().withMessage(String.format(
                "Expected to throw %s, instead got %s",
                CYAN.format(throwableClass.getSimpleName()),
                CYAN.format(formatter.apply(actualValue))
            )).withInput(input == null ? null : input.get());
        }
        catch (Throwable e) {
            if (throwableClass.isInstance(e)) {
                return TestResult.success();
            }
            else {
                return TestResult
                    .failure()
                    .withMessage(e.getMessage())
                    .withInput(input == null ? null : input.get());
            }
        }
    }

    public <T> TestCaseBuilder1<T, R> testing(Function<T, R> func) {
        return new TestCaseBuilder1<>(func, input == null ? null : x -> input.get(), formatter);
    }

    public <T1, T2> TestCaseBuilder2<T1, T2, R> testing(BiFunction<T1, T2, R> func) {
        return new TestCaseBuilder2<>(
            func,
            input == null ? null : x -> input.get(),
            null,
            formatter
        );
    }

    public static class TestCaseBuilder1<T, R> {
        private final Function<T, R> function;
        private final Function<T, String> inputFormatter;
        private final Function<R, String> outputFormatter;

        private TestCaseBuilder1(Function<T, R> function, Function<T, String> inputFormatter,
                                 Function<R, String> outputFormatter) {
            this.function = function;
            this.inputFormatter = inputFormatter;
            this.outputFormatter = outputFormatter;
        }

        public TestCaseBuilder1<T, R> withInputFormatter(Function<T, String> formatter) {
            return new TestCaseBuilder1<>(function, formatter, outputFormatter);
        }

        public TestCaseBuilder1<T, R> withOutputFormatter(Function<R, String> formatter) {
            return new TestCaseBuilder1<>(function, inputFormatter, formatter);
        }

        public TestCaseBuilder<R> withInput(T input) {
            return new TestCaseBuilder<>(
                () -> function.apply(input),
                outputFormatter,
                inputFormatter == null ? null : () -> inputFormatter.apply(input)
            );
        }
    }

    public static class TestCaseBuilder2<T1, T2, R> {
        private final BiFunction<T1, T2, R> function;
        private final Function<T1, String> input1Formatter;
        private final Function<T2, String> input2Formatter;
        private final Function<R, String> outputFormatter;

        private TestCaseBuilder2(BiFunction<T1, T2, R> function,
                                 Function<T1, String> input1Formatter,
                                 Function<T2, String> input2Formatter,
                                 Function<R, String> outputFormatter) {
            this.function = function;
            this.input1Formatter = input1Formatter;
            this.input2Formatter = input2Formatter;
            this.outputFormatter = outputFormatter;
        }

        public TestCaseBuilder2<T1, T2, R> withInputFormatter(Function<T1, String> input1Formatter,
                                                              Function<T2, String> input2Formatter) {
            return new TestCaseBuilder2<>(
                function,
                input1Formatter,
                input2Formatter,
                outputFormatter
            );
        }

        public TestCaseBuilder2<T1, T2, R> withOutputFormatter(
            Function<R, String> outputFormatter) {
            return new TestCaseBuilder2<>(
                function,
                input1Formatter,
                input2Formatter,
                outputFormatter
            );
        }

        public TestCaseBuilder<R> expectCall(T1 input1, T2 input2) {
            return new TestCaseBuilder<>(
                () -> function.apply(input1, input2), outputFormatter,
                () -> formatInputs(input1, input2)
            );
        }

        private String formatInputs(T1 input1, T2 input2) {
            String input1String = input1Formatter == null ? null :
                input1Formatter.apply(input1);
            String input2String = input2Formatter == null ? null :
                input2Formatter.apply(input2);

            return input1String == null ? input2String : input2String == null ? input1String
                : input1String + ", " + input2String;
        }
    }
}
