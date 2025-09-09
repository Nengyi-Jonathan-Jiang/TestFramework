public class Foo {
    private int a;
    private String b;

    public Foo(int a, String b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return b + ": " + a;
    }

    public static Foo random() {
        return new Foo((int)(Math.random() * 10), "This was random");
    }
}