package physics;

import java.util.Objects;
import org.jbox2d.common.Vec2;

/**
 * An idealized mechanical spring.
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class Spring {

    private final double length;
    private final double strength;
    private final Vec2 a, b;

    /**
     * Create a new spring.
     *
     * The spring is anchored at a specific point relative to the origin.
     *
     * @param length The equilibrium length.
     * @param strength The stiffness constant k.
     * @param a anchor point on a
     * @param b anchor point on b
     */
    public Spring(double length, double strength, Vec2 a, Vec2 b) {
        assert length > 0;
        assert strength > 0;
        this.length = length;
        this.strength = strength;
        this.a = a;
        this.b = b;
    }

    public Spring(double length, double strength) {
        this(length, strength, new Vec2(), new Vec2());
    }

    /**
     * Calculate the spring's currently exerted force.
     *
     * @param distance The current (positive) length l of the spring.
     * @return The force (positive force is directed inward to the other point).
     */
    public double getForce(double distance) {
        return (distance - length) * strength;
    }

    /**
     * Calculate the spring's currently exerted force.
     *
     * @param a the first point
     * @param b the second point
     * @return the force acting on the first point (flip sign for second)
     */
    public Vec2 getForce(Vec2 a, Vec2 b) {
        Vec2 relative = b.add(this.b).sub(a.add(this.a));
        double distance = relative.normalize();
        return relative.mul((float) getForce(distance));
    }

    public Spring reverse() {
        return new Spring(length, strength, b, a);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Spring other = (Spring) obj;
        if (Double.doubleToLongBits(this.length) != Double.doubleToLongBits(other.length)) {
            return false;
        }
        if (Double.doubleToLongBits(this.strength) != Double.doubleToLongBits(other.strength)) {
            return false;
        }
        if (!Objects.equals(this.a, other.a)) {
            return false;
        }
        if (!Objects.equals(this.b, other.b)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.length) ^ (Double.doubleToLongBits(this.length) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.strength) ^ (Double.doubleToLongBits(this.strength) >>> 32));
        hash = 53 * hash + Objects.hashCode(this.a);
        hash = 53 * hash + Objects.hashCode(this.b);
        return hash;
    }

}
