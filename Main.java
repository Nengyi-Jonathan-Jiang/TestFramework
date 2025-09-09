import UTester.TestFramework;

public class Main {
    public static void main(String[] args) {
        TestFramework.runBasicEqualityTest(() -> true, true, "Test 1");
        TestFramework.runBasicEqualityTest(() -> 2, 2, "Test 2");
        TestFramework.runBasicEqualityTest(() -> new Foo(2, "WOWOW"), new Foo(2, "XXXXX"), "Test 3");
        TestFramework.runBasicEqualityTest(() -> Foo.random(), new Foo(2, "XXXXX"), "Test 3");
    }
}