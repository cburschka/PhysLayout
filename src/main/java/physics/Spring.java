package physics;

import org.jbox2d.common.Vec2;

/**
 * An idealized mechanical spring.
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class Spring {

    private final double length;
    private final double strength;

    /**
     * Create a new spring.
     *
     * @param length The equilibrium length.
     * @param strength The stiffness constant k.
     */
    public Spring(double length, double strength) {
        assert length > 0;
        assert strength > 0;
        this.length = length;
        this.strength = strength;
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
        Vec2 relative = b.sub(a);
        double distance = relative.normalize();
        return relative.mul((float) getForce(distance));
    }

}
