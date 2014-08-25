package physics;

import org.jbox2d.common.Vec2;

/**
 * A field emanating from a single point location.
 * 
 * The strength drops off quadratically with distance, and acts in the
 * direction of their difference.
 * 
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class PointForceField extends ForceField {
    private final Vec2 source;
    double strength;

    /**
     * Create a new point source at the specified location.
     * 
     * A positive strength repulses all particles.
     * 
     * @param source
     * @param strength 
     */
    public PointForceField(Vec2 source, double strength) {
        this.source = source;
        this.strength = strength;
    }

    @Override
    public Vec2 force(Vec2 location) {
        Vec2 relative = location.sub(source);
        double distance = relative.normalize();
        return relative.mul((float)(strength / (distance * distance)));
    }
}
