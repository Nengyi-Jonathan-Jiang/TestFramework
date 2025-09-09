package testFramework;

public abstract class TestResult {
    // Private constructor ensures that only the static inner classes may extend this class
    // The goal is to emulate a sum type, where a TestResult may be a Success (empty class) or a
    // Failure (which has a few instance variables).
    private TestResult() {}

    public abstract boolean didPass();

    public final Failure asFailure() {
        if (didPass()) {
            throw new ClassCastException("Cannot cast passed test result to Failure");
        }
        return (Failure) this;
    }

    public static Success success() {
        return new Success();
    }

    public static Failure failure() {
        return new Failure();
    }

    public static final class Success extends TestResult {
        private Success() {}

        @Override
        public boolean didPass() {
            return true;
        }
    }

    public static final class Failure extends TestResult {
        private final String message;
        private final String input;

        private Failure() {
            this(null, null);
        }

        private Failure(String message, String input) {
            this.message = message;
            this.input = input;
        }

        @Override
        public boolean didPass() {
            return false;
        }

        public Failure withMessage(String message) {
            return new Failure(message, input);
        }

        public Failure withInput(String input) {
            return new Failure(message, input);
        }

        public String message() {
            return message;
        }

        public String input() {
            return input;
        }
    }
}