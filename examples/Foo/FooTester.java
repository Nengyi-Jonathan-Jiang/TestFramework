package examples.Foo;

import UTester.*;

public class FooTester {
    public static void main(String[] args) {
        TestFramework.runTest("Testing", () -> {
            return true;
        }).;
    }
}
