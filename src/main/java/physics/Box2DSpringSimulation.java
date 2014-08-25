package physics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.scene.Node;
import layout.PhysLayout;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

/**
 * Manage a JBox2D simulation of multiple JavaFX nodes. Nodes have no collision,
 * and are moved by applying forces (from mechanical springs and forcefields).
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class Box2DSpringSimulation {

    private final PhysLayout layout;
    private final Map<Node, Body> bodies;
    private final World world;
    private final Set<ForceField> fields;
    private double friction;

    public Box2DSpringSimulation(PhysLayout layout) {
        this.layout = layout;
        bodies = new HashMap<>();
        fields = new HashSet<>();

        // New zero-gravity world:
        world = new World(new Vec2(0, 0));
        BodyDef def = new BodyDef();

        layout.getNodes().stream().forEach((node) -> {
            def.position.set((float) node.getLayoutX(), (float) node.getLayoutY());
            Body body = world.createBody(def);
            // Attach an infinitely dense point mass to the body:
            body.createFixture(new ShapelessShape((layout.getMass(node))), Float.POSITIVE_INFINITY);
            bodies.put(node, body);
        });
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
        Vec2 force = spring.getForce(pA, pB);
        a.applyForceToCenter(force);
        b.applyForceToCenter(force.negate());
    }

    private void applyFriction(Body a) {
        Vec2 v = a.getLinearVelocity();
        a.applyForceToCenter(v.mul((float) -friction));
    }

    private void applyAllForces() {
        layout.getAllConnections().stream().forEach((e) -> {
            applySpring(bodies.get(e.getKey().getA()), bodies.get(e.getKey().getB()), e.getValue());
        });
        bodies.values().stream().forEach((a) -> {
            applyFriction(a);
            fields.stream().forEach((field) -> {
                a.applyForceToCenter(field.force(a.getWorldCenter()));
            });
        });
    }
}
