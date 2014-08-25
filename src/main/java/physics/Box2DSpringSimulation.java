package physics;

import java.util.Map;
import java.util.Map.Entry;
import javafx.scene.Node;
import layout.PhysLayout;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import util.UnorderedPair;

/**
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class Box2DSpringSimulation {

    private PhysLayout layout;
    private Map<Node, Body> bodies;
    private double friction;

    public Box2DSpringSimulation(PhysLayout layout) {
        this.layout = layout;

    }

    /**
     * Applies spring force between a and b.
     *
     * @param a
     * @param b
     * @param spring
     */
    private void applySpring(Body a, Body b, Spring spring) {
        Vec2 pA = a.getWorldCenter();
        Vec2 pB = b.getWorldCenter();
        Vec2 diff = pB.sub(pA);
        double force = spring.getForce(diff.normalize());
        diff.mulLocal((float) force);
        a.applyForceToCenter(diff);
        b.applyForceToCenter(diff.negate());
    }

    private void applyFriction(Body a) {
        Vec2 v = a.getLinearVelocity();
        a.applyForceToCenter(v.mul((float) -friction));
    }

    private void applyAllForces() {
        for (Entry<UnorderedPair<Node>, Spring> e : layout.getAllConnections()) {
            applySpring(bodies.get(e.getKey().getA()), bodies.get(e.getKey().getB()), e.getValue());
        }
        for (Body a : bodies.values()) {
            applyFriction(a);
        }
    }
}
