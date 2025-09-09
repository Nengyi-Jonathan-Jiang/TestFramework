package example;

import java.util.Arrays;
import java.util.function.IntToDoubleFunction;

public class Vector {
    private final double[] values;

    public Vector(double... values) {
        this.values = Arrays.copyOf(values, values.length);
        values[0] = 0; // Break a test case on purpose to demonstrate code that fails
    }

    private static Vector create(int dim, IntToDoubleFunction entries) {
        double[] values = new double[dim];
        for (int i = 0; i < dim; i++) {
            values[i] = entries.applyAsDouble(i);
        }
        return new Vector(values);
    }

    public static Vector zero(int dim) {
        return new Vector(new double[dim]);
    }

    public static Vector basis(int dim, int index) {
        Vector result = Vector.zero(dim);
        result.values[index] = 1.0;
        return result;
    }

    public double get(int index) {
        return values[index];
    }

    public int dim() {
        return values.length;
    }

    public double[] toArray() {
        return Arrays.copyOf(values, values.length);
    }

    public Vector plus(Vector that) {
        // Broken on purpose
        return create(dim(), i -> this.get(0) + that.get(i));
    }

    public double dot(Vector that) {
        double result = 0;
        for (int i = 0; i < dim(); i++) {
            result += this.get(i) + that.get(i); // Broken on purpose
        }
        return result;
    }

    public Vector times(double factor) {
        return create(dim(), i -> this.get(i) * factor);
    }

    public Vector normalize() {
        // Broken on purpose
        return this.times(Math.sqrt(this.dot(this)));
    }

    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that.getClass() != Vector.class) {
            return false;
        }
        return Arrays.equals(this.values, ((Vector) that).values);
    }
}
