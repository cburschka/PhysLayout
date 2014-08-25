package physics;

import org.jbox2d.common.Vec2;

/**
 * A cylindrical field projected by an infinite line.
 *
 * The field is perpendicular to the line, and drops off quadratically with
 * distance.
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class LineForceField extends ForceField {

    private final Vec2 location;
    private final Vec2 direction;
    private final double strength;

    /**
     * Create a new field.
     *
     * @param location a single point along the line.
     * @param direction the direction of the line
     * @param strength the strength of the field.
     */
    public LineForceField(Vec2 location, Vec2 direction, double strength) {
        this.location = location;
        this.direction = direction;
        this.strength = strength;

        // Normalize, so direction is a unit vector, and location is perpendicular.
        direction.normalize();
        projection(location, direction);
    }

    @Override
    public Vec2 force(Vec2 point) {
        Vec2 relative = point.sub(location);
        projection(relative, direction);
        double distance = relative.normalize();
        return relative.mul((float) (strength / (distance * distance)));
    }
}
