package examples.Foo;

public class Foo {
    int a;
    String b;

    public Foo(int a, String b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public toString() {
        return b + ": " + a;
    }
}