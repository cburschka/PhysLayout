package physics;

import org.jbox2d.common.Vec2;

/**
 * Implements a field that applies a force on all particles.
 *
 * The field acts on all particles equally, either repulsing or attracting.
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public abstract class ForceField {

    public abstract Vec2 force(Vec2 location);

    /**
     * Project a 2D vector onto another.
     *
     * Takes two vectors a and b and calculate a = b*l + c, where l is scalar
     * and c is perpendicular to b.
     *
     * @param source the projection source (will be changed to the perpendicular
     * remainder)
     * @param target the projection target (unchanged)
     * @return the scalar product
     */
    public static double projection(Vec2 source, Vec2 target) {
        double dot = source.x * target.x + source.y * target.y;
        source.subLocal(target.mul((float) dot));
        return dot;
    }
}
