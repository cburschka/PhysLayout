package physics;

/**
 * A two-dimensional vector. *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class Vector {

    private double x;
    private double y;

    /**
     * Create a new vector.
     *
     * @param x horizontal component
     * @param y vertical component
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void add(Vector v) {
        x += v.x;
        y += v.y;
    }

    public Vector difference(Vector v) {
        return new Vector(x - v.x, y - v.y);
    }

    public void scale(double a) {
        x *= a;
        y *= a;
    }

    public void unit() {
        scale(norm());
    }

    public double norm() {
        return Math.pow(x * x + y * y, 0.5);
    }
}
