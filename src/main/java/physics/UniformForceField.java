package physics;

import org.jbox2d.common.Vec2;

/**
 * A uniform force field projected by an infinite plane.
 *
 * Assuming the plane is perpendicular to the X-Y plane, the force is
 * perpendicular to the intersection, and remains constant on each side of the
 * intersection.
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class UniformForceField extends ForceField {

    private final Vec2 location;
    private final Vec2 intersection;
    private final double strength;

    /**
     * Create a new field.
     *
     * @param location a point on the intersection with X-Y.
     * @param normal the normal vector to the plane.
     * @param strength the strength of the field.

     */
    public UniformForceField(Vec2 location, Vec2 normal, double strength) {
        this.location = location;
        this.strength = strength;
        normal.normalize();

        this.intersection = new Vec2(normal.y, normal.x);
        projection(location, intersection);
    }

    @Override
    public Vec2 force(Vec2 point) {
        Vec2 relative = point.sub(location);
        relative.normalize();
        projection(relative, intersection);
        return relative.mul((float)strength);
    }
}
